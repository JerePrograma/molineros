-- Nomencladores configurables de Compras por sector y tipo de cotizacion.
-- PostgreSQL 9.6. ISO-8859-1 sin BOM. NO APLICADO por Codex.
-- Ejecutar con PGCLIENTENCODING=LATIN1 y psql -X -v ON_ERROR_STOP=1.
-- Fuente: definiciones instaladas leidas el 13/10/2026 en devmolineros,
-- PostgreSQL 9.6.15. Este script no recrea triggers ni cambia historicos.
-- Instalar con la aplicacion detenida para impedir operaciones intermedias.
-- La administracion posterior consiste solamente en INSERT/UPDATE/DELETE
-- sobre compras.sector_nomenclador. No requiere reiniciar Tomcat.
-- Si se elimina la ultima fila especifica, vuelve a regir el conjunto
-- general del sector, si existe: esa eliminacion NO deshabilita el rubro.

BEGIN;

DO $precondicion$
DECLARE
    v_faltantes TEXT;
BEGIN
    SELECT string_agg(requisito, E'\n' ORDER BY requisito)
    INTO v_faltantes
    FROM (VALUES
        ('tabla compras.sector_requerimiento', to_regclass('compras.sector_requerimiento') IS NOT NULL),
        ('tabla compras.tipo_prestacion', to_regclass('compras.tipo_prestacion') IS NOT NULL),
        ('tabla public.tipo_nomenclador', to_regclass('public.tipo_nomenclador') IS NOT NULL),
        ('tabla autorizaciones.nomenclador', to_regclass('autorizaciones.nomenclador') IS NOT NULL),
        ('tipo autorizaciones.nomenclador_detalle', to_regtype('autorizaciones.nomenclador_detalle') IS NOT NULL),
        ('funcion compras.normalizar_sector(varchar)', to_regprocedure('compras.normalizar_sector(character varying)') IS NOT NULL),
        ('funcion compras.normalizar_usuario(varchar)', to_regprocedure('compras.normalizar_usuario(character varying)') IS NOT NULL),
        ('funcion autorizaciones.busca_nomenclador(integer,varchar,integer,varchar,boolean,varchar)',
            to_regprocedure('autorizaciones.busca_nomenclador(integer,character varying,integer,character varying,boolean,character varying)') IS NOT NULL),
        ('funcion compras.guardar_requerimiento_detalle de 13 argumentos',
            to_regprocedure('compras.guardar_requerimiento_detalle(integer,integer,character varying,integer,integer,character varying,character varying,integer,integer,character varying,integer,text,character varying)') IS NOT NULL),
        ('funcion compras.guardar_requerimiento_detalle_clasificado de 14 argumentos',
            to_regprocedure('compras.guardar_requerimiento_detalle_clasificado(integer,integer,character varying,integer,integer,character varying,character varying,integer,integer,character varying,integer,text,integer,character varying)') IS NOT NULL)
    ) AS requisitos(requisito, instalado)
    WHERE NOT instalado;
    IF v_faltantes IS NOT NULL THEN
        RAISE EXCEPTION 'Faltan objetos necesarios para configurar nomencladores de Compras.'
            USING DETAIL = v_faltantes;
    END IF;

    -- Comparar cuerpos normalizados de las definiciones instaladas leidas,
    -- o de esta misma entrega para una reejecucion. No alterar cuerpos con
    -- reemplazos sobre pg_get_functiondef ni sobrescribir otra revision.
    IF EXISTS (
        SELECT 1 FROM (VALUES
            ('autorizaciones.busca_nomenclador(integer,character varying,integer,character varying,boolean,character varying)',
             ARRAY['130a6fa525b8ce019bdd704885add4a2']::TEXT[]),
            ('compras.guardar_requerimiento_detalle(integer,integer,character varying,integer,integer,character varying,character varying,integer,integer,character varying,integer,text,character varying)',
             ARRAY['74ed87bb5dd09b0f1dac72b9683bc159', '7b9ccfce93c709b3c402cf2d267f6d86']::TEXT[]),
            ('compras.guardar_requerimiento_detalle_clasificado(integer,integer,character varying,integer,integer,character varying,character varying,integer,integer,character varying,integer,text,integer,character varying)',
             ARRAY['f2eca47dd6573aae231c0a061634e143', 'd90183584e6ce4c578dd26a96f09cee2']::TEXT[])
        ) AS contrato(firma, cuerpos_admitidos)
        JOIN pg_proc p ON p.oid = to_regprocedure(contrato.firma)
        WHERE NOT (md5(replace(p.prosrc, E'\r\n', E'\n')) = ANY(contrato.cuerpos_admitidos))
    ) THEN
        RAISE EXCEPTION 'Las funciones instaladas difieren de los contratos verificados para esta migracion.'
            USING HINT = 'Volver a leer las definiciones actuales y adaptar el SQL puntualmente; no reemplazar una revision diferente.';
    END IF;
    IF to_regprocedure('compras.guardar_requerimiento_detalle(integer,integer,character varying,integer,integer,character varying,character varying,integer,integer,character varying,integer,text,integer,character varying)') IS NOT NULL
       AND (SELECT md5(replace(p.prosrc, E'\r\n', E'\n')) FROM pg_proc p
            WHERE p.oid = to_regprocedure('compras.guardar_requerimiento_detalle(integer,integer,character varying,integer,integer,character varying,character varying,integer,integer,character varying,integer,text,integer,character varying)'))
           <> 'fbb7d73f6122436ae6aed19ad8ff8f5b' THEN
        RAISE EXCEPTION 'La sobrecarga de guardado con contexto completo ya existe con otra definicion.';
    END IF;
    -- En el ambiente verificado estas funciones estan huerfanas. No
    -- activarlas ni eliminar las garantias de otro ambiente para instalar.
    IF EXISTS (
        SELECT 1 FROM pg_trigger t
        JOIN pg_proc p ON p.oid = t.tgfoid
        JOIN pg_namespace n ON n.oid = p.pronamespace
        WHERE NOT t.tgisinternal AND t.tgenabled <> 'D'
          AND n.nspname = 'compras'
          AND p.proname IN ('validar_requerimiento_detalle_fila',
                           'validar_tipo_prestacion_detalle_nuevo')
    ) THEN
        RAISE EXCEPTION 'Hay triggers de asociaciones legacy activos que difieren del ambiente verificado.'
            USING HINT = 'Relevar sus definiciones y adaptar puntualmente esas validaciones antes de instalar. No desactivar restricciones para continuar.';
    END IF;
END;
$precondicion$;

DO $tabla_y_carga_inicial$
DECLARE
    v_sector INTEGER;
    v_cantidad INTEGER;
    v_nombre VARCHAR;
BEGIN
    IF to_regclass('compras.sector_nomenclador') IS NOT NULL THEN
        IF (SELECT count(*) FROM pg_attribute
            WHERE attrelid = to_regclass('compras.sector_nomenclador')
              AND attnum > 0 AND NOT attisdropped) <> 5
           OR EXISTS (
            SELECT 1 FROM (VALUES
                ('id_sector', 'integer'::regtype, -1, TRUE),
                ('id_tipo_prestacion', 'smallint'::regtype, -1, FALSE),
                ('id_tipo_nomenclador', 'integer'::regtype, -1, FALSE),
                ('marca_rein_liq', 'smallint'::regtype, -1, FALSE),
                ('codigo_nomenclador', 'character varying'::regtype, 14, FALSE)
            ) esperado(nombre, tipo, modificador, obligatorio)
            LEFT JOIN pg_attribute a
              ON a.attrelid = to_regclass('compras.sector_nomenclador')
             AND a.attname = esperado.nombre AND NOT a.attisdropped
            WHERE a.attname IS NULL OR a.atttypid <> esperado.tipo
               OR a.atttypmod <> esperado.modificador
               OR a.attnotnull <> esperado.obligatorio
        ) OR NOT EXISTS (
            SELECT 1 FROM pg_constraint
            WHERE conrelid = to_regclass('compras.sector_nomenclador')
              AND conname = 'ck_compras_sector_nomenclador_criterios'
              AND contype = 'c' AND convalidated
        ) OR NOT EXISTS (
            SELECT 1 FROM pg_index i
            WHERE i.indexrelid = to_regclass('compras.uq_compras_sector_nomenclador')
              AND i.indrelid = to_regclass('compras.sector_nomenclador')
              AND i.indisunique AND i.indisvalid AND i.indisready
              AND i.indnatts = 5 AND i.indpred IS NULL
              AND i.indkey::TEXT = (SELECT a.attnum::TEXT || ' 0 0 0 0'
                  FROM pg_attribute a WHERE a.attrelid = i.indrelid
                    AND a.attname = 'id_sector' AND NOT a.attisdropped)
              -- El deparser 9.6 agrega casts y parentesis implicitos.
              -- Normalizar SOLO estas expresiones del indice para verificar
              -- estructura y centinelas; no se transforma codigo ejecutable.
              AND NOT EXISTS (
                  SELECT 1 FROM (VALUES
                      (2, 'coalesceid_tipo_prestacion,-1'),
                      (3, 'coalesceid_tipo_nomenclador,-1'),
                      (4, 'coalescemarca_rein_liq,-32769'),
                      (5, 'coalescecodigo_nomenclador,')
                  ) expresion(posicion, esperada)
                  WHERE regexp_replace(
                      regexp_replace(lower(pg_get_indexdef(i.indexrelid, expresion.posicion, TRUE)),
                                     '[[:space:]()'']', '', 'g'),
                      '::(integer|smallint|text|charactervarying)', '', 'g'
                  ) <> expresion.esperada
              )
        ) OR EXISTS (
            SELECT 1 FROM (VALUES
                ('id_sector', 'compras.sector_requerimiento', 'id_sector'),
                ('id_tipo_prestacion', 'compras.tipo_prestacion', 'id_tipo_prestacion'),
                ('id_tipo_nomenclador', 'public.tipo_nomenclador', 'id_tipo_nomenclador')
            ) referencia(columna, tabla_maestra, clave)
            WHERE NOT EXISTS (
                SELECT 1 FROM pg_constraint fk
                WHERE fk.conrelid = to_regclass('compras.sector_nomenclador')
                  AND fk.contype = 'f' AND fk.convalidated
                  AND fk.confrelid = to_regclass(referencia.tabla_maestra)
                  AND fk.conkey = ARRAY[(SELECT a.attnum FROM pg_attribute a
                      WHERE a.attrelid = to_regclass('compras.sector_nomenclador')
                        AND a.attname = referencia.columna AND NOT a.attisdropped)]
                  AND fk.confkey = ARRAY[(SELECT a.attnum FROM pg_attribute a
                      WHERE a.attrelid = to_regclass(referencia.tabla_maestra)
                        AND a.attname = referencia.clave AND NOT a.attisdropped)]
            )
        ) THEN
            RAISE EXCEPTION 'compras.sector_nomenclador ya existe con un contrato diferente.';
        END IF;
        RAISE NOTICE 'Tabla existente: NO se ejecuta la carga inicial ni se restauran asociaciones eliminadas.';
        RETURN;
    END IF;

    CREATE TABLE compras.sector_nomenclador (
        id_sector INTEGER NOT NULL
            REFERENCES compras.sector_requerimiento (id_sector),
        id_tipo_prestacion SMALLINT
            REFERENCES compras.tipo_prestacion (id_tipo_prestacion),
        id_tipo_nomenclador INTEGER
            REFERENCES public.tipo_nomenclador (id_tipo_nomenclador),
        marca_rein_liq SMALLINT,
        codigo_nomenclador VARCHAR(10),
        CONSTRAINT ck_compras_sector_nomenclador_criterios CHECK (
            (id_tipo_nomenclador IS NOT NULL OR marca_rein_liq IS NOT NULL
                OR codigo_nomenclador IS NOT NULL)
            AND (id_tipo_nomenclador IS NULL OR id_tipo_nomenclador > 0)
            AND (id_tipo_prestacion IS NULL OR id_tipo_prestacion > 0)
            AND (codigo_nomenclador IS NULL
                 OR (codigo_nomenclador = btrim(codigo_nomenclador)
                     AND length(codigo_nomenclador) > 0))
        )
    );
    -- PG 9.6: UNIQUE normal no evita duplicados con NULL. Los centinelas
    -- solamente identifican la fila en este indice; no son tipos reales.
    -- Para marca se usa -32769, fuera de SMALLINT: una marca -1 explicita
    -- sigue siendo distinta de NULL.
    CREATE UNIQUE INDEX uq_compras_sector_nomenclador
        ON compras.sector_nomenclador (
            id_sector, COALESCE(id_tipo_prestacion, -1),
            COALESCE(id_tipo_nomenclador, -1), COALESCE(marca_rein_liq::INTEGER, -32769),
            COALESCE(codigo_nomenclador, '')
        );

    IF (SELECT count(*) FROM public.tipo_nomenclador
        WHERE id_tipo_nomenclador IN (1, 2, 3, 4, 6, 9, 10, 14)) <> 8 THEN
        RAISE EXCEPTION 'No coincide el catalogo de tipos de nomenclador con la matriz legacy verificada.';
    END IF;

    FOREACH v_nombre IN ARRAY ARRAY['FARMACIA', 'ODONTOLOGIA', 'PRESTACIONES MEDICAS', 'DISCAPACIDAD']
    LOOP
        SELECT count(*), min(s.id_sector) INTO v_cantidad, v_sector
        FROM compras.sector_requerimiento s
        WHERE compras.normalizar_sector(s.descripcion) = v_nombre
          AND s.activo = TRUE AND s.baja_fecha IS NULL;
        IF v_cantidad > 1 THEN
            RAISE EXCEPTION 'Hay mas de un sector activo identificado como %.', v_nombre;
        ELSIF v_cantidad = 0 THEN
            RAISE NOTICE 'Sector % ausente: no se crea ni se carga su configuracion.', v_nombre;
            CONTINUE;
        END IF;

        IF v_nombre = 'FARMACIA' THEN
            INSERT INTO compras.sector_nomenclador (id_sector, id_tipo_nomenclador)
            VALUES (v_sector, 9);
        ELSIF v_nombre = 'ODONTOLOGIA' THEN
            INSERT INTO compras.sector_nomenclador (id_sector, id_tipo_nomenclador)
            VALUES (v_sector, 1);
        ELSIF v_nombre = 'DISCAPACIDAD' THEN
            -- busca_nomenclador_marca_reinliq instalada incorpora codigo
            -- 431003 ademas de marca 6. El validador exige codigo exacto.
            INSERT INTO compras.sector_nomenclador (id_sector, marca_rein_liq)
            VALUES (v_sector, 6);
            INSERT INTO compras.sector_nomenclador (id_sector, codigo_nomenclador)
            VALUES (v_sector, '431003');
        ELSE
            IF (SELECT count(*) FROM compras.tipo_prestacion t
                WHERE t.id_sector = v_sector
                  AND ((t.id_tipo_prestacion = 3 AND t.descripcion = 'PROTESIS TRAUMATOLOGIA')
                    OR (t.id_tipo_prestacion = 4 AND t.descripcion = 'PROTESIS CARDIOLOGIA')
                    OR (t.id_tipo_prestacion = 5 AND t.descripcion = 'PROTESIS GENERAL')
                    OR (t.id_tipo_prestacion = 6 AND t.descripcion = 'INSUMOS'))) <> 4 THEN
                RAISE EXCEPTION 'Los rubros 3, 4, 5 y 6 no coinciden con Prestaciones Medicas e identificaciones verificadas.';
            END IF;
            INSERT INTO compras.sector_nomenclador (id_sector, id_tipo_nomenclador)
            SELECT v_sector, tipo FROM (VALUES (2), (3), (4), (6)) tipos(tipo);
            INSERT INTO compras.sector_nomenclador
                (id_sector, id_tipo_prestacion, id_tipo_nomenclador)
            SELECT v_sector, rubro, tipo
            FROM (VALUES (3::SMALLINT), (4::SMALLINT), (5::SMALLINT)) rubros(rubro)
            CROSS JOIN (VALUES (2), (3), (4), (6), (14)) tipos(tipo);
            INSERT INTO compras.sector_nomenclador
                (id_sector, id_tipo_prestacion, id_tipo_nomenclador)
            VALUES (v_sector, 6, 10);
            -- INSUMOS DIABETES (8 en la lectura del 13/10/2026) conserva
            -- la herencia general. La configuracion solo 10 es otro SQL.
        END IF;
    END LOOP;
END;
$tabla_y_carga_inicial$;

CREATE OR REPLACE FUNCTION compras.resolver_sector_nomenclador(
    p_id_sector INTEGER,
    p_id_tipo_prestacion INTEGER
)
RETURNS SETOF compras.sector_nomenclador
AS $func$
DECLARE
    v_sector_tipo INTEGER;
    v_especifico BOOLEAN;
BEGIN
    IF p_id_sector IS NULL OR p_id_sector <= 0 OR NOT EXISTS (
        SELECT 1 FROM compras.sector_requerimiento s
        WHERE s.id_sector = p_id_sector AND s.activo = TRUE
          AND s.baja_fecha IS NULL
    ) THEN
        RAISE EXCEPTION 'El sector informado no existe o no esta disponible.';
    END IF;
    IF p_id_tipo_prestacion IS NOT NULL THEN
        SELECT t.id_sector INTO v_sector_tipo
        FROM compras.tipo_prestacion t
        WHERE t.id_tipo_prestacion = p_id_tipo_prestacion;
        IF NOT FOUND THEN
            RAISE EXCEPTION 'El tipo de cotizacion informado no existe.';
        ELSIF v_sector_tipo <> p_id_sector THEN
            RAISE EXCEPTION 'El tipo de cotizacion no pertenece al sector informado.';
        END IF;
    END IF;
    SELECT EXISTS (
        SELECT 1 FROM compras.sector_nomenclador c
        WHERE c.id_sector = p_id_sector
          AND c.id_tipo_prestacion = p_id_tipo_prestacion
    ) INTO v_especifico;
    IF EXISTS (
        SELECT 1 FROM compras.sector_nomenclador c
        WHERE c.id_sector = p_id_sector
          AND ((v_especifico AND c.id_tipo_prestacion = p_id_tipo_prestacion)
            OR (NOT v_especifico AND c.id_tipo_prestacion IS NULL))
          AND ((c.id_tipo_nomenclador IS NULL AND c.marca_rein_liq IS NULL
                  AND c.codigo_nomenclador IS NULL)
            OR c.id_tipo_nomenclador <= 0
            OR (c.codigo_nomenclador IS NOT NULL
                AND (length(btrim(c.codigo_nomenclador)) = 0
                     OR c.codigo_nomenclador <> btrim(c.codigo_nomenclador))))
    ) THEN
        RAISE EXCEPTION 'Una asociacion efectiva tiene criterios de seleccion invalidos.';
    END IF;
    RETURN QUERY
    SELECT c.* FROM compras.sector_nomenclador c
    WHERE c.id_sector = p_id_sector
      AND ((v_especifico AND c.id_tipo_prestacion = p_id_tipo_prestacion)
        OR (NOT v_especifico AND c.id_tipo_prestacion IS NULL))
    ORDER BY c.id_tipo_nomenclador, c.marca_rein_liq, c.codigo_nomenclador;
END;
$func$
LANGUAGE plpgsql STABLE;

CREATE OR REPLACE FUNCTION compras.buscar_nomencladores(
    p_id_sector INTEGER,
    p_id_tipo_prestacion INTEGER,
    p_descripcion VARCHAR,
    p_codigo VARCHAR,
    p_troqueles INTEGER[] DEFAULT NULL
)
RETURNS SETOF autorizaciones.nomenclador_detalle
AS $func$
DECLARE
    v_configuracion compras.sector_nomenclador[];
    v_descripcion VARCHAR;
    v_codigo VARCHAR;
    v_comparar_sin_acentos BOOLEAN;
BEGIN
    SELECT array_agg(c) INTO v_configuracion
    FROM compras.resolver_sector_nomenclador(p_id_sector, p_id_tipo_prestacion) c;
    IF v_configuracion IS NULL THEN
        RAISE EXCEPTION 'El contexto no tiene nomencladores configurados.';
    END IF;
    v_descripcion := NULLIF(btrim(p_descripcion), '');
    v_codigo := NULLIF(btrim(p_codigo), '');
    -- Conserva exclusivamente el contrato de comparacion del buscador
    -- tecnico existente; esta condicion no elige tipos permitidos.
    SELECT compras.normalizar_sector(s.descripcion) = 'PRESTACIONES MEDICAS'
    INTO v_comparar_sin_acentos
    FROM compras.sector_requerimiento s WHERE s.id_sector = p_id_sector;
    RETURN QUERY
    SELECT resultado.* FROM (
        SELECT DISTINCT ON (n.id_tipo_nomenclador, n.id_prestacion) n.*
        FROM unnest(v_configuracion) c
        CROSS JOIN LATERAL autorizaciones.busca_nomenclador(
            c.id_tipo_nomenclador,
            CASE WHEN v_comparar_sin_acentos THEN NULL ELSE v_descripcion END,
            NULL, v_codigo, FALSE, NULL
        ) n
        WHERE n.id_prestacion > 0 AND n.id_tipo_nomenclador > 0
          AND n.baja_fecha IS NULL
          AND (p_troqueles IS NULL OR
               CASE WHEN btrim(n.codigo) ~ '^[0-9]+$'
                    THEN btrim(n.codigo)::NUMERIC ELSE NULL END = ANY(p_troqueles))
          AND (c.marca_rein_liq IS NULL OR n.marca_rein_liq = c.marca_rein_liq)
          AND (c.codigo_nomenclador IS NULL OR btrim(n.codigo) = c.codigo_nomenclador)
          AND (NOT v_comparar_sin_acentos OR v_descripcion IS NULL
            OR translate(upper(COALESCE(n.descripcion, '')),
                chr(193)||chr(201)||chr(205)||chr(211)||chr(218)||chr(220)||chr(209)||chr(192)
                ||chr(200)||chr(204)||chr(210)||chr(217)||chr(196)||chr(203)||chr(207)||chr(214)
                ||chr(220)||chr(194)||chr(202)||chr(206)||chr(212)||chr(219),
                'AEIOUUNAEIOUAEIOUAEIOU')
             LIKE '%' || translate(upper(v_descripcion),
                chr(193)||chr(201)||chr(205)||chr(211)||chr(218)||chr(220)||chr(209)||chr(192)
                ||chr(200)||chr(204)||chr(210)||chr(217)||chr(196)||chr(203)||chr(207)||chr(214)
                ||chr(220)||chr(194)||chr(202)||chr(206)||chr(212)||chr(219),
                'AEIOUUNAEIOUAEIOUAEIOU') || '%')
        ORDER BY n.id_tipo_nomenclador, n.id_prestacion
    ) resultado
    ORDER BY resultado.codigo;
END;
$func$
LANGUAGE plpgsql STABLE;

CREATE OR REPLACE FUNCTION compras.validar_nomenclador_configurado(
    p_id_sector INTEGER,
    p_id_tipo_prestacion INTEGER,
    p_id_prestacion INTEGER,
    p_id_tipo_nomenclador INTEGER
)
RETURNS VOID
AS $func$
DECLARE
    v_configuracion compras.sector_nomenclador[];
    v_nomenclador autorizaciones.nomenclador%ROWTYPE;
BEGIN
    SELECT array_agg(c) INTO v_configuracion
    FROM compras.resolver_sector_nomenclador(p_id_sector, p_id_tipo_prestacion) c;
    IF v_configuracion IS NULL THEN
        RAISE EXCEPTION 'El contexto no tiene nomencladores configurados.';
    END IF;
    SELECT n.* INTO v_nomenclador FROM autorizaciones.nomenclador n
    WHERE n.id_prestacion = p_id_prestacion AND n.id_prestacion > 0
      AND n.id_tipo_nomenclador > 0 AND n.baja_fecha IS NULL;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'La prestacion seleccionada no existe o no esta activa.';
    END IF;
    IF NULLIF(btrim(v_nomenclador.codigo), '') IS NULL
       OR NULLIF(btrim(v_nomenclador.descripcion), '') IS NULL THEN
        RAISE EXCEPTION 'La prestacion real no tiene codigo y descripcion validos.';
    END IF;
    IF p_id_tipo_nomenclador IS NULL OR p_id_tipo_nomenclador <= 0
       OR p_id_tipo_nomenclador <> v_nomenclador.id_tipo_nomenclador THEN
        RAISE EXCEPTION 'El tipo informado no corresponde al tipo real de la prestacion seleccionada.';
    END IF;
    IF NOT EXISTS (
        SELECT 1 FROM unnest(v_configuracion) c
        WHERE (c.id_tipo_nomenclador IS NULL OR c.id_tipo_nomenclador = v_nomenclador.id_tipo_nomenclador)
          AND (c.marca_rein_liq IS NULL OR c.marca_rein_liq = v_nomenclador.marca_rein_liq)
          AND (c.codigo_nomenclador IS NULL OR c.codigo_nomenclador = btrim(v_nomenclador.codigo))
    ) THEN
        RAISE EXCEPTION 'La prestacion seleccionada no pertenece a los nomencladores configurados para este contexto.';
    END IF;
END;
$func$
LANGUAGE plpgsql STABLE;

-- Sobrecarga interna: el rubro llega antes de escribir y pertenece al
-- contexto completo. No depende de que un UPDATE posterior ya exista.
CREATE OR REPLACE FUNCTION compras.guardar_requerimiento_detalle(p_id integer, p_id_requerimiento integer, p_tipo_item character varying, p_id_prestacion integer, p_id_tipo_nomenclador integer, p_codigo_nomenclador character varying, p_descripcion_nomenclador character varying, p_id_medicamento integer, p_troquel integer, p_nombre_medicamento character varying, p_cantidad integer, p_observaciones text, p_id_tipo_prestacion integer, p_usuario character varying)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
v_id INTEGER;
    v_usuario VARCHAR(100);
    v_tipo_item VARCHAR(20);
    v_tipo_item_actual VARCHAR(20);
    v_tipo_item_esperado VARCHAR(20);
    v_sector VARCHAR(200);
    v_id_tipo_nomenclador_real INTEGER;
    v_id_sector INTEGER;
    v_detalle compras.requerimiento_detalle%ROWTYPE;
    v_nueva_seleccion BOOLEAN;
    v_codigo_real VARCHAR;
    v_descripcion_real VARCHAR;
BEGIN
    v_usuario :=
        compras.normalizar_usuario(
            p_usuario
        );

    v_tipo_item :=
        upper(
            btrim(
                COALESCE(
                    p_tipo_item,
                    ''
                )
            )
        );

    IF v_tipo_item NOT IN (
        'NOMENCLADOR',
        'MEDICAMENTO',
        'OBSERVACION'
    ) THEN
        RAISE EXCEPTION
            'Tipo de item inválido.';
END IF;

    IF p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN

        RAISE EXCEPTION
            'Debe informar el requerimiento de compra.';
END IF;

    IF p_cantidad IS NULL
       OR p_cantidad <= 0 THEN

        RAISE EXCEPTION
            'La cantidad debe ser mayor a cero.';
END IF;

SELECT compras.normalizar_sector(sr.descripcion), r.id_sector
INTO v_sector, v_id_sector
FROM compras.requerimiento r
         JOIN compras.sector_requerimiento sr
              ON sr.id_sector = r.id_sector
WHERE r.id_requerimiento = p_id_requerimiento
  AND r.estado = 1
  AND r.baja_fecha IS NULL
    FOR UPDATE OF r;

IF NOT FOUND THEN
        RAISE EXCEPTION
            'Los detalles estructurales solo pueden modificarse en estado PENDIENTE.';
END IF;

    IF p_id_tipo_prestacion IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM compras.tipo_prestacion t
        WHERE t.id_tipo_prestacion = p_id_tipo_prestacion
          AND t.id_sector = v_id_sector
    ) THEN
        RAISE EXCEPTION 'El tipo de cotizacion no existe o no pertenece al sector del requerimiento.';
    END IF;
    IF (p_id IS NULL OR p_id <= 0) AND p_id_tipo_prestacion IS NULL
       AND EXISTS (SELECT 1 FROM compras.tipo_prestacion t WHERE t.id_sector = v_id_sector) THEN
        RAISE EXCEPTION 'Debe seleccionar el tipo de prestacion.';
    END IF;

    -- Conserva los circuitos de observacion. Los demas contextos requieren
    -- configuracion efectiva al validar una nueva seleccion NOMENCLADOR.
    IF v_sector IN ('RRHH', 'LEGALES', 'SISTEMAS', 'OTROS') THEN
        v_tipo_item_esperado := 'OBSERVACION';
    ELSE
        v_tipo_item_esperado := 'NOMENCLADOR';
    END IF;

    /*
     * ALTAS
     *
     * El sector determina si el detalle usa nomenclador u observación.
     */
    IF p_id IS NULL
       OR p_id <= 0 THEN

        IF v_tipo_item <> v_tipo_item_esperado THEN
            RAISE EXCEPTION
                'El sector % requiere detalles de tipo %.',
                v_sector,
                v_tipo_item_esperado;
END IF;

        IF v_tipo_item = 'OBSERVACION' THEN
            IF NULLIF(btrim(p_observaciones), '') IS NULL THEN
                RAISE EXCEPTION
                    'Debe informar las observaciones del detalle.';
END IF;

            IF p_id_prestacion IS NOT NULL
               OR p_id_tipo_nomenclador IS NOT NULL
               OR NULLIF(btrim(p_codigo_nomenclador), '') IS NOT NULL
               OR NULLIF(btrim(p_descripcion_nomenclador), '') IS NOT NULL
               OR p_id_medicamento IS NOT NULL
               OR p_troquel IS NOT NULL
               OR NULLIF(btrim(p_nombre_medicamento), '') IS NOT NULL THEN

                RAISE EXCEPTION
                    'Un detalle de observación no puede contener datos técnicos.';
END IF;
ELSE

        IF p_id_prestacion IS NULL
           OR p_id_prestacion <= 0 THEN

            RAISE EXCEPTION
                'Debe informar la prestación del nomenclador.';
END IF;

        IF p_id_tipo_nomenclador IS NULL
           OR p_id_tipo_nomenclador <= 0 THEN

            RAISE EXCEPTION
                'Debe informar el tipo real de nomenclador.';
END IF;

SELECT n.id_tipo_nomenclador, n.codigo, n.descripcion
INTO v_id_tipo_nomenclador_real, v_codigo_real, v_descripcion_real
FROM autorizaciones.nomenclador n
WHERE n.id_prestacion = p_id_prestacion
  AND n.baja_fecha IS NULL;

IF NOT FOUND THEN
            RAISE EXCEPTION
                'La prestación seleccionada no existe o no está activa.';
END IF;

        IF v_id_tipo_nomenclador_real
                <> p_id_tipo_nomenclador THEN

            RAISE EXCEPTION
                'El tipo de nomenclador informado no corresponde a la prestación seleccionada.';
END IF;

    PERFORM compras.validar_nomenclador_configurado(
        v_id_sector, p_id_tipo_prestacion, p_id_prestacion, p_id_tipo_nomenclador);

        IF p_codigo_nomenclador IS NULL
           OR length(
               btrim(
                   p_codigo_nomenclador
               )
           ) = 0 THEN

            RAISE EXCEPTION
                'Debe informar el código de nomenclador.';
END IF;

        IF p_descripcion_nomenclador IS NULL
           OR length(
               btrim(
                   p_descripcion_nomenclador
               )
           ) = 0 THEN

            RAISE EXCEPTION
                'Debe informar la descripción del nomenclador.';
END IF;

        IF p_id_medicamento IS NOT NULL
           OR p_troquel IS NOT NULL
           OR NULLIF(
               btrim(
                   p_nombre_medicamento
               ),
               ''
           ) IS NOT NULL THEN

            RAISE EXCEPTION
                'Un detalle nuevo de nomenclador no puede contener datos de medicamento.';
END IF;
END IF;

INSERT INTO compras.requerimiento_detalle (
    id_requerimiento,
    tipo_item,
    id_tipo_prestacion,

    id_prestacion,
    id_tipo_nomenclador,
    codigo_nomenclador,
    descripcion_nomenclador,

    id_medicamento,
    troquel,
    nombre_medicamento,

    cantidad,
    precio_unitario_estimado,
    precio_total_estimado,
    id_prestador,

    observaciones,
    alta_usr
)
VALUES (
           p_id_requerimiento,
           v_tipo_item,
           p_id_tipo_prestacion,

           CASE WHEN v_tipo_item = 'NOMENCLADOR'
                THEN p_id_prestacion ELSE NULL END,
           CASE WHEN v_tipo_item = 'NOMENCLADOR'
                THEN v_id_tipo_nomenclador_real ELSE NULL END,
           CASE WHEN v_tipo_item = 'NOMENCLADOR' THEN NULLIF(
                   btrim(
                           v_codigo_real
                   ),
                   ''
           ) ELSE NULL END,
           CASE WHEN v_tipo_item = 'NOMENCLADOR' THEN NULLIF(
                   btrim(
                           v_descripcion_real
                   ),
                   ''
           ) ELSE NULL END,

           NULL,
           NULL,
           NULL,

           p_cantidad,
           NULL,
           NULL,
           NULL,

           NULLIF(
                   btrim(
                           p_observaciones
                   ),
                   ''
           ),
           v_usuario
       )
    RETURNING id_detalle
INTO v_id;

RETURN v_id;
END IF;

    /*
     * EDICIONES
     *
     * Se bloquea la fila y se determina el tipo realmente
     * persistido. El tipo recibido por HTTP no es autoritativo.
     */
SELECT d.*
INTO v_detalle
FROM compras.requerimiento_detalle d
WHERE d.id_detalle = p_id
  AND d.id_requerimiento = p_id_requerimiento
  AND d.baja_fecha IS NULL
    FOR UPDATE;

IF NOT FOUND THEN
        RAISE EXCEPTION
            'No se encontró el detalle activo a modificar.';
END IF;

    v_tipo_item_actual := v_detalle.tipo_item;
    v_nueva_seleccion := v_detalle.id_prestacion IS DISTINCT FROM p_id_prestacion
        OR v_detalle.id_tipo_prestacion IS DISTINCT FROM p_id_tipo_prestacion;
    IF v_detalle.id_tipo_prestacion IS NOT NULL AND p_id_tipo_prestacion IS NULL THEN
        RAISE EXCEPTION 'El tipo de prestacion ya informado no puede quitarse.';
    END IF;
    IF v_nueva_seleccion AND p_id_tipo_prestacion IS NULL
       AND EXISTS (SELECT 1 FROM compras.tipo_prestacion t WHERE t.id_sector = v_id_sector) THEN
        RAISE EXCEPTION 'Debe seleccionar el tipo de prestacion.';
    END IF;
    IF v_tipo_item_actual = 'OBSERVACION' THEN
        IF v_tipo_item_esperado <> 'OBSERVACION'
           OR v_tipo_item <> 'OBSERVACION' THEN
            RAISE EXCEPTION
                'Un detalle de observación no puede convertirse a otro tipo.';
END IF;

        IF NULLIF(btrim(p_observaciones), '') IS NULL THEN
            RAISE EXCEPTION
                'Debe informar las observaciones del detalle.';
END IF;

        IF p_id_prestacion IS NOT NULL
           OR p_id_tipo_nomenclador IS NOT NULL
           OR NULLIF(btrim(p_codigo_nomenclador), '') IS NOT NULL
           OR NULLIF(btrim(p_descripcion_nomenclador), '') IS NOT NULL
           OR p_id_medicamento IS NOT NULL
           OR p_troquel IS NOT NULL
           OR NULLIF(btrim(p_nombre_medicamento), '') IS NOT NULL THEN

            RAISE EXCEPTION
                'Un detalle de observación no puede contener datos técnicos.';
END IF;

UPDATE compras.requerimiento_detalle
SET id_tipo_prestacion = p_id_tipo_prestacion,
    cantidad = p_cantidad,
    precio_unitario_estimado = NULL,
    precio_total_estimado = NULL,
    id_prestador = NULL,
    observaciones = NULLIF(btrim(p_observaciones), ''),
    modi_fecha = now(),
    modi_usr = v_usuario
WHERE id_detalle = p_id
  AND id_requerimiento = p_id_requerimiento
  AND baja_fecha IS NULL
    RETURNING id_detalle
INTO v_id;

RETURN v_id;
END IF;

    /*
     * Histórico MEDICAMENTO:
     *
     * sólo cambia Cantidad y Observaciones.
     * No se toca ID, troquel ni nombre.
     */
    IF v_tipo_item_actual = 'MEDICAMENTO' THEN

        IF v_tipo_item <> 'MEDICAMENTO' THEN
            RAISE EXCEPTION
                'El detalle histórico de medicamento no puede convertirse directamente.';
END IF;

UPDATE compras.requerimiento_detalle
SET id_tipo_prestacion = p_id_tipo_prestacion,
    cantidad = p_cantidad,

    precio_unitario_estimado = NULL,
    precio_total_estimado = NULL,
    id_prestador = NULL,

    observaciones =
        NULLIF(
                btrim(
                        p_observaciones
                ),
                ''
        ),

    modi_fecha = now(),
    modi_usr = v_usuario
WHERE id_detalle = p_id
  AND id_requerimiento = p_id_requerimiento
  AND baja_fecha IS NULL
    RETURNING id_detalle
INTO v_id;

IF v_id IS NULL THEN
            RAISE EXCEPTION
                'No se encontró el detalle histórico a modificar.';
END IF;

RETURN v_id;
END IF;

    IF v_tipo_item_actual <> 'NOMENCLADOR' THEN
        RAISE EXCEPTION
            'El detalle persistido tiene un tipo técnico desconocido.';
END IF;

    IF v_tipo_item_esperado <> 'NOMENCLADOR' THEN
        RAISE EXCEPTION
            'El sector % requiere detalles de tipo %.',
            v_sector,
            v_tipo_item_esperado;
END IF;

    IF v_tipo_item <> 'NOMENCLADOR' THEN
        RAISE EXCEPTION
            'Un detalle de nomenclador no puede convertirse a otro tipo.';
END IF;

    IF p_id_prestacion IS NULL
       OR p_id_prestacion <= 0 THEN

        RAISE EXCEPTION
            'Debe informar la prestación del nomenclador.';
END IF;

    IF p_id_tipo_nomenclador IS NULL
       OR p_id_tipo_nomenclador <= 0 THEN

        RAISE EXCEPTION
            'Debe informar el tipo real de nomenclador.';
END IF;

SELECT n.id_tipo_nomenclador, n.codigo, n.descripcion
INTO v_id_tipo_nomenclador_real, v_codigo_real, v_descripcion_real
FROM autorizaciones.nomenclador n
WHERE n.id_prestacion = p_id_prestacion
  AND n.baja_fecha IS NULL;

IF NOT FOUND THEN
        RAISE EXCEPTION
            'La prestación seleccionada no existe o no está activa.';
END IF;

    IF v_id_tipo_nomenclador_real
            <> p_id_tipo_nomenclador THEN

        RAISE EXCEPTION
            'El tipo de nomenclador informado no corresponde a la prestación seleccionada.';
END IF;

    IF v_nueva_seleccion THEN
        PERFORM compras.validar_nomenclador_configurado(
            v_id_sector, p_id_tipo_prestacion, p_id_prestacion, p_id_tipo_nomenclador);
    END IF;

    IF p_codigo_nomenclador IS NULL
       OR length(
           btrim(
               p_codigo_nomenclador
           )
       ) = 0 THEN

        RAISE EXCEPTION
            'Debe informar el código de nomenclador.';
END IF;

    IF p_descripcion_nomenclador IS NULL
       OR length(
           btrim(
               p_descripcion_nomenclador
           )
       ) = 0 THEN

        RAISE EXCEPTION
            'Debe informar la descripción del nomenclador.';
END IF;

    IF p_id_medicamento IS NOT NULL
       OR p_troquel IS NOT NULL
       OR NULLIF(
           btrim(
               p_nombre_medicamento
           ),
           ''
       ) IS NOT NULL THEN

        RAISE EXCEPTION
            'El detalle de nomenclador contiene datos de medicamento.';
END IF;

UPDATE compras.requerimiento_detalle
SET tipo_item = 'NOMENCLADOR',
    id_tipo_prestacion = p_id_tipo_prestacion,

    id_prestacion =
        p_id_prestacion,

    id_tipo_nomenclador =
        CASE WHEN v_nueva_seleccion THEN v_id_tipo_nomenclador_real
             ELSE v_detalle.id_tipo_nomenclador END,

    codigo_nomenclador =
        NULLIF(
                btrim(
                        CASE WHEN v_nueva_seleccion THEN v_codigo_real
                             ELSE v_detalle.codigo_nomenclador END
                ),
                ''
        ),

    descripcion_nomenclador =
        NULLIF(
                btrim(
                        CASE WHEN v_nueva_seleccion THEN v_descripcion_real
                             ELSE v_detalle.descripcion_nomenclador END
                ),
                ''
        ),

    id_medicamento = NULL,
    troquel = NULL,
    nombre_medicamento = NULL,

    cantidad = p_cantidad,

    precio_unitario_estimado = NULL,
    precio_total_estimado = NULL,
    id_prestador = NULL,

    observaciones =
        NULLIF(
                btrim(
                        p_observaciones
                ),
                ''
        ),

    modi_fecha = now(),
    modi_usr = v_usuario
WHERE id_detalle = p_id
  AND id_requerimiento = p_id_requerimiento
  AND baja_fecha IS NULL
    RETURNING id_detalle
INTO v_id;

IF v_id IS NULL THEN
        RAISE EXCEPTION
            'No se encontró el detalle a modificar.';
END IF;

RETURN v_id;
END;
$function$;

-- Conserva la firma publica de 13 argumentos. En una edicion reutiliza
-- el rubro persistido; un alta de un sector con rubros requiere clasificado.
CREATE OR REPLACE FUNCTION compras.guardar_requerimiento_detalle(
    p_id INTEGER, p_id_requerimiento INTEGER, p_tipo_item VARCHAR,
    p_id_prestacion INTEGER, p_id_tipo_nomenclador INTEGER,
    p_codigo_nomenclador VARCHAR, p_descripcion_nomenclador VARCHAR,
    p_id_medicamento INTEGER, p_troquel INTEGER, p_nombre_medicamento VARCHAR,
    p_cantidad INTEGER, p_observaciones TEXT, p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
    v_rubro INTEGER;
BEGIN
    IF p_id IS NOT NULL AND p_id > 0 THEN
        SELECT d.id_tipo_prestacion INTO v_rubro FROM compras.requerimiento_detalle d
        WHERE d.id_detalle = p_id AND d.id_requerimiento = p_id_requerimiento
          AND d.baja_fecha IS NULL;
    END IF;
    RETURN compras.guardar_requerimiento_detalle(
        p_id, p_id_requerimiento, p_tipo_item, p_id_prestacion, p_id_tipo_nomenclador,
        p_codigo_nomenclador, p_descripcion_nomenclador, p_id_medicamento, p_troquel,
        p_nombre_medicamento, p_cantidad, p_observaciones, v_rubro, p_usuario);
END;
$func$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION compras.guardar_requerimiento_detalle_clasificado(p_id integer, p_id_requerimiento integer, p_tipo_item character varying, p_id_prestacion integer, p_id_tipo_nomenclador integer, p_codigo_nomenclador character varying, p_descripcion_nomenclador character varying, p_id_medicamento integer, p_troquel integer, p_nombre_medicamento character varying, p_cantidad integer, p_observaciones text, p_id_tipo_prestacion integer, p_usuario character varying)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_id_detalle INTEGER;
BEGIN
    v_id_detalle := compras.guardar_requerimiento_detalle(
        p_id,
        p_id_requerimiento,
        p_tipo_item,
        p_id_prestacion,
        p_id_tipo_nomenclador,
        p_codigo_nomenclador,
        p_descripcion_nomenclador,
        p_id_medicamento,
        p_troquel,
        p_nombre_medicamento,
        p_cantidad,
        p_observaciones,
        p_id_tipo_prestacion,
        p_usuario
    );

    UPDATE compras.requerimiento_detalle
    SET id_tipo_prestacion = p_id_tipo_prestacion
    WHERE id_detalle = v_id_detalle
      AND id_requerimiento = p_id_requerimiento
      AND baja_fecha IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No se pudo asociar el tipo al detalle del requerimiento.';
    END IF;

    RETURN v_id_detalle;
END;
$function$;

COMMIT;

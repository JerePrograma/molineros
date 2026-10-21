-- Reparacion incremental del guardado de detalles de Compras.
-- PostgreSQL 9.6+. ASCII sin BOM (valido ISO-8859-1 y UTF8).
-- Fuente exclusiva: 20261013_sector_nomenclador.sql, tres cuerpos canonicos.
-- NO EJECUTADO contra produccion por Codex.
-- Abrir una conexion nueva, con la aplicacion detenida y respaldo disponible.
-- Ejecutar TODO este archivo y leer resultado en Data Output tras COMMIT.
-- REPARACION_CONFIRMADA: tres cuerpos verificados y transaccion confirmada.
-- ERROR_REPARACION_REVERTIDA: no se aplico ningun cambio de esta reparacion.
-- Un codigo 0 del cliente NO sustituye la columna resultado.
-- No crea tablas de negocio ni cambia configuracion, datos, triggers o Java.
-- Requiere resolver/validador/configuracion ya instalados con contrato conocido.
-- Los U& externos preservan las tildes originales aunque el cliente use UTF8.

BEGIN;
SET LOCAL standard_conforming_strings = on;
SET LOCAL lock_timeout = '10s';
DROP TABLE IF EXISTS pg_temp.compras_reparar_detalle_resultado;
CREATE TEMP TABLE compras_reparar_detalle_resultado (
    resultado TEXT, etapa TEXT, sqlstate TEXT, mensaje TEXT,
    detalle TEXT, sugerencia TEXT
) ON COMMIT PRESERVE ROWS;

DO $reparacion$
DECLARE
    v_etapa TEXT := 'precondiciones';
    v_faltantes TEXT;
    v_nueva BOOLEAN;
    v_firma13 TEXT := 'compras.guardar_requerimiento_detalle(integer,integer,character varying,integer,integer,character varying,character varying,integer,integer,character varying,integer,text,character varying)';
    v_firma14 TEXT := 'compras.guardar_requerimiento_detalle(integer,integer,character varying,integer,integer,character varying,character varying,integer,integer,character varying,integer,text,integer,character varying)';
    v_firmac TEXT := 'compras.guardar_requerimiento_detalle_clasificado(integer,integer,character varying,integer,integer,character varying,character varying,integer,integer,character varying,integer,text,integer,character varying)';
    v_owner NAME;
    v_acl RECORD;
    v_destino TEXT;
    v_estado TEXT;
    v_mensaje TEXT;
    v_detalle TEXT;
    v_sugerencia TEXT;
BEGIN
    BEGIN
        IF current_setting('server_version_num')::INTEGER < 90600 THEN
            RAISE EXCEPTION 'Se requiere PostgreSQL 9.6 o posterior.';
        END IF;
        IF current_setting('server_encoding') <> 'UTF8' THEN
            RAISE EXCEPTION 'El contrato verificado requiere server_encoding UTF8.';
        END IF;
        SELECT string_agg(requisito, E'\n' ORDER BY requisito)
        INTO v_faltantes
        FROM (VALUES
            ('tabla compras.requerimiento', to_regclass('compras.requerimiento') IS NOT NULL),
            ('tabla compras.requerimiento_detalle', to_regclass('compras.requerimiento_detalle') IS NOT NULL),
            ('tabla compras.sector_requerimiento', to_regclass('compras.sector_requerimiento') IS NOT NULL),
            ('tabla compras.tipo_prestacion', to_regclass('compras.tipo_prestacion') IS NOT NULL),
            ('tabla compras.sector_nomenclador', to_regclass('compras.sector_nomenclador') IS NOT NULL),
            ('tabla autorizaciones.nomenclador', to_regclass('autorizaciones.nomenclador') IS NOT NULL),
            ('normalizar_usuario', to_regprocedure('compras.normalizar_usuario(character varying)') IS NOT NULL),
            ('normalizar_sector', to_regprocedure('compras.normalizar_sector(character varying)') IS NOT NULL),
            ('resolver_sector_nomenclador', to_regprocedure('compras.resolver_sector_nomenclador(integer,integer)') IS NOT NULL),
            ('validar_nomenclador_configurado', to_regprocedure('compras.validar_nomenclador_configurado(integer,integer,integer,integer)') IS NOT NULL),
            ('guardado publico de 13 argumentos', to_regprocedure(v_firma13) IS NOT NULL),
            ('guardado clasificado de 14 argumentos', to_regprocedure(v_firmac) IS NOT NULL)
        ) AS requisitos(requisito, instalado) WHERE NOT instalado;
        IF v_faltantes IS NOT NULL THEN
            RAISE EXCEPTION 'Faltan dependencias del guardado de detalles.'
                USING DETAIL = v_faltantes,
                HINT = 'Relevar los objetos faltantes. Este parche no reinstala el historial de Compras.';
        END IF;

        IF EXISTS (
            SELECT 1 FROM (VALUES
                (v_firma13, ARRAY['74ed87bb5dd09b0f1dac72b9683bc159','7b9ccfce93c709b3c402cf2d267f6d86']::TEXT[]),
                (v_firmac, ARRAY['f2eca47dd6573aae231c0a061634e143','d90183584e6ce4c578dd26a96f09cee2']::TEXT[]),
                (v_firma14, ARRAY['fbb7d73f6122436ae6aed19ad8ff8f5b']::TEXT[]),
                ('compras.resolver_sector_nomenclador(integer,integer)', ARRAY['50bba9bf1944b402f91adfb30f6ed63c']::TEXT[]),
                ('compras.validar_nomenclador_configurado(integer,integer,integer,integer)', ARRAY['f28954130217db83c850f50ede69daea']::TEXT[])
            ) AS contrato(firma, cuerpos_admitidos)
            JOIN pg_proc p ON p.oid = to_regprocedure(contrato.firma)
            WHERE NOT (md5(replace(p.prosrc, E'\r\n', E'\n')) = ANY(contrato.cuerpos_admitidos))
        ) THEN
            RAISE EXCEPTION 'Hay una funcion con cuerpo distinto de las revisiones verificadas.'
                USING HINT = 'Obtener pg_get_functiondef de las tres funciones y sus dos auxiliares. No quitar este control.';
        END IF;
        IF EXISTS (
            SELECT 1 FROM pg_proc p JOIN pg_language l ON l.oid = p.prolang
            WHERE p.oid IN (to_regprocedure(v_firma13), to_regprocedure(v_firma14), to_regprocedure(v_firmac))
              AND (p.prorettype <> 'integer'::regtype OR p.proretset
                   OR l.lanname <> 'plpgsql' OR p.provolatile <> 'v'
                   OR p.prosecdef OR p.proisstrict OR p.proconfig IS NOT NULL
                   OR p.pronargdefaults <> 0)
        ) THEN
            RAISE EXCEPTION 'La metadata del guardado difiere del contrato verificado.';
        END IF;
        IF EXISTS (
            SELECT 1 FROM pg_trigger t
            JOIN pg_proc p ON p.oid = t.tgfoid
            JOIN pg_namespace n ON n.oid = p.pronamespace
            WHERE NOT t.tgisinternal AND t.tgenabled <> 'D'
              AND n.nspname = 'compras'
              AND p.proname IN ('validar_requerimiento_detalle_fila', 'validar_tipo_prestacion_detalle_nuevo')
        ) THEN
            RAISE EXCEPTION 'Hay triggers de asociaciones legacy activos fuera del contrato verificado.'
                USING HINT = 'Relevar y adaptar puntualmente sus validaciones; no desactivar restricciones.';
        END IF;
        v_nueva := to_regprocedure(v_firma14) IS NULL;
        SELECT r.rolname INTO v_owner FROM pg_proc p JOIN pg_roles r ON r.oid = p.proowner
        WHERE p.oid = to_regprocedure(v_firma13);

        v_etapa := 'instalar funcion 1 de 3';
        EXECUTE replace(U&'
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
                    ''''
                )
            )
        );

    IF v_tipo_item NOT IN (
        ''NOMENCLADOR'',
        ''MEDICAMENTO'',
        ''OBSERVACION''
    ) THEN
        RAISE EXCEPTION
            ''Tipo de item inv\00E1lido.'';
END IF;

    IF p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN

        RAISE EXCEPTION
            ''Debe informar el requerimiento de compra.'';
END IF;

    IF p_cantidad IS NULL
       OR p_cantidad <= 0 THEN

        RAISE EXCEPTION
            ''La cantidad debe ser mayor a cero.'';
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
            ''Los detalles estructurales solo pueden modificarse en estado PENDIENTE.'';
END IF;

    IF p_id_tipo_prestacion IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM compras.tipo_prestacion t
        WHERE t.id_tipo_prestacion = p_id_tipo_prestacion
          AND t.id_sector = v_id_sector
    ) THEN
        RAISE EXCEPTION ''El tipo de cotizacion no existe o no pertenece al sector del requerimiento.'';
    END IF;
    IF (p_id IS NULL OR p_id <= 0) AND p_id_tipo_prestacion IS NULL
       AND EXISTS (SELECT 1 FROM compras.tipo_prestacion t WHERE t.id_sector = v_id_sector) THEN
        RAISE EXCEPTION ''Debe seleccionar el tipo de prestacion.'';
    END IF;

    -- Conserva los circuitos de observacion. Los demas contextos requieren
    -- configuracion efectiva al validar una nueva seleccion NOMENCLADOR.
    IF v_sector IN (''RRHH'', ''LEGALES'', ''SISTEMAS'', ''OTROS'') THEN
        v_tipo_item_esperado := ''OBSERVACION'';
    ELSE
        v_tipo_item_esperado := ''NOMENCLADOR'';
    END IF;

    /*
     * ALTAS
     *
     * El sector determina si el detalle usa nomenclador u observaci\00F3n.
     */
    IF p_id IS NULL
       OR p_id <= 0 THEN

        IF v_tipo_item <> v_tipo_item_esperado THEN
            RAISE EXCEPTION
                ''El sector % requiere detalles de tipo %.'',
                v_sector,
                v_tipo_item_esperado;
END IF;

        IF v_tipo_item = ''OBSERVACION'' THEN
            IF NULLIF(btrim(p_observaciones), '''') IS NULL THEN
                RAISE EXCEPTION
                    ''Debe informar las observaciones del detalle.'';
END IF;

            IF p_id_prestacion IS NOT NULL
               OR p_id_tipo_nomenclador IS NOT NULL
               OR NULLIF(btrim(p_codigo_nomenclador), '''') IS NOT NULL
               OR NULLIF(btrim(p_descripcion_nomenclador), '''') IS NOT NULL
               OR p_id_medicamento IS NOT NULL
               OR p_troquel IS NOT NULL
               OR NULLIF(btrim(p_nombre_medicamento), '''') IS NOT NULL THEN

                RAISE EXCEPTION
                    ''Un detalle de observaci\00F3n no puede contener datos t\00E9cnicos.'';
END IF;
ELSE

        IF p_id_prestacion IS NULL
           OR p_id_prestacion <= 0 THEN

            RAISE EXCEPTION
                ''Debe informar la prestaci\00F3n del nomenclador.'';
END IF;

        IF p_id_tipo_nomenclador IS NULL
           OR p_id_tipo_nomenclador <= 0 THEN

            RAISE EXCEPTION
                ''Debe informar el tipo real de nomenclador.'';
END IF;

SELECT n.id_tipo_nomenclador, n.codigo, n.descripcion
INTO v_id_tipo_nomenclador_real, v_codigo_real, v_descripcion_real
FROM autorizaciones.nomenclador n
WHERE n.id_prestacion = p_id_prestacion
  AND n.baja_fecha IS NULL;

IF NOT FOUND THEN
            RAISE EXCEPTION
                ''La prestaci\00F3n seleccionada no existe o no est\00E1 activa.'';
END IF;

        IF v_id_tipo_nomenclador_real
                <> p_id_tipo_nomenclador THEN

            RAISE EXCEPTION
                ''El tipo de nomenclador informado no corresponde a la prestaci\00F3n seleccionada.'';
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
                ''Debe informar el c\00F3digo de nomenclador.'';
END IF;

        IF p_descripcion_nomenclador IS NULL
           OR length(
               btrim(
                   p_descripcion_nomenclador
               )
           ) = 0 THEN

            RAISE EXCEPTION
                ''Debe informar la descripci\00F3n del nomenclador.'';
END IF;

        IF p_id_medicamento IS NOT NULL
           OR p_troquel IS NOT NULL
           OR NULLIF(
               btrim(
                   p_nombre_medicamento
               ),
               ''''
           ) IS NOT NULL THEN

            RAISE EXCEPTION
                ''Un detalle nuevo de nomenclador no puede contener datos de medicamento.'';
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

           CASE WHEN v_tipo_item = ''NOMENCLADOR''
                THEN p_id_prestacion ELSE NULL END,
           CASE WHEN v_tipo_item = ''NOMENCLADOR''
                THEN v_id_tipo_nomenclador_real ELSE NULL END,
           CASE WHEN v_tipo_item = ''NOMENCLADOR'' THEN NULLIF(
                   btrim(
                           v_codigo_real
                   ),
                   ''''
           ) ELSE NULL END,
           CASE WHEN v_tipo_item = ''NOMENCLADOR'' THEN NULLIF(
                   btrim(
                           v_descripcion_real
                   ),
                   ''''
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
                   ''''
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
            ''No se encontr\00F3 el detalle activo a modificar.'';
END IF;

    v_tipo_item_actual := v_detalle.tipo_item;
    v_nueva_seleccion := v_detalle.id_prestacion IS DISTINCT FROM p_id_prestacion
        OR v_detalle.id_tipo_prestacion IS DISTINCT FROM p_id_tipo_prestacion;
    IF v_detalle.id_tipo_prestacion IS NOT NULL AND p_id_tipo_prestacion IS NULL THEN
        RAISE EXCEPTION ''El tipo de prestacion ya informado no puede quitarse.'';
    END IF;
    IF v_nueva_seleccion AND p_id_tipo_prestacion IS NULL
       AND EXISTS (SELECT 1 FROM compras.tipo_prestacion t WHERE t.id_sector = v_id_sector) THEN
        RAISE EXCEPTION ''Debe seleccionar el tipo de prestacion.'';
    END IF;
    IF v_tipo_item_actual = ''OBSERVACION'' THEN
        IF v_tipo_item_esperado <> ''OBSERVACION''
           OR v_tipo_item <> ''OBSERVACION'' THEN
            RAISE EXCEPTION
                ''Un detalle de observaci\00F3n no puede convertirse a otro tipo.'';
END IF;

        IF NULLIF(btrim(p_observaciones), '''') IS NULL THEN
            RAISE EXCEPTION
                ''Debe informar las observaciones del detalle.'';
END IF;

        IF p_id_prestacion IS NOT NULL
           OR p_id_tipo_nomenclador IS NOT NULL
           OR NULLIF(btrim(p_codigo_nomenclador), '''') IS NOT NULL
           OR NULLIF(btrim(p_descripcion_nomenclador), '''') IS NOT NULL
           OR p_id_medicamento IS NOT NULL
           OR p_troquel IS NOT NULL
           OR NULLIF(btrim(p_nombre_medicamento), '''') IS NOT NULL THEN

            RAISE EXCEPTION
                ''Un detalle de observaci\00F3n no puede contener datos t\00E9cnicos.'';
END IF;

UPDATE compras.requerimiento_detalle
SET id_tipo_prestacion = p_id_tipo_prestacion,
    cantidad = p_cantidad,
    precio_unitario_estimado = NULL,
    precio_total_estimado = NULL,
    id_prestador = NULL,
    observaciones = NULLIF(btrim(p_observaciones), ''''),
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
     * Hist\00F3rico MEDICAMENTO:
     *
     * s\00F3lo cambia Cantidad y Observaciones.
     * No se toca ID, troquel ni nombre.
     */
    IF v_tipo_item_actual = ''MEDICAMENTO'' THEN

        IF v_tipo_item <> ''MEDICAMENTO'' THEN
            RAISE EXCEPTION
                ''El detalle hist\00F3rico de medicamento no puede convertirse directamente.'';
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
                ''''
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
                ''No se encontr\00F3 el detalle hist\00F3rico a modificar.'';
END IF;

RETURN v_id;
END IF;

    IF v_tipo_item_actual <> ''NOMENCLADOR'' THEN
        RAISE EXCEPTION
            ''El detalle persistido tiene un tipo t\00E9cnico desconocido.'';
END IF;

    IF v_tipo_item_esperado <> ''NOMENCLADOR'' THEN
        RAISE EXCEPTION
            ''El sector % requiere detalles de tipo %.'',
            v_sector,
            v_tipo_item_esperado;
END IF;

    IF v_tipo_item <> ''NOMENCLADOR'' THEN
        RAISE EXCEPTION
            ''Un detalle de nomenclador no puede convertirse a otro tipo.'';
END IF;

    IF p_id_prestacion IS NULL
       OR p_id_prestacion <= 0 THEN

        RAISE EXCEPTION
            ''Debe informar la prestaci\00F3n del nomenclador.'';
END IF;

    IF p_id_tipo_nomenclador IS NULL
       OR p_id_tipo_nomenclador <= 0 THEN

        RAISE EXCEPTION
            ''Debe informar el tipo real de nomenclador.'';
END IF;

SELECT n.id_tipo_nomenclador, n.codigo, n.descripcion
INTO v_id_tipo_nomenclador_real, v_codigo_real, v_descripcion_real
FROM autorizaciones.nomenclador n
WHERE n.id_prestacion = p_id_prestacion
  AND n.baja_fecha IS NULL;

IF NOT FOUND THEN
        RAISE EXCEPTION
            ''La prestaci\00F3n seleccionada no existe o no est\00E1 activa.'';
END IF;

    IF v_id_tipo_nomenclador_real
            <> p_id_tipo_nomenclador THEN

        RAISE EXCEPTION
            ''El tipo de nomenclador informado no corresponde a la prestaci\00F3n seleccionada.'';
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
            ''Debe informar el c\00F3digo de nomenclador.'';
END IF;

    IF p_descripcion_nomenclador IS NULL
       OR length(
           btrim(
               p_descripcion_nomenclador
           )
       ) = 0 THEN

        RAISE EXCEPTION
            ''Debe informar la descripci\00F3n del nomenclador.'';
END IF;

    IF p_id_medicamento IS NOT NULL
       OR p_troquel IS NOT NULL
       OR NULLIF(
           btrim(
               p_nombre_medicamento
           ),
           ''''
       ) IS NOT NULL THEN

        RAISE EXCEPTION
            ''El detalle de nomenclador contiene datos de medicamento.'';
END IF;

UPDATE compras.requerimiento_detalle
SET tipo_item = ''NOMENCLADOR'',
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
                ''''
        ),

    descripcion_nomenclador =
        NULLIF(
                btrim(
                        CASE WHEN v_nueva_seleccion THEN v_descripcion_real
                             ELSE v_detalle.descripcion_nomenclador END
                ),
                ''''
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
                ''''
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
            ''No se encontr\00F3 el detalle a modificar.'';
END IF;

RETURN v_id;
END;
$function$;
', E'\r\n', E'\n');

        v_etapa := 'instalar funcion 2 de 3';
        EXECUTE replace(U&'
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
', E'\r\n', E'\n');

        v_etapa := 'instalar funcion 3 de 3';
        EXECUTE replace(U&'
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
            ''No se pudo asociar el tipo al detalle del requerimiento.'';
    END IF;

    RETURN v_id_detalle;
END;
$function$;
', E'\r\n', E'\n');

        -- Las firmas existentes conservan owner y ACL por CREATE OR REPLACE.
        -- Una 14 nueva copia propietario y EXECUTE de la 13; no abre PUBLIC
        -- si la firma original no lo permitia. No altera ACL de una 14 existente.
        v_etapa := 'permisos de la sobrecarga nueva';
        IF v_nueva THEN
            EXECUTE format('ALTER FUNCTION %s OWNER TO %I', v_firma14, v_owner);
            FOR v_acl IN
                SELECT DISTINCT a.grantee FROM pg_proc p,
                    LATERAL aclexplode(COALESCE(p.proacl, acldefault('f', p.proowner))) a
                WHERE p.oid = to_regprocedure(v_firma14)
            LOOP
                v_destino := CASE WHEN v_acl.grantee = 0 THEN 'PUBLIC'
                    ELSE quote_ident(pg_get_userbyid(v_acl.grantee)) END;
                EXECUTE format('REVOKE ALL ON FUNCTION %s FROM %s', v_firma14, v_destino);
            END LOOP;
            FOR v_acl IN
                SELECT a.grantee, bool_or(a.is_grantable) AS con_opcion FROM pg_proc p,
                    LATERAL aclexplode(COALESCE(p.proacl, acldefault('f', p.proowner))) a
                WHERE p.oid = to_regprocedure(v_firma13) AND a.privilege_type = 'EXECUTE'
                GROUP BY a.grantee
            LOOP
                v_destino := CASE WHEN v_acl.grantee = 0 THEN 'PUBLIC'
                    ELSE quote_ident(pg_get_userbyid(v_acl.grantee)) END;
                EXECUTE format('GRANT EXECUTE ON FUNCTION %s TO %s%s', v_firma14, v_destino,
                    CASE WHEN v_acl.con_opcion THEN ' WITH GRANT OPTION' ELSE '' END);
            END LOOP;
        END IF;

        v_etapa := 'validacion final de las tres funciones';
        SELECT string_agg(contrato.firma, E'\n') INTO v_faltantes
        FROM (VALUES
            (v_firma14, 'fbb7d73f6122436ae6aed19ad8ff8f5b'),
            (v_firma13, '7b9ccfce93c709b3c402cf2d267f6d86'),
            (v_firmac, 'd90183584e6ce4c578dd26a96f09cee2')
        ) AS contrato(firma, huella)
        LEFT JOIN pg_proc p ON p.oid = to_regprocedure(contrato.firma)
        WHERE p.oid IS NULL OR md5(replace(p.prosrc, E'\r\n', E'\n')) <> contrato.huella;
        IF v_faltantes IS NOT NULL THEN
            RAISE EXCEPTION 'No coinciden las tres funciones finales.' USING DETAIL = v_faltantes;
        END IF;
        IF v_nueva AND EXISTS (
            SELECT 1 FROM (
                (SELECT a.grantee, bool_or(a.is_grantable) AS opcion
                 FROM pg_proc p, LATERAL aclexplode(COALESCE(p.proacl, acldefault('f',p.proowner))) a
                 WHERE p.oid = to_regprocedure(v_firma13) GROUP BY a.grantee
                 EXCEPT
                 SELECT a.grantee, bool_or(a.is_grantable)
                 FROM pg_proc p, LATERAL aclexplode(COALESCE(p.proacl, acldefault('f',p.proowner))) a
                 WHERE p.oid = to_regprocedure(v_firma14) GROUP BY a.grantee)
                UNION ALL
                (SELECT a.grantee, bool_or(a.is_grantable)
                 FROM pg_proc p, LATERAL aclexplode(COALESCE(p.proacl, acldefault('f',p.proowner))) a
                 WHERE p.oid = to_regprocedure(v_firma14) GROUP BY a.grantee
                 EXCEPT
                 SELECT a.grantee, bool_or(a.is_grantable)
                 FROM pg_proc p, LATERAL aclexplode(COALESCE(p.proacl, acldefault('f',p.proowner))) a
                 WHERE p.oid = to_regprocedure(v_firma13) GROUP BY a.grantee)
            ) diferencias
        ) THEN
            RAISE EXCEPTION 'No se conservaron las autorizaciones EXECUTE de la firma publica.';
        END IF;
        -- SECURITY INVOKER: todo rol autorizado en un wrapper debe poder
        -- ejecutar tambien la funcion interna. Incluye membresias y PUBLIC.
        SELECT string_agg(quote_ident(r.rolname), ', ' ORDER BY r.rolname)
        INTO v_faltantes FROM pg_roles r
        WHERE (has_function_privilege(r.oid, to_regprocedure(v_firma13), 'EXECUTE')
            OR has_function_privilege(r.oid, to_regprocedure(v_firmac), 'EXECUTE'))
          AND NOT has_function_privilege(r.oid, to_regprocedure(v_firma14), 'EXECUTE');
        IF v_faltantes IS NOT NULL THEN
            RAISE EXCEPTION 'Hay roles autorizados en los guardados publicos sin EXECUTE en la funcion interna.'
                USING DETAIL = v_faltantes,
                HINT = 'Revisar los permisos del circuito para esos roles. Este parche no amplia autorizaciones existentes.';
        END IF;
        INSERT INTO pg_temp.compras_reparar_detalle_resultado
        VALUES ('REPARACION_CONFIRMADA', v_etapa, NULL,
            'Tres funciones de guardado verificadas. Configuracion de nomencladores conservada.', NULL, NULL);
    EXCEPTION WHEN OTHERS THEN
        GET STACKED DIAGNOSTICS v_estado = RETURNED_SQLSTATE, v_mensaje = MESSAGE_TEXT,
            v_detalle = PG_EXCEPTION_DETAIL, v_sugerencia = PG_EXCEPTION_HINT;
        INSERT INTO pg_temp.compras_reparar_detalle_resultado
        VALUES ('ERROR_REPARACION_REVERTIDA', v_etapa, v_estado, v_mensaje, v_detalle, v_sugerencia);
    END;
END;
$reparacion$;
COMMIT;

-- Esta fila se devuelve despues del COMMIT, tanto en pgAdmin como en psql.
SELECT resultado, current_database() AS base, current_user AS usuario,
       etapa, sqlstate, mensaje, detalle, sugerencia
FROM pg_temp.compras_reparar_detalle_resultado;

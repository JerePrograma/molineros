-- INSTALACION PARCIAL DE COMPRAS: SIN PASO 66 Y SIN SOBRECARGA. PostgreSQL 9.6.x.
-- Omision solicitada expresamente por el usuario el 21/10/2026.
-- ASCII: compatible con lectura UTF8/LATIN1; no depende de las tildes del editor.
-- Ejecutar TODO en una CONEXION NUEVA; archivo ISO-8859-1/LATIN1 sin BOM.
-- Aplicacion detenida y respaldo probado. No ejecutar fragmentos ni ambos instaladores.
-- Ejecuta 62 de 66 sentencias originales. Omite 32,33,34,66; conserva guardados previos.
-- Un bloque protegido revierte sus cambios de negocio si cualquier sentencia falla.
-- El ultimo SELECT, DESPUES del COMMIT, devuelve el resultado en Data Output.
-- ERROR_MIGRACION_REVERTIDA significa instalacion fallida: leer bloque/SQLSTATE/mensaje.
-- El COMMIT en ese caso conserva SOLO el reporte temporal; no una instalacion parcial.
-- La tabla temporal desaparece al cerrar esta conexion. No instala funciones auxiliares.
-- No es universalmente reejecutable: mantiene los controles originales de revision.
-- Consolidado origen SHA256: e90886fc863c52f6230cb2e5321efc4f0a93027287ea6452c79e6e6b4bf6a8a6

BEGIN;
SET LOCAL standard_conforming_strings = on;
DROP TABLE IF EXISTS pg_temp.codex_compras_parcial_20261021;
CREATE TEMPORARY TABLE codex_compras_parcial_20261021 (
    resultado TEXT NOT NULL,
    bloque TEXT NOT NULL,
    paso INTEGER NOT NULL,
    comando TEXT,
    sqlstate TEXT NOT NULL,
    mensaje TEXT NOT NULL,
    detalle TEXT,
    sugerencia TEXT,
    contexto TEXT,
    fecha TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp()
) ON COMMIT PRESERVE ROWS;

DO $ejecucion_compras_parcial$
DECLARE
    v_bloque TEXT := 'PREPARACION';
    v_paso INTEGER := 0;
    v_comando TEXT;
    v_estado TEXT;
    v_mensaje TEXT;
    v_detalle TEXT;
    v_sugerencia TEXT;
    v_contexto TEXT;
BEGIN
    -- Ningun cambio de negocio queda fuera de este bloque con EXCEPTION.
    BEGIN
        v_bloque := 'PREPARACION';
        v_paso := 1;
        v_comando := 'SET LOCAL lock_timeout = ''10s'';';
        EXECUTE replace($sentencia_compras_001$
SET LOCAL lock_timeout = '10s';$sentencia_compras_001$, E'\r\n', E'\n');

        v_bloque := 'PREPARACION';
        v_paso := 2;
        v_comando := 'SET LOCAL check_function_bodies = on;';
        EXECUTE replace($sentencia_compras_002$
SET LOCAL check_function_bodies = on;$sentencia_compras_002$, E'\r\n', E'\n');

        v_bloque := 'PREFLIGHT';
        v_paso := 3;
        v_comando := 'DO $preflight_megascript$';
        EXECUTE replace($sentencia_compras_003$

DO $preflight_megascript$
DECLARE
    v_version INTEGER := current_setting('server_version_num')::INTEGER;
    v_faltantes TEXT;
BEGIN
    IF v_version < 90600 OR v_version >= 100000 THEN
        RAISE EXCEPTION 'Este consolidado requiere PostgreSQL 9.6.x; version detectada: %.', version();
    END IF;
    IF current_setting('server_encoding') <> 'UTF8' THEN
        RAISE EXCEPTION 'El catalogo fuente usa UTF8. Revisar los contratos y hashes para otra codificacion de servidor.';
    END IF;
    IF pg_is_in_recovery() THEN
        RAISE EXCEPTION 'No ejecutar la migracion en una replica en recuperacion.';
    END IF;
    IF NOT pg_try_advisory_xact_lock(20261021, 20260928) THEN
        RAISE EXCEPTION 'Ya hay otra ejecucion de este megascript en esta base.';
    END IF;

    SELECT string_agg(objeto, E'\n' ORDER BY objeto) INTO v_faltantes
    FROM (VALUES
        ('compras.requerimiento'), ('compras.requerimiento_detalle'),
        ('compras.requerimiento_presupuesto'), ('compras.requerimiento_cotizacion_prestador'),
        ('compras.requerimiento_reclamo_prestacional'), ('compras.sector_requerimiento'),
        ('compras.tipo_prestacion'), ('public.prestador'), ('autorizaciones.reclamos_prestacionales'),
        ('autorizaciones.nomenclador'), ('public.tipo_nomenclador'), ('public.medicamentos')
    ) AS necesarios(objeto)
    WHERE to_regclass(objeto) IS NULL;
    IF v_faltantes IS NOT NULL THEN
        RAISE EXCEPTION 'Faltan tablas del contrato anterior al corte.'
            USING DETAIL = v_faltantes,
                  HINT = 'Relevar el destino con 20260922_compras_fuente_verdad_produccion.sql. No instalar un schema completo como atajo.';
    END IF;

    IF to_regnamespace('farmacia') IS NULL THEN
        RAISE EXCEPTION 'Falta el schema farmacia del contrato previo.';
    END IF;
    IF to_regprocedure('compras.listar_prestadores_enviados(integer,integer)') IS NULL
       OR to_regprocedure('compras.bloquear_requerimiento_reclamo_prestacional(integer)') IS NULL
       OR to_regprocedure('compras.es_prestador_compatible_cotizacion(integer,integer)') IS NULL
       OR to_regprocedure('compras.guardar_requerimiento(integer,character varying,integer,integer,character varying,character varying,character varying,character varying,character varying,character varying,character varying,character varying,character varying,character varying,integer,integer,integer,character varying,boolean,boolean,boolean,text,character varying)') IS NULL THEN
        RAISE EXCEPTION 'Faltan funciones del contrato anterior al corte (prestadores, bloqueo RP o guardado de 23 argumentos).';
    END IF;

    -- La adaptacion 13/10 reconoce el cuerpo anterior a 15/10. No degradar
    -- silenciosamente una revision posterior para intentar volver a instalarla.
    IF EXISTS (
        SELECT 1 FROM pg_proc p
        WHERE p.oid = to_regprocedure('compras.cambiar_estado_requerimiento(integer,integer,character varying)')
          AND position('Autorizacion previa a la OC' IN p.prosrc) > 0
    ) THEN
        RAISE EXCEPTION 'La autorizacion corporativa del 15/10 ya esta instalada.'
            USING HINT = 'No reejecutar todo el historial. Comparar el inventario y aplicar solo los incrementales faltantes, previa revision.';
    END IF;
END;
$preflight_megascript$;$sentencia_compras_003$, E'\r\n', E'\n');

        v_bloque := 'PREFLIGHT';
        v_paso := 4;
        v_comando := 'SELECT current_database() AS base_objetivo, current_user AS usuario_ejecutor,';
        EXECUTE replace($sentencia_compras_004$

SELECT current_database() AS base_objetivo, current_user AS usuario_ejecutor,
       version() AS version_postgresql, now() AS inicio_transaccion;$sentencia_compras_004$, E'\r\n', E'\n');

        v_bloque := 'PRERREQUISITO 00: docs/sql/compras/20260923_requerimiento_comparativa.sql';
        v_paso := 5;
        v_comando := 'DO $prerrequisito_comparativas$';
        EXECUTE replace($sentencia_compras_005$

-- ===== PRERREQUISITO 00: docs/sql/compras/20260923_requerimiento_comparativa.sql =====
-- SHA256 original (bytes): c2155fe158a3d1aa346703a3a8c01dde792197744d2a4350af62921a02acec2b
-- Dependencia anterior al corte, faltante en el catalogo informado por el usuario.
-- El duplicado sin extension tiene el mismo DDL; solo se incluye esta fuente.
-- Crear ambas tablas vacias si faltan; conservar y validar si ya existen.
DO $prerrequisito_comparativas$
DECLARE
    v_cabecera REGCLASS := to_regclass('compras.requerimiento_comparativa');
    v_detalle REGCLASS := to_regclass('compras.requerimiento_comparativa_detalle');
    v_faltantes TEXT;
BEGIN
    IF (v_cabecera IS NULL) <> (v_detalle IS NULL) THEN
        RAISE EXCEPTION 'Comparativas parcialmente instaladas: existe solo una de las dos tablas.'
            USING HINT = 'Relevar la tabla existente. No borrarla ni sobrescribirla para continuar.';
    END IF;

    SELECT string_agg(x.tabla || '.' || x.columna, E'\n' ORDER BY x.tabla)
      INTO v_faltantes
      FROM (VALUES ('compras.requerimiento','id_requerimiento'),
                   ('public.prestador','id_prestador'),
                   ('autorizaciones.nomenclador','id_prestacion')) x(tabla,columna)
      LEFT JOIN pg_attribute a ON a.attrelid=to_regclass(x.tabla)
           AND a.attname=x.columna AND NOT a.attisdropped
     WHERE a.attname IS NULL OR NOT EXISTS (
         SELECT 1 FROM pg_index i
         WHERE i.indrelid=to_regclass(x.tabla)
           AND i.indisunique AND i.indisvalid AND i.indisready AND i.indimmediate
           AND i.indpred IS NULL AND i.indexprs IS NULL
           AND i.indnatts=1 AND i.indkey[0]=a.attnum
     );
    IF v_faltantes IS NOT NULL THEN
        RAISE EXCEPTION 'Faltan claves padre referenciables para las comparativas del 23/09.'
            USING DETAIL=v_faltantes;
    END IF;

    IF v_cabecera IS NULL AND v_detalle IS NULL THEN
-- INICIO DDL ORIGINAL 20260923 (sin modificaciones).
-- Comparativa vigente por requerimiento y prestador.
CREATE TABLE compras.requerimiento_comparativa (
    id_comparativa SERIAL PRIMARY KEY,
    id_requerimiento INTEGER NOT NULL REFERENCES compras.requerimiento (id_requerimiento),
    id_prestador INTEGER NOT NULL REFERENCES public.prestador (id_prestador),
    fecha_presupuesto DATE,
    forma_pago INTEGER NOT NULL DEFAULT 30,
    plazo_entrega VARCHAR(5),
    validez_presupuesto INTEGER,
    envio VARCHAR(20),
    iva NUMERIC(18,2),
    iibb NUMERIC(18,2),
    alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    alta_usr VARCHAR(100) NOT NULL,
    modi_fecha TIMESTAMP WITHOUT TIME ZONE,
    modi_usr VARCHAR(100),
    CONSTRAINT uq_compras_comparativa_prestador UNIQUE (id_requerimiento, id_prestador)
);

CREATE TABLE compras.requerimiento_comparativa_detalle (
    id_detalle SERIAL PRIMARY KEY,
    id_comparativa INTEGER NOT NULL REFERENCES compras.requerimiento_comparativa (id_comparativa),
    id_prestacion INTEGER NOT NULL REFERENCES autorizaciones.nomenclador (id_prestacion),
    cantidad INTEGER,
    importe_unitario NUMERIC(18,2),
    alta_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    alta_usr VARCHAR(100) NOT NULL,
    modi_fecha TIMESTAMP WITHOUT TIME ZONE,
    modi_usr VARCHAR(100),
    CONSTRAINT uq_compras_comparativa_prestacion UNIQUE (id_comparativa, id_prestacion)
);

-- FIN DDL ORIGINAL 20260923.
        RAISE NOTICE 'Comparativas del 23/09 creadas vacias dentro de la transaccion de instalacion.';
    ELSE
        RAISE NOTICE 'Comparativas existentes: se conservan sus tablas, secuencias y datos.';
    END IF;

    -- Comprobar el contrato completo de columnas de la fuente, sin cambiarlo.
    SELECT string_agg(x.tabla || '.' || x.columna, E'
' ORDER BY x.tabla,x.columna)
      INTO v_faltantes
      FROM (VALUES
        ('compras.requerimiento_comparativa','id_comparativa','integer',-1,TRUE),
        ('compras.requerimiento_comparativa','id_requerimiento','integer',-1,TRUE),
        ('compras.requerimiento_comparativa','id_prestador','integer',-1,TRUE),
        ('compras.requerimiento_comparativa','fecha_presupuesto','date',-1,FALSE),
        ('compras.requerimiento_comparativa','forma_pago','integer',-1,TRUE),
        ('compras.requerimiento_comparativa','plazo_entrega','character varying',9,FALSE),
        ('compras.requerimiento_comparativa','validez_presupuesto','integer',-1,FALSE),
        ('compras.requerimiento_comparativa','envio','character varying',24,FALSE),
        ('compras.requerimiento_comparativa','iva','numeric',1179654,FALSE),
        ('compras.requerimiento_comparativa','iibb','numeric',1179654,FALSE),
        ('compras.requerimiento_comparativa','alta_fecha','timestamp without time zone',-1,TRUE),
        ('compras.requerimiento_comparativa','alta_usr','character varying',104,TRUE),
        ('compras.requerimiento_comparativa','modi_fecha','timestamp without time zone',-1,FALSE),
        ('compras.requerimiento_comparativa','modi_usr','character varying',104,FALSE),
        ('compras.requerimiento_comparativa_detalle','id_detalle','integer',-1,TRUE),
        ('compras.requerimiento_comparativa_detalle','id_comparativa','integer',-1,TRUE),
        ('compras.requerimiento_comparativa_detalle','id_prestacion','integer',-1,TRUE),
        ('compras.requerimiento_comparativa_detalle','cantidad','integer',-1,FALSE),
        ('compras.requerimiento_comparativa_detalle','importe_unitario','numeric',1179654,FALSE),
        ('compras.requerimiento_comparativa_detalle','alta_fecha','timestamp without time zone',-1,TRUE),
        ('compras.requerimiento_comparativa_detalle','alta_usr','character varying',104,TRUE),
        ('compras.requerimiento_comparativa_detalle','modi_fecha','timestamp without time zone',-1,FALSE),
        ('compras.requerimiento_comparativa_detalle','modi_usr','character varying',104,FALSE)
      ) x(tabla,columna,tipo,modificador,obligatorio)
      LEFT JOIN pg_attribute a ON a.attrelid=to_regclass(x.tabla)
           AND a.attname=x.columna AND NOT a.attisdropped
     WHERE a.attname IS NULL OR a.atttypid<>to_regtype(x.tipo)
        OR a.atttypmod<>x.modificador OR a.attnotnull<>x.obligatorio;
    IF v_faltantes IS NOT NULL THEN
        RAISE EXCEPTION 'Contrato de columnas de comparativas distinto del SQL 23/09.'
            USING DETAIL=v_faltantes;
    END IF;

    IF (SELECT count(*) FROM pg_attribute
        WHERE attrelid=to_regclass('compras.requerimiento_comparativa') AND attnum>0 AND NOT attisdropped)<>14
       OR (SELECT count(*) FROM pg_attribute
        WHERE attrelid=to_regclass('compras.requerimiento_comparativa_detalle') AND attnum>0 AND NOT attisdropped)<>9
       OR EXISTS (SELECT 1 FROM pg_class
          WHERE oid IN(to_regclass('compras.requerimiento_comparativa'),to_regclass('compras.requerimiento_comparativa_detalle'))
            AND relkind<>'r')
       OR pg_get_serial_sequence('compras.requerimiento_comparativa','id_comparativa') IS NULL
       OR pg_get_serial_sequence('compras.requerimiento_comparativa_detalle','id_detalle') IS NULL THEN
        RAISE EXCEPTION 'Estructura o secuencias de comparativas distintas del SQL 23/09.';
    END IF;

    -- PK/UNIQUE y FK con las mismas columnas, sin depender del nombre generado.
    SELECT string_agg(x.tabla || '(' || x.columnas || ')', E'
' ORDER BY x.tabla,x.columnas)
      INTO v_faltantes
      FROM (VALUES
        ('compras.requerimiento_comparativa','p','id_comparativa',NULL::TEXT,NULL::TEXT),
        ('compras.requerimiento_comparativa','u','id_requerimiento,id_prestador',NULL,NULL),
        ('compras.requerimiento_comparativa','f','id_requerimiento','compras.requerimiento','id_requerimiento'),
        ('compras.requerimiento_comparativa','f','id_prestador','public.prestador','id_prestador'),
        ('compras.requerimiento_comparativa_detalle','p','id_detalle',NULL,NULL),
        ('compras.requerimiento_comparativa_detalle','u','id_comparativa,id_prestacion',NULL,NULL),
        ('compras.requerimiento_comparativa_detalle','f','id_comparativa','compras.requerimiento_comparativa','id_comparativa'),
        ('compras.requerimiento_comparativa_detalle','f','id_prestacion','autorizaciones.nomenclador','id_prestacion')
      ) x(tabla,tipo,columnas,padre,claves_padre)
     WHERE NOT EXISTS (
        SELECT 1 FROM pg_constraint c
        WHERE c.conrelid=to_regclass(x.tabla) AND c.contype::TEXT=x.tipo
          AND c.convalidated AND NOT c.condeferrable
          AND c.conkey=(SELECT array_agg(a.attnum ORDER BY k.orden)
              FROM unnest(string_to_array(x.columnas,',')) WITH ORDINALITY k(nombre,orden)
              JOIN pg_attribute a ON a.attrelid=to_regclass(x.tabla) AND a.attname=k.nombre AND NOT a.attisdropped)
          AND (x.padre IS NULL OR (
              c.confrelid=to_regclass(x.padre)
              AND c.confkey=(SELECT array_agg(a.attnum ORDER BY k.orden)
                  FROM unnest(string_to_array(x.claves_padre,',')) WITH ORDINALITY k(nombre,orden)
                  JOIN pg_attribute a ON a.attrelid=to_regclass(x.padre) AND a.attname=k.nombre AND NOT a.attisdropped)
              AND c.confupdtype='a' AND c.confdeltype='a' AND c.confmatchtype='s'
          ))
     );
    IF v_faltantes IS NOT NULL THEN
        RAISE EXCEPTION 'Claves o relaciones de comparativas distintas del SQL 23/09.' USING DETAIL=v_faltantes;
    END IF;

    IF (SELECT count(*) FROM pg_constraint
        WHERE conrelid IN(to_regclass('compras.requerimiento_comparativa'),to_regclass('compras.requerimiento_comparativa_detalle')))<>8 THEN
        RAISE EXCEPTION 'Comparativas con restricciones adicionales al contrato 23/09. Relevar sin desactivarlas.';
    END IF;

    IF EXISTS(SELECT 1 FROM (VALUES
            ('compras.requerimiento_comparativa','id_comparativa'),
            ('compras.requerimiento_comparativa_detalle','id_detalle')) x(tabla,columna)
        WHERE NOT EXISTS(SELECT 1 FROM pg_attrdef d JOIN pg_attribute a ON a.attrelid=d.adrelid AND a.attnum=d.adnum
            WHERE d.adrelid=to_regclass(x.tabla) AND a.attname=x.columna
              AND pg_get_expr(d.adbin,d.adrelid)=format('nextval(%L::regclass)',to_regclass(pg_get_serial_sequence(x.tabla,x.columna))::TEXT))) THEN
        RAISE EXCEPTION 'Default SERIAL de comparativas distinto de su secuencia asociada. Relevar sin modificarlo.';
    END IF;

    IF NOT EXISTS(SELECT 1 FROM pg_attrdef d JOIN pg_attribute a ON a.attrelid=d.adrelid AND a.attnum=d.adnum
        WHERE d.adrelid=to_regclass('compras.requerimiento_comparativa') AND a.attname='forma_pago'
          AND regexp_replace(pg_get_expr(d.adbin,d.adrelid),'[[:space:]()]','','g')='30')
       OR EXISTS(SELECT 1 FROM (VALUES ('compras.requerimiento_comparativa'),('compras.requerimiento_comparativa_detalle')) x(tabla)
          WHERE NOT EXISTS(SELECT 1 FROM pg_attrdef d JOIN pg_attribute a ON a.attrelid=d.adrelid AND a.attnum=d.adnum
              WHERE d.adrelid=to_regclass(x.tabla) AND a.attname='alta_fecha'
                AND lower(regexp_replace(pg_get_expr(d.adbin,d.adrelid),'[[:space:]]','','g'))='now()')) THEN
        RAISE EXCEPTION 'Valores por defecto de comparativas distintos del SQL 23/09.';
    END IF;
    IF (SELECT count(*) FROM pg_attrdef
        WHERE adrelid IN(to_regclass('compras.requerimiento_comparativa'),to_regclass('compras.requerimiento_comparativa_detalle')))<>5 THEN
        RAISE EXCEPTION 'Comparativas con valores por defecto adicionales al SQL 23/09.';
    END IF;
END;
$prerrequisito_comparativas$;$sentencia_compras_005$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 01/14: docs/sql/compras/20260925_cotizaciones_empresas_alta_adjudicacion.sql';
        v_paso := 6;
        v_comando := 'DO $precondicion$';
        EXECUTE replace($sentencia_compras_006$

-- ===== BLOQUE 01/14: docs/sql/compras/20260925_cotizaciones_empresas_alta_adjudicacion.sql =====
-- SHA256 original (bytes): 8e2a233f65a4cf29cdd3e05b92dc92a1c699543edeaef4805782be6b44b1b522
-- Solo se retiraron BEGIN/COMMIT exteriores; cuerpo funcional conservado.

-- Cotizaciones RRHH/SISTEMAS en alta y Empresa adjudicada.
-- Basado en catalogo real consultado el 25/09/2026, solo lectura y rollback:
-- devmolineros PostgreSQL 9.6.15; compatible con PostgreSQL 9.6.5.
-- listar/get_documentos_requerimiento devuelven SETOF requerimiento_presupuesto
-- con SELECT rp.*: la nueva columna conserva firmas y tipos de retorno.
-- Ya existe ux_compras_presupuesto_requerimiento_empresa_activa por CUIT+sucursal.
-- Este archivo NO fue aplicado. Revisar/ejecutar junto con el despliegue Java/JSP.
-- ISO-8859-1 sin BOM. Para psql: PGCLIENTENCODING=LATIN1 y ON_ERROR_STOP=1.
-- No modifica maestros, estados de Prestador ni datos de cotizaciones existentes.


DO $precondicion$
BEGIN
    IF to_regprocedure('compras.listar_documentos_requerimiento(integer,integer)') IS NULL
       OR to_regprocedure('compras.get_documento_requerimiento(integer,integer,integer)') IS NULL
       OR to_regprocedure('compras.confirmar_orden_compra_requerimiento(integer,character varying)') IS NULL
       OR to_regprocedure('compras.buscar_empresas_cotizacion(character varying,character varying,character varying,integer)') IS NULL THEN
        RAISE EXCEPTION 'No esta instalado el contrato de cotizaciones de Empresas relevado.';
    END IF;
END;
$precondicion$;$sentencia_compras_006$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 01/14: docs/sql/compras/20260925_cotizaciones_empresas_alta_adjudicacion.sql';
        v_paso := 7;
        v_comando := 'ALTER TABLE compras.requerimiento_presupuesto';
        EXECUTE replace($sentencia_compras_007$

ALTER TABLE compras.requerimiento_presupuesto
    ADD COLUMN IF NOT EXISTS empresa_adjudicada BOOLEAN NOT NULL DEFAULT FALSE;$sentencia_compras_007$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 01/14: docs/sql/compras/20260925_cotizaciones_empresas_alta_adjudicacion.sql';
        v_paso := 8;
        v_comando := 'CREATE UNIQUE INDEX IF NOT EXISTS ux_compras_empresa_adjudicada_activa';
        EXECUTE replace($sentencia_compras_008$

CREATE UNIQUE INDEX IF NOT EXISTS ux_compras_empresa_adjudicada_activa
    ON compras.requerimiento_presupuesto (id_requerimiento)
    WHERE tipo_documento = 3 AND baja_fecha IS NULL AND empresa_adjudicada;$sentencia_compras_008$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 01/14: docs/sql/compras/20260925_cotizaciones_empresas_alta_adjudicacion.sql';
        v_paso := 9;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.guardar_empresa_adjudicada(';
        EXECUTE replace($sentencia_compras_009$

CREATE OR REPLACE FUNCTION compras.guardar_empresa_adjudicada(
    p_id_requerimiento INTEGER,
    p_empresa_cuit VARCHAR,
    p_empresa_sucursal VARCHAR,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
    v_estado INTEGER;
    v_sector VARCHAR(200);
    v_id_presupuesto INTEGER;
    v_cuit VARCHAR;
    v_sucursal VARCHAR;
BEGIN
    SELECT r.estado, sr.descripcion
      INTO v_estado, v_sector
      FROM compras.requerimiento r
      JOIN compras.sector_requerimiento sr ON sr.id_sector = r.id_sector
       AND sr.activo = TRUE AND sr.baja_fecha IS NULL
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE OF r;

    IF NOT FOUND OR v_estado <> 1
       OR compras.normalizar_sector(v_sector) NOT IN ('RRHH', 'SISTEMAS') THEN
        RAISE EXCEPTION
            'La adjudicacion de Empresa requiere un requerimiento activo de RRHH o SISTEMAS PENDIENTE.';
    END IF;

    v_cuit := NULLIF(btrim(p_empresa_cuit), '');
    v_sucursal := NULLIF(btrim(p_empresa_sucursal), '');
    IF (v_cuit IS NULL) <> (v_sucursal IS NULL) THEN
        RAISE EXCEPTION 'Debe informar CUIT y sucursal de la Empresa adjudicada.';
    END IF;

    IF v_cuit IS NOT NULL THEN
        IF NOT EXISTS (
            SELECT 1 FROM compras.buscar_empresas_cotizacion(
                    v_cuit, NULL, v_sucursal, 1)
        ) THEN
            RAISE EXCEPTION 'La Empresa adjudicada no existe o no esta activa en el padron.';
        END IF;

        SELECT rp.id_requerimiento_presupuesto INTO v_id_presupuesto
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 3
           AND rp.baja_fecha IS NULL
           AND rp.empresa_cuit = v_cuit
           AND rp.empresa_sucursal = v_sucursal
         FOR UPDATE;
        IF NOT FOUND THEN
            RAISE EXCEPTION 'La Empresa adjudicada debe tener una cotizacion activa en este requerimiento.';
        END IF;
    END IF;

    -- Limpiar primero evita una violacion transitoria del indice unico.
    UPDATE compras.requerimiento_presupuesto
       SET empresa_adjudicada = FALSE
     WHERE id_requerimiento = p_id_requerimiento
       AND tipo_documento = 3 AND empresa_adjudicada;

    IF v_id_presupuesto IS NOT NULL THEN
        UPDATE compras.requerimiento_presupuesto
           SET empresa_adjudicada = TRUE
         WHERE id_requerimiento_presupuesto = v_id_presupuesto;
    END IF;

    UPDATE compras.requerimiento
       SET modi_fecha = now(), modi_usr = compras.normalizar_usuario(p_usuario)
     WHERE id_requerimiento = p_id_requerimiento;
    RETURN COALESCE(v_id_presupuesto, 0);
END;
$func$
LANGUAGE plpgsql;$sentencia_compras_009$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 01/14: docs/sql/compras/20260925_cotizaciones_empresas_alta_adjudicacion.sql';
        v_paso := 10;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.registrar_requerimiento_presupuesto(';
        EXECUTE replace(U&'

CREATE OR REPLACE FUNCTION compras.registrar_requerimiento_presupuesto(
    p_id_requerimiento INTEGER,
    p_tipo_documento SMALLINT,
    p_id_prestador INTEGER,
    p_empresa_cuit VARCHAR,
    p_empresa_sucursal VARCHAR,
    p_descripcion_empresa VARCHAR,
    p_dl_group_id BIGINT,
    p_dl_folder_id BIGINT,
    p_dl_file_entry_id BIGINT,
    p_dl_file_uuid VARCHAR,
    p_nombre_original VARCHAR,
    p_nombre_persistido VARCHAR,
    p_titulo VARCHAR,
    p_descripcion_prestador VARCHAR,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
    v_id INTEGER;
    v_estado_requerimiento INTEGER;
    v_sector_descripcion VARCHAR(200);
    v_usuario VARCHAR(100);
    v_descripcion_empresa VARCHAR(200);
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION
            ''El requerimiento informado no es v!00E1lido.'';
    END IF;

    IF p_tipo_documento IS NULL OR p_tipo_documento <> 3 THEN
        RAISE EXCEPTION
            ''El tipo documental no corresponde a una cotizaci!00F3n de Empresa.'';
    END IF;

    IF p_id_prestador IS NOT NULL
       OR NULLIF(btrim(p_descripcion_prestador), '''') IS NOT NULL THEN

        RAISE EXCEPTION
            ''Una cotizaci!00F3n de Empresa no puede asociarse a un prestador.'';
    END IF;

    IF NULLIF(btrim(p_empresa_cuit), '''') IS NULL
       OR length(btrim(p_empresa_cuit)) > 11
       OR NULLIF(btrim(p_empresa_sucursal), '''') IS NULL
       OR length(btrim(p_empresa_sucursal)) > 6
       OR NULLIF(btrim(p_descripcion_empresa), '''') IS NULL
       OR length(btrim(p_descripcion_empresa)) > 200 THEN

        RAISE EXCEPTION
            ''La identidad de la Empresa de la cotizaci!00F3n no es v!00E1lida.'';
    END IF;

    IF p_dl_group_id IS NULL OR p_dl_group_id <= 0
       OR p_dl_folder_id IS NULL OR p_dl_folder_id < 0
       OR p_dl_file_entry_id IS NULL OR p_dl_file_entry_id <= 0 THEN

        RAISE EXCEPTION
            ''La identidad del documento de cotizaci!00F3n no es v!00E1lida.'';
    END IF;

    v_usuario := COALESCE(NULLIF(btrim(p_usuario), ''''), ''sistema'');

    SELECT
        r.estado,
        sr.descripcion
      INTO
        v_estado_requerimiento,
        v_sector_descripcion
      FROM compras.requerimiento r
      JOIN compras.sector_requerimiento sr
        ON sr.id_sector = r.id_sector
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE OF r;

    IF NOT FOUND
       OR v_estado_requerimiento <> 1
       OR compras.normalizar_sector(v_sector_descripcion)
            NOT IN (''RRHH'', ''SISTEMAS'') THEN

        RAISE EXCEPTION
            ''La cotizaci!00F3n de Empresa requiere un requerimiento activo ''
            ''de RRHH o SISTEMAS en estado PENDIENTE.'';
    END IF;

    SELECT e.razon_soc INTO v_descripcion_empresa
      FROM compras.buscar_empresas_cotizacion(
              p_empresa_cuit, NULL, p_empresa_sucursal, 1) e;
    IF NOT FOUND OR length(v_descripcion_empresa) > 200 THEN
        RAISE EXCEPTION
            ''La Empresa no existe o no esta activa en el padron.'';
    END IF;

    IF EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 3
           AND rp.empresa_cuit = btrim(p_empresa_cuit)
           AND rp.empresa_sucursal = btrim(p_empresa_sucursal)
           AND rp.baja_fecha IS NULL
    ) THEN
        RAISE EXCEPTION
            ''La Empresa ya tiene una cotizaci!00F3n activa para este requerimiento.'';
    END IF;

    INSERT INTO compras.requerimiento_presupuesto (
        id_requerimiento,
        tipo_documento,
        fecha_documento,
        id_prestador,
        empresa_cuit,
        empresa_sucursal,
        descripcion_empresa,
        dl_group_id,
        dl_folder_id,
        dl_file_entry_id,
        dl_file_uuid,
        nombre_original,
        nombre_persistido,
        titulo,
        descripcion_prestador,
        alta_usr
    )
    VALUES (
        p_id_requerimiento,
        3,
        NULL,
        NULL,
        btrim(p_empresa_cuit),
        btrim(p_empresa_sucursal),
        v_descripcion_empresa,
        p_dl_group_id,
        p_dl_folder_id,
        p_dl_file_entry_id,
        NULLIF(btrim(p_dl_file_uuid), ''''),
        btrim(p_nombre_original),
        btrim(p_nombre_persistido),
        btrim(p_titulo),
        NULL,
        v_usuario
    )
    RETURNING id_requerimiento_presupuesto
    INTO v_id;

    RETURN v_id;
END;
$func$
LANGUAGE plpgsql;' UESCAPE '!', E'\r\n', E'\n');

        v_bloque := 'BLOQUE 01/14: docs/sql/compras/20260925_cotizaciones_empresas_alta_adjudicacion.sql';
        v_paso := 11;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.baja_cotizacion_empresa_requerimiento(';
        EXECUTE replace($sentencia_compras_011$

CREATE OR REPLACE FUNCTION compras.baja_cotizacion_empresa_requerimiento(
    p_id_requerimiento_presupuesto INTEGER,
    p_id_requerimiento INTEGER,
    p_usuario VARCHAR
)
RETURNS BOOLEAN
AS $func$
DECLARE
    v_estado_requerimiento INTEGER;
    v_sector_descripcion VARCHAR(200);
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento_presupuesto IS NULL
       OR p_id_requerimiento_presupuesto <= 0
       OR p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN

        RETURN FALSE;
    END IF;

    v_usuario := COALESCE(NULLIF(btrim(p_usuario), ''), 'sistema');

    SELECT
        r.estado,
        sr.descripcion
      INTO
        v_estado_requerimiento,
        v_sector_descripcion
      FROM compras.requerimiento r
      JOIN compras.sector_requerimiento sr
        ON sr.id_sector = r.id_sector
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE OF r;

    IF NOT FOUND
       OR v_estado_requerimiento <> 1
       OR compras.normalizar_sector(v_sector_descripcion)
            NOT IN ('RRHH', 'SISTEMAS') THEN

        RAISE EXCEPTION
            'Las cotizaciones de Empresas solo pueden eliminarse '
            'en requerimientos PENDIENTES de RRHH o SISTEMAS.';
    END IF;

    UPDATE compras.requerimiento_presupuesto
       SET baja_fecha = now(),
           baja_usr = v_usuario,
           empresa_adjudicada = FALSE
     WHERE id_requerimiento_presupuesto = p_id_requerimiento_presupuesto
       AND id_requerimiento = p_id_requerimiento
       AND tipo_documento = 3
       AND baja_fecha IS NULL;

    RETURN FOUND;
END;
$func$
LANGUAGE plpgsql;$sentencia_compras_011$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 01/14: docs/sql/compras/20260925_cotizaciones_empresas_alta_adjudicacion.sql';
        v_paso := 12;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.confirmar_orden_compra_requerimiento(';
        EXECUTE replace($sentencia_compras_012$

CREATE OR REPLACE FUNCTION compras.confirmar_orden_compra_requerimiento(
    p_id_requerimiento INTEGER,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
    v_estado INTEGER;
    v_sector_descripcion VARCHAR(120);
BEGIN
    IF p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN

        RAISE EXCEPTION
            'Debe informar el requerimiento de compra.';
    END IF;

    SELECT
        r.estado,
        sr.descripcion
    INTO
        v_estado,
        v_sector_descripcion
    FROM compras.requerimiento r
    JOIN compras.sector_requerimiento sr
      ON sr.id_sector = r.id_sector
     AND sr.activo = TRUE
     AND sr.baja_fecha IS NULL
    WHERE r.id_requerimiento = p_id_requerimiento
      AND r.baja_fecha IS NULL
    FOR UPDATE OF r;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No se encontro el requerimiento activo.';
    END IF;

    IF v_estado = 5 THEN
        RETURN 5;
    END IF;

    IF v_estado <> 1 THEN
        RAISE EXCEPTION
            'El requerimiento solo puede pasar a ORDEN_COMPRA desde PENDIENTE.';
    END IF;

    IF compras.normalizar_sector(
            v_sector_descripcion
       ) NOT IN ('RRHH', 'SISTEMAS') THEN

        RAISE EXCEPTION
            'Solo los requerimientos de RRHH o SISTEMAS pueden pasar directamente a ORDEN_COMPRA.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM compras.requerimiento_detalle d
         WHERE d.id_requerimiento = p_id_requerimiento
           AND d.baja_fecha IS NULL
    ) THEN

        RAISE EXCEPTION
            'Debe existir al menos un detalle antes de pasar a ORDEN_COMPRA.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 3
           AND rp.baja_fecha IS NULL
    ) THEN

        RAISE EXCEPTION
            'Debe existir al menos una cotizacion de Empresa activa antes de pasar a ORDEN_COMPRA.';
    END IF;

    IF (SELECT count(*)
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 3
           AND rp.baja_fecha IS NULL
           AND rp.empresa_adjudicada) <> 1 THEN

        RAISE EXCEPTION
            'Debe seleccionar exactamente una Empresa adjudicada con cotizacion activa.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
          JOIN informacion_afip.empresa e
            ON btrim(e.cuit) = rp.empresa_cuit
           AND btrim(e.sucursal) = rp.empresa_sucursal
           AND e.baja_fecha IS NULL
           AND NULLIF(btrim(e.razon_soc), '') IS NOT NULL
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 3
           AND rp.baja_fecha IS NULL
           AND rp.empresa_adjudicada
    ) THEN
        RAISE EXCEPTION
            'La Empresa adjudicada no existe o no esta activa en el padron.';
    END IF;

    UPDATE compras.requerimiento
       SET estado = 5,
           modi_fecha = now(),
           modi_usr = compras.normalizar_usuario(p_usuario)
     WHERE id_requerimiento = p_id_requerimiento
       AND estado = 1
       AND baja_fecha IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'El requerimiento fue modificado por otro proceso.';
    END IF;

    RETURN 5;
END;
$func$
LANGUAGE plpgsql;$sentencia_compras_012$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 01/14: docs/sql/compras/20260925_cotizaciones_empresas_alta_adjudicacion.sql';
        v_paso := 13;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.cambiar_estado_requerimiento(';
        EXECUTE replace(U&'

CREATE OR REPLACE FUNCTION compras.cambiar_estado_requerimiento(
    p_id_requerimiento INTEGER,
    p_estado_nuevo INTEGER,
    p_usuario VARCHAR
)
    RETURNS VOID
AS $func$
DECLARE
v_estado_actual INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    v_usuario := compras.normalizar_usuario(
        p_usuario
    );

SELECT r.estado
INTO v_estado_actual
FROM compras.requerimiento r
WHERE r.id_requerimiento =
      p_id_requerimiento
  AND (
    r.baja_fecha IS NULL
        OR r.estado = 99
    )
    FOR UPDATE;

IF v_estado_actual IS NULL THEN
        RAISE EXCEPTION
            ''No se encontr!00F3 el requerimiento.'';
END IF;

    IF p_estado_nuevo = v_estado_actual THEN
        RAISE EXCEPTION
            ''La transicion al mismo estado no es v!00E1lida.'';
END IF;

    -- La ruta generica tampoco permite saltear las reglas de Empresas.
    IF p_estado_nuevo = 5 AND EXISTS (
        SELECT 1
          FROM compras.requerimiento r
          JOIN compras.sector_requerimiento sr ON sr.id_sector = r.id_sector
         WHERE r.id_requerimiento = p_id_requerimiento
           AND compras.normalizar_sector(sr.descripcion) IN (''RRHH'', ''SISTEMAS'')
    ) THEN
        PERFORM compras.confirmar_orden_compra_requerimiento(
                p_id_requerimiento, p_usuario);
        RETURN;
    END IF;

UPDATE compras.requerimiento
SET estado = p_estado_nuevo,
    modi_fecha = now(),
    modi_usr = v_usuario,
    baja_usr =
        CASE
            WHEN p_estado_nuevo = 99
                THEN v_usuario
            ELSE baja_usr
            END
WHERE id_requerimiento =
      p_id_requerimiento
  AND estado =
      v_estado_actual;

IF NOT FOUND THEN
        RAISE EXCEPTION
            ''El requerimiento fue modificado por otro proceso.'';
END IF;
END;
$func$
LANGUAGE plpgsql;' UESCAPE '!', E'\r\n', E'\n');

        v_bloque := 'BLOQUE 02/14: docs/sql/20260930_buscar_medicamentos_droga_compras.sql';
        v_paso := 14;
        v_comando := 'CREATE OR REPLACE FUNCTION farmacia.buscar_medicamentos_droga(';
        EXECUTE replace($sentencia_compras_014$

-- ===== BLOQUE 02/14: docs/sql/20260930_buscar_medicamentos_droga_compras.sql =====
-- SHA256 original (bytes): 9fe11339879a73a8fd35c1e5cf396ff8e86c4eec19fa9388f96a3d49299d2359
-- Solo se retiraron BEGIN/COMMIT exteriores; cuerpo funcional conservado.

-- Filtro por droga para el nomenclador de medicamentos de Compras.
-- Devuelve todos los troqueles coincidentes, sin paginacion.
-- Conserva la comparacion por droga, la fecha y las bajas del buscador OSPIM.


CREATE OR REPLACE FUNCTION farmacia.buscar_medicamentos_droga(
    droga_v character varying)
RETURNS TABLE(troquel numeric)
AS $BODY$
BEGIN
    return query
    select distinct m.troquel
    from public.medicamentos m
    where droga_v is not null
    and droga_v <> ''
    and translate(
        upper(m.droga),
        chr(193) || chr(201) || chr(205) || chr(211)
            || chr(218) || chr(220) || chr(209) || chr(192)
            || chr(200) || chr(204) || chr(210) || chr(217)
            || chr(196) || chr(203) || chr(207) || chr(214)
            || chr(220) || chr(194) || chr(202) || chr(206)
            || chr(212) || chr(219),
        'AEIOUUNAEIOUAEIOUAEIOU'
    ) like '%' || translate(
        upper(droga_v),
        chr(193) || chr(201) || chr(205) || chr(211)
            || chr(218) || chr(220) || chr(209) || chr(192)
            || chr(200) || chr(204) || chr(210) || chr(217)
            || chr(196) || chr(203) || chr(207) || chr(214)
            || chr(220) || chr(194) || chr(202) || chr(206)
            || chr(212) || chr(219),
        'AEIOUUNAEIOUAEIOUAEIOU'
    ) || '%'
    and m.fecha <= current_date
    and m.baja_fecha is null;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100
ROWS 1000;$sentencia_compras_014$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 03/14: docs/sql/compras/20261006_cotizaciones_identificacion_fiscal_diferida.sql';
        v_paso := 15;
        v_comando := 'DO $precondicion$';
        EXECUTE replace($sentencia_compras_015$

-- ===== BLOQUE 03/14: docs/sql/compras/20261006_cotizaciones_identificacion_fiscal_diferida.sql =====
-- SHA256 original (bytes): 8f463fa7ad7bad05bd4edc4dedf6b00e499eb4332719121ea6599a18ab0e7133
-- Solo se retiraron BEGIN/COMMIT exteriores; cuerpo funcional conservado.

-- Identificacion fiscal diferida de cotizaciones de RRHH/SISTEMAS.
-- Referencias: Liquidaciones, prestador externo (nombre obligatorio/CUIT opcional
-- y actualizacion por id); contrato de Compras del 25/09/2026.
-- Requiere el contrato de 20260925_cotizaciones_empresas_alta_adjudicacion.sql.
-- La precondicion informa los objetos faltantes; no exige CHECKs no instalados.
-- PostgreSQL 9.6. No ejecutar separado del despliegue Java/JSP correspondiente.
-- Este archivo NO fue aplicado. ISO-8859-1 sin BOM.
-- Conserva snapshots, adjuntos, adjudicacion y maestros existentes.


DO $precondicion$
DECLARE
    v_faltantes TEXT;
BEGIN
    SELECT string_agg(requisito, E'\n' ORDER BY requisito)
      INTO v_faltantes
      FROM (VALUES
          ('tabla compras.requerimiento_presupuesto',
           to_regclass('compras.requerimiento_presupuesto') IS NOT NULL),
          ('funcion compras.normalizar_sector(character varying)',
           to_regprocedure('compras.normalizar_sector(character varying)') IS NOT NULL),
          ('funcion compras.normalizar_usuario(character varying)',
           to_regprocedure('compras.normalizar_usuario(character varying)') IS NOT NULL),
          ('funcion compras.buscar_empresas_cotizacion(character varying,character varying,character varying,integer)',
           to_regprocedure('compras.buscar_empresas_cotizacion(character varying,character varying,character varying,integer)') IS NOT NULL),
          ('funcion compras.registrar_requerimiento_presupuesto(integer,smallint,integer,character varying,character varying,character varying,bigint,bigint,bigint,character varying,character varying,character varying,character varying,character varying,character varying)',
           to_regprocedure('compras.registrar_requerimiento_presupuesto(integer,smallint,integer,character varying,character varying,character varying,bigint,bigint,bigint,character varying,character varying,character varying,character varying,character varying,character varying)') IS NOT NULL),
          ('funcion compras.guardar_empresa_adjudicada(integer,character varying,character varying,character varying)',
           to_regprocedure('compras.guardar_empresa_adjudicada(integer,character varying,character varying,character varying)') IS NOT NULL),
          ('funcion compras.confirmar_orden_compra_requerimiento(integer,character varying)',
           to_regprocedure('compras.confirmar_orden_compra_requerimiento(integer,character varying)') IS NOT NULL),
          ('funcion compras.listar_documentos_requerimiento(integer,integer)',
           to_regprocedure('compras.listar_documentos_requerimiento(integer,integer)') IS NOT NULL),
          ('funcion compras.get_documento_requerimiento(integer,integer,integer)',
           to_regprocedure('compras.get_documento_requerimiento(integer,integer,integer)') IS NOT NULL),
          ('indice compras.ux_compras_presupuesto_requerimiento_empresa_activa',
           to_regclass('compras.ux_compras_presupuesto_requerimiento_empresa_activa') IS NOT NULL),
          ('indice compras.ux_compras_empresa_adjudicada_activa',
           to_regclass('compras.ux_compras_empresa_adjudicada_activa') IS NOT NULL),
          ('columna compras.requerimiento_presupuesto.empresa_adjudicada',
           EXISTS (SELECT 1 FROM pg_attribute
                    WHERE attrelid = to_regclass('compras.requerimiento_presupuesto')
                      AND attname = 'empresa_adjudicada' AND NOT attisdropped)),
          ('columna nullable compras.requerimiento_presupuesto.empresa_cuit',
           EXISTS (SELECT 1 FROM pg_attribute
                    WHERE attrelid = to_regclass('compras.requerimiento_presupuesto')
                      AND attname = 'empresa_cuit' AND NOT attisdropped AND NOT attnotnull)),
          ('columna nullable compras.requerimiento_presupuesto.empresa_sucursal',
           EXISTS (SELECT 1 FROM pg_attribute
                    WHERE attrelid = to_regclass('compras.requerimiento_presupuesto')
                      AND attname = 'empresa_sucursal' AND NOT attisdropped AND NOT attnotnull)),
          ('columna compras.requerimiento_presupuesto.descripcion_empresa',
           EXISTS (SELECT 1 FROM pg_attribute
                    WHERE attrelid = to_regclass('compras.requerimiento_presupuesto')
                      AND attname = 'descripcion_empresa' AND NOT attisdropped))
      ) AS requisitos(requisito, instalado)
     WHERE NOT instalado;

    IF v_faltantes IS NOT NULL THEN
        RAISE EXCEPTION
            'No esta completo el contrato SQL previo de cotizaciones y adjudicacion de Empresas.'
            USING DETAIL = v_faltantes,
                  HINT = 'Revisar estos objetos y sus firmas en la base objetivo; no ejecutar compras_schema.sql como reemplazo del catalogo instalado.';
    END IF;
END;
$precondicion$;$sentencia_compras_015$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 03/14: docs/sql/compras/20261006_cotizaciones_identificacion_fiscal_diferida.sql';
        v_paso := 16;
        v_comando := 'DO $constraint_empresa$';
        EXECUTE replace($sentencia_compras_016$

-- El relevamiento de la base del 22/09/2026 no tenia CHECKs en Compras.
-- Adaptar este CHECK solamente en ambientes donde ya este instalado.
-- No crear una constraint nueva tomando compras_schema.sql como catalogo real.
-- El nombre obligatorio se valida tambien en Helper y en la funcion de guardado.
DO $constraint_empresa$
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_constraint
         WHERE conrelid = to_regclass('compras.requerimiento_presupuesto')
           AND conname = 'ck_compras_presupuesto_empresa_datos'
           AND contype = 'c'
    ) THEN
        ALTER TABLE compras.requerimiento_presupuesto
            DROP CONSTRAINT ck_compras_presupuesto_empresa_datos;

        ALTER TABLE compras.requerimiento_presupuesto
            ADD CONSTRAINT ck_compras_presupuesto_empresa_datos CHECK (
                (
                    tipo_documento IN (1, 2)
                    AND empresa_cuit IS NULL
                    AND empresa_sucursal IS NULL
                    AND descripcion_empresa IS NULL
                )
                OR (
                    tipo_documento = 3
                    AND NULLIF(btrim(descripcion_empresa), '') IS NOT NULL
                    AND descripcion_empresa = btrim(descripcion_empresa)
                    AND (
                        (empresa_cuit IS NULL AND empresa_sucursal IS NULL)
                        OR (
                            empresa_cuit IS NOT NULL
                            AND NULLIF(btrim(empresa_cuit), '') IS NOT NULL
                            AND length(empresa_cuit) <= 11
                            AND empresa_cuit = btrim(empresa_cuit)
                            AND (
                                empresa_sucursal IS NULL
                                OR (
                                    NULLIF(btrim(empresa_sucursal), '') IS NOT NULL
                                    AND empresa_sucursal = btrim(empresa_sucursal)
                                )
                            )
                        )
                    )
                )
            );
    END IF;
END;
$constraint_empresa$;$sentencia_compras_016$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 03/14: docs/sql/compras/20261006_cotizaciones_identificacion_fiscal_diferida.sql';
        v_paso := 17;
        v_comando := 'DROP INDEX compras.ux_compras_presupuesto_requerimiento_empresa_activa;';
        EXECUTE replace($sentencia_compras_017$

-- NULL no es una identidad fiscal. No comparar empresas solamente por nombre.
-- El indice mantiene la clave fiscal legacy y cubre CUIT conocido sin sucursal.
DROP INDEX compras.ux_compras_presupuesto_requerimiento_empresa_activa;$sentencia_compras_017$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 03/14: docs/sql/compras/20261006_cotizaciones_identificacion_fiscal_diferida.sql';
        v_paso := 18;
        v_comando := 'CREATE UNIQUE INDEX ux_compras_presupuesto_requerimiento_empresa_activa';
        EXECUTE replace($sentencia_compras_018$
CREATE UNIQUE INDEX ux_compras_presupuesto_requerimiento_empresa_activa
    ON compras.requerimiento_presupuesto (
        id_requerimiento, empresa_cuit, COALESCE(empresa_sucursal, '')
    )
    WHERE baja_fecha IS NULL AND tipo_documento = 3
      AND empresa_cuit IS NOT NULL;$sentencia_compras_018$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 03/14: docs/sql/compras/20261006_cotizaciones_identificacion_fiscal_diferida.sql';
        v_paso := 19;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.registrar_requerimiento_presupuesto(';
        EXECUTE replace($sentencia_compras_019$

CREATE OR REPLACE FUNCTION compras.registrar_requerimiento_presupuesto(
    p_id_requerimiento INTEGER,
    p_tipo_documento SMALLINT,
    p_id_prestador INTEGER,
    p_empresa_cuit VARCHAR,
    p_empresa_sucursal VARCHAR,
    p_descripcion_empresa VARCHAR,
    p_dl_group_id BIGINT,
    p_dl_folder_id BIGINT,
    p_dl_file_entry_id BIGINT,
    p_dl_file_uuid VARCHAR,
    p_nombre_original VARCHAR,
    p_nombre_persistido VARCHAR,
    p_titulo VARCHAR,
    p_descripcion_prestador VARCHAR,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
    v_id INTEGER;
    v_estado_requerimiento INTEGER;
    v_sector_descripcion VARCHAR(200);
    v_usuario VARCHAR(100);
    v_cuit VARCHAR;
    v_sucursal VARCHAR;
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION 'El requerimiento informado no es valido.';
    END IF;

    IF p_tipo_documento IS NULL OR p_tipo_documento <> 3 THEN
        RAISE EXCEPTION
            'El tipo documental no corresponde a una cotizacion de Empresa.';
    END IF;

    IF p_id_prestador IS NOT NULL
       OR NULLIF(btrim(p_descripcion_prestador), '') IS NOT NULL THEN
        RAISE EXCEPTION
            'Una cotizacion de Empresa no puede asociarse a un prestador.';
    END IF;

    IF NULLIF(btrim(p_descripcion_empresa), '') IS NULL
       OR length(btrim(p_descripcion_empresa)) > 200 THEN
        RAISE EXCEPTION 'Debe informar el nombre de la Empresa de la cotizacion.';
    END IF;

    v_cuit := NULLIF(btrim(p_empresa_cuit), '');
    v_sucursal := NULLIF(btrim(p_empresa_sucursal), '');
    IF v_cuit IS NOT NULL AND v_cuit !~ '^[0-9]{11}$' THEN
        RAISE EXCEPTION 'El CUIT de la Empresa de la cotizacion no es valido.';
    END IF;
    IF v_sucursal IS NOT NULL
       AND (v_cuit IS NULL OR length(v_sucursal) > 6) THEN
        RAISE EXCEPTION 'La sucursal requiere un CUIT valido de Empresa.';
    END IF;

    IF p_dl_group_id IS NULL OR p_dl_group_id <= 0
       OR p_dl_folder_id IS NULL OR p_dl_folder_id < 0
       OR p_dl_file_entry_id IS NULL OR p_dl_file_entry_id <= 0 THEN
        RAISE EXCEPTION 'La identidad del documento de cotizacion no es valida.';
    END IF;

    v_usuario := COALESCE(NULLIF(btrim(p_usuario), ''), 'sistema');

    SELECT r.estado, sr.descripcion
      INTO v_estado_requerimiento, v_sector_descripcion
      FROM compras.requerimiento r
      JOIN compras.sector_requerimiento sr ON sr.id_sector = r.id_sector
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE OF r;

    IF NOT FOUND OR v_estado_requerimiento <> 1
       OR compras.normalizar_sector(v_sector_descripcion)
            NOT IN ('RRHH', 'SISTEMAS') THEN
        RAISE EXCEPTION
            'La cotizacion de Empresa requiere un requerimiento activo de RRHH o SISTEMAS en estado PENDIENTE.';
    END IF;

    -- La sucursal identifica una vinculacion al padron; el CUIT solo puede
    -- informarse sin dar de alta ni seleccionar una Empresa maestra.
    IF v_sucursal IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM compras.buscar_empresas_cotizacion(
                v_cuit, NULL, v_sucursal, 1)
    ) THEN
        RAISE EXCEPTION 'La Empresa no existe o no esta activa en el padron.';
    END IF;

    IF v_cuit IS NOT NULL AND EXISTS (
        SELECT 1 FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 3 AND rp.baja_fecha IS NULL
           AND rp.empresa_cuit = v_cuit
           AND COALESCE(rp.empresa_sucursal, '') = COALESCE(v_sucursal, '')
    ) THEN
        RAISE EXCEPTION
            'La Empresa ya tiene una cotizacion activa para este requerimiento.';
    END IF;

    INSERT INTO compras.requerimiento_presupuesto (
        id_requerimiento, tipo_documento, fecha_documento, id_prestador,
        empresa_cuit, empresa_sucursal, descripcion_empresa,
        dl_group_id, dl_folder_id, dl_file_entry_id, dl_file_uuid,
        nombre_original, nombre_persistido, titulo, descripcion_prestador, alta_usr
    ) VALUES (
        p_id_requerimiento, 3, NULL, NULL,
        v_cuit, v_sucursal, btrim(p_descripcion_empresa),
        p_dl_group_id, p_dl_folder_id, p_dl_file_entry_id,
        NULLIF(btrim(p_dl_file_uuid), ''), btrim(p_nombre_original),
        btrim(p_nombre_persistido), btrim(p_titulo), NULL, v_usuario
    )
    RETURNING id_requerimiento_presupuesto INTO v_id;

    RETURN v_id;
END;
$func$
LANGUAGE plpgsql;$sentencia_compras_019$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 03/14: docs/sql/compras/20261006_cotizaciones_identificacion_fiscal_diferida.sql';
        v_paso := 20;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.guardar_empresa_adjudicada(';
        EXECUTE replace($sentencia_compras_020$

-- Sobrecarga por la identidad de la cotizacion, independiente de su CUIT.
-- La firma anterior por CUIT+sucursal se conserva para sus callers legacy.
CREATE OR REPLACE FUNCTION compras.guardar_empresa_adjudicada(
    p_id_requerimiento INTEGER,
    p_id_requerimiento_presupuesto INTEGER,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
    v_estado INTEGER;
    v_sector VARCHAR(200);
    v_id_presupuesto INTEGER;
BEGIN
    IF p_id_requerimiento_presupuesto IS NOT NULL
       AND p_id_requerimiento_presupuesto < 0 THEN
        RAISE EXCEPTION 'La cotizacion de la Empresa adjudicada no es valida.';
    END IF;

    SELECT r.estado, sr.descripcion
      INTO v_estado, v_sector
      FROM compras.requerimiento r
      JOIN compras.sector_requerimiento sr ON sr.id_sector = r.id_sector
       AND sr.activo = TRUE AND sr.baja_fecha IS NULL
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE OF r;

    IF NOT FOUND OR v_estado <> 1
       OR compras.normalizar_sector(v_sector) NOT IN ('RRHH', 'SISTEMAS') THEN
        RAISE EXCEPTION
            'La adjudicacion de Empresa requiere un requerimiento activo de RRHH o SISTEMAS PENDIENTE.';
    END IF;

    IF COALESCE(p_id_requerimiento_presupuesto, 0) > 0 THEN
        SELECT rp.id_requerimiento_presupuesto INTO v_id_presupuesto
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento_presupuesto = p_id_requerimiento_presupuesto
           AND rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 3 AND rp.baja_fecha IS NULL
           AND NULLIF(btrim(rp.descripcion_empresa), '') IS NOT NULL
         FOR UPDATE;
        IF NOT FOUND THEN
            RAISE EXCEPTION
                'La Empresa adjudicada debe tener una cotizacion activa en este requerimiento.';
        END IF;
    END IF;

    -- Limpiar primero evita una violacion transitoria del indice unico vigente.
    UPDATE compras.requerimiento_presupuesto
       SET empresa_adjudicada = FALSE
     WHERE id_requerimiento = p_id_requerimiento
       AND tipo_documento = 3 AND empresa_adjudicada;

    IF v_id_presupuesto IS NOT NULL THEN
        UPDATE compras.requerimiento_presupuesto
           SET empresa_adjudicada = TRUE
         WHERE id_requerimiento_presupuesto = v_id_presupuesto;
    END IF;

    UPDATE compras.requerimiento
       SET modi_fecha = now(), modi_usr = compras.normalizar_usuario(p_usuario)
     WHERE id_requerimiento = p_id_requerimiento;
    RETURN COALESCE(v_id_presupuesto, 0);
END;
$func$
LANGUAGE plpgsql;$sentencia_compras_020$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 03/14: docs/sql/compras/20261006_cotizaciones_identificacion_fiscal_diferida.sql';
        v_paso := 21;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.completar_empresa_cotizacion(';
        EXECUTE replace($sentencia_compras_021$

CREATE OR REPLACE FUNCTION compras.completar_empresa_cotizacion(
    p_id_requerimiento INTEGER,
    p_id_requerimiento_presupuesto INTEGER,
    p_empresa_cuit VARCHAR,
    p_empresa_sucursal VARCHAR,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
    v_estado INTEGER;
    v_sector VARCHAR(200);
    v_cuit VARCHAR;
    v_sucursal VARCHAR;
    v_cuit_actual VARCHAR;
    v_sucursal_actual VARCHAR;
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0
       OR p_id_requerimiento_presupuesto IS NULL
       OR p_id_requerimiento_presupuesto <= 0 THEN
        RAISE EXCEPTION 'Debe informar la cotizacion de Empresa del requerimiento.';
    END IF;

    v_cuit := NULLIF(btrim(p_empresa_cuit), '');
    v_sucursal := NULLIF(btrim(p_empresa_sucursal), '');
    IF v_cuit IS NULL OR v_cuit !~ '^[0-9]{11}$' THEN
        RAISE EXCEPTION 'Debe informar un CUIT valido para completar la Empresa.';
    END IF;
    IF v_sucursal IS NOT NULL AND length(v_sucursal) > 6 THEN
        RAISE EXCEPTION 'La sucursal de la Empresa no es valida.';
    END IF;

    -- Mismo orden de bloqueo que alta/adjudicacion: requerimiento y documento.
    SELECT r.estado, sr.descripcion
      INTO v_estado, v_sector
      FROM compras.requerimiento r
      JOIN compras.sector_requerimiento sr ON sr.id_sector = r.id_sector
       AND sr.activo = TRUE AND sr.baja_fecha IS NULL
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE OF r;

    IF NOT FOUND OR v_estado NOT IN (1, 5)
       OR compras.normalizar_sector(v_sector) NOT IN ('RRHH', 'SISTEMAS') THEN
        RAISE EXCEPTION
            'Solo puede completar el CUIT en RRHH o SISTEMAS PENDIENTE o en ORDEN_COMPRA.';
    END IF;

    SELECT rp.empresa_cuit, rp.empresa_sucursal
      INTO v_cuit_actual, v_sucursal_actual
      FROM compras.requerimiento_presupuesto rp
     WHERE rp.id_requerimiento_presupuesto = p_id_requerimiento_presupuesto
       AND rp.id_requerimiento = p_id_requerimiento
       AND rp.tipo_documento = 3 AND rp.baja_fecha IS NULL
       AND NULLIF(btrim(rp.descripcion_empresa), '') IS NOT NULL
     FOR UPDATE;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'No se encontro la cotizacion activa de Empresa.';
    END IF;

    IF NULLIF(btrim(v_cuit_actual), '') IS NOT NULL
       AND btrim(v_cuit_actual) <> v_cuit THEN
        RAISE EXCEPTION 'El CUIT de la cotizacion ya fue informado y no puede reemplazarse.';
    END IF;
    -- Completar no elimina una vinculacion fiscal ya informada.
    v_sucursal := COALESCE(v_sucursal, NULLIF(btrim(v_sucursal_actual), ''));

    IF v_sucursal IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM compras.buscar_empresas_cotizacion(
                v_cuit, NULL, v_sucursal, 1)
    ) THEN
        RAISE EXCEPTION 'La Empresa no existe o no esta activa en el padron.';
    END IF;

    IF EXISTS (
        SELECT 1 FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 3 AND rp.baja_fecha IS NULL
           AND rp.id_requerimiento_presupuesto <> p_id_requerimiento_presupuesto
           AND rp.empresa_cuit = v_cuit
           AND COALESCE(rp.empresa_sucursal, '') = COALESCE(v_sucursal, '')
    ) THEN
        RAISE EXCEPTION
            'La Empresa ya tiene otra cotizacion activa para este requerimiento.';
    END IF;

    UPDATE compras.requerimiento_presupuesto
       SET empresa_cuit = v_cuit, empresa_sucursal = v_sucursal
     WHERE id_requerimiento_presupuesto = p_id_requerimiento_presupuesto;

    -- El documento no tiene columnas modi_fecha/modi_usr; el requerimiento si.
    UPDATE compras.requerimiento
       SET modi_fecha = now(), modi_usr = compras.normalizar_usuario(p_usuario)
     WHERE id_requerimiento = p_id_requerimiento;

    RETURN p_id_requerimiento_presupuesto;
END;
$func$
LANGUAGE plpgsql;$sentencia_compras_021$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 03/14: docs/sql/compras/20261006_cotizaciones_identificacion_fiscal_diferida.sql';
        v_paso := 22;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.confirmar_orden_compra_requerimiento(';
        EXECUTE replace($sentencia_compras_022$
CREATE OR REPLACE FUNCTION compras.confirmar_orden_compra_requerimiento(
    p_id_requerimiento INTEGER,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
    v_estado INTEGER;
    v_sector_descripcion VARCHAR(120);
BEGIN
    IF p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN

        RAISE EXCEPTION
            'Debe informar el requerimiento de compra.';
    END IF;

    SELECT
        r.estado,
        sr.descripcion
    INTO
        v_estado,
        v_sector_descripcion
    FROM compras.requerimiento r
    JOIN compras.sector_requerimiento sr
      ON sr.id_sector = r.id_sector
     AND sr.activo = TRUE
     AND sr.baja_fecha IS NULL
    WHERE r.id_requerimiento = p_id_requerimiento
      AND r.baja_fecha IS NULL
    FOR UPDATE OF r;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No se encontro el requerimiento activo.';
    END IF;

    IF v_estado = 5 THEN
        RETURN 5;
    END IF;

    IF v_estado <> 1 THEN
        RAISE EXCEPTION
            'El requerimiento solo puede pasar a ORDEN_COMPRA desde PENDIENTE.';
    END IF;

    IF compras.normalizar_sector(
            v_sector_descripcion
       ) NOT IN ('RRHH', 'SISTEMAS') THEN

        RAISE EXCEPTION
            'Solo los requerimientos de RRHH o SISTEMAS pueden pasar directamente a ORDEN_COMPRA.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM compras.requerimiento_detalle d
         WHERE d.id_requerimiento = p_id_requerimiento
           AND d.baja_fecha IS NULL
    ) THEN

        RAISE EXCEPTION
            'Debe existir al menos un detalle antes de pasar a ORDEN_COMPRA.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 3
           AND rp.baja_fecha IS NULL
    ) THEN

        RAISE EXCEPTION
            'Debe existir al menos una cotizacion de Empresa activa antes de pasar a ORDEN_COMPRA.';
    END IF;

    IF (SELECT count(*)
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 3
           AND rp.baja_fecha IS NULL
           AND rp.empresa_adjudicada) <> 1 THEN

        RAISE EXCEPTION
            'Debe seleccionar exactamente una Empresa adjudicada con cotizacion activa.';
    END IF;

    IF EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 3
           AND rp.baja_fecha IS NULL
           AND rp.empresa_adjudicada
           AND (
               NULLIF(btrim(rp.descripcion_empresa), '') IS NULL
               OR (rp.empresa_cuit IS NOT NULL
                   AND (NULLIF(btrim(rp.empresa_cuit), '') IS NULL
                        OR length(rp.empresa_cuit) > 11
                        OR rp.empresa_cuit <> btrim(rp.empresa_cuit)))
               OR (rp.empresa_sucursal IS NOT NULL
                   AND (rp.empresa_cuit IS NULL OR NOT EXISTS (
                       SELECT 1 FROM compras.buscar_empresas_cotizacion(
                           rp.empresa_cuit, NULL, rp.empresa_sucursal, 1)
                   )))
           )
    ) THEN
        RAISE EXCEPTION
            'La Empresa adjudicada debe tener nombre y una identidad fiscal valida cuando este informada.';
    END IF;

    UPDATE compras.requerimiento
       SET estado = 5,
           modi_fecha = now(),
           modi_usr = compras.normalizar_usuario(p_usuario)
     WHERE id_requerimiento = p_id_requerimiento
       AND estado = 1
       AND baja_fecha IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'El requerimiento fue modificado por otro proceso.';
    END IF;

    RETURN 5;
END;
$func$
LANGUAGE plpgsql;$sentencia_compras_022$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 04/14: docs/sql/compras/20261007_edicion_empresa_cotizacion.sql';
        v_paso := 23;
        v_comando := 'DO $precondicion$';
        EXECUTE replace($sentencia_compras_023$

-- ===== BLOQUE 04/14: docs/sql/compras/20261007_edicion_empresa_cotizacion.sql =====
-- SHA256 original (bytes): 543a3471a5666f08a3cf34789dbf14d0e5d7d88f5d53fbcc24d3abf593b8f653
-- Solo se retiraron BEGIN/COMMIT exteriores; cuerpo funcional conservado.

-- Edicion legacy de Empresa en una cotizacion de RRHH/SISTEMAS.
-- Referencia: view_prestador_externo.jsp (nombre obligatorio y CUIT opcional).
-- Requiere el contrato de identificacion fiscal diferida del 06/10/2026.
-- PostgreSQL 9.6. ISO-8859-1 sin BOM. No aplicado por Codex.
-- Actualiza el mismo presupuesto sin modificar PDF, detalle ni adjudicacion.
-- Conserva completar_empresa_cotizacion y sus callers anteriores.


DO $precondicion$
DECLARE
    v_faltantes TEXT;
BEGIN
    SELECT string_agg(requisito, E'\n' ORDER BY requisito)
      INTO v_faltantes
      FROM (VALUES
          ('funcion compras.normalizar_sector(character varying)',
           to_regprocedure('compras.normalizar_sector(character varying)') IS NOT NULL),
          ('funcion compras.normalizar_usuario(character varying)',
           to_regprocedure('compras.normalizar_usuario(character varying)') IS NOT NULL),
          ('funcion compras.buscar_empresas_cotizacion(character varying,character varying,character varying,integer)',
           to_regprocedure('compras.buscar_empresas_cotizacion(character varying,character varying,character varying,integer)') IS NOT NULL),
          ('indice compras.ux_compras_presupuesto_requerimiento_empresa_activa',
           to_regclass('compras.ux_compras_presupuesto_requerimiento_empresa_activa') IS NOT NULL),
          ('columna nullable compras.requerimiento_presupuesto.empresa_cuit',
           EXISTS (SELECT 1 FROM pg_attribute
                    WHERE attrelid = to_regclass('compras.requerimiento_presupuesto')
                      AND attname = 'empresa_cuit' AND NOT attisdropped AND NOT attnotnull)),
          ('columna nullable compras.requerimiento_presupuesto.empresa_sucursal',
           EXISTS (SELECT 1 FROM pg_attribute
                    WHERE attrelid = to_regclass('compras.requerimiento_presupuesto')
                      AND attname = 'empresa_sucursal' AND NOT attisdropped AND NOT attnotnull)),
          ('columna compras.requerimiento_presupuesto.descripcion_empresa',
           EXISTS (SELECT 1 FROM pg_attribute
                    WHERE attrelid = to_regclass('compras.requerimiento_presupuesto')
                      AND attname = 'descripcion_empresa' AND NOT attisdropped))
      ) AS requisitos(requisito, instalado)
     WHERE NOT instalado;

    IF v_faltantes IS NOT NULL THEN
        RAISE EXCEPTION 'Faltan objetos SQL requeridos para editar la Empresa de la cotizacion.'
            USING DETAIL = v_faltantes,
                  HINT = 'Revisar estos objetos y sus firmas en la base objetivo; este script complementa 20261006_cotizaciones_identificacion_fiscal_diferida.sql.';
    END IF;
END;
$precondicion$;$sentencia_compras_023$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 04/14: docs/sql/compras/20261007_edicion_empresa_cotizacion.sql';
        v_paso := 24;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.editar_empresa_cotizacion(';
        EXECUTE replace($sentencia_compras_024$

CREATE OR REPLACE FUNCTION compras.editar_empresa_cotizacion(
    p_id_requerimiento INTEGER,
    p_id_requerimiento_presupuesto INTEGER,
    p_descripcion_empresa VARCHAR,
    p_empresa_cuit VARCHAR,
    p_empresa_sucursal VARCHAR,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
    v_estado INTEGER;
    v_sector VARCHAR(200);
    v_cuit VARCHAR;
    v_sucursal VARCHAR;
    v_cuit_actual VARCHAR;
    v_sucursal_actual VARCHAR;
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0
       OR p_id_requerimiento_presupuesto IS NULL
       OR p_id_requerimiento_presupuesto <= 0 THEN
        RAISE EXCEPTION 'Debe informar la cotizacion de Empresa del requerimiento.';
    END IF;

    IF NULLIF(btrim(p_descripcion_empresa), '') IS NULL
       OR length(btrim(p_descripcion_empresa)) > 200 THEN
        RAISE EXCEPTION 'Debe informar el nombre de la Empresa (hasta 200 caracteres).';
    END IF;

    v_cuit := NULLIF(btrim(p_empresa_cuit), '');
    v_sucursal := NULLIF(btrim(p_empresa_sucursal), '');
    IF v_cuit IS NOT NULL AND v_cuit !~ '^[0-9]{11}$' THEN
        RAISE EXCEPTION 'El CUIT informado de la Empresa no es valido.';
    END IF;
    IF v_sucursal IS NOT NULL AND length(v_sucursal) > 6 THEN
        RAISE EXCEPTION 'La sucursal de la Empresa no es valida.';
    END IF;

    -- Mismo orden de bloqueo que alta/adjudicacion: requerimiento y documento.
    SELECT r.estado, sr.descripcion
      INTO v_estado, v_sector
      FROM compras.requerimiento r
      JOIN compras.sector_requerimiento sr ON sr.id_sector = r.id_sector
       AND sr.activo = TRUE AND sr.baja_fecha IS NULL
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE OF r;

    IF NOT FOUND OR v_estado NOT IN (1, 5)
       OR compras.normalizar_sector(v_sector) NOT IN ('RRHH', 'SISTEMAS') THEN
        RAISE EXCEPTION
            'Solo puede editar la Empresa en RRHH o SISTEMAS PENDIENTE o en ORDEN_COMPRA.';
    END IF;

    SELECT rp.empresa_cuit, rp.empresa_sucursal
      INTO v_cuit_actual, v_sucursal_actual
      FROM compras.requerimiento_presupuesto rp
     WHERE rp.id_requerimiento_presupuesto = p_id_requerimiento_presupuesto
       AND rp.id_requerimiento = p_id_requerimiento
       AND rp.tipo_documento = 3 AND rp.baja_fecha IS NULL
     FOR UPDATE;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'No se encontro la cotizacion activa de Empresa.';
    END IF;

    IF NULLIF(btrim(v_cuit_actual), '') IS NOT NULL
       AND (v_cuit IS NULL OR btrim(v_cuit_actual) <> v_cuit) THEN
        RAISE EXCEPTION 'El CUIT de la cotizacion ya fue informado y no puede reemplazarse.';
    END IF;
    -- Completar no elimina una vinculacion fiscal ya informada.
    v_sucursal := COALESCE(v_sucursal, NULLIF(btrim(v_sucursal_actual), ''));

    IF v_sucursal IS NOT NULL AND v_cuit IS NULL THEN
        RAISE EXCEPTION 'La sucursal requiere un CUIT valido de Empresa.';
    END IF;

    IF v_sucursal IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM compras.buscar_empresas_cotizacion(
                v_cuit, NULL, v_sucursal, 1)
    ) THEN
        RAISE EXCEPTION 'La Empresa no existe o no esta activa en el padron.';
    END IF;

    IF v_cuit IS NOT NULL AND EXISTS (
        SELECT 1 FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 3 AND rp.baja_fecha IS NULL
           AND rp.id_requerimiento_presupuesto <> p_id_requerimiento_presupuesto
           AND rp.empresa_cuit = v_cuit
           AND COALESCE(rp.empresa_sucursal, '') = COALESCE(v_sucursal, '')
    ) THEN
        RAISE EXCEPTION
            'La Empresa ya tiene otra cotizacion activa para este requerimiento.';
    END IF;

    UPDATE compras.requerimiento_presupuesto
       SET descripcion_empresa = btrim(p_descripcion_empresa),
           empresa_cuit = v_cuit, empresa_sucursal = v_sucursal
     WHERE id_requerimiento_presupuesto = p_id_requerimiento_presupuesto;

    -- El documento no tiene columnas modi_fecha/modi_usr; el requerimiento si.
    UPDATE compras.requerimiento
       SET modi_fecha = now(), modi_usr = compras.normalizar_usuario(p_usuario)
     WHERE id_requerimiento = p_id_requerimiento;

    RETURN p_id_requerimiento_presupuesto;
END;
$func$
LANGUAGE plpgsql;$sentencia_compras_024$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 05/14: docs/sql/20261008_observacion_interna_requerimiento_compras.sql';
        v_paso := 25;
        v_comando := 'DO $block$';
        EXECUTE replace($sentencia_compras_025$

-- ===== BLOQUE 05/14: docs/sql/20261008_observacion_interna_requerimiento_compras.sql =====
-- SHA256 original (bytes): 9acd0a0f2987059940355f8ff94be9d5b44caef7f8356ce82a818d56eb9cc5d2
-- Solo se retiraron BEGIN/COMMIT exteriores; cuerpo funcional conservado.

-- Incorporar observacion_interna sin reemplazar el guardado vigente.
-- Ejecutar antes de instalar el codigo que utiliza la variante de 24 argumentos.

DO $block$
BEGIN
    IF NOT EXISTS (
        SELECT 1
          FROM information_schema.columns
         WHERE table_schema = 'compras'
           AND table_name = 'requerimiento'
           AND column_name = 'observacion_interna'
    ) THEN
        ALTER TABLE compras.requerimiento
            ADD COLUMN observacion_interna TEXT;
    END IF;
END;
$block$;$sentencia_compras_025$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 05/14: docs/sql/20261008_observacion_interna_requerimiento_compras.sql';
        v_paso := 26;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.guardar_requerimiento(';
        EXECUTE replace($sentencia_compras_026$

CREATE OR REPLACE FUNCTION compras.guardar_requerimiento(
    p_id INTEGER,
    p_afiliado_cuil_titular VARCHAR,
    p_afiliado_int INTEGER,
    p_afiliado_id_ospim INTEGER,
    p_afiliado_nombre VARCHAR,
    p_afiliado_apellido VARCHAR,
    p_afiliado_documento_tipo VARCHAR,
    p_afiliado_documento_nro VARCHAR,
    p_afiliado_direccion VARCHAR,
    p_afiliado_localidad VARCHAR,
    p_afiliado_provincia VARCHAR,
    p_afiliado_celular VARCHAR,
    p_afiliado_telefono VARCHAR,
    p_afiliado_email VARCHAR,
    p_id_sector INTEGER,
    p_cargo_ospim INTEGER,
    p_cargo_tercerizadora INTEGER,
    p_id_tercerizadora VARCHAR,
    p_recupero BOOLEAN,
    p_surge BOOLEAN,
    p_legales BOOLEAN,
    p_observaciones TEXT,
    p_usuario VARCHAR,
    p_observacion_interna TEXT
)
RETURNS INTEGER
AS $func$
DECLARE
    v_id INTEGER;
BEGIN
    v_id := compras.guardar_requerimiento(
        p_id, p_afiliado_cuil_titular, p_afiliado_int,
        p_afiliado_id_ospim, p_afiliado_nombre, p_afiliado_apellido,
        p_afiliado_documento_tipo, p_afiliado_documento_nro,
        p_afiliado_direccion, p_afiliado_localidad, p_afiliado_provincia,
        p_afiliado_celular, p_afiliado_telefono, p_afiliado_email,
        p_id_sector, p_cargo_ospim, p_cargo_tercerizadora,
        p_id_tercerizadora, p_recupero, p_surge, p_legales,
        p_observaciones, p_usuario
    );

    UPDATE compras.requerimiento
       SET observacion_interna = NULLIF(btrim(p_observacion_interna), '')
     WHERE id_requerimiento = v_id
       AND estado = 1
       AND baja_fecha IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'La observacion interna solo puede modificarse en estado PENDIENTE.';
    END IF;

    RETURN v_id;
END;
$func$
LANGUAGE plpgsql;$sentencia_compras_026$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 06/14: docs/sql/compras/20261013_sector_nomenclador.sql';
        v_paso := 27;
        v_comando := 'DO $precondicion$';
        EXECUTE replace($sentencia_compras_027$

-- ===== BLOQUE 06/14: docs/sql/compras/20261013_sector_nomenclador.sql =====
-- SHA256 original (bytes): 6c96ea89a27b6eeee6889640ee584dc56d9eb1a3dfe4230438f1a45267274c21
-- Solo se retiraron BEGIN/COMMIT exteriores; cuerpo funcional conservado.

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
$precondicion$;$sentencia_compras_027$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 06/14: docs/sql/compras/20261013_sector_nomenclador.sql';
        v_paso := 28;
        v_comando := 'DO $tabla_y_carga_inicial$';
        EXECUTE replace($sentencia_compras_028$

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
$tabla_y_carga_inicial$;$sentencia_compras_028$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 06/14: docs/sql/compras/20261013_sector_nomenclador.sql';
        v_paso := 29;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.resolver_sector_nomenclador(';
        EXECUTE replace($sentencia_compras_029$

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
LANGUAGE plpgsql STABLE;$sentencia_compras_029$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 06/14: docs/sql/compras/20261013_sector_nomenclador.sql';
        v_paso := 30;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.buscar_nomencladores(';
        EXECUTE replace($sentencia_compras_030$

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
LANGUAGE plpgsql STABLE;$sentencia_compras_030$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 06/14: docs/sql/compras/20261013_sector_nomenclador.sql';
        v_paso := 31;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.validar_nomenclador_configurado(';
        EXECUTE replace($sentencia_compras_031$

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
LANGUAGE plpgsql STABLE;$sentencia_compras_031$, E'\r\n', E'\n');

        -- OMITIDO PASO 32: Sobrecarga interna guardar_requerimiento_detalle de14argumentos.

        -- OMITIDO PASO 33: Conservar guardar_requerimiento_detalle de13argumentos; su reemplazo llama a la sobrecarga omitida.

        -- OMITIDO PASO 34: Conservar guardar_requerimiento_detalle_clasificado; su reemplazo llama a la sobrecarga omitida.

        v_bloque := 'BLOQUE 07/14: docs/sql/compras/20261013_corregir_item_antes_rp.sql';
        v_paso := 35;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.corregir_item_requerimiento(';
        EXECUTE replace($sentencia_compras_035$

-- ===== BLOQUE 07/14: docs/sql/compras/20261013_corregir_item_antes_rp.sql =====
-- SHA256 original (bytes): 675ea6ab119d5fbe7253057e4cbd9eaa2db0c2e0324ad0b5194319bd6ca7da7b
-- Solo se retiraron BEGIN/COMMIT exteriores; cuerpo funcional conservado.

-- Compras: corregir identidad del mismo item antes de iniciar un RP.
-- Estados 1/2/3; cualquier relacion RP (RESERVADO/VINCULADO/ERROR) bloquea.
-- Preserva cantidad, observaciones, importes, adjudicacion, PDFs y IDs.
-- Referencia legacy: EditarLiquidacionServiceImpl.actualizaLiquidacionPrestacionEntry.
-- Catalogo QA consultado solo lectura el 13/10/2026, PostgreSQL 9.6.15.
-- Instalar antes del Java/JSP; no aplicado por Codex.


CREATE OR REPLACE FUNCTION compras.corregir_item_requerimiento(
    p_id_requerimiento INTEGER,
    p_id_detalle INTEGER,
    p_id_prestacion INTEGER,
    p_id_tipo_nomenclador INTEGER,
    p_codigo_nomenclador VARCHAR,
    p_descripcion_nomenclador VARCHAR,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
    v_estado INTEGER;
    v_id_sector INTEGER;
    v_detalle compras.requerimiento_detalle%ROWTYPE;
    v_nomenclador autorizaciones.nomenclador%ROWTYPE;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0
       OR p_id_detalle IS NULL OR p_id_detalle <= 0 THEN
        RAISE EXCEPTION 'Debe informar el requerimiento y el item a corregir.';
    END IF;

    PERFORM compras.bloquear_requerimiento_reclamo_prestacional(p_id_requerimiento);
    SELECT r.estado, r.id_sector INTO v_estado, v_id_sector
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = p_id_requerimiento AND r.baja_fecha IS NULL
     FOR UPDATE;
    IF NOT FOUND OR v_estado NOT IN (1, 2, 3) THEN
        RAISE EXCEPTION 'El item no puede corregirse en el estado actual. Actualice la pantalla.';
    END IF;
    IF EXISTS (
        SELECT 1 FROM compras.requerimiento_reclamo_prestacional rr
        WHERE rr.id_requerimiento = p_id_requerimiento
    ) THEN
        RAISE EXCEPTION 'El requerimiento ya tiene una creacion de RP iniciada. Actualice la pantalla.';
    END IF;

    SELECT d.* INTO v_detalle FROM compras.requerimiento_detalle d
     WHERE d.id_detalle = p_id_detalle
       AND d.id_requerimiento = p_id_requerimiento AND d.baja_fecha IS NULL
     FOR UPDATE;
    IF NOT FOUND OR v_detalle.tipo_item NOT IN ('NOMENCLADOR', 'MEDICAMENTO') THEN
        RAISE EXCEPTION 'No se encontro un item activo de medicamento o prestacion para corregir.';
    END IF;

    PERFORM compras.validar_nomenclador_configurado(
        v_id_sector, v_detalle.id_tipo_prestacion,
        p_id_prestacion, p_id_tipo_nomenclador);
    SELECT n.* INTO v_nomenclador FROM autorizaciones.nomenclador n
     WHERE n.id_prestacion = p_id_prestacion AND n.baja_fecha IS NULL;
    IF NOT FOUND
       OR v_nomenclador.id_tipo_nomenclador IS DISTINCT FROM p_id_tipo_nomenclador
       OR btrim(v_nomenclador.codigo) IS DISTINCT FROM btrim(p_codigo_nomenclador)
       OR btrim(v_nomenclador.descripcion) IS DISTINCT FROM btrim(p_descripcion_nomenclador) THEN
        RAISE EXCEPTION 'La seleccion no coincide con el nomenclador vigente. Actualice la pantalla.';
    END IF;
    IF EXISTS (
        SELECT 1 FROM compras.requerimiento_detalle d
        WHERE d.id_requerimiento = p_id_requerimiento AND d.baja_fecha IS NULL
          AND d.id_detalle <> p_id_detalle AND d.id_prestacion = p_id_prestacion
    ) THEN
        RAISE EXCEPTION 'El requerimiento ya contiene la prestacion seleccionada.';
    END IF;

    IF v_detalle.tipo_item = 'NOMENCLADOR'
       AND v_detalle.id_prestacion = p_id_prestacion
       AND v_detalle.id_tipo_nomenclador = p_id_tipo_nomenclador
       AND btrim(v_detalle.codigo_nomenclador) = btrim(v_nomenclador.codigo)
       AND btrim(v_detalle.descripcion_nomenclador) = btrim(v_nomenclador.descripcion) THEN
        RETURN p_id_detalle;
    END IF;

    PERFORM 1 FROM compras.requerimiento_comparativa c
     WHERE c.id_requerimiento = p_id_requerimiento
     ORDER BY c.id_comparativa FOR UPDATE;
    PERFORM 1 FROM compras.requerimiento_comparativa_detalle cd
      JOIN compras.requerimiento_comparativa c ON c.id_comparativa = cd.id_comparativa
     WHERE c.id_requerimiento = p_id_requerimiento
     ORDER BY cd.id_detalle FOR UPDATE OF cd;

    IF v_detalle.id_prestacion IS DISTINCT FROM p_id_prestacion THEN
        IF EXISTS (
            SELECT 1 FROM compras.requerimiento_comparativa_detalle cd
            JOIN compras.requerimiento_comparativa c ON c.id_comparativa = cd.id_comparativa
            WHERE c.id_requerimiento = p_id_requerimiento
              AND cd.id_prestacion = p_id_prestacion
        ) THEN
            RAISE EXCEPTION 'La comparativa ya contiene la prestacion de destino. No se modificaron importes.';
        END IF;
        IF EXISTS (
            SELECT 1 FROM compras.requerimiento_detalle d
            WHERE d.id_requerimiento = p_id_requerimiento AND d.baja_fecha IS NULL
              AND d.id_detalle <> p_id_detalle AND d.id_prestacion = v_detalle.id_prestacion
        ) AND EXISTS (
            SELECT 1 FROM compras.requerimiento_comparativa_detalle cd
            JOIN compras.requerimiento_comparativa c ON c.id_comparativa = cd.id_comparativa
            WHERE c.id_requerimiento = p_id_requerimiento
              AND cd.id_prestacion = v_detalle.id_prestacion
        ) THEN
            RAISE EXCEPTION 'La prestacion anterior esta compartida. No se pueden reasignar sus importes.';
        END IF;
    END IF;

    v_usuario := compras.normalizar_usuario(p_usuario);
    UPDATE compras.requerimiento_comparativa_detalle cd
       SET id_prestacion = p_id_prestacion, modi_fecha = now(), modi_usr = v_usuario
      FROM compras.requerimiento_comparativa c
     WHERE c.id_comparativa = cd.id_comparativa
       AND c.id_requerimiento = p_id_requerimiento
       AND cd.id_prestacion = v_detalle.id_prestacion
       AND cd.id_prestacion IS DISTINCT FROM p_id_prestacion;

    UPDATE compras.requerimiento_detalle
       SET tipo_item = 'NOMENCLADOR', id_prestacion = p_id_prestacion,
           id_tipo_nomenclador = v_nomenclador.id_tipo_nomenclador,
           codigo_nomenclador = btrim(v_nomenclador.codigo),
           descripcion_nomenclador = btrim(v_nomenclador.descripcion),
           id_medicamento = NULL, troquel = NULL, nombre_medicamento = NULL,
           modi_fecha = now(), modi_usr = v_usuario
     WHERE id_detalle = p_id_detalle AND id_requerimiento = p_id_requerimiento
       AND baja_fecha IS NULL;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'El item ya no esta disponible. Actualice la pantalla.';
    END IF;
    IF v_detalle.id_prestador IS NOT NULL
       AND NOT compras.es_prestador_compatible_cotizacion(
           p_id_requerimiento, v_detalle.id_prestador) THEN
        RAISE EXCEPTION 'El prestador adjudicado no es compatible con la nueva prestacion. No se modificaron importes.';
    END IF;
    RETURN p_id_detalle;
END;
$func$
LANGUAGE plpgsql;$sentencia_compras_035$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 07/14: docs/sql/compras/20261013_corregir_item_antes_rp.sql';
        v_paso := 36;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.bloquear_guardado_comparativa(';
        EXECUTE replace($sentencia_compras_036$

CREATE OR REPLACE FUNCTION compras.bloquear_guardado_comparativa(
    p_id_requerimiento INTEGER,
    p_prestaciones_esperadas VARCHAR
)
RETURNS VOID
AS $func$
DECLARE
    v_estado INTEGER;
    v_prestaciones INTEGER[];
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION 'Debe informar el requerimiento de la comparativa.';
    END IF;
    IF p_prestaciones_esperadas IS NULL
       OR p_prestaciones_esperadas !~ '^[1-9][0-9]*(,[1-9][0-9]*)*$' THEN
        RAISE EXCEPTION 'No se pudo validar la lista de prestaciones. Actualice la pantalla.';
    END IF;
    v_prestaciones := string_to_array(p_prestaciones_esperadas, ',')::INTEGER[];
    IF array_length(v_prestaciones, 1) <> (
           SELECT count(DISTINCT id) FROM unnest(v_prestaciones) id
       ) THEN
        RAISE EXCEPTION 'La lista de prestaciones no es valida. Actualice la pantalla.';
    END IF;

    PERFORM compras.bloquear_requerimiento_reclamo_prestacional(p_id_requerimiento);
    SELECT r.estado INTO v_estado FROM compras.requerimiento r
     WHERE r.id_requerimiento = p_id_requerimiento AND r.baja_fecha IS NULL
     FOR UPDATE;
    IF NOT FOUND OR v_estado <> 2 THEN
        RAISE EXCEPTION 'La comparativa ya no puede modificarse en el estado actual. Actualice la pantalla.';
    END IF;
    IF EXISTS (
        SELECT 1 FROM compras.requerimiento_reclamo_prestacional rr
        WHERE rr.id_requerimiento = p_id_requerimiento
    ) THEN
        RAISE EXCEPTION 'El requerimiento ya tiene una creacion de RP iniciada. Actualice la pantalla.';
    END IF;
    PERFORM 1 FROM compras.requerimiento_detalle d
     WHERE d.id_requerimiento = p_id_requerimiento AND d.baja_fecha IS NULL
     ORDER BY d.id_detalle FOR UPDATE;
    IF EXISTS (
        SELECT id FROM unnest(v_prestaciones) id
        EXCEPT
        SELECT d.id_prestacion FROM compras.requerimiento_detalle d
        WHERE d.id_requerimiento = p_id_requerimiento AND d.baja_fecha IS NULL
    ) OR EXISTS (
        SELECT d.id_prestacion FROM compras.requerimiento_detalle d
        WHERE d.id_requerimiento = p_id_requerimiento AND d.baja_fecha IS NULL
        EXCEPT
        SELECT id FROM unnest(v_prestaciones) id
    ) OR array_length(v_prestaciones, 1) <> (
        SELECT count(*) FROM compras.requerimiento_detalle d
        WHERE d.id_requerimiento = p_id_requerimiento AND d.baja_fecha IS NULL
    ) THEN
        RAISE EXCEPTION 'Las prestaciones cambiaron durante la carga. Actualice la pantalla.';
    END IF;
END;
$func$
LANGUAGE plpgsql;$sentencia_compras_036$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 07/14: docs/sql/compras/20261013_corregir_item_antes_rp.sql';
        v_paso := 37;
        v_comando := 'DO $adaptar_detalle$';
        EXECUTE replace($sentencia_compras_037$

-- Adaptacion limitada del trigger existente: no instalar triggers nuevos.
DO $adaptar_detalle$
DECLARE
    v_oid REGPROCEDURE;
    v_def TEXT;
    v_original TEXT;
    v_viejo TEXT;
    v_nuevo TEXT;
BEGIN
    v_oid := to_regprocedure('compras.validar_requerimiento_detalle_fila()');
    IF v_oid IS NULL THEN
        RETURN;
    END IF;
    v_def := pg_get_functiondef(v_oid);
    v_original := v_def;

    v_viejo := $viejo$    v_total_detalles_activos INTEGER;
BEGIN$viejo$;
    v_nuevo := $nuevo$    v_total_detalles_activos INTEGER;
    v_id_sector_correccion INTEGER;
    v_fila_correccion compras.requerimiento_detalle%ROWTYPE;
BEGIN$nuevo$;
    IF position(v_nuevo IN v_def) = 0 THEN
        IF length(v_def) - length(replace(v_def, v_viejo, '')) <> length(v_viejo) THEN
            RAISE EXCEPTION 'Variante desconocida del trigger de detalle. No se reemplazo su cuerpo.';
        END IF;
        v_def := replace(v_def, v_viejo, v_nuevo);
    END IF;

    v_viejo := $viejo$    IF TG_OP = 'UPDATE'
       AND OLD.baja_fecha IS NOT NULL$viejo$;
    v_nuevo := $nuevo$    IF TG_OP = 'UPDATE'
       AND OLD.baja_fecha IS NULL AND NEW.baja_fecha IS NULL
       AND OLD.tipo_item IN ('NOMENCLADOR', 'MEDICAMENTO')
       AND NEW.tipo_item = 'NOMENCLADOR'
       AND (v_estado IN (2, 3) OR OLD.tipo_item = 'MEDICAMENTO')
       AND (NEW.tipo_item IS DISTINCT FROM OLD.tipo_item
            OR NEW.id_prestacion IS DISTINCT FROM OLD.id_prestacion
            OR NEW.id_tipo_nomenclador IS DISTINCT FROM OLD.id_tipo_nomenclador
            OR NEW.codigo_nomenclador IS DISTINCT FROM OLD.codigo_nomenclador
            OR NEW.descripcion_nomenclador IS DISTINCT FROM OLD.descripcion_nomenclador) THEN

        PERFORM compras.bloquear_requerimiento_reclamo_prestacional(NEW.id_requerimiento);
        SELECT r.estado, r.id_sector INTO v_estado, v_id_sector_correccion
          FROM compras.requerimiento r
         WHERE r.id_requerimiento = NEW.id_requerimiento AND r.baja_fecha IS NULL
         FOR UPDATE;
        IF NOT FOUND OR v_estado NOT IN (1, 2, 3)
           OR EXISTS (SELECT 1 FROM compras.requerimiento_reclamo_prestacional rr
                      WHERE rr.id_requerimiento = NEW.id_requerimiento) THEN
            RAISE EXCEPTION 'El item no puede corregirse despues de iniciar el RP.';
        END IF;
        v_fila_correccion := NEW;
        v_fila_correccion.tipo_item := OLD.tipo_item;
        v_fila_correccion.id_prestacion := OLD.id_prestacion;
        v_fila_correccion.id_tipo_nomenclador := OLD.id_tipo_nomenclador;
        v_fila_correccion.codigo_nomenclador := OLD.codigo_nomenclador;
        v_fila_correccion.descripcion_nomenclador := OLD.descripcion_nomenclador;
        v_fila_correccion.id_medicamento := OLD.id_medicamento;
        v_fila_correccion.troquel := OLD.troquel;
        v_fila_correccion.nombre_medicamento := OLD.nombre_medicamento;
        v_fila_correccion.modi_fecha := OLD.modi_fecha;
        v_fila_correccion.modi_usr := OLD.modi_usr;
        IF v_fila_correccion IS DISTINCT FROM OLD THEN
            RAISE EXCEPTION 'La correccion del item debe conservar cantidades, observaciones e importes.';
        END IF;
        PERFORM compras.validar_nomenclador_configurado(
            v_id_sector_correccion, OLD.id_tipo_prestacion,
            NEW.id_prestacion, NEW.id_tipo_nomenclador);
        IF NEW.id_medicamento IS NOT NULL OR NEW.troquel IS NOT NULL
           OR NEW.nombre_medicamento IS NOT NULL OR NOT EXISTS (
            SELECT 1 FROM autorizaciones.nomenclador n
            WHERE n.id_prestacion = NEW.id_prestacion AND n.baja_fecha IS NULL
              AND btrim(n.codigo) = NEW.codigo_nomenclador
              AND btrim(n.descripcion) = NEW.descripcion_nomenclador
        ) THEN
            RAISE EXCEPTION 'La correccion debe conservar una referencia canonica de nomenclador.';
        END IF;
        IF NEW.id_prestacion IS DISTINCT FROM OLD.id_prestacion AND EXISTS (
            SELECT 1 FROM compras.requerimiento_comparativa_detalle cd
            JOIN compras.requerimiento_comparativa c ON c.id_comparativa = cd.id_comparativa
            WHERE c.id_requerimiento = NEW.id_requerimiento
              AND cd.id_prestacion = OLD.id_prestacion
        ) THEN
            RAISE EXCEPTION 'La correccion debe actualizar la comparativa en la misma transaccion.';
        END IF;
        IF EXISTS (SELECT 1 FROM compras.requerimiento_detalle d
                   WHERE d.id_requerimiento = NEW.id_requerimiento AND d.baja_fecha IS NULL
                     AND d.id_detalle <> NEW.id_detalle AND d.id_prestacion = NEW.id_prestacion) THEN
            RAISE EXCEPTION 'El requerimiento ya contiene la prestacion seleccionada.';
        END IF;
        RETURN NEW;
    END IF;

    IF TG_OP = 'UPDATE'
       AND OLD.baja_fecha IS NOT NULL$nuevo$;
    IF position(v_nuevo IN v_def) = 0 THEN
        IF length(v_def) - length(replace(v_def, v_viejo, '')) <> length(v_viejo) THEN
            RAISE EXCEPTION 'Variante desconocida del trigger de detalle. No se reemplazo su cuerpo.';
        END IF;
        v_def := replace(v_def, v_viejo, v_nuevo);
    END IF;

    IF v_def IS DISTINCT FROM v_original THEN
        EXECUTE v_def;
    END IF;
END;
$adaptar_detalle$;$sentencia_compras_037$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 08/14: docs/sql/compras/20261013_autorizacion_requerimiento.sql';
        v_paso := 38;
        v_comando := 'DO $compatibilidad$';
        EXECUTE replace($sentencia_compras_038$

-- ===== BLOQUE 08/14: docs/sql/compras/20261013_autorizacion_requerimiento.sql =====
-- SHA256 original (bytes): 037afb77bcc64cbe89bb11eecf37bd09c300b57736ea774ed03cdbd151c05417
-- Solo se retiraron BEGIN/COMMIT exteriores; cuerpo funcional conservado.

-- Compras: estados PARA AUTORIZAR (6) y AUTORIZADO (7).
-- Referencia JDBC legacy: ReintegroServiceImpl.cambiarEstadoCuota.
-- Catalogo QA leido el 13/10/2026: PostgreSQL 9.6.15, sin CHECK de estado
-- y sin trigger asociado a validar_requerimiento_fila().
-- No reemplazar cambiar_estado_requerimiento() ni agregar triggers/constraints.
-- Instalar antes de publicar Java/JSP. No se aplico a QA desde Codex.


DO $compatibilidad$
DECLARE
    v_constraint RECORD;
    v_def VARCHAR;
BEGIN
    IF to_regprocedure(
        'compras.cambiar_estado_autorizacion_requerimiento(integer,integer,character varying)'
    ) IS NULL AND EXISTS (
        SELECT 1 FROM compras.requerimiento WHERE estado IN (6, 7)
    ) THEN
        RAISE EXCEPTION
            'Los estados 6 o 7 ya estan en uso. Revise su significado antes de migrar.';
    END IF;

    FOR v_constraint IN
        SELECT conname, pg_get_constraintdef(oid) AS definicion
          FROM pg_constraint
         WHERE conrelid = 'compras.requerimiento'::regclass
           AND contype = 'c'
           AND position('estado' IN lower(pg_get_constraintdef(oid))) > 0
    LOOP
        v_def := regexp_replace(v_constraint.definicion, '[[:space:]]', '', 'g');
        IF v_constraint.conname <> 'ck_compras_requerimiento_estado'
           OR v_def NOT IN (
              'CHECK((estado=ANY(ARRAY[1,2,3,4,5,99])))',
              'CHECK((estado=ANY(ARRAY[1,2,3,4,5,6,7,99])))'
           ) THEN
            RAISE EXCEPTION
                'CHECK de estado desconocido: %. Revisar sin reemplazar reglas ajenas.',
                v_constraint.conname;
        END IF;

        IF v_def = 'CHECK((estado=ANY(ARRAY[1,2,3,4,5,99])))' THEN
            ALTER TABLE compras.requerimiento
                DROP CONSTRAINT ck_compras_requerimiento_estado;
            ALTER TABLE compras.requerimiento
                ADD CONSTRAINT ck_compras_requerimiento_estado
                CHECK (estado IN (1, 2, 3, 4, 5, 6, 7, 99));
        END IF;
    END LOOP;
END;
$compatibilidad$;$sentencia_compras_038$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 08/14: docs/sql/compras/20261013_autorizacion_requerimiento.sql';
        v_paso := 39;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.estado_requerimiento_descripcion(';
        EXECUTE replace($sentencia_compras_039$

CREATE OR REPLACE FUNCTION compras.estado_requerimiento_descripcion(
    p_estado INTEGER
)
    RETURNS VARCHAR
AS $func$
BEGIN
RETURN CASE p_estado
           WHEN 1 THEN 'PENDIENTE'
           WHEN 2 THEN 'A COTIZAR'
           WHEN 3 THEN 'COTIZADO'
           WHEN 4 THEN 'RECLAMO (RP)'
           WHEN 5 THEN 'ORDEN DE COMPRA'
           WHEN 6 THEN 'PARA AUTORIZAR'
           WHEN 7 THEN 'AUTORIZADO'
           WHEN 99 THEN 'ANULADO'
           ELSE 'DESCONOCIDO'
    END;
END;
$func$
LANGUAGE plpgsql
IMMUTABLE;$sentencia_compras_039$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 08/14: docs/sql/compras/20261013_autorizacion_requerimiento.sql';
        v_paso := 40;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.listar_estados_requerimiento()';
        EXECUTE replace($sentencia_compras_040$

CREATE OR REPLACE FUNCTION compras.listar_estados_requerimiento()
    RETURNS TABLE (
                      id INTEGER,
                      descripcion VARCHAR
                  )
    AS $func$
BEGIN
RETURN QUERY
SELECT *
FROM (
         VALUES
             (1, 'PENDIENTE'::VARCHAR),
             (2, 'A COTIZAR'::VARCHAR),
             (3, 'COTIZADO'::VARCHAR),
             (4, 'RECLAMO (RP)'::VARCHAR),
             (5, 'ORDEN DE COMPRA'::VARCHAR),
             (6, 'PARA AUTORIZAR'::VARCHAR),
             (7, 'AUTORIZADO'::VARCHAR),
             (99, 'ANULADO'::VARCHAR)
     ) estados(id, descripcion);
END;
$func$
LANGUAGE plpgsql
IMMUTABLE;$sentencia_compras_040$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 08/14: docs/sql/compras/20261013_autorizacion_requerimiento.sql';
        v_paso := 41;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.cambiar_estado_autorizacion_requerimiento(';
        EXECUTE replace($sentencia_compras_041$

CREATE OR REPLACE FUNCTION compras.cambiar_estado_autorizacion_requerimiento(
    p_id_requerimiento INTEGER,
    p_estado_nuevo INTEGER,
    p_usuario VARCHAR
)
RETURNS VOID
AS $func$
DECLARE
    v_estado_actual INTEGER;
    v_id_reclamo INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION 'Debe informar el requerimiento de compra.';
    END IF;

    IF p_estado_nuevo IS NULL OR p_estado_nuevo NOT IN (6, 7) THEN
        RAISE EXCEPTION 'El estado de autorizacion seleccionado no es valido.';
    END IF;

    v_usuario := compras.normalizar_usuario(p_usuario);

    SELECT r.estado
      INTO v_estado_actual
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'No se encontro el requerimiento activo.';
    END IF;

    IF NOT (
            (v_estado_actual = 4 AND p_estado_nuevo = 6)
         OR (v_estado_actual = 6 AND p_estado_nuevo = 7)
    ) THEN
        RAISE EXCEPTION
            'El requerimiento no puede pasar al estado seleccionado desde su estado actual.';
    END IF;

    SELECT rr.id_reclamo_prestacional
      INTO v_id_reclamo
      FROM compras.requerimiento_reclamo_prestacional rr
      JOIN autorizaciones.reclamos_prestacionales rp
        ON rp.id = rr.id_reclamo_prestacional
       AND rp.baja_fecha IS NULL
     WHERE rr.id_requerimiento = p_id_requerimiento
       AND rr.estado = 'VINCULADO'
       AND rr.id_reclamo_prestacional > 0
     FOR UPDATE OF rr, rp;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'El requerimiento debe tener un Reclamo Prestacional activo y vinculado.';
    END IF;

    UPDATE compras.requerimiento
       SET estado = p_estado_nuevo,
           modi_fecha = now(),
           modi_usr = v_usuario
     WHERE id_requerimiento = p_id_requerimiento
       AND estado = v_estado_actual
       AND baja_fecha IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'El requerimiento fue modificado por otro proceso.';
    END IF;
END;
$func$
LANGUAGE plpgsql;$sentencia_compras_041$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 08/14: docs/sql/compras/20261013_autorizacion_requerimiento.sql';
        v_paso := 42;
        v_comando := 'DO $adaptar_trigger$';
        EXECUTE replace($sentencia_compras_042$

-- Preserva toda validacion instalada fuera de la transicion de autorizacion.
-- Si la funcion no existe, no se crea una validacion nueva ni un trigger.
DO $adaptar_trigger$
DECLARE
    v_oid REGPROCEDURE;
    v_def TEXT;
    v_original TEXT;
    v_viejo TEXT;
    v_nuevo TEXT;
BEGIN
    v_oid := to_regprocedure('compras.validar_requerimiento_fila()');
    IF v_oid IS NULL THEN
        RETURN;
    END IF;

    v_def := pg_get_functiondef(v_oid);
    v_original := v_def;

    v_viejo := $viejo$    v_cambio_estructura BOOLEAN;
    v_usuario VARCHAR(100);$viejo$;
    v_nuevo := $nuevo$    v_cambio_estructura BOOLEAN;
    v_usuario VARCHAR(100);
    v_fila_transicion compras.requerimiento%ROWTYPE;$nuevo$;
    IF position(v_nuevo IN v_def) = 0 THEN
        IF length(v_def) - length(replace(v_def, v_viejo, ''))
           <> length(v_viejo) THEN
            RAISE EXCEPTION
                'Variante desconocida de validar_requerimiento_fila. No se reemplazo su cuerpo.';
        END IF;
        v_def := replace(v_def, v_viejo, v_nuevo);
    END IF;

    v_viejo := $viejo$        IF OLD.estado IN (4, 5, 99)
           AND NEW IS DISTINCT FROM OLD THEN

            RAISE EXCEPTION
                'El requerimiento no puede modificarse en el estado actual.';
END IF;$viejo$;
    v_nuevo := $nuevo$        IF OLD.estado IN (5, 7, 99)
           AND NEW IS DISTINCT FROM OLD THEN

            RAISE EXCEPTION
                'El requerimiento no puede modificarse en el estado actual.';
END IF;

        IF OLD.estado IN (4, 6)
           AND NEW IS DISTINCT FROM OLD THEN

            v_fila_transicion := NEW;
            v_fila_transicion.estado := OLD.estado;
            v_fila_transicion.modi_fecha := OLD.modi_fecha;
            v_fila_transicion.modi_usr := OLD.modi_usr;

            IF NOT (
                    (OLD.estado = 4 AND NEW.estado = 6)
                 OR (OLD.estado = 6 AND NEW.estado = 7)
               )
               OR v_fila_transicion IS DISTINCT FROM OLD THEN

                RAISE EXCEPTION
                    'El requerimiento no puede modificarse en el estado actual.';
            END IF;
        END IF;$nuevo$;
    IF position(v_nuevo IN v_def) = 0 THEN
        IF length(v_def) - length(replace(v_def, v_viejo, ''))
           <> length(v_viejo) THEN
            RAISE EXCEPTION
                'Variante desconocida de validar_requerimiento_fila. No se reemplazo su cuerpo.';
        END IF;
        v_def := replace(v_def, v_viejo, v_nuevo);
    END IF;

    v_viejo := $viejo$                 OR (OLD.estado = 3 AND NEW.estado = 4)
            ) THEN$viejo$;
    v_nuevo := $nuevo$                 OR (OLD.estado = 3 AND NEW.estado = 4)
                 OR (OLD.estado = 4 AND NEW.estado = 6)
                 OR (OLD.estado = 6 AND NEW.estado = 7)
            ) THEN$nuevo$;
    IF position(v_nuevo IN v_def) = 0 THEN
        IF length(v_def) - length(replace(v_def, v_viejo, ''))
           <> length(v_viejo) THEN
            RAISE EXCEPTION
                'Variante desconocida de validar_requerimiento_fila. No se reemplazo su cuerpo.';
        END IF;
        v_def := replace(v_def, v_viejo, v_nuevo);
    END IF;

    v_def := replace(v_def,
        'RECLAMO_RP, ORDEN_COMPRA y ANULADO son completamente',
        'ORDEN_COMPRA, AUTORIZADO y ANULADO son completamente');
    IF v_def IS DISTINCT FROM v_original THEN
        EXECUTE v_def;
    END IF;
END;
$adaptar_trigger$;$sentencia_compras_042$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 09/14: docs/sql/compras/20261014_baja_vinculo_reclamo_prestacional.sql';
        v_paso := 43;
        v_comando := 'ALTER TABLE compras.requerimiento_reclamo_prestacional';
        EXECUTE replace($sentencia_compras_043$

-- ===== BLOQUE 09/14: docs/sql/compras/20261014_baja_vinculo_reclamo_prestacional.sql =====
-- SHA256 original (bytes): 837a4029b8e6ba0f05fc48a6bb839b5512bb3848a25ecdf96b588123f445f899
-- Solo se retiraron BEGIN/COMMIT exteriores; cuerpo funcional conservado.

-- Baja RP -> baja logica del vinculo Compras. PostgreSQL 9.6.
-- Mantiene la funcion legacy autorizaciones.borra_reclamo_prestacional.

ALTER TABLE compras.requerimiento_reclamo_prestacional
    ADD COLUMN IF NOT EXISTS baja_fecha TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS baja_usr VARCHAR(100);$sentencia_compras_043$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 09/14: docs/sql/compras/20261014_baja_vinculo_reclamo_prestacional.sql';
        v_paso := 44;
        v_comando := 'DO $checks$';
        EXECUTE replace($sentencia_compras_044$

-- Conservar la ausencia de CHECK en ambientes que no los tienen instalados.
DO $checks$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_constraint
               WHERE conrelid = 'compras.requerimiento_reclamo_prestacional'::REGCLASS
                 AND conname = 'ck_compras_requerimiento_reclamo_estado') THEN
        ALTER TABLE compras.requerimiento_reclamo_prestacional
            DROP CONSTRAINT ck_compras_requerimiento_reclamo_estado;
        ALTER TABLE compras.requerimiento_reclamo_prestacional
            ADD CONSTRAINT ck_compras_requerimiento_reclamo_estado
            CHECK (estado IN ('RESERVADO', 'VINCULADO', 'ERROR', 'BAJA'));
    END IF;
    IF EXISTS (SELECT 1 FROM pg_constraint
               WHERE conrelid = 'compras.requerimiento_reclamo_prestacional'::REGCLASS
                 AND conname = 'ck_compras_requerimiento_reclamo_datos') THEN
        ALTER TABLE compras.requerimiento_reclamo_prestacional
            DROP CONSTRAINT ck_compras_requerimiento_reclamo_datos;
        ALTER TABLE compras.requerimiento_reclamo_prestacional
            ADD CONSTRAINT ck_compras_requerimiento_reclamo_datos CHECK (
                (estado = 'RESERVADO' AND id_reclamo_prestacional IS NULL
                    AND NULLIF(btrim(token_reserva), '') IS NOT NULL)
                OR (estado IN ('VINCULADO', 'ERROR') AND id_reclamo_prestacional IS NOT NULL)
                OR (estado = 'BAJA' AND id_reclamo_prestacional IS NOT NULL AND baja_fecha IS NOT NULL)
            );
    END IF;
END;
$checks$;$sentencia_compras_044$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 09/14: docs/sql/compras/20261014_baja_vinculo_reclamo_prestacional.sql';
        v_paso := 45;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.baja_vinculo_reclamo_prestacional(';
        EXECUTE replace($sentencia_compras_045$

CREATE OR REPLACE FUNCTION compras.baja_vinculo_reclamo_prestacional(
    p_id_reclamo_prestacional INTEGER,
    p_usuario VARCHAR
)
RETURNS BOOLEAN
AS $func$
DECLARE
    v_baja_fecha TIMESTAMP WITHOUT TIME ZONE;
    v_usuario VARCHAR(100);
    v_filas INTEGER;
BEGIN
    SELECT rp.baja_fecha INTO v_baja_fecha
      FROM autorizaciones.reclamos_prestacionales rp
     WHERE rp.id = p_id_reclamo_prestacional
     FOR UPDATE;
    IF NOT FOUND OR v_baja_fecha IS NULL THEN
        RETURN FALSE;
    END IF;

    v_usuario := compras.normalizar_usuario(p_usuario);
    UPDATE compras.requerimiento_reclamo_prestacional
       SET estado = 'BAJA',
           baja_fecha = v_baja_fecha,
           baja_usr = v_usuario,
           token_reserva = NULL,
           modi_fecha = now(),
           modi_usr = v_usuario
     WHERE id_reclamo_prestacional = p_id_reclamo_prestacional
       AND estado IN ('VINCULADO', 'ERROR');
    GET DIAGNOSTICS v_filas = ROW_COUNT;
    RETURN v_filas > 0;
END;
$func$
LANGUAGE plpgsql VOLATILE;$sentencia_compras_045$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 09/14: docs/sql/compras/20261014_baja_vinculo_reclamo_prestacional.sql';
        v_paso := 46;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.baja_vinculo_reclamo_prestacional_trigger()';
        EXECUTE replace($sentencia_compras_046$

CREATE OR REPLACE FUNCTION compras.baja_vinculo_reclamo_prestacional_trigger()
RETURNS TRIGGER
AS $func$
BEGIN
    PERFORM compras.baja_vinculo_reclamo_prestacional(NEW.id, NEW.baja_usr);
    RETURN NEW;
END;
$func$
LANGUAGE plpgsql;$sentencia_compras_046$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 09/14: docs/sql/compras/20261014_baja_vinculo_reclamo_prestacional.sql';
        v_paso := 47;
        v_comando := 'DROP TRIGGER IF EXISTS trg_compras_baja_vinculo_reclamo';
        EXECUTE replace($sentencia_compras_047$

DROP TRIGGER IF EXISTS trg_compras_baja_vinculo_reclamo
    ON autorizaciones.reclamos_prestacionales;$sentencia_compras_047$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 09/14: docs/sql/compras/20261014_baja_vinculo_reclamo_prestacional.sql';
        v_paso := 48;
        v_comando := 'CREATE TRIGGER trg_compras_baja_vinculo_reclamo';
        EXECUTE replace($sentencia_compras_048$
CREATE TRIGGER trg_compras_baja_vinculo_reclamo
    AFTER UPDATE
    ON autorizaciones.reclamos_prestacionales
    FOR EACH ROW WHEN (NEW.baja_fecha IS NOT NULL)
    EXECUTE PROCEDURE compras.baja_vinculo_reclamo_prestacional_trigger();$sentencia_compras_048$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 09/14: docs/sql/compras/20261014_baja_vinculo_reclamo_prestacional.sql';
        v_paso := 49;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.puede_anular_requerimiento(';
        EXECUTE replace($sentencia_compras_049$

CREATE OR REPLACE FUNCTION compras.puede_anular_requerimiento(
    p_id_requerimiento INTEGER
)
RETURNS BOOLEAN
AS $func$
DECLARE
    v_estado INTEGER;
    v_id_reclamo INTEGER;
BEGIN
    SELECT r.estado INTO v_estado
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = p_id_requerimiento AND r.baja_fecha IS NULL;
    IF NOT FOUND OR v_estado NOT IN (1, 2, 3, 4) THEN
        RETURN FALSE;
    END IF;

    -- Mismo orden de bloqueo que la baja RP: RP antes del vinculo.
    FOR v_id_reclamo IN
        SELECT rp.id FROM autorizaciones.reclamos_prestacionales rp
        JOIN compras.requerimiento_reclamo_prestacional rr
          ON rr.id_reclamo_prestacional = rp.id
        WHERE rr.id_requerimiento = p_id_requerimiento
        ORDER BY rp.id FOR UPDATE OF rp
    LOOP
        NULL;
    END LOOP;

    IF v_estado IN (1, 2, 3) THEN
        RETURN NOT EXISTS (
            SELECT 1 FROM compras.requerimiento_reclamo_prestacional rr
            WHERE rr.id_requerimiento = p_id_requerimiento
              AND (rr.estado <> 'BAJA' OR rr.baja_fecha IS NULL OR NOT EXISTS (
                  SELECT 1 FROM autorizaciones.reclamos_prestacionales rp
                  WHERE rp.id = rr.id_reclamo_prestacional
                    AND rp.baja_fecha IS NOT NULL
              ))
        );
    END IF;

    RETURN EXISTS (
        SELECT 1 FROM compras.requerimiento_reclamo_prestacional rr
        JOIN autorizaciones.reclamos_prestacionales rp
          ON rp.id = rr.id_reclamo_prestacional
        WHERE rr.id_requerimiento = p_id_requerimiento
          AND rr.estado = 'BAJA' AND rr.baja_fecha IS NOT NULL
          AND rp.baja_fecha IS NOT NULL
    );
END;
$func$
LANGUAGE plpgsql VOLATILE;$sentencia_compras_049$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 09/14: docs/sql/compras/20261014_baja_vinculo_reclamo_prestacional.sql';
        v_paso := 50;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.anular_requerimiento(';
        EXECUTE replace($sentencia_compras_050$

CREATE OR REPLACE FUNCTION compras.anular_requerimiento(
    p_id_requerimiento INTEGER,
    p_motivo_baja TEXT,
    p_usuario VARCHAR
)
RETURNS VOID
AS $func$
DECLARE
    v_estado_actual INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    v_usuario := compras.normalizar_usuario(p_usuario);
    PERFORM compras.bloquear_requerimiento_reclamo_prestacional(p_id_requerimiento);
    SELECT r.estado INTO v_estado_actual FROM compras.requerimiento r
     WHERE r.id_requerimiento = p_id_requerimiento AND r.baja_fecha IS NULL
     FOR UPDATE;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'No se encontro el requerimiento activo.';
    END IF;
    IF NOT compras.puede_anular_requerimiento(p_id_requerimiento) THEN
        RAISE EXCEPTION 'El requerimiento no puede anularse: verifique su estado y la baja del RP asociado.';
    END IF;
    UPDATE compras.requerimiento
       SET estado = 99,
           motivo_baja = COALESCE(p_motivo_baja, ''),
           modi_fecha = now(), modi_usr = v_usuario,
           baja_fecha = now(), baja_usr = v_usuario
     WHERE id_requerimiento = p_id_requerimiento
       AND estado = v_estado_actual AND baja_fecha IS NULL;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'El requerimiento fue modificado por otro proceso.';
    END IF;
END;
$func$
LANGUAGE plpgsql;$sentencia_compras_050$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 09/14: docs/sql/compras/20261014_baja_vinculo_reclamo_prestacional.sql';
        v_paso := 51;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.listar_relaciones_reclamo_prestacional_batch(';
        EXECUTE replace($sentencia_compras_051$

CREATE OR REPLACE FUNCTION compras.listar_relaciones_reclamo_prestacional_batch(
    p_estado VARCHAR,
    p_ids_requerimientos TEXT
)
RETURNS SETOF compras.requerimiento_reclamo_prestacional
AS $func$
    SELECT relacion.* FROM compras.requerimiento_reclamo_prestacional relacion
    WHERE relacion.id_reclamo_prestacional IS NOT NULL
      AND relacion.id_requerimiento = ANY ($2::INTEGER[])
      AND (($1 IS NOT NULL AND relacion.estado = $1)
        OR ($1 IS NULL AND (relacion.estado = 'VINCULADO'
          OR (relacion.estado = 'BAJA' AND relacion.baja_fecha IS NOT NULL
            AND EXISTS (SELECT 1 FROM autorizaciones.reclamos_prestacionales rp
                        WHERE rp.id = relacion.id_reclamo_prestacional
                          AND rp.baja_fecha IS NOT NULL)))))
    ORDER BY relacion.id_requerimiento;
$func$
LANGUAGE sql STABLE;$sentencia_compras_051$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 09/14: docs/sql/compras/20261014_baja_vinculo_reclamo_prestacional.sql';
        v_paso := 52;
        v_comando := 'DO $adaptar_validacion$';
        EXECUTE replace($sentencia_compras_052$

DO $adaptar_validacion$
DECLARE
    v_oid OID;
    v_def TEXT;
    v_viejo TEXT;
    v_nuevo TEXT;
    v_inicio INTEGER;
BEGIN
    v_oid := to_regprocedure('compras.validar_requerimiento_fila()');
    IF v_oid IS NULL THEN RETURN; END IF;
    v_def := pg_get_functiondef(v_oid);
    IF position('v_fila_anulacion compras.requerimiento%ROWTYPE;' IN v_def) > 0 THEN
        RETURN;
    END IF;
    v_viejo := '    v_usuario VARCHAR(100);';
    v_nuevo := E'    v_usuario VARCHAR(100);\n    v_fila_anulacion compras.requerimiento%ROWTYPE;';
    IF length(v_def) - length(replace(v_def, v_viejo, '')) <> length(v_viejo) THEN
        RAISE EXCEPTION 'Variante desconocida de validar_requerimiento_fila. No se modifico su cuerpo.';
    END IF;
    v_def := replace(v_def, v_viejo, v_nuevo);
    v_inicio := position(E'BEGIN\n' IN v_def);
    IF v_inicio = 0 THEN
        RAISE EXCEPTION 'No se encontro el bloque de validar_requerimiento_fila.';
    END IF;
    v_nuevo := $guard$
    -- Anulacion acotada: preserva cabecera y comprueba el RP bajo bloqueo.
    IF TG_OP = 'UPDATE' AND NEW.estado = 99 AND OLD.estado IN (1, 2, 3, 4) THEN
        IF NOT compras.puede_anular_requerimiento(OLD.id_requerimiento) THEN
            RAISE EXCEPTION 'El requerimiento no puede anularse desde su estado o vinculo actual.';
        END IF;
        v_fila_anulacion := NEW;
        v_fila_anulacion.estado := OLD.estado;
        v_fila_anulacion.motivo_baja := OLD.motivo_baja;
        v_fila_anulacion.modi_fecha := OLD.modi_fecha;
        v_fila_anulacion.modi_usr := OLD.modi_usr;
        v_fila_anulacion.baja_fecha := OLD.baja_fecha;
        v_fila_anulacion.baja_usr := OLD.baja_usr;
        IF v_fila_anulacion IS DISTINCT FROM OLD THEN
            RAISE EXCEPTION 'La anulacion debe conservar los datos del requerimiento.';
        END IF;
        NEW.baja_fecha := COALESCE(NEW.baja_fecha, now());
        NEW.baja_usr := compras.normalizar_usuario(COALESCE(NEW.baja_usr, NEW.modi_usr));
        RETURN NEW;
    END IF;
$guard$;
    v_def := overlay(v_def placing v_nuevo from v_inicio + length(E'BEGIN\n') for 0);
    EXECUTE v_def;
END;
$adaptar_validacion$;$sentencia_compras_052$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 09/14: docs/sql/compras/20261014_baja_vinculo_reclamo_prestacional.sql';
        v_paso := 53;
        v_comando := 'DO $orden_autorizacion$';
        EXECUTE replace($sentencia_compras_053$

-- Alinear el bloqueo de autorizacion con la baja concurrente del RP.
DO $orden_autorizacion$
DECLARE v_oid OID; v_def TEXT; v_viejo TEXT; v_nuevo TEXT;
BEGIN
    v_oid := to_regprocedure('compras.cambiar_estado_autorizacion_requerimiento(integer,integer,character varying)');
    IF v_oid IS NULL THEN RETURN; END IF;
    v_def := pg_get_functiondef(v_oid);
    IF position('Orden compartido con la baja RP' IN v_def) > 0 THEN RETURN; END IF;
    v_viejo := E'    SELECT rr.id_reclamo_prestacional\n      INTO v_id_reclamo';
    IF length(v_def) - length(replace(v_def, v_viejo, '')) <> length(v_viejo) THEN
        RAISE EXCEPTION 'Variante desconocida de cambiar_estado_autorizacion_requerimiento.';
    END IF;
    v_nuevo := $bloqueo$
    -- Orden compartido con la baja RP: primero RP, luego vinculo.
    PERFORM rp.id FROM autorizaciones.reclamos_prestacionales rp
    JOIN compras.requerimiento_reclamo_prestacional rr
      ON rr.id_reclamo_prestacional = rp.id
    WHERE rr.id_requerimiento = p_id_requerimiento
    FOR UPDATE OF rp;

$bloqueo$ || v_viejo;
    EXECUTE replace(v_def, v_viejo, v_nuevo);
END;
$orden_autorizacion$;$sentencia_compras_053$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 09/14: docs/sql/compras/20261014_baja_vinculo_reclamo_prestacional.sql';
        v_paso := 54;
        v_comando := 'DO $reconciliar$';
        EXECUTE replace($sentencia_compras_054$

-- Reconciliar asociaciones cuyos RP ya estaban dados de baja.
DO $reconciliar$
DECLARE v_rp RECORD;
BEGIN
    FOR v_rp IN
        SELECT DISTINCT rp.id, rp.baja_usr
          FROM autorizaciones.reclamos_prestacionales rp
          JOIN compras.requerimiento_reclamo_prestacional rr
            ON rr.id_reclamo_prestacional = rp.id
         WHERE rp.baja_fecha IS NOT NULL AND rr.estado IN ('VINCULADO', 'ERROR')
    LOOP
        PERFORM compras.baja_vinculo_reclamo_prestacional(v_rp.id, v_rp.baja_usr);
    END LOOP;
END;
$reconciliar$;$sentencia_compras_054$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 10/14: docs/sql/compras/20261015_autorizacion_orden_compra_empresas.sql';
        v_paso := 55;
        v_comando := 'DO $precondicion$';
        EXECUTE replace($sentencia_compras_055$

-- ===== BLOQUE 10/14: docs/sql/compras/20261015_autorizacion_orden_compra_empresas.sql =====
-- SHA256 original (bytes): d4bb68e9d8d205c41af24296f65a6901827eb748fc96cc8cb98463df823257e1
-- Solo se retiraron BEGIN/COMMIT exteriores; cuerpo funcional conservado.

-- Autorizacion antes de la Orden de Compra de RRHH/SISTEMAS.
-- Referencias: cambio_estado_cuota.sql / ReintegroServiceImpl.cambiarEstadoCuota;
-- contratos de Compras de 20261006, 20261013 y orden de bloqueo RP de 20261014.
-- PostgreSQL 9.6. Aplicar despues de 20261013_autorizacion_requerimiento.sql.
-- No crea triggers ni modifica datos. Java/JSP requieren esta migracion.
-- ISO-8859-1 sin BOM. No aplicada por Codex a QA.


DO $precondicion$
DECLARE v_faltantes TEXT; v_def TEXT;
BEGIN
    SELECT string_agg(requisito, E'\n' ORDER BY requisito)
      INTO v_faltantes
      FROM (VALUES
          ('compras.cambiar_estado_autorizacion_requerimiento(integer,integer,character varying) - instalar 20261013',
           to_regprocedure('compras.cambiar_estado_autorizacion_requerimiento(integer,integer,character varying)') IS NOT NULL),
          ('compras.cambiar_estado_requerimiento(integer,integer,character varying)',
           to_regprocedure('compras.cambiar_estado_requerimiento(integer,integer,character varying)') IS NOT NULL),
          ('compras.confirmar_orden_compra_requerimiento(integer,character varying)',
           to_regprocedure('compras.confirmar_orden_compra_requerimiento(integer,character varying)') IS NOT NULL),
          ('compras.buscar_empresas_cotizacion(character varying,character varying,character varying,integer)',
           to_regprocedure('compras.buscar_empresas_cotizacion(character varying,character varying,character varying,integer)') IS NOT NULL),
          ('compras.normalizar_sector(character varying)',
           to_regprocedure('compras.normalizar_sector(character varying)') IS NOT NULL),
          ('compras.normalizar_usuario(character varying)',
           to_regprocedure('compras.normalizar_usuario(character varying)') IS NOT NULL),
          ('indice compras.ux_compras_empresa_adjudicada_activa - instalar contrato 20260925/20261006',
           to_regclass('compras.ux_compras_empresa_adjudicada_activa') IS NOT NULL),
          ('columna compras.requerimiento_presupuesto.empresa_adjudicada',
           EXISTS (SELECT 1 FROM pg_attribute
                    WHERE attrelid = to_regclass('compras.requerimiento_presupuesto')
                      AND attname = 'empresa_adjudicada' AND NOT attisdropped)),
          ('columna compras.requerimiento_presupuesto.descripcion_empresa',
           EXISTS (SELECT 1 FROM pg_attribute
                    WHERE attrelid = to_regclass('compras.requerimiento_presupuesto')
                      AND attname = 'descripcion_empresa' AND NOT attisdropped))
      ) AS requisitos(requisito, existe)
     WHERE NOT existe;
    IF v_faltantes IS NOT NULL THEN
        RAISE EXCEPTION 'Faltan prerequisitos de autorizacion de Compras:%', E'\n' || v_faltantes;
    END IF;

    v_def := pg_get_functiondef(to_regprocedure(
        'compras.confirmar_orden_compra_requerimiento(integer,character varying)'));
    IF position('rp.empresa_adjudicada' IN v_def) = 0
       OR position('rp.descripcion_empresa' IN v_def) = 0
       OR position('compras.buscar_empresas_cotizacion(' IN v_def) = 0 THEN
        RAISE EXCEPTION 'Contrato de Empresa desconocido: instalar/revisar 20261006 antes de migrar.';
    END IF;
END;
$precondicion$;$sentencia_compras_055$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 10/14: docs/sql/compras/20261015_autorizacion_orden_compra_empresas.sql';
        v_paso := 56;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.cambiar_estado_autorizacion_requerimiento(';
        EXECUTE replace($sentencia_compras_056$

CREATE OR REPLACE FUNCTION compras.cambiar_estado_autorizacion_requerimiento(
    p_id_requerimiento INTEGER,
    p_estado_nuevo INTEGER,
    p_usuario VARCHAR
)
RETURNS VOID
AS $func$
DECLARE
    v_estado_actual INTEGER;
    v_id_reclamo INTEGER;
    v_sector_descripcion VARCHAR(120);
    v_es_empresa BOOLEAN;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION 'Debe informar el requerimiento de compra.';
    END IF;

    IF p_estado_nuevo IS NULL OR p_estado_nuevo NOT IN (6, 7) THEN
        RAISE EXCEPTION 'El estado de autorizacion seleccionado no es valido.';
    END IF;

    v_usuario := compras.normalizar_usuario(p_usuario);

    SELECT r.estado, sr.descripcion
      INTO v_estado_actual, v_sector_descripcion
      FROM compras.requerimiento r
      JOIN compras.sector_requerimiento sr ON sr.id_sector = r.id_sector
       AND sr.activo = TRUE AND sr.baja_fecha IS NULL
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE OF r;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'No se encontro el requerimiento activo.';
    END IF;

    v_es_empresa := compras.normalizar_sector(v_sector_descripcion)
        IN ('RRHH', 'SISTEMAS');

    IF v_es_empresa THEN
        IF NOT (
                (v_estado_actual = 1 AND p_estado_nuevo = 6)
             OR (v_estado_actual = 6 AND p_estado_nuevo = 7)
        ) THEN
            RAISE EXCEPTION
                'RRHH o SISTEMAS deben pasar de PENDIENTE a PARA AUTORIZAR y luego a AUTORIZADO.';
        END IF;

        IF NOT EXISTS (
            SELECT 1
              FROM compras.requerimiento_detalle d
             WHERE d.id_requerimiento = p_id_requerimiento
               AND d.baja_fecha IS NULL
        ) THEN

            RAISE EXCEPTION
                'Debe existir al menos un detalle antes de enviar o autorizar el requerimiento.';
        END IF;

        IF NOT EXISTS (
            SELECT 1
              FROM compras.requerimiento_presupuesto rp
             WHERE rp.id_requerimiento = p_id_requerimiento
               AND rp.tipo_documento = 3
               AND rp.baja_fecha IS NULL
        ) THEN

            RAISE EXCEPTION
                'Debe existir al menos una cotizacion de Empresa activa antes de enviar o autorizar el requerimiento.';
        END IF;

        IF (SELECT count(*)
              FROM compras.requerimiento_presupuesto rp
             WHERE rp.id_requerimiento = p_id_requerimiento
               AND rp.tipo_documento = 3
               AND rp.baja_fecha IS NULL
               AND rp.empresa_adjudicada) <> 1 THEN

            RAISE EXCEPTION
                'Debe seleccionar exactamente una Empresa adjudicada con cotizacion activa.';
        END IF;

        IF EXISTS (
            SELECT 1
              FROM compras.requerimiento_presupuesto rp
             WHERE rp.id_requerimiento = p_id_requerimiento
               AND rp.tipo_documento = 3
               AND rp.baja_fecha IS NULL
               AND rp.empresa_adjudicada
               AND (
                   NULLIF(btrim(rp.descripcion_empresa), '') IS NULL
                   OR (rp.empresa_cuit IS NOT NULL
                       AND (NULLIF(btrim(rp.empresa_cuit), '') IS NULL
                            OR length(rp.empresa_cuit) > 11
                            OR rp.empresa_cuit <> btrim(rp.empresa_cuit)))
                   OR (rp.empresa_sucursal IS NOT NULL
                       AND (rp.empresa_cuit IS NULL OR NOT EXISTS (
                           SELECT 1 FROM compras.buscar_empresas_cotizacion(
                               rp.empresa_cuit, NULL, rp.empresa_sucursal, 1)
                       )))
               )
        ) THEN
            RAISE EXCEPTION
                'La Empresa adjudicada debe tener nombre y una identidad fiscal valida cuando este informada.';
        END IF;
    ELSE
        IF NOT (
                (v_estado_actual = 4 AND p_estado_nuevo = 6)
             OR (v_estado_actual = 6 AND p_estado_nuevo = 7)
        ) THEN
            RAISE EXCEPTION
                'El requerimiento no puede pasar al estado seleccionado desde su estado actual.';
        END IF;

        -- Orden compartido con la baja RP: primero RP, luego vinculo.
        PERFORM rp.id FROM autorizaciones.reclamos_prestacionales rp
        JOIN compras.requerimiento_reclamo_prestacional rr
          ON rr.id_reclamo_prestacional = rp.id
        WHERE rr.id_requerimiento = p_id_requerimiento
        FOR UPDATE OF rp;

        SELECT rr.id_reclamo_prestacional
          INTO v_id_reclamo
          FROM compras.requerimiento_reclamo_prestacional rr
          JOIN autorizaciones.reclamos_prestacionales rp
            ON rp.id = rr.id_reclamo_prestacional
           AND rp.baja_fecha IS NULL
         WHERE rr.id_requerimiento = p_id_requerimiento
           AND rr.estado = 'VINCULADO'
           AND rr.id_reclamo_prestacional > 0
         FOR UPDATE OF rr, rp;

        IF NOT FOUND THEN
            RAISE EXCEPTION
                'El requerimiento debe tener un Reclamo Prestacional activo y vinculado.';
        END IF;
    END IF;

    UPDATE compras.requerimiento
       SET estado = p_estado_nuevo,
           modi_fecha = now(),
           modi_usr = v_usuario
     WHERE id_requerimiento = p_id_requerimiento
       AND estado = v_estado_actual
       AND baja_fecha IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'El requerimiento fue modificado por otro proceso.';
    END IF;
END;
$func$
LANGUAGE plpgsql;$sentencia_compras_056$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 10/14: docs/sql/compras/20261015_autorizacion_orden_compra_empresas.sql';
        v_paso := 57;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.confirmar_orden_compra_requerimiento(';
        EXECUTE replace($sentencia_compras_057$

CREATE OR REPLACE FUNCTION compras.confirmar_orden_compra_requerimiento(
    p_id_requerimiento INTEGER,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
    v_estado INTEGER;
    v_sector_descripcion VARCHAR(120);
BEGIN
    IF p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN

        RAISE EXCEPTION
            'Debe informar el requerimiento de compra.';
    END IF;

    SELECT
        r.estado,
        sr.descripcion
    INTO
        v_estado,
        v_sector_descripcion
    FROM compras.requerimiento r
    JOIN compras.sector_requerimiento sr
      ON sr.id_sector = r.id_sector
     AND sr.activo = TRUE
     AND sr.baja_fecha IS NULL
    WHERE r.id_requerimiento = p_id_requerimiento
      AND r.baja_fecha IS NULL
    FOR UPDATE OF r;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No se encontro el requerimiento activo.';
    END IF;

    IF v_estado = 5 THEN
        RETURN 5;
    END IF;

    IF v_estado <> 7 THEN
        RAISE EXCEPTION
            'El requerimiento solo puede pasar a ORDEN_COMPRA desde AUTORIZADO.';
    END IF;

    IF compras.normalizar_sector(
            v_sector_descripcion
       ) NOT IN ('RRHH', 'SISTEMAS') THEN

        RAISE EXCEPTION
            'Solo los requerimientos de RRHH o SISTEMAS pueden crear una ORDEN_COMPRA.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM compras.requerimiento_detalle d
         WHERE d.id_requerimiento = p_id_requerimiento
           AND d.baja_fecha IS NULL
    ) THEN

        RAISE EXCEPTION
            'Debe existir al menos un detalle antes de pasar a ORDEN_COMPRA.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 3
           AND rp.baja_fecha IS NULL
    ) THEN

        RAISE EXCEPTION
            'Debe existir al menos una cotizacion de Empresa activa antes de pasar a ORDEN_COMPRA.';
    END IF;

    IF (SELECT count(*)
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 3
           AND rp.baja_fecha IS NULL
           AND rp.empresa_adjudicada) <> 1 THEN

        RAISE EXCEPTION
            'Debe seleccionar exactamente una Empresa adjudicada con cotizacion activa.';
    END IF;

    IF EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 3
           AND rp.baja_fecha IS NULL
           AND rp.empresa_adjudicada
           AND (
               NULLIF(btrim(rp.descripcion_empresa), '') IS NULL
               OR (rp.empresa_cuit IS NOT NULL
                   AND (NULLIF(btrim(rp.empresa_cuit), '') IS NULL
                        OR length(rp.empresa_cuit) > 11
                        OR rp.empresa_cuit <> btrim(rp.empresa_cuit)))
               OR (rp.empresa_sucursal IS NOT NULL
                   AND (rp.empresa_cuit IS NULL OR NOT EXISTS (
                       SELECT 1 FROM compras.buscar_empresas_cotizacion(
                           rp.empresa_cuit, NULL, rp.empresa_sucursal, 1)
                   )))
           )
    ) THEN
        RAISE EXCEPTION
            'La Empresa adjudicada debe tener nombre y una identidad fiscal valida cuando este informada.';
    END IF;

    UPDATE compras.requerimiento
       SET estado = 5,
           modi_fecha = now(),
           modi_usr = compras.normalizar_usuario(p_usuario)
     WHERE id_requerimiento = p_id_requerimiento
       AND estado = 7
       AND baja_fecha IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'El requerimiento fue modificado por otro proceso.';
    END IF;

    RETURN 5;
END;
$func$
LANGUAGE plpgsql;$sentencia_compras_057$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 10/14: docs/sql/compras/20261015_autorizacion_orden_compra_empresas.sql';
        v_paso := 58;
        v_comando := 'DO $proteger_ruta_generica$';
        EXECUTE replace($sentencia_compras_058$

DO $proteger_ruta_generica$
DECLARE v_oid OID; v_def TEXT; v_viejo TEXT; v_nuevo TEXT;
BEGIN
    v_oid := to_regprocedure('compras.cambiar_estado_requerimiento(integer,integer,character varying)');
    IF v_oid IS NULL THEN RETURN; END IF;
    v_def := replace(pg_get_functiondef(v_oid), E'\r', '');
    IF position('Autorizacion previa a la OC' IN v_def) > 0 THEN RETURN; END IF;

    v_viejo := $viejo$    -- La ruta generica tampoco permite saltear las reglas de Empresas.
    IF p_estado_nuevo = 5 AND EXISTS (
        SELECT 1
          FROM compras.requerimiento r
          JOIN compras.sector_requerimiento sr ON sr.id_sector = r.id_sector
         WHERE r.id_requerimiento = p_id_requerimiento
           AND compras.normalizar_sector(sr.descripcion) IN ('RRHH', 'SISTEMAS')
    ) THEN
        PERFORM compras.confirmar_orden_compra_requerimiento(
                p_id_requerimiento, p_usuario);
        RETURN;
    END IF;$viejo$;
    v_nuevo := $nuevo$    -- Autorizacion previa a la OC: tampoco puede omitirse por la ruta generica.
    IF p_estado_nuevo IN (6, 7) THEN
        PERFORM compras.cambiar_estado_autorizacion_requerimiento(
                p_id_requerimiento, p_estado_nuevo, p_usuario);
        RETURN;
    END IF;

    IF p_estado_nuevo = 5 THEN
        PERFORM compras.confirmar_orden_compra_requerimiento(
                p_id_requerimiento, p_usuario);
        RETURN;
    END IF;

    IF v_estado_actual IN (6, 7) THEN
        RAISE EXCEPTION
            'El requerimiento no puede modificarse en el estado actual.';
    END IF;$nuevo$;
    IF length(v_def) - length(replace(v_def, v_viejo, '')) <> length(v_viejo) THEN
        RAISE EXCEPTION 'Variante desconocida de proteger_ruta_generica. No se reemplazo su cuerpo.';
    END IF;
    v_def := replace(v_def, v_viejo, v_nuevo);

    EXECUTE v_def;
END;
$proteger_ruta_generica$;$sentencia_compras_058$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 10/14: docs/sql/compras/20261015_autorizacion_orden_compra_empresas.sql';
        v_paso := 59;
        v_comando := 'DO $adaptar_validacion$';
        EXECUTE replace($sentencia_compras_059$

-- Adapta unicamente fragmentos conocidos y preserva la configuracion de triggers.
DO $adaptar_validacion$
DECLARE v_oid OID; v_def TEXT; v_viejo TEXT; v_nuevo TEXT;
BEGIN
    v_oid := to_regprocedure('compras.validar_requerimiento_fila()');
    IF v_oid IS NULL THEN RETURN; END IF;
    v_def := replace(pg_get_functiondef(v_oid), E'\r', '');
    IF position('Autorizacion previa a la OC' IN v_def) > 0 THEN RETURN; END IF;

    v_viejo := $viejo$        IF OLD.estado IN (5, 7, 99)
           AND NEW IS DISTINCT FROM OLD THEN

            RAISE EXCEPTION
                'El requerimiento no puede modificarse en el estado actual.';
END IF;

        IF OLD.estado IN (4, 6)
           AND NEW IS DISTINCT FROM OLD THEN

            v_fila_transicion := NEW;
            v_fila_transicion.estado := OLD.estado;
            v_fila_transicion.modi_fecha := OLD.modi_fecha;
            v_fila_transicion.modi_usr := OLD.modi_usr;

            IF NOT (
                    (OLD.estado = 4 AND NEW.estado = 6)
                 OR (OLD.estado = 6 AND NEW.estado = 7)
               )
               OR v_fila_transicion IS DISTINCT FROM OLD THEN

                RAISE EXCEPTION
                    'El requerimiento no puede modificarse en el estado actual.';
            END IF;
        END IF;$viejo$;
    v_nuevo := $nuevo$        IF OLD.estado IN (5, 99)
           AND NEW IS DISTINCT FROM OLD THEN

            RAISE EXCEPTION
                'El requerimiento no puede modificarse en el estado actual.';
END IF;

        -- Autorizacion previa a la OC: conservar toda la fila salvo estado y auditoria.
        IF OLD.estado IN (4, 6, 7)
           AND NEW IS DISTINCT FROM OLD THEN

            v_fila_transicion := NEW;
            v_fila_transicion.estado := OLD.estado;
            v_fila_transicion.modi_fecha := OLD.modi_fecha;
            v_fila_transicion.modi_usr := OLD.modi_usr;

            IF NOT (
                    (OLD.estado = 4 AND NEW.estado = 6
                     AND compras.normalizar_sector(v_sector_descripcion) NOT IN ('RRHH', 'SISTEMAS'))
                 OR (OLD.estado = 6 AND NEW.estado = 7)
                 OR (OLD.estado = 7 AND NEW.estado = 5
                     AND compras.normalizar_sector(v_sector_descripcion) IN ('RRHH', 'SISTEMAS'))
               )
               OR v_fila_transicion IS DISTINCT FROM OLD THEN

                RAISE EXCEPTION
                    'El requerimiento no puede modificarse en el estado actual.';
            END IF;
        END IF;$nuevo$;
    IF length(v_def) - length(replace(v_def, v_viejo, '')) <> length(v_viejo) THEN
        RAISE EXCEPTION 'Variante desconocida de adaptar_validacion. No se reemplazo su cuerpo.';
    END IF;
    v_def := replace(v_def, v_viejo, v_nuevo);

    v_viejo := $viejo$                    (OLD.estado = 1 AND NEW.estado IN (2, 5, 99))
                 OR (OLD.estado = 2 AND NEW.estado IN (3, 99))
                 OR (OLD.estado = 3 AND NEW.estado = 4)
                 OR (OLD.estado = 4 AND NEW.estado = 6)
                 OR (OLD.estado = 6 AND NEW.estado = 7)$viejo$;
    v_nuevo := $nuevo$                    (OLD.estado = 1 AND NEW.estado IN (2, 99))
                 OR (OLD.estado = 1 AND NEW.estado = 6
                     AND compras.normalizar_sector(v_sector_descripcion) IN ('RRHH', 'SISTEMAS'))
                 OR (OLD.estado = 2 AND NEW.estado IN (3, 99))
                 OR (OLD.estado = 3 AND NEW.estado = 4)
                 OR (OLD.estado = 4 AND NEW.estado = 6
                     AND compras.normalizar_sector(v_sector_descripcion) NOT IN ('RRHH', 'SISTEMAS'))
                 OR (OLD.estado = 6 AND NEW.estado = 7)
                 OR (OLD.estado = 7 AND NEW.estado = 5
                     AND compras.normalizar_sector(v_sector_descripcion) IN ('RRHH', 'SISTEMAS'))$nuevo$;
    IF length(v_def) - length(replace(v_def, v_viejo, '')) <> length(v_viejo) THEN
        RAISE EXCEPTION 'Variante desconocida de adaptar_validacion. No se reemplazo su cuerpo.';
    END IF;
    v_def := replace(v_def, v_viejo, v_nuevo);

    v_viejo := $viejo$            IF OLD.estado = 1
               AND NEW.estado = 5 THEN

                IF compras.normalizar_sector(
                        v_sector_descripcion
                   ) NOT IN ('RRHH', 'SISTEMAS') THEN

                    RAISE EXCEPTION
                        'Solo los requerimientos de RRHH o SISTEMAS pueden pasar directamente a ORDEN_COMPRA.';
                END IF;

                IF NOT EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_detalle d
                     WHERE d.id_requerimiento =
                           NEW.id_requerimiento
                       AND d.baja_fecha IS NULL
                ) THEN

                    RAISE EXCEPTION
                        'Debe existir al menos un detalle antes de pasar a ORDEN_COMPRA.';
                END IF;

                IF NOT EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_presupuesto rp
                     WHERE rp.id_requerimiento =
                           NEW.id_requerimiento
                       AND rp.tipo_documento = 3
                       AND rp.baja_fecha IS NULL
                ) THEN

                    RAISE EXCEPTION
                        'Debe existir al menos una cotizacion de Empresa activa antes de pasar a ORDEN_COMPRA.';
                END IF;

            END IF;$viejo$;
    v_nuevo := $nuevo$            IF ((OLD.estado = 1 AND NEW.estado = 6)
                OR (OLD.estado = 6 AND NEW.estado = 7))
               AND compras.normalizar_sector(v_sector_descripcion) IN ('RRHH', 'SISTEMAS')
               OR (OLD.estado = 7 AND NEW.estado = 5) THEN

                IF compras.normalizar_sector(v_sector_descripcion) NOT IN ('RRHH', 'SISTEMAS') THEN
                    RAISE EXCEPTION 'Solo RRHH o SISTEMAS pueden crear una ORDEN_COMPRA.';
                END IF;

                IF NOT EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_detalle d
                     WHERE d.id_requerimiento = NEW.id_requerimiento
                       AND d.baja_fecha IS NULL
                ) THEN

                    RAISE EXCEPTION
                        'Debe existir al menos un detalle antes de pasar a ORDEN_COMPRA.';
                END IF;

                IF NOT EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_presupuesto rp
                     WHERE rp.id_requerimiento = NEW.id_requerimiento
                       AND rp.tipo_documento = 3
                       AND rp.baja_fecha IS NULL
                ) THEN

                    RAISE EXCEPTION
                        'Debe existir al menos una cotizacion de Empresa activa antes de pasar a ORDEN_COMPRA.';
                END IF;

                IF (SELECT count(*)
                      FROM compras.requerimiento_presupuesto rp
                     WHERE rp.id_requerimiento = NEW.id_requerimiento
                       AND rp.tipo_documento = 3
                       AND rp.baja_fecha IS NULL
                       AND rp.empresa_adjudicada) <> 1 THEN

                    RAISE EXCEPTION
                        'Debe seleccionar exactamente una Empresa adjudicada con cotizacion activa.';
                END IF;

                IF EXISTS (
                    SELECT 1
                      FROM compras.requerimiento_presupuesto rp
                     WHERE rp.id_requerimiento = NEW.id_requerimiento
                       AND rp.tipo_documento = 3
                       AND rp.baja_fecha IS NULL
                       AND rp.empresa_adjudicada
                       AND (
                           NULLIF(btrim(rp.descripcion_empresa), '') IS NULL
                           OR (rp.empresa_cuit IS NOT NULL
                               AND (NULLIF(btrim(rp.empresa_cuit), '') IS NULL
                                    OR length(rp.empresa_cuit) > 11
                                    OR rp.empresa_cuit <> btrim(rp.empresa_cuit)))
                           OR (rp.empresa_sucursal IS NOT NULL
                               AND (rp.empresa_cuit IS NULL OR NOT EXISTS (
                                   SELECT 1 FROM compras.buscar_empresas_cotizacion(
                                       rp.empresa_cuit, NULL, rp.empresa_sucursal, 1)
                               )))
                       )
                ) THEN
                    RAISE EXCEPTION
                        'La Empresa adjudicada debe tener nombre y una identidad fiscal valida cuando este informada.';
                END IF;
            END IF;$nuevo$;
    IF length(v_def) - length(replace(v_def, v_viejo, '')) <> length(v_viejo) THEN
        RAISE EXCEPTION 'Variante desconocida de adaptar_validacion. No se reemplazo su cuerpo.';
    END IF;
    v_def := replace(v_def, v_viejo, v_nuevo);

    EXECUTE v_def;
END;
$adaptar_validacion$;$sentencia_compras_059$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 11/14: docs/sql/20261020_normalizar_drogas_compras.sql';
        v_paso := 60;
        v_comando := 'CREATE OR REPLACE FUNCTION farmacia.buscar_medicamentos_droga(';
        EXECUTE replace($sentencia_compras_060$

-- ===== BLOQUE 11/14: docs/sql/20261020_normalizar_drogas_compras.sql =====
-- SHA256 original (bytes): 1beab9206638a5429b7fb64b8d0d6d57d35aaa88792b74f5e2a8130f80c09ebd
-- Solo se retiraron BEGIN/COMMIT exteriores; cuerpo funcional conservado.

-- Normalizacion de tildes y mayusculas en la busqueda de drogas de Compras.
-- Devuelve todos los troqueles coincidentes, sin paginacion.
-- Reutiliza translate(upper(...)) del nomenclador de Autorizaciones.
-- Conserva firma, troqueles, fecha y bajas del buscador OSPIM.


CREATE OR REPLACE FUNCTION farmacia.buscar_medicamentos_droga(
    droga_v character varying)
RETURNS TABLE(troquel numeric)
AS $BODY$
BEGIN
    return query
    select distinct m.troquel
    from public.medicamentos m
    where droga_v is not null
    and droga_v <> ''
    and translate(
        upper(m.droga),
        chr(193) || chr(201) || chr(205) || chr(211)
            || chr(218) || chr(220) || chr(209) || chr(192)
            || chr(200) || chr(204) || chr(210) || chr(217)
            || chr(196) || chr(203) || chr(207) || chr(214)
            || chr(220) || chr(194) || chr(202) || chr(206)
            || chr(212) || chr(219),
        'AEIOUUNAEIOUAEIOUAEIOU'
    ) like '%' || translate(
        upper(droga_v),
        chr(193) || chr(201) || chr(205) || chr(211)
            || chr(218) || chr(220) || chr(209) || chr(192)
            || chr(200) || chr(204) || chr(210) || chr(217)
            || chr(196) || chr(203) || chr(207) || chr(214)
            || chr(220) || chr(194) || chr(202) || chr(206)
            || chr(212) || chr(219),
        'AEIOUUNAEIOUAEIOUAEIOU'
    ) || '%'
    and m.fecha <= current_date
    and m.baja_fecha is null;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100
ROWS 1000;$sentencia_compras_060$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 12/14: docs/sql/compras/20261020_prestadores_visibles_autorizacion.sql';
        v_paso := 61;
        v_comando := 'DO $prestadores_visibles$';
        EXECUTE replace($sentencia_compras_061$

-- ===== BLOQUE 12/14: docs/sql/compras/20261020_prestadores_visibles_autorizacion.sql =====
-- SHA256 original (bytes): 6ef3136166caae03b34f216c83c37d1f211fb1e678ea0f3ad1571b453e9eed0c
-- Solo se retiraron BEGIN/COMMIT exteriores; cuerpo funcional conservado.

-- Compras: conservar los prestadores notificados en PARA AUTORIZAR y AUTORIZADO.
-- Referencias: listar_prestadores_enviados vigente y migracion de autorizacion
-- 20261013_autorizacion_requerimiento.sql (parche guardado de funcion instalada).
-- No modifica datos, roles ni transiciones. Ejecutar antes del smoke test.
-- Conserva el cuerpo instalado y cambia unicamente el predicado conocido.


DO $prestadores_visibles$
DECLARE
    v_funcion REGPROCEDURE;
    v_definicion TEXT;
    v_normalizada TEXT;
    v_actualizada TEXT;
BEGIN
    v_funcion := to_regprocedure(
        'compras.listar_prestadores_enviados(integer,integer)'
    );

    IF v_funcion IS NULL THEN
        RAISE EXCEPTION
            'No existe compras.listar_prestadores_enviados(integer,integer).';
    END IF;

    v_definicion := pg_get_functiondef(v_funcion);
    v_normalizada := regexp_replace(
        lower(v_definicion), '[[:space:]]', '', 'g'
    );

    IF position('r.estadoin(2,3,4,5,6,7,99)' IN v_normalizada) > 0 THEN
        RAISE NOTICE
            'listar_prestadores_enviados ya contempla los estados 6 y 7.';
        RETURN;
    END IF;

    IF position('r.estadoin(2,3,4,5,99)' IN v_normalizada) = 0 THEN
        RAISE EXCEPTION
            'Predicado de estados desconocido en listar_prestadores_enviados. Revisar la funcion instalada sin reemplazar reglas ajenas.';
    END IF;

    v_actualizada := regexp_replace(
        v_definicion,
        'r[.]estado[[:space:]]+IN[[:space:]]*[(][[:space:]]*2[[:space:]]*,[[:space:]]*3[[:space:]]*,[[:space:]]*4[[:space:]]*,[[:space:]]*5[[:space:]]*,[[:space:]]*99[[:space:]]*[)]',
        'r.estado IN (2, 3, 4, 5, 6, 7, 99)',
        'i'
    );

    IF v_actualizada = v_definicion THEN
        RAISE EXCEPTION
            'No se pudo ampliar el predicado de listar_prestadores_enviados.';
    END IF;

    EXECUTE v_actualizada;
END;
$prestadores_visibles$;$sentencia_compras_061$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 13/14: docs/sql/compras/20261021_observacion_interna_autorizador.sql';
        v_paso := 62;
        v_comando := 'CREATE OR REPLACE FUNCTION compras.guardar_observacion_interna_requerimiento(';
        EXECUTE replace($sentencia_compras_062$

-- ===== BLOQUE 13/14: docs/sql/compras/20261021_observacion_interna_autorizador.sql =====
-- SHA256 original (bytes): dd5ab0e32b95fb67651ca04b460de72e477d30ff17fb166f66f57aacdbcda82d
-- Solo se retiraron BEGIN/COMMIT exteriores; cuerpo funcional conservado.

-- Compras: guardado exclusivo de observacion interna en requerimientos activos.
-- Estados habilitados 1 a 7; no modifica estructura, detalles ni estado.
-- El permiso AUTORIZAR_COMPRAS se valida en la Action; la funcion controla estado y baja.
-- Requiere la columna observacion_interna (migracion 20261008).
-- Instalar antes del Java/JSP de esta entrega. No aplicada a QA por Codex.


CREATE OR REPLACE FUNCTION compras.guardar_observacion_interna_requerimiento(
    p_id_requerimiento INTEGER,
    p_observacion_interna TEXT,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
DECLARE
    v_id INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION 'Debe informar el requerimiento de compra que desea editar.';
    END IF;

    v_usuario := compras.normalizar_usuario(p_usuario);
    UPDATE compras.requerimiento
       SET observacion_interna = NULLIF(btrim(p_observacion_interna), ''),
           modi_fecha = now(),
           modi_usr = v_usuario
     WHERE id_requerimiento = p_id_requerimiento
       AND estado IN (1, 2, 3, 4, 5, 6, 7)
       AND baja_fecha IS NULL
     RETURNING id_requerimiento INTO v_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'La observacion interna no puede modificarse en el estado actual. Actualice la pantalla.';
    END IF;
    RETURN v_id;
END;
$func$
LANGUAGE plpgsql;$sentencia_compras_062$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 14/14: docs/sql/compras/20261013_insumos_diabetes_nomenclador.sql';
        v_paso := 63;
        v_comando := 'SELECT t.id_tipo_prestacion, t.descripcion, t.id_sector,';
        EXECUTE replace($sentencia_compras_063$

-- ===== BLOQUE 14/14: docs/sql/compras/20261013_insumos_diabetes_nomenclador.sql =====
-- SHA256 original (bytes): 54ac776a1a7caf24d11c130aa152e84300dabb21295301db788d53f27ed4a61b
-- Regla funcional confirmada por el usuario el 21/10/2026: incluir DIABETES solo 10.
-- El encabezado historico del ejemplo se conserva como parte de la fuente original.
-- Solo se retiraron BEGIN/COMMIT exteriores; cuerpo funcional conservado.

-- EJEMPLO OPERATIVO SEPARADO, NO EJECUTADO POR CODEX.
-- PostgreSQL 9.6. ISO-8859-1 sin BOM.
-- SOLO ejecutar despues de confirmar funcionalmente que INSUMOS DIABETES
-- debe buscar y guardar exclusivamente el nomenclador 10. Su nombre no
-- implica esa regla. Este cambio NO forma parte de la migracion compatible.
-- No modifica compras.tipo_prestacion ni public.prestador_rubro.
-- El ID se resuelve de nuevo; NO supone que sea 6 ni el 8 observado el 13/10.

-- 1. Resolver el ID real y verificar el sector antes de configurar.
SELECT t.id_tipo_prestacion, t.descripcion, t.id_sector,
       s.descripcion AS sector, s.activo, s.baja_fecha,
       CASE WHEN EXISTS (
           SELECT 1 FROM compras.sector_nomenclador c
           WHERE c.id_sector = t.id_sector
             AND c.id_tipo_prestacion = t.id_tipo_prestacion
       ) THEN 'ESPECIFICA'
         WHEN EXISTS (
           SELECT 1 FROM compras.sector_nomenclador c
           WHERE c.id_sector = t.id_sector AND c.id_tipo_prestacion IS NULL
       ) THEN 'GENERAL HEREDADA' ELSE 'SIN CONFIGURACION' END AS origen
FROM compras.tipo_prestacion t
JOIN compras.sector_requerimiento s ON s.id_sector = t.id_sector
WHERE compras.normalizar_sector(t.descripcion) = 'INSUMOS DIABETES';$sentencia_compras_063$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 14/14: docs/sql/compras/20261013_insumos_diabetes_nomenclador.sql';
        v_paso := 64;
        v_comando := 'DO $configurar_diabetes$';
        EXECUTE replace($sentencia_compras_064$

-- 2. Cambio de negocio explicito: reemplazar SU conjunto especifico por 10.
DO $configurar_diabetes$
DECLARE
    v_rubro SMALLINT;
    v_sector INTEGER;
    v_cantidad INTEGER;
BEGIN
    SELECT count(*), min(t.id_tipo_prestacion), min(t.id_sector)
    INTO v_cantidad, v_rubro, v_sector
    FROM compras.tipo_prestacion t
    JOIN compras.sector_requerimiento s ON s.id_sector = t.id_sector
    WHERE compras.normalizar_sector(t.descripcion) = 'INSUMOS DIABETES'
      AND compras.normalizar_sector(s.descripcion) = 'PRESTACIONES MEDICAS'
      AND s.activo = TRUE AND s.baja_fecha IS NULL;
    IF v_cantidad <> 1 THEN
        RAISE EXCEPTION 'Se requiere un unico rubro INSUMOS DIABETES perteneciente al sector activo PRESTACIONES MEDICAS; encontrados: %.', v_cantidad;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM public.tipo_nomenclador WHERE id_tipo_nomenclador = 10) THEN
        RAISE EXCEPTION 'No existe el tipo real de nomenclador 10 en el catalogo.';
    END IF;
    -- Elimina solo asociaciones propias de ESTE rubro. No modifica la
    -- configuracion general, otros rubros ni prestaciones historicas.
    DELETE FROM compras.sector_nomenclador
    WHERE id_sector = v_sector AND id_tipo_prestacion = v_rubro;
    INSERT INTO compras.sector_nomenclador
        (id_sector, id_tipo_prestacion, id_tipo_nomenclador)
    VALUES (v_sector, v_rubro, 10);
    IF (SELECT count(*) FROM compras.resolver_sector_nomenclador(v_sector, v_rubro)) <> 1 THEN
        RAISE EXCEPTION 'No quedo una unica asociacion efectiva para INSUMOS DIABETES.';
    END IF;
END;
$configurar_diabetes$;$sentencia_compras_064$, E'\r\n', E'\n');

        v_bloque := 'BLOQUE 14/14: docs/sql/compras/20261013_insumos_diabetes_nomenclador.sql';
        v_paso := 65;
        v_comando := 'SELECT t.id_tipo_prestacion, t.descripcion, s.id_sector,';
        EXECUTE replace($sentencia_compras_065$

-- 3. Confirmar el conjunto efectivo: solo 10, sin sumar el general.
SELECT t.id_tipo_prestacion, t.descripcion, s.id_sector,
       s.descripcion AS sector, 'ESPECIFICA'::VARCHAR AS origen,
       c.id_tipo_nomenclador, tn.descripcion AS nomenclador,
       c.marca_rein_liq, c.codigo_nomenclador
FROM compras.tipo_prestacion t
JOIN compras.sector_requerimiento s ON s.id_sector = t.id_sector
CROSS JOIN LATERAL compras.resolver_sector_nomenclador(s.id_sector, t.id_tipo_prestacion) c
LEFT JOIN public.tipo_nomenclador tn ON tn.id_tipo_nomenclador = c.id_tipo_nomenclador
WHERE compras.normalizar_sector(t.descripcion) = 'INSUMOS DIABETES'
  AND compras.normalizar_sector(s.descripcion) = 'PRESTACIONES MEDICAS'
  AND s.activo = TRUE AND s.baja_fecha IS NULL;$sentencia_compras_065$, E'\r\n', E'\n');

        -- OMITIDO PASO 66: Validacion final omitida por indicacion expresa del usuario.

        INSERT INTO pg_temp.codex_compras_parcial_20261021
            (resultado,bloque,paso,comando,sqlstate,mensaje)
        VALUES ('INSTALACION_PARCIAL_CONFIRMADA',v_bloque,v_paso,v_comando,'00000',
                '62 sentencias aplicadas. Pasos omitidos:32,33,34,66. Sobrecarga y validacion final pendientes por pedido del usuario.');
    EXCEPTION WHEN OTHERS THEN
        GET STACKED DIAGNOSTICS
            v_estado = RETURNED_SQLSTATE,
            v_mensaje = MESSAGE_TEXT,
            v_detalle = PG_EXCEPTION_DETAIL,
            v_sugerencia = PG_EXCEPTION_HINT,
            v_contexto = PG_EXCEPTION_CONTEXT;
        -- Al entrar aqui PostgreSQL ya revirtio todo el bloque protegido.
        INSERT INTO pg_temp.codex_compras_parcial_20261021
            (resultado,bloque,paso,comando,sqlstate,mensaje,detalle,sugerencia,contexto)
        VALUES ('ERROR_MIGRACION_REVERTIDA',v_bloque,v_paso,v_comando,
                v_estado,v_mensaje,v_detalle,v_sugerencia,v_contexto);
    END;
END;
$ejecucion_compras_parcial$;

COMMIT;

-- Ultima sentencia: devuelve FILAS, no depende de la visualizacion de NOTICE.
SELECT r.resultado, '32,33,34,66'::TEXT AS pasos_omitidos,
       'Sobrecarga de 14 argumentos, sus dos reemplazos llamadores y validacion final'::TEXT AS pendientes,
       current_database() AS base, inet_server_addr() AS servidor,
       inet_server_port() AS puerto, current_setting('server_version') AS version,
       current_user AS usuario, pg_backend_pid() AS conexion,
       r.bloque,r.paso,r.sqlstate,r.mensaje,r.detalle,r.sugerencia,r.comando,r.contexto,r.fecha
FROM pg_temp.codex_compras_parcial_20261021 r;

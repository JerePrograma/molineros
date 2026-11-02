-- Compras: historial de reasignaciones de afiliado en borrador.
-- Referencia legacy: autorizaciones.nomenclador_bitacora, escrita por
-- autorizaciones.update_nomenclador con valores/operacion/fecha/usuario.
-- Caller: NomencladorServiceImpl.updateNomenclador(..., Connection), lineas 585-628.
-- Se adapta el INSERT explicito a JDBC Compras con la misma conexion del UPDATE.
-- Fecha timestamp y usuario VARCHAR(100) respetan la auditoria de Compras.
-- No instala funciones ni triggers. No modifica requerimientos ni documentos.
-- Aplicar antes de habilitar la reasignacion Java; no se aplica desde la app.
BEGIN;
SET LOCAL lock_timeout = '2s';
SET LOCAL statement_timeout = '15s';
-- Referencia: 20261030_refactor_java_diabetes_15_catalogo_qa.sql, catalogos/secuencias.
SET LOCAL search_path = pg_catalog,public,compras;

DO $precondicion$
BEGIN
    IF host(inet_server_addr()) IS DISTINCT FROM '12.1.1.12'
       OR current_database() IS DISTINCT FROM 'devmolineros' THEN
        RAISE EXCEPTION 'Destino incorrecto: este historial solo se instala en QA 12.1.1.12/devmolineros.';
    END IF;
    IF to_regclass('compras.requerimiento') IS NULL THEN
        RAISE EXCEPTION 'Falta compras.requerimiento. No se puede instalar el historial de afiliados.';
    END IF;
END;
$precondicion$;

CREATE TABLE IF NOT EXISTS compras.requerimiento_afiliado_historial (
    id_historial BIGSERIAL NOT NULL,
    id_requerimiento INTEGER NOT NULL,
    afiliado_cuil_titular_anterior CHARACTER VARYING,
    afiliado_int_anterior INTEGER,
    afiliado_cuil_titular_nuevo CHARACTER VARYING NOT NULL,
    afiliado_int_nuevo INTEGER NOT NULL,
    modi_usr CHARACTER VARYING(100) NOT NULL,
    modi_fecha TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT requerimiento_afiliado_historial_pkey PRIMARY KEY (id_historial),
    CONSTRAINT requerimiento_afiliado_historial_requerimiento_fk
        FOREIGN KEY (id_requerimiento) REFERENCES compras.requerimiento (id_requerimiento)
);

-- Una tabla preexistente incompatible se informa; nunca se altera ni se reemplaza.
DO $validacion$
DECLARE
    v_tabla OID := 'compras.requerimiento_afiliado_historial'::regclass;
    v_id_historial SMALLINT;
    v_id_requerimiento SMALLINT;
    v_id_referenciado SMALLINT;
    v_secuencia REGCLASS;
    v_default OID;
    v_expresion TEXT;
BEGIN
    IF EXISTS (
        SELECT 1
        FROM (VALUES
            ('id_historial', 'bigint', TRUE),
            ('id_requerimiento', 'integer', TRUE),
            ('afiliado_cuil_titular_anterior', 'character varying', FALSE),
            ('afiliado_int_anterior', 'integer', FALSE),
            ('afiliado_cuil_titular_nuevo', 'character varying', TRUE),
            ('afiliado_int_nuevo', 'integer', TRUE),
            ('modi_usr', 'character varying(100)', TRUE),
            ('modi_fecha', 'timestamp without time zone', TRUE)
        ) AS esperado(nombre, tipo, requerido)
        LEFT JOIN pg_attribute a
            ON a.attrelid = v_tabla AND a.attname = esperado.nombre
               AND a.attnum > 0 AND NOT a.attisdropped
        WHERE a.attnum IS NULL
           OR format_type(a.atttypid, a.atttypmod) <> esperado.tipo
           OR a.attnotnull <> esperado.requerido
    ) OR (SELECT COUNT(*) FROM pg_attribute
          WHERE attrelid = v_tabla AND attnum > 0 AND NOT attisdropped) <> 8 THEN
        RAISE EXCEPTION 'compras.requerimiento_afiliado_historial tiene columnas incompatibles. Revisar antes de instalar.';
    END IF;

    SELECT attnum INTO v_id_historial FROM pg_attribute
    WHERE attrelid = v_tabla AND attname = 'id_historial';
    SELECT attnum INTO v_id_requerimiento FROM pg_attribute
    WHERE attrelid = v_tabla AND attname = 'id_requerimiento';
    SELECT attnum INTO v_id_referenciado FROM pg_attribute
    WHERE attrelid = 'compras.requerimiento'::regclass AND attname = 'id_requerimiento';

    IF NOT EXISTS (SELECT 1 FROM pg_constraint
        WHERE conrelid = v_tabla AND contype = 'p'
          AND conkey = ARRAY[v_id_historial])
       OR NOT EXISTS (SELECT 1 FROM pg_constraint
        WHERE conrelid = v_tabla AND contype = 'f' AND convalidated
          AND confupdtype = 'a' AND confdeltype = 'a'
          AND conkey = ARRAY[v_id_requerimiento]
          AND confrelid = 'compras.requerimiento'::regclass
          AND confkey = ARRAY[v_id_referenciado]) THEN
        RAISE EXCEPTION 'El historial de afiliados no tiene la PK/FK esperada. Revisar antes de instalar.';
    END IF;

    -- Inspeccionar propiedad y DEFAULT; no consumir ni reparar secuencias existentes.
    IF (SELECT COUNT(*) FROM pg_depend propiedad
        JOIN pg_class secuencia ON secuencia.oid = propiedad.objid AND secuencia.relkind = 'S'
        WHERE propiedad.classid = 'pg_class'::regclass AND propiedad.objsubid = 0
          AND propiedad.refclassid = 'pg_class'::regclass
          AND propiedad.refobjid = v_tabla AND propiedad.refobjsubid = v_id_historial
          AND propiedad.deptype = 'a') <> 1 THEN
        RAISE EXCEPTION 'El identificador del historial no tiene una unica secuencia propia.';
    END IF;

    v_secuencia := pg_get_serial_sequence('compras.requerimiento_afiliado_historial', 'id_historial')::regclass;
    IF v_secuencia IS NULL OR NOT EXISTS (SELECT 1 FROM pg_depend propiedad
        WHERE propiedad.classid = 'pg_class'::regclass AND propiedad.objid = v_secuencia::oid
          AND propiedad.objsubid = 0 AND propiedad.refclassid = 'pg_class'::regclass
          AND propiedad.refobjid = v_tabla AND propiedad.refobjsubid = v_id_historial
          AND propiedad.deptype = 'a') THEN
        RAISE EXCEPTION 'La secuencia del historial no pertenece a su identificador.';
    END IF;

    SELECT defecto.oid, pg_get_expr(defecto.adbin, defecto.adrelid)
    INTO v_default, v_expresion
    FROM pg_attrdef defecto
    WHERE defecto.adrelid = v_tabla AND defecto.adnum = v_id_historial;

    IF v_default IS NULL
       OR v_expresion IS DISTINCT FROM format('nextval(%L::regclass)', v_secuencia::text)
       OR (SELECT COUNT(*) FROM pg_depend referencia
           JOIN pg_class secuencia ON secuencia.oid = referencia.refobjid AND secuencia.relkind = 'S'
           WHERE referencia.classid = 'pg_attrdef'::regclass AND referencia.objid = v_default
             AND referencia.objsubid = 0 AND referencia.refclassid = 'pg_class'::regclass
             AND referencia.refobjsubid = 0 AND referencia.deptype = 'n') <> 1
       OR NOT EXISTS (SELECT 1 FROM pg_depend referencia
           WHERE referencia.classid = 'pg_attrdef'::regclass AND referencia.objid = v_default
             AND referencia.objsubid = 0 AND referencia.refclassid = 'pg_class'::regclass
             AND referencia.refobjid = v_secuencia::oid AND referencia.refobjsubid = 0
             AND referencia.deptype = 'n') THEN
        RAISE EXCEPTION 'El DEFAULT del historial no usa exclusivamente su secuencia propia.';
    END IF;
END;
$validacion$;

COMMIT;
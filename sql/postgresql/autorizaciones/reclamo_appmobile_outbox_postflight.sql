\set ON_ERROR_STOP on

DO $$
BEGIN
    IF to_regclass(
        'autorizaciones.reclamo_appmobile_outbox'
    ) IS NULL THEN
        RAISE EXCEPTION
            'No existe autorizaciones.reclamo_appmobile_outbox.';
    END IF;
END;
$$;

DO $$
DECLARE
    missing_columns text;
BEGIN
    SELECT string_agg(
        expected.column_name,
        ', ' ORDER BY expected.column_name
    )
    INTO missing_columns
    FROM (
        VALUES
            ('id', 'bigint', 'NO'),
            ('id_reclamo', 'integer', 'NO'),
            ('id_reintegro_app', 'integer', 'NO'),
            ('estado_destino', 'character varying', 'NO'),
            ('estado_proceso', 'character varying', 'NO'),
            ('intentos', 'integer', 'NO'),
            ('proximo_intento', 'timestamp without time zone', 'NO'),
            ('bloqueado_hasta', 'timestamp without time zone', 'YES'),
            ('ultimo_error', 'character varying', 'YES'),
            ('creado_en', 'timestamp without time zone', 'NO'),
            ('actualizado_en', 'timestamp without time zone', 'NO'),
            ('procesado_en', 'timestamp without time zone', 'YES')
    ) AS expected(column_name, data_type, is_nullable)
    WHERE NOT EXISTS (
        SELECT 1
        FROM information_schema.columns actual
        WHERE actual.table_schema = 'autorizaciones'
          AND actual.table_name = 'reclamo_appmobile_outbox'
          AND actual.column_name = expected.column_name
          AND actual.data_type = expected.data_type
          AND actual.is_nullable = expected.is_nullable
    );

    IF missing_columns IS NOT NULL THEN
        RAISE EXCEPTION
            'Columnas ausentes o incompatibles: %',
            missing_columns;
    END IF;
END;
$$;

DO $$
DECLARE
    definition text;
BEGIN
    SELECT indexdef
    INTO definition
    FROM pg_indexes
    WHERE schemaname = 'autorizaciones'
      AND tablename = 'reclamo_appmobile_outbox'
      AND indexname = 'ux_reclamo_appmobile_outbox_pendiente';

    IF definition IS NULL THEN
        RAISE EXCEPTION
            'Falta ux_reclamo_appmobile_outbox_pendiente.';
    END IF;

    IF position('UNIQUE INDEX' IN upper(definition)) = 0
       OR position('(id_reintegro_app, estado_destino)' IN definition) = 0
       OR position('WHERE (procesado_en IS NULL)' IN definition) = 0 THEN
        RAISE EXCEPTION
            'Índice único parcial incompatible: %',
            definition;
    END IF;
END;
$$;

DO $$
DECLARE
    definition text;
BEGIN
    SELECT indexdef
    INTO definition
    FROM pg_indexes
    WHERE schemaname = 'autorizaciones'
      AND tablename = 'reclamo_appmobile_outbox'
      AND indexname = 'ix_reclamo_appmobile_outbox_proceso';

    IF definition IS NULL THEN
        RAISE EXCEPTION
            'Falta ix_reclamo_appmobile_outbox_proceso.';
    END IF;

    IF position(
        '(estado_proceso, proximo_intento, id)'
        IN definition
    ) = 0 THEN
        RAISE EXCEPTION
            'Índice operativo incompatible: %',
            definition;
    END IF;
END;
$$;

DO $$
DECLARE
    missing_constraints text;
BEGIN
    SELECT string_agg(required.name, ', ' ORDER BY required.name)
    INTO missing_constraints
    FROM (
        VALUES
            ('ck_reclamo_appmobile_outbox_estado'),
            ('ck_reclamo_appmobile_outbox_reintegro'),
            ('ck_reclamo_appmobile_outbox_intentos')
    ) AS required(name)
    WHERE NOT EXISTS (
        SELECT 1
        FROM pg_constraint constraint_data
        JOIN pg_class table_data
          ON table_data.oid = constraint_data.conrelid
        JOIN pg_namespace schema_data
          ON schema_data.oid = table_data.relnamespace
        WHERE schema_data.nspname = 'autorizaciones'
          AND table_data.relname = 'reclamo_appmobile_outbox'
          AND constraint_data.conname = required.name
    );

    IF missing_constraints IS NOT NULL THEN
        RAISE EXCEPTION
            'Restricciones faltantes: %',
            missing_constraints;
    END IF;
END;
$$;

SELECT
    has_table_privilege(
        current_user,
        'autorizaciones.reclamo_appmobile_outbox',
        'SELECT'
    ) AS can_select,
    has_table_privilege(
        current_user,
        'autorizaciones.reclamo_appmobile_outbox',
        'INSERT'
    ) AS can_insert,
    has_table_privilege(
        current_user,
        'autorizaciones.reclamo_appmobile_outbox',
        'UPDATE'
    ) AS can_update;

SELECT
    count(*) FILTER (WHERE procesado_en IS NULL) AS pending,
    count(*) FILTER (
        WHERE estado_proceso = 'PROCESANDO'
          AND procesado_en IS NULL
    ) AS processing,
    count(*) FILTER (
        WHERE estado_proceso = 'PROCESADO'
    ) AS processed
FROM autorizaciones.reclamo_appmobile_outbox;

SELECT 'OUTBOX_POSTFLIGHT_OK' AS result;

\set ON_ERROR_STOP on

SELECT
    current_database() AS database_name,
    current_user AS database_user,
    current_setting('server_version') AS server_version,
    current_setting('server_version_num')::integer AS server_version_num;

DO $$
DECLARE
    version_num integer;
BEGIN
    version_num := current_setting('server_version_num')::integer;
    IF version_num < 90500 THEN
        RAISE EXCEPTION
            'PostgreSQL 9.5 o superior requerido; versión numérica actual: %',
            version_num;
    END IF;
END;
$$;

SELECT
    has_schema_privilege(current_user, 'autorizaciones', 'USAGE')
        AS can_use_schema,
    has_schema_privilege(current_user, 'autorizaciones', 'CREATE')
        AS can_create_in_schema;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_namespace
        WHERE nspname = 'autorizaciones'
    ) THEN
        RAISE NOTICE
            'El schema autorizaciones no existe; la migración intentará crearlo.';
    ELSIF NOT has_schema_privilege(
        current_user,
        'autorizaciones',
        'USAGE'
    ) THEN
        RAISE EXCEPTION
            'El usuario % no tiene USAGE sobre autorizaciones.',
            current_user;
    ELSIF NOT has_schema_privilege(
        current_user,
        'autorizaciones',
        'CREATE'
    ) AND to_regclass(
        'autorizaciones.reclamo_appmobile_outbox'
    ) IS NULL THEN
        RAISE EXCEPTION
            'El usuario % no puede crear la tabla en autorizaciones.',
            current_user;
    END IF;
END;
$$;

SELECT
    to_regclass('autorizaciones.reclamo_appmobile_outbox')
        AS existing_outbox_table;

DO $$
DECLARE
    missing_columns text;
BEGIN
    IF to_regclass(
        'autorizaciones.reclamo_appmobile_outbox'
    ) IS NULL THEN
        RETURN;
    END IF;

    SELECT string_agg(required.column_name, ', ' ORDER BY required.column_name)
    INTO missing_columns
    FROM (
        VALUES
            ('id'),
            ('id_reclamo'),
            ('id_reintegro_app'),
            ('estado_destino'),
            ('estado_proceso'),
            ('intentos'),
            ('proximo_intento'),
            ('bloqueado_hasta'),
            ('ultimo_error'),
            ('creado_en'),
            ('actualizado_en'),
            ('procesado_en')
    ) AS required(column_name)
    WHERE NOT EXISTS (
        SELECT 1
        FROM information_schema.columns c
        WHERE c.table_schema = 'autorizaciones'
          AND c.table_name = 'reclamo_appmobile_outbox'
          AND c.column_name = required.column_name
    );

    IF missing_columns IS NOT NULL THEN
        RAISE EXCEPTION
            'La tabla outbox existente es incompatible. Columnas faltantes: %',
            missing_columns;
    END IF;
END;
$$;

DO $$
DECLARE
    duplicate_count bigint;
BEGIN
    IF to_regclass(
        'autorizaciones.reclamo_appmobile_outbox'
    ) IS NULL THEN
        RETURN;
    END IF;

    SELECT count(*)
    INTO duplicate_count
    FROM (
        SELECT id_reintegro_app, estado_destino
        FROM autorizaciones.reclamo_appmobile_outbox
        WHERE procesado_en IS NULL
        GROUP BY id_reintegro_app, estado_destino
        HAVING count(*) > 1
    ) duplicated;

    IF duplicate_count > 0 THEN
        RAISE EXCEPTION
            'Existen % claves externas pendientes duplicadas. Reconciliar antes de crear el índice único.',
            duplicate_count;
    END IF;
END;
$$;

SELECT
    CASE
        WHEN to_regclass(
            'autorizaciones.reclamo_appmobile_outbox'
        ) IS NULL THEN 0
        ELSE (
            SELECT count(*)
            FROM autorizaciones.reclamo_appmobile_outbox
            WHERE procesado_en IS NULL
        )
    END AS pending_before_migration;

SELECT 'OUTBOX_PREFLIGHT_OK' AS result;

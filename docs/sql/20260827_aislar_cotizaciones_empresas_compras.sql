-- Alinea una base Compras instalada con las cotizaciones de Empresas.
-- Solo modifica objetos propios del schema compras. La tabla externa de
-- Empresas se consulta en modo de solo lectura.
-- Ejecutar con:
-- psql -X -v ON_ERROR_STOP=1 -f 20260827_aislar_cotizaciones_empresas_compras.sql

\encoding LATIN1

BEGIN;

DO $precondition$
BEGIN
    IF to_regclass('compras.requerimiento_presupuesto') IS NULL THEN
        RAISE EXCEPTION
            'No existe compras.requerimiento_presupuesto.';
    END IF;

    IF to_regprocedure(
           'compras.normalizar_sector(character varying)'
       ) IS NULL THEN

        RAISE EXCEPTION
            'No existe compras.normalizar_sector(character varying).';
    END IF;
END;
$precondition$;

ALTER TABLE compras.requerimiento_presupuesto
    ADD COLUMN IF NOT EXISTS empresa_cuit VARCHAR(11),
    ADD COLUMN IF NOT EXISTS empresa_sucursal VARCHAR(6),
    ADD COLUMN IF NOT EXISTS descripcion_empresa VARCHAR(200);

ALTER TABLE compras.requerimiento_presupuesto
    DROP CONSTRAINT IF EXISTS ck_compras_presupuesto_tipo_documento,
    DROP CONSTRAINT IF EXISTS ck_compras_presupuesto_prestador,
    DROP CONSTRAINT IF EXISTS ck_compras_orden_medica_numero_receta,
    DROP CONSTRAINT IF EXISTS ck_compras_orden_medica_datos,
    DROP CONSTRAINT IF EXISTS ck_compras_presupuesto_empresa_datos;

ALTER TABLE compras.requerimiento_presupuesto
    ADD CONSTRAINT ck_compras_presupuesto_tipo_documento
        CHECK (tipo_documento IN (1, 2, 3)),

    ADD CONSTRAINT ck_compras_presupuesto_prestador
        CHECK (
            (
                tipo_documento = 1
                AND id_prestador IS NOT NULL
                AND id_prestador > 0
            )
            OR (
                tipo_documento IN (2, 3)
                AND id_prestador IS NULL
            )
        ),

    ADD CONSTRAINT ck_compras_orden_medica_numero_receta
        CHECK (
            (
                tipo_documento IN (1, 3)
                AND numero_receta IS NULL
            )
            OR (
                tipo_documento = 2
                AND (
                    numero_receta IS NULL
                    OR (
                        NULLIF(
                            regexp_replace(
                                upper(btrim(numero_receta)),
                                '[[:space:]]+',
                                '',
                                'g'
                            ),
                            ''
                        ) IS NOT NULL
                        AND numero_receta = regexp_replace(
                            upper(btrim(numero_receta)),
                            '[[:space:]]+',
                            '',
                            'g'
                        )
                    )
                )
            )
        ),

    ADD CONSTRAINT ck_compras_orden_medica_datos
        CHECK (
            tipo_documento = 1
            OR descripcion_prestador IS NULL
        ),

    ADD CONSTRAINT ck_compras_presupuesto_empresa_datos
        CHECK (
            (
                tipo_documento IN (1, 2)
                AND empresa_cuit IS NULL
                AND empresa_sucursal IS NULL
                AND descripcion_empresa IS NULL
            )
            OR (
                tipo_documento = 3
                AND NULLIF(btrim(empresa_cuit), '') IS NOT NULL
                AND NULLIF(btrim(empresa_sucursal), '') IS NOT NULL
                AND NULLIF(btrim(descripcion_empresa), '') IS NOT NULL
                AND empresa_cuit = btrim(empresa_cuit)
                AND empresa_sucursal = btrim(empresa_sucursal)
                AND descripcion_empresa = btrim(descripcion_empresa)
            )
        );

CREATE UNIQUE INDEX IF NOT EXISTS
ux_compras_presupuesto_requerimiento_empresa_activa
    ON compras.requerimiento_presupuesto (
        id_requerimiento,
        empresa_cuit,
        empresa_sucursal
    )
    WHERE baja_fecha IS NULL
      AND tipo_documento = 3;

CREATE FUNCTION compras.buscar_empresas_cotizacion(
    p_cuit VARCHAR,
    p_descripcion VARCHAR,
    p_sucursal VARCHAR,
    p_limite INTEGER
)
RETURNS TABLE (
    cuit VARCHAR,
    sucursal VARCHAR,
    razon_soc VARCHAR
)
AS $func$
    SELECT
        btrim(e.cuit)::VARCHAR,
        btrim(e.sucursal)::VARCHAR,
        btrim(e.razon_soc)::VARCHAR
    FROM informacion_afip.empresa e
    WHERE e.baja_fecha IS NULL
      AND NULLIF(btrim(e.cuit), '') IS NOT NULL
      AND length(btrim(e.cuit)) <= 11
      AND NULLIF(btrim(e.sucursal), '') IS NOT NULL
      AND length(btrim(e.sucursal)) <= 6
      AND NULLIF(btrim(e.razon_soc), '') IS NOT NULL
      AND (
            NULLIF(btrim($1), '') IS NULL
            OR btrim(e.cuit) = btrim($1)
          )
      AND (
            NULLIF(btrim($2), '') IS NULL
            OR upper(e.razon_soc)
                LIKE '%' || upper(btrim($2)) || '%'
          )
      AND (
            NULLIF(btrim($3), '') IS NULL
            OR btrim(e.sucursal) = btrim($3)
          )
    ORDER BY
        e.razon_soc,
        e.cuit,
        e.sucursal
    LIMIT CASE
        WHEN COALESCE($4, 0) <= 0 THEN 100
        ELSE LEAST($4, 100)
    END;
$func$
LANGUAGE sql
STABLE;

CREATE FUNCTION compras.registrar_requerimiento_presupuesto(
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
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION
            'El requerimiento informado no es válido.';
    END IF;

    IF p_tipo_documento IS NULL OR p_tipo_documento <> 3 THEN
        RAISE EXCEPTION
            'El tipo documental no corresponde a una cotización de Empresa.';
    END IF;

    IF p_id_prestador IS NOT NULL
       OR NULLIF(btrim(p_descripcion_prestador), '') IS NOT NULL THEN

        RAISE EXCEPTION
            'Una cotización de Empresa no puede asociarse a un prestador.';
    END IF;

    IF NULLIF(btrim(p_empresa_cuit), '') IS NULL
       OR length(btrim(p_empresa_cuit)) > 11
       OR NULLIF(btrim(p_empresa_sucursal), '') IS NULL
       OR length(btrim(p_empresa_sucursal)) > 6
       OR NULLIF(btrim(p_descripcion_empresa), '') IS NULL
       OR length(btrim(p_descripcion_empresa)) > 200 THEN

        RAISE EXCEPTION
            'La identidad de la Empresa de la cotización no es válida.';
    END IF;

    IF p_dl_group_id IS NULL OR p_dl_group_id <= 0
       OR p_dl_folder_id IS NULL OR p_dl_folder_id < 0
       OR p_dl_file_entry_id IS NULL OR p_dl_file_entry_id <= 0 THEN

        RAISE EXCEPTION
            'La identidad del documento de cotización no es válida.';
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
            'La cotización de Empresa requiere un requerimiento activo '
            'de RRHH o SISTEMAS en estado PENDIENTE.';
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
            'La Empresa ya tiene una cotización activa para este requerimiento.';
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
        btrim(p_descripcion_empresa),
        p_dl_group_id,
        p_dl_folder_id,
        p_dl_file_entry_id,
        NULLIF(btrim(p_dl_file_uuid), ''),
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
LANGUAGE plpgsql;

CREATE FUNCTION compras.baja_cotizacion_empresa_requerimiento(
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
           baja_usr = v_usuario
     WHERE id_requerimiento_presupuesto = p_id_requerimiento_presupuesto
       AND id_requerimiento = p_id_requerimiento
       AND tipo_documento = 3
       AND baja_fecha IS NULL;

    RETURN FOUND;
END;
$func$
LANGUAGE plpgsql;

CREATE FUNCTION compras.reactivar_cotizacion_empresa_requerimiento(
    p_id_requerimiento_presupuesto INTEGER,
    p_id_requerimiento INTEGER
)
RETURNS BOOLEAN
AS $func$
DECLARE
    v_empresa_cuit VARCHAR(11);
    v_empresa_sucursal VARCHAR(6);
    v_estado_requerimiento INTEGER;
    v_sector_descripcion VARCHAR(200);
BEGIN
    IF p_id_requerimiento_presupuesto IS NULL
       OR p_id_requerimiento_presupuesto <= 0
       OR p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN

        RETURN FALSE;
    END IF;

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
            'Las cotizaciones de Empresas solo pueden reactivarse '
            'en requerimientos PENDIENTES de RRHH o SISTEMAS.';
    END IF;

    SELECT
        rp.empresa_cuit,
        rp.empresa_sucursal
      INTO
        v_empresa_cuit,
        v_empresa_sucursal
      FROM compras.requerimiento_presupuesto rp
     WHERE rp.id_requerimiento_presupuesto = p_id_requerimiento_presupuesto
       AND rp.id_requerimiento = p_id_requerimiento
       AND rp.tipo_documento = 3
       AND rp.baja_fecha IS NOT NULL
     FOR UPDATE;

    IF NOT FOUND THEN
        RETURN FALSE;
    END IF;

    IF EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 3
           AND rp.empresa_cuit = v_empresa_cuit
           AND rp.empresa_sucursal = v_empresa_sucursal
           AND rp.baja_fecha IS NULL
           AND rp.id_requerimiento_presupuesto
                <> p_id_requerimiento_presupuesto
    ) THEN
        RAISE EXCEPTION
            'La Empresa ya tiene otra cotización activa para este requerimiento.';
    END IF;

    UPDATE compras.requerimiento_presupuesto
       SET baja_fecha = NULL,
           baja_usr = NULL
     WHERE id_requerimiento_presupuesto = p_id_requerimiento_presupuesto
       AND id_requerimiento = p_id_requerimiento
       AND tipo_documento = 3
       AND baja_fecha IS NOT NULL;

    RETURN FOUND;
END;
$func$
LANGUAGE plpgsql;

COMMIT;

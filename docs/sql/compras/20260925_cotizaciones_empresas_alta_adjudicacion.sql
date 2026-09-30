-- Cotizaciones RRHH/SISTEMAS en alta y Empresa adjudicada.
-- Basado en catalogo real consultado el 25/09/2026, solo lectura y rollback:
-- devmolineros PostgreSQL 9.6.15; compatible con PostgreSQL 9.6.5.
-- listar/get_documentos_requerimiento devuelven SETOF requerimiento_presupuesto
-- con SELECT rp.*: la nueva columna conserva firmas y tipos de retorno.
-- Ya existe ux_compras_presupuesto_requerimiento_empresa_activa por CUIT+sucursal.
-- Este archivo NO fue aplicado. Revisar/ejecutar junto con el despliegue Java/JSP.
-- ISO-8859-1 sin BOM. Para psql: PGCLIENTENCODING=LATIN1 y ON_ERROR_STOP=1.
-- No modifica maestros, estados de Prestador ni datos de cotizaciones existentes.

BEGIN;

DO $precondicion$
BEGIN
    IF to_regprocedure('compras.listar_documentos_requerimiento(integer,integer)') IS NULL
       OR to_regprocedure('compras.get_documento_requerimiento(integer,integer,integer)') IS NULL
       OR to_regprocedure('compras.confirmar_orden_compra_requerimiento(integer,character varying)') IS NULL
       OR to_regprocedure('compras.buscar_empresas_cotizacion(character varying,character varying,character varying,integer)') IS NULL THEN
        RAISE EXCEPTION 'No esta instalado el contrato de cotizaciones de Empresas relevado.';
    END IF;
END;
$precondicion$;

ALTER TABLE compras.requerimiento_presupuesto
    ADD COLUMN IF NOT EXISTS empresa_adjudicada BOOLEAN NOT NULL DEFAULT FALSE;

CREATE UNIQUE INDEX IF NOT EXISTS ux_compras_empresa_adjudicada_activa
    ON compras.requerimiento_presupuesto (id_requerimiento)
    WHERE tipo_documento = 3 AND baja_fecha IS NULL AND empresa_adjudicada;

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
LANGUAGE plpgsql;

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

    SELECT e.razon_soc INTO v_descripcion_empresa
      FROM compras.buscar_empresas_cotizacion(
              p_empresa_cuit, NULL, p_empresa_sucursal, 1) e;
    IF NOT FOUND OR length(v_descripcion_empresa) > 200 THEN
        RAISE EXCEPTION
            'La Empresa no existe o no esta activa en el padron.';
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
        v_descripcion_empresa,
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
LANGUAGE plpgsql;

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
LANGUAGE plpgsql;

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
            'No se encontró el requerimiento.';
END IF;

    IF p_estado_nuevo = v_estado_actual THEN
        RAISE EXCEPTION
            'La transicion al mismo estado no es válida.';
END IF;

    -- La ruta generica tampoco permite saltear las reglas de Empresas.
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
            'El requerimiento fue modificado por otro proceso.';
END IF;
END;
$func$
LANGUAGE plpgsql;

COMMIT;

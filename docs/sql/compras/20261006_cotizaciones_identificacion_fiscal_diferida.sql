-- Identificacion fiscal diferida de cotizaciones de RRHH/SISTEMAS.
-- Referencias: Liquidaciones, prestador externo (nombre obligatorio/CUIT opcional
-- y actualizacion por id); contrato de Compras del 25/09/2026.
-- Requiere el contrato de 20260925_cotizaciones_empresas_alta_adjudicacion.sql.
-- La precondicion informa los objetos faltantes; no exige CHECKs no instalados.
-- PostgreSQL 9.6. No ejecutar separado del despliegue Java/JSP correspondiente.
-- Este archivo NO fue aplicado. ISO-8859-1 sin BOM.
-- Conserva snapshots, adjuntos, adjudicacion y maestros existentes.

BEGIN;

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
$precondicion$;

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
$constraint_empresa$;

-- NULL no es una identidad fiscal. No comparar empresas solamente por nombre.
-- El indice mantiene la clave fiscal legacy y cubre CUIT conocido sin sucursal.
DROP INDEX compras.ux_compras_presupuesto_requerimiento_empresa_activa;
CREATE UNIQUE INDEX ux_compras_presupuesto_requerimiento_empresa_activa
    ON compras.requerimiento_presupuesto (
        id_requerimiento, empresa_cuit, COALESCE(empresa_sucursal, '')
    )
    WHERE baja_fecha IS NULL AND tipo_documento = 3
      AND empresa_cuit IS NOT NULL;

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
LANGUAGE plpgsql;

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
LANGUAGE plpgsql;

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
LANGUAGE plpgsql;

COMMIT;

-- Edicion legacy de Empresa en una cotizacion de RRHH/SISTEMAS.
-- Referencia: view_prestador_externo.jsp (nombre obligatorio y CUIT opcional).
-- Requiere el contrato de identificacion fiscal diferida del 06/10/2026.
-- PostgreSQL 9.6. ISO-8859-1 sin BOM. No aplicado por Codex.
-- Actualiza el mismo presupuesto sin modificar PDF, detalle ni adjudicacion.
-- Conserva completar_empresa_cotizacion y sus callers anteriores.

BEGIN;

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
$precondicion$;

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
LANGUAGE plpgsql;

COMMIT;

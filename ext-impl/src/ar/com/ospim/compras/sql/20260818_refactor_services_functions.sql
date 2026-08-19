-- =====================================================================
-- COMPRAS - Refactor de persistencia Java a funciones PostgreSQL
-- Fecha: 2026-08-18
--
-- Contiene UNICAMENTE funciones nuevas requeridas por el refactor.
-- No crea tablas, indices, constraints ni modifica funciones existentes.
-- PostgreSQL 9.6+
-- =====================================================================

BEGIN;

-- ---------------------------------------------------------------------
-- Reemplaza SQL inline de BusquedaRequerimientoCompraServiceImpl.
-- ---------------------------------------------------------------------

CREATE OR REPLACE FUNCTION compras.buscar_items_historicos_afiliado(
    p_cuil_titular VARCHAR,
    p_inte INTEGER,
    p_id_sector INTEGER,
    p_id_requerimiento_excluir INTEGER,
    p_limite INTEGER
)
RETURNS TABLE (
    id_prestacion INTEGER,
    id_tipo_nomenclador INTEGER,
    codigo VARCHAR,
    descripcion VARCHAR
)
AS $func$
    SELECT
        historico.id_prestacion,
        historico.id_tipo_nomenclador,
        historico.codigo,
        historico.descripcion
    FROM (
        SELECT DISTINCT ON (
            d.id_prestacion,
            d.id_tipo_nomenclador
        )
            d.id_prestacion,
            d.id_tipo_nomenclador,
            NULLIF(BTRIM(d.codigo_nomenclador), '') AS codigo,
            NULLIF(BTRIM(d.descripcion_nomenclador), '') AS descripcion,
            r.alta_fecha AS fecha_requerimiento,
            r.id_requerimiento AS id_requerimiento_origen,
            d.id_detalle AS id_detalle_origen
        FROM compras.requerimiento r
        INNER JOIN compras.requerimiento_detalle d
            ON d.id_requerimiento = r.id_requerimiento
        WHERE r.afiliado_cuil_titular = p_cuil_titular
          AND r.afiliado_int = p_inte
          AND r.id_sector = p_id_sector
          AND r.id_requerimiento <> p_id_requerimiento_excluir
          AND r.baja_fecha IS NULL
          AND d.baja_fecha IS NULL
          AND d.tipo_item = 'NOMENCLADOR'
          AND d.id_prestacion IS NOT NULL
          AND d.id_prestacion > 0
          AND d.id_tipo_nomenclador IS NOT NULL
          AND d.id_tipo_nomenclador > 0
          AND NULLIF(BTRIM(d.codigo_nomenclador), '') IS NOT NULL
          AND NULLIF(BTRIM(d.descripcion_nomenclador), '') IS NOT NULL
        ORDER BY
            d.id_prestacion,
            d.id_tipo_nomenclador,
            r.alta_fecha DESC NULLS LAST,
            r.id_requerimiento DESC,
            d.id_detalle DESC
    ) historico
    ORDER BY
        historico.fecha_requerimiento DESC NULLS LAST,
        historico.id_requerimiento_origen DESC,
        historico.id_detalle_origen DESC
    LIMIT p_limite;
$func$
LANGUAGE sql
STABLE;


CREATE OR REPLACE FUNCTION compras.tiene_situacion_medica_vigente(
    p_cuil_titular VARCHAR,
    p_inte INTEGER
)
RETURNS BOOLEAN
AS $func$
    SELECT EXISTS (
        SELECT 1
        FROM public.afi_situ_medica sm
        WHERE sm.cuil_titular = p_cuil_titular
          AND sm.inte = p_inte
          AND sm.baja_fecha IS NULL
          AND (
              sm.vigen_hasta IS NULL
              OR sm.vigen_hasta > CURRENT_DATE
          )
    );
$func$
LANGUAGE sql
STABLE;


CREATE OR REPLACE FUNCTION compras.listar_prestadores_adjudicados(
    p_id_requerimiento INTEGER
)
RETURNS TABLE (
    id_prestador INTEGER
)
AS $func$
    SELECT DISTINCT d.id_prestador
    FROM compras.requerimiento_detalle d
    WHERE d.id_requerimiento = p_id_requerimiento
      AND d.baja_fecha IS NULL;
$func$
LANGUAGE sql
STABLE;


CREATE OR REPLACE FUNCTION compras.listar_presupuestos_prestador(
    p_id_requerimiento INTEGER,
    p_id_prestador INTEGER
)
RETURNS SETOF compras.requerimiento_presupuesto
AS $func$
    SELECT rp.*
    FROM compras.requerimiento_presupuesto rp
    WHERE rp.id_requerimiento = p_id_requerimiento
      AND rp.id_prestador = p_id_prestador
      AND rp.tipo_documento = 1
      AND rp.baja_fecha IS NULL
    ORDER BY rp.id_requerimiento_presupuesto;
$func$
LANGUAGE sql
STABLE;


-- ---------------------------------------------------------------------
-- Wrapper JDBC para evitar SQL nativo con CAST de arrays en Java.
-- El pool legacy no expone Connection.createArrayOf().
-- ---------------------------------------------------------------------

CREATE OR REPLACE FUNCTION compras.guardar_cotizacion_requerimiento_call(
    p_id_requerimiento INTEGER,
    p_ids_detalle_array VARCHAR,
    p_precios_unitarios_array VARCHAR,
    p_id_prestador INTEGER,
    p_usuario VARCHAR
)
RETURNS INTEGER
AS $func$
    SELECT compras.guardar_cotizacion_requerimiento(
        p_id_requerimiento,
        CAST(p_ids_detalle_array AS INTEGER[]),
        CAST(p_precios_unitarios_array AS NUMERIC[]),
        p_id_prestador,
        p_usuario
    );
$func$
LANGUAGE sql
VOLATILE;


-- ---------------------------------------------------------------------
-- Reemplaza SQL inline del vinculo Compras / Reclamo Prestacional.
-- ---------------------------------------------------------------------

CREATE OR REPLACE FUNCTION
compras.listar_requerimientos_reclamo_prestacional_vinculados(
    p_estado VARCHAR,
    p_ids_requerimientos_array VARCHAR
)
RETURNS SETOF compras.requerimiento_reclamo_prestacional
AS $func$
    SELECT relacion.*
    FROM compras.requerimiento_reclamo_prestacional relacion
    WHERE relacion.estado = p_estado
      AND relacion.id_reclamo_prestacional IS NOT NULL
      AND relacion.id_requerimiento = ANY (
          CAST(p_ids_requerimientos_array AS INTEGER[])
      )
    ORDER BY relacion.id_requerimiento;
$func$
LANGUAGE sql
STABLE;


CREATE OR REPLACE FUNCTION
compras.get_requerimiento_por_reclamo_prestacional(
    p_id_reclamo_prestacional INTEGER,
    p_estado VARCHAR
)
RETURNS SETOF compras.requerimiento_reclamo_prestacional
AS $func$
    SELECT relacion.*
    FROM compras.requerimiento_reclamo_prestacional relacion
    INNER JOIN compras.requerimiento requerimiento
        ON requerimiento.id_requerimiento = relacion.id_requerimiento
    WHERE relacion.id_reclamo_prestacional = p_id_reclamo_prestacional
      AND relacion.estado = p_estado
      AND requerimiento.baja_fecha IS NULL
    ORDER BY relacion.id_requerimiento;
$func$
LANGUAGE sql
STABLE;


CREATE OR REPLACE FUNCTION
compras.bloquear_requerimiento_reclamo_prestacional(
    p_id_requerimiento INTEGER
)
RETURNS BOOLEAN
AS $func$
BEGIN
    PERFORM pg_advisory_xact_lock(
        5391184,
        p_id_requerimiento
    );

    RETURN TRUE;
END;
$func$
LANGUAGE plpgsql
VOLATILE;


CREATE OR REPLACE FUNCTION
compras.get_estado_requerimiento_for_update(
    p_id_requerimiento INTEGER
)
RETURNS INTEGER
AS $func$
DECLARE
    v_estado INTEGER;
BEGIN
    SELECT r.estado
    INTO v_estado
    FROM compras.requerimiento r
    WHERE r.id_requerimiento = p_id_requerimiento
      AND r.baja_fecha IS NULL
    FOR UPDATE;

    RETURN v_estado;
END;
$func$
LANGUAGE plpgsql
VOLATILE;

COMMIT;

-- Compras: excluir gastos automaticos de las sugerencias del historial.
-- Codigos 101002 y 9999009. PostgreSQL 9.6+; ISO-8859-1 sin BOM.
-- Aplicar en la base del portal. Reejecutable; no modifica detalles ni triggers.
-- Filtrar antes del DISTINCT y LIMIT conserva hasta 20 items validos.
BEGIN;

DO $precondicion$
BEGIN
    IF to_regprocedure('compras.buscar_items_historicos_afiliado(character varying,integer,integer,integer,integer)') IS NULL
       OR to_regprocedure('compras.buscar_items_historicos_afiliado_clasificado(character varying,integer,integer,integer,integer)') IS NULL THEN
        RAISE EXCEPTION 'Faltan funciones de historial de Compras; instale primero el esquema vigente.';
    END IF;
END;
$precondicion$;

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
        SELECT DISTINCT ON (d.id_prestacion, d.id_tipo_nomenclador)
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
          AND BTRIM(d.codigo_nomenclador) NOT IN ('101002', '9999009')
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

CREATE OR REPLACE FUNCTION compras.buscar_items_historicos_afiliado_clasificado(
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
    descripcion VARCHAR,
    id_tipo_prestacion INTEGER
)
AS $func$
    SELECT
        historico.id_prestacion,
        historico.id_tipo_nomenclador,
        historico.codigo,
        historico.descripcion,
        historico.id_tipo_prestacion
    FROM (
        SELECT DISTINCT ON (d.id_prestacion, d.id_tipo_nomenclador)
            d.id_prestacion,
            d.id_tipo_nomenclador,
            NULLIF(BTRIM(d.codigo_nomenclador), '')::VARCHAR AS codigo,
            NULLIF(BTRIM(d.descripcion_nomenclador), '')::VARCHAR AS descripcion,
            d.id_tipo_prestacion::INTEGER AS id_tipo_prestacion,
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
          AND BTRIM(d.codigo_nomenclador) NOT IN ('101002', '9999009')
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

COMMIT;

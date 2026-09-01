-- Optimiza el selector de Empresas propio de Compras.
-- No reemplaza funciones legacy ni modifica informacion_afip.empresa.
-- Ejecutar con:
-- psql -X -v ON_ERROR_STOP=1 -f 20260901_optimizar_busqueda_empresas_compras.sql

\encoding LATIN1

BEGIN;

DO $precondition$
BEGIN
    IF to_regclass('compras.requerimiento') IS NULL THEN
        RAISE EXCEPTION 'No existe compras.requerimiento.';
    END IF;

    IF to_regclass('compras.sector_requerimiento') IS NULL THEN
        RAISE EXCEPTION 'No existe compras.sector_requerimiento.';
    END IF;

    IF to_regclass('informacion_afip.empresa') IS NULL THEN
        RAISE EXCEPTION 'No existe informacion_afip.empresa.';
    END IF;

    IF to_regprocedure(
           'compras.normalizar_sector(character varying)'
       ) IS NULL THEN

        RAISE EXCEPTION
            'No existe compras.normalizar_sector(character varying).';
    END IF;

    IF to_regprocedure(
           'compras.buscar_empresas_cotizacion('
           'character varying,character varying,character varying,integer)'
       ) IS NULL THEN

        RAISE EXCEPTION
            'No existe compras.buscar_empresas_cotizacion vigente.';
    END IF;
END;
$precondition$;

CREATE OR REPLACE FUNCTION compras.buscar_empresas_cotizacion_rapida(
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
DECLARE
    v_cuit VARCHAR := NULLIF(btrim(p_cuit), '');
    v_descripcion VARCHAR := NULLIF(btrim(p_descripcion), '');
    v_sucursal VARCHAR := NULLIF(btrim(p_sucursal), '');
    v_limite INTEGER := CASE
        WHEN COALESCE(p_limite, 0) <= 0 THEN 101
        ELSE LEAST(p_limite, 101)
    END;
BEGIN
    IF v_cuit IS NULL
       AND (
            v_descripcion IS NULL
            OR length(v_descripcion) < 3
       ) THEN

        RETURN;
    END IF;

    IF v_cuit IS NOT NULL
       AND (
            length(v_cuit) <> 11
            OR v_cuit !~ '^[0-9]{11}$'
       ) THEN

        RETURN;
    END IF;

    IF v_cuit IS NOT NULL THEN
        RETURN QUERY
        SELECT
            q.cuit,
            q.sucursal,
            q.razon_soc
        FROM (
            SELECT
                btrim(e.cuit)::VARCHAR AS cuit,
                btrim(e.sucursal)::VARCHAR AS sucursal,
                btrim(e.razon_soc)::VARCHAR AS razon_soc
            FROM informacion_afip.empresa e
            WHERE e.baja_fecha IS NULL
              AND e.cuit = v_cuit
              AND (
                    v_sucursal IS NULL
                    OR e.sucursal = v_sucursal
                  )
              AND (
                    v_descripcion IS NULL
                    OR upper(e.razon_soc)
                        LIKE '%' || upper(v_descripcion) || '%'
                  )
              AND NULLIF(btrim(e.cuit), '') IS NOT NULL
              AND length(btrim(e.cuit)) <= 11
              AND NULLIF(btrim(e.sucursal), '') IS NOT NULL
              AND length(btrim(e.sucursal)) <= 6
              AND NULLIF(btrim(e.razon_soc), '') IS NOT NULL
            ORDER BY e.cuit, e.sucursal
            LIMIT v_limite
        ) q
        ORDER BY q.razon_soc, q.cuit, q.sucursal;

        RETURN;
    END IF;

    RETURN QUERY
    SELECT
        q.cuit,
        q.sucursal,
        q.razon_soc
    FROM (
        SELECT
            btrim(e.cuit)::VARCHAR AS cuit,
            btrim(e.sucursal)::VARCHAR AS sucursal,
            btrim(e.razon_soc)::VARCHAR AS razon_soc
        FROM informacion_afip.empresa e
        WHERE e.baja_fecha IS NULL
          AND upper(e.razon_soc)
                LIKE '%' || upper(v_descripcion) || '%'
          AND (
                v_sucursal IS NULL
                OR btrim(e.sucursal) = v_sucursal
              )
          AND NULLIF(btrim(e.cuit), '') IS NOT NULL
          AND length(btrim(e.cuit)) <= 11
          AND NULLIF(btrim(e.sucursal), '') IS NOT NULL
          AND length(btrim(e.sucursal)) <= 6
          AND NULLIF(btrim(e.razon_soc), '') IS NOT NULL
        -- El subconjunto se corta sin orden interno por rendimiento.
        -- El orden exterior solo estabiliza la presentacion de esas filas.
        LIMIT v_limite
    ) q
    ORDER BY q.razon_soc, q.cuit, q.sucursal;
END;
$func$
LANGUAGE plpgsql
STABLE;

CREATE OR REPLACE FUNCTION
compras.es_requerimiento_habilitado_busqueda_empresa_cotizacion(
    p_id_requerimiento INTEGER
)
RETURNS BOOLEAN
AS $func$
    SELECT COALESCE($1, 0) > 0
       AND EXISTS (
            SELECT 1
            FROM compras.requerimiento r
            JOIN compras.sector_requerimiento sr
              ON sr.id_sector = r.id_sector
            WHERE r.id_requerimiento = $1
              AND r.estado = 1
              AND r.baja_fecha IS NULL
              AND compras.normalizar_sector(sr.descripcion)
                    IN ('RRHH', 'SISTEMAS')
       );
$func$
LANGUAGE sql
STABLE;

COMMIT;

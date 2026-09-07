-- Extension de solo lectura para Exportar en Requerimientos.
-- Fuente canonica: ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql
-- Preparado para revision; no ejecutado sobre bases existentes.
\encoding LATIN1

BEGIN;

DO $precondition$
BEGIN
    IF to_regprocedure('compras.listar_prestadores_adjudicados(integer)') IS NULL THEN
        RAISE EXCEPTION 'No esta disponible la adjudicacion base de Compras.';
    END IF;
END;
$precondition$;

CREATE OR REPLACE FUNCTION compras.listar_prestadores_adjudicados_batch(
    p_ids_requerimientos TEXT
)
RETURNS TABLE (
    id_requerimiento INTEGER,
    id_prestador INTEGER,
    descripcion VARCHAR
)
AS $func$
    -- Misma identidad que listar_prestadores_adjudicados: detalle activo.
    -- DISTINCT incluye NULL para detectar detalles parcialmente adjudicados.
    -- No se une a presupuestos/documentos: no multiplican adjudicatarios.
    SELECT DISTINCT d.id_requerimiento::INTEGER,
           d.id_prestador::INTEGER,
           p.descripcion::VARCHAR
      FROM compras.requerimiento_detalle d
      JOIN compras.requerimiento r ON r.id = d.id_requerimiento
      LEFT JOIN public.prestador p ON p.id_prestador = d.id_prestador
     WHERE d.id_requerimiento = ANY(p_ids_requerimientos::INTEGER[])
       AND d.baja_fecha IS NULL
       AND r.baja_fecha IS NULL
       AND r.estado <> 99
     ORDER BY d.id_requerimiento::INTEGER, d.id_prestador::INTEGER;
$func$
LANGUAGE sql
STABLE;

COMMIT;

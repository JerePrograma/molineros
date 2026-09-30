-- Filtro por droga para el nomenclador de medicamentos de Compras.
-- Devuelve todos los troqueles coincidentes, sin paginacion.
-- Conserva la comparacion por droga, la fecha y las bajas del buscador OSPIM.

BEGIN;

CREATE OR REPLACE FUNCTION farmacia.buscar_medicamentos_droga(
    droga_v character varying)
RETURNS TABLE(troquel numeric)
AS $BODY$
BEGIN
    return query
    select distinct m.troquel
    from public.medicamentos m
    where droga_v is not null
    and droga_v <> ''
    and upper(m.droga) like '%'||upper(droga_v)||'%'
    and m.fecha <= current_date
    and m.baja_fecha is null;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100
ROWS 1000;

COMMIT;

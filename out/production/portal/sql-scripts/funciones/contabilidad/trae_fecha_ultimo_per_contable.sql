CREATE OR REPLACE FUNCTION trae_fecha_cierre_asientos() 
RETURNS TABLE(fecha_cierre timestamp without time zone)
    LANGUAGE sql
    AS $BODY$

select max(fecha_cierre) from cierre_periodo_contable_asientos where baja_fecha is null;

$BODY$;
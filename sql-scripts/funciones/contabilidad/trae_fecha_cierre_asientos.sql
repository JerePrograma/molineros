CREATE OR REPLACE FUNCTION trae_fecha_ultimo_per_contable() 
RETURNS TABLE(fecha_cierre timestamp without time zone)
    LANGUAGE sql
    AS $BODY$

select max(fecha_cierre) from cierre_periodo_contable where baja_fecha is null;

$BODY$;
CREATE OR REPLACE FUNCTION actualiza_liquidacion_debitos_terceros(
 l_periodo_hasta timestamp without time zone,
 l_observaciones character varying,
 l_usuario character varying
 ) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
BEGIN
	
UPDATE liquidacion_debitos_terceros l 
set
observaciones = l_observaciones, 
modi_fecha = localtimestamp,
modi_usr = l_usuario

where l.periodo_hasta =  l_periodo_hasta;

return 1;
END;
$BODY$;
CREATE OR REPLACE FUNCTION eliminar_detalle_asiento(
	p_detalle_asiento_id integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN
	delete from detalle_asiento where id = p_detalle_asiento_id;
 
return 1;
END;
$BODY$;

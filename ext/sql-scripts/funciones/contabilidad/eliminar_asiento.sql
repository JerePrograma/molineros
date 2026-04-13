create or replace function eliminar_asiento(p_asiento_id integer)
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
BEGIN
	delete from detalle_asiento where asiento_id = p_asiento_id;
	delete from asiento where id = p_asiento_id;
return 1;
END;
$BODY$;

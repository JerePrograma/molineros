DROP FUNCTION eliminar_tipo_mov_bcrio(p_id_tipo_mov integer);

 CREATE OR REPLACE FUNCTION eliminar_tipo_mov_bcrio(p_id_tipo_mov integer, p_usr character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN
	update tipo_mov_bcrio set baja_fecha = localtimestamp, baja_usr = p_usr where id_tipo_mov_maestro = p_id_tipo_mov;

return 1;
END;
$BODY$;

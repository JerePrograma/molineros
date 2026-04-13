 drop FUNCTION eliminar_prestacion_concepto(p_id_nomenclador_concepto integer) ;
 
 CREATE OR REPLACE FUNCTION eliminar_prestacion_concepto(p_id_prestacion integer, p_username character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN
	--delete from nomenclador_conceptos where id_prestacion = p_id_prestacion;
	--delete from plan_prestacion where id_prestacion = p_id_prestacion;
	update nomenclador set baja_fecha = localtimestamp, baja_usr = p_username where id_prestacion = p_id_prestacion;
return 1;
END;
$BODY$;

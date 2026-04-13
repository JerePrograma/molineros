DROP FUNCTION actualizar_tipo_mov_bcrio(
 p_descripcion character varying,
 p_valido_desde date,
 p_valido_hasta date,
 p_id_concepto integer,
 p_id_tipo_mov_bcrio integer) ;
 
 
 CREATE OR REPLACE FUNCTION actualizar_tipo_mov_bcrio(
 p_descripcion character varying,
 p_valido_desde date,
 p_valido_hasta date,
 p_id_concepto integer,
 p_id_tipo_mov_bcrio integer,
 p_usr character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN
	
UPDATE tipo_mov_bcrio
   SET descripcion = p_descripcion,
   concepto_id = p_id_concepto,
   modi_fecha = current_date,
   modi_usr = p_usr
 WHERE id_tipo_mov_maestro=p_id_tipo_mov_bcrio
 and    valido_desde = p_valido_desde
 and valido_hasta = p_valido_hasta;

return 1;
END;
$BODY$;

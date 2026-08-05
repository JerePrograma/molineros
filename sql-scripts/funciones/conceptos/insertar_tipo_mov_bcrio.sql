DROP FUNCTION insertar_tipo_mov_bcrio(
 p_descripcion character varying,
 p_valido_desde date,
 p_valido_hasta date,
 p_concepto_id integer);
 
 CREATE OR REPLACE FUNCTION insertar_tipo_mov_bcrio(
 p_descripcion character varying,
 p_valido_desde date,
 p_valido_hasta date,
 p_concepto_id integer,
 p_usr character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN
	
insert into tipo_mov_bcrio_maestro (descripcion_original, valido_desde, valido_hasta, alta_fecha, alta_usr, modi_fecha, modi_usr)
values ( p_descripcion , p_valido_desde , p_valido_hasta, current_date, p_usr, current_date, p_usr );

insert into tipo_mov_bcrio (descripcion, concepto_id, valido_desde, valido_hasta, alta_fecha, alta_usr, modi_fecha, modi_usr, id_tipo_mov_maestro)
values ( p_descripcion , p_concepto_id , p_valido_desde , p_valido_hasta, current_date, p_usr, current_date, p_usr,currval('tipo_mov_bcrio_maestro_id_seq') );
 

return currval('tipo_mov_bcrio_maestro_id_seq') ;
END;
$BODY$;

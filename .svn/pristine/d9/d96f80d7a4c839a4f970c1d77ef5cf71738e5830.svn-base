DROP FUNCTION reemplazar_tipo_mov_bcrio(
 p_id_tipo_mov_bcrio_viejo integer, 
 p_tipo_mov_bcrio_viejo_valido_desde date,
 p_tipo_mov_bcrio_viejo_valido_hasta date,
 p_descripcion character varying,
 p_valido_desde date,
 p_valido_hasta date,
 p_id_concepto integer);
 
 CREATE OR REPLACE FUNCTION reemplazar_tipo_mov_bcrio(
 p_id_tipo_mov_bcrio_viejo integer, 
 p_tipo_mov_bcrio_viejo_valido_desde date,
 p_tipo_mov_bcrio_viejo_valido_hasta date,
 p_descripcion character varying,
 p_valido_desde date,
 p_valido_hasta date,
 p_id_concepto integer,
 p_usr character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
declare nuevoId integer;
declare idSolapamientoIntermedio integer;
BEGIN
	
	insert into tipo_mov_bcrio (descripcion,  valido_desde,  valido_hasta , concepto_id,  alta_fecha, alta_usr, modi_fecha, modi_usr, id_tipo_mov_maestro) 
		 values (p_descripcion, 
		 		p_valido_desde,
		 		p_valido_hasta,
		 		p_id_concepto,
		 		current_date,
		 		p_usr,
		 		current_date,
		 		p_usr,
		 		p_id_tipo_mov_bcrio_viejo
		 	);
 	nuevoId=currval('tipo_mov_bcrio_id_seq');

	if (p_valido_desde > p_tipo_mov_bcrio_viejo_valido_desde and  p_valido_hasta  = p_tipo_mov_bcrio_viejo_valido_hasta) then
	 	 update tipo_mov_bcrio  
		 set valido_hasta = p_valido_desde - interval '1 day', modi_fecha = current_date, modi_usr = p_usr  
		 where id_tipo_mov_maestro = p_id_tipo_mov_bcrio_viejo
		 and valido_desde = p_tipo_mov_bcrio_viejo_valido_desde
		 and valido_hasta = p_tipo_mov_bcrio_viejo_valido_hasta;
	end if;

	if (p_valido_desde = p_tipo_mov_bcrio_viejo_valido_desde and  p_valido_hasta  < p_tipo_mov_bcrio_viejo_valido_hasta) then
	 	 update tipo_mov_bcrio  
		 set valido_desde = p_valido_hasta + interval '1 day', modi_fecha = current_date, modi_usr = p_usr
		 where id_tipo_mov_maestro = p_id_tipo_mov_bcrio_viejo
		 and valido_desde = p_tipo_mov_bcrio_viejo_valido_desde
		 and valido_hasta = p_tipo_mov_bcrio_viejo_valido_hasta;
	end if;

	if (p_valido_desde > p_tipo_mov_bcrio_viejo_valido_desde and  p_valido_hasta  < p_tipo_mov_bcrio_viejo_valido_hasta) then
		insert into tipo_mov_bcrio (descripcion, concepto_id, valido_desde, valido_hasta, alta_fecha, alta_usr, modi_fecha, modi_usr, id_tipo_mov_maestro)
		select distinct descripcion, concepto_id, p_valido_hasta + interval '1 day', p_tipo_mov_bcrio_viejo_valido_hasta, current_date, p_usr, current_date, p_usr,p_id_tipo_mov_bcrio_viejo 
		from tipo_mov_bcrio  
		where id_tipo_mov_maestro = p_id_tipo_mov_bcrio_viejo
		 and valido_desde = p_tipo_mov_bcrio_viejo_valido_desde
		 and valido_hasta = p_tipo_mov_bcrio_viejo_valido_hasta;
		idSolapamientoIntermedio=currval('tipo_mov_bcrio_id_seq');
		
	 	update tipo_mov_bcrio  
		set valido_hasta = p_valido_desde - interval '1 day', modi_fecha = current_date, modi_usr = p_usr
		where id_tipo_mov_maestro = p_id_tipo_mov_bcrio_viejo
		 and valido_desde = p_tipo_mov_bcrio_viejo_valido_desde
		 and valido_hasta = p_tipo_mov_bcrio_viejo_valido_hasta;
	 	 
	end if; 
	
	return p_id_tipo_mov_bcrio_viejo;
END;
$BODY$;

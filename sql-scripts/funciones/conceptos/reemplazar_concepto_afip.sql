DROP FUNCTION reemplazar_concepto_afip(
 p_id_concepto_afip_viejo integer, 
 p_concepto_afip_viejo_valido_desde date,
 p_concepto_afip_viejo_valido_hasta date,
 p_descripcion character varying,
 p_valido_desde date,
 p_valido_hasta date,
 p_id_concepto integer) ;
 
 CREATE OR REPLACE FUNCTION reemplazar_concepto_afip(
 p_id_concepto_afip_viejo integer, 
 p_concepto_afip_viejo_valido_desde date,
 p_concepto_afip_viejo_valido_hasta date,
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
	
	insert into concepto_transferencia  (concepto_transf, liquidable, concepto_id, valido_desde, valido_hasta, alta_fecha, alta_usr, modi_fecha, modi_usr)
	select concepto_transf, liquidable, p_id_concepto, p_valido_desde, p_valido_hasta, current_date, p_usr, current_date, p_usr
	from concepto_transferencia where id = p_id_concepto_afip_viejo;

 	nuevoId=currval('concepto_transferencia_id_seq');

	if (p_valido_desde > p_concepto_afip_viejo_valido_desde and  p_valido_hasta  = p_concepto_afip_viejo_valido_hasta) then
	 	 update concepto_transferencia  
		 set valido_hasta = p_valido_desde - interval '1 day', modi_fecha = current_date, modi_usr = p_usr
		 where id = p_id_concepto_afip_viejo;
	end if;

	if (p_valido_desde = p_concepto_afip_viejo_valido_desde and  p_valido_hasta  < p_concepto_afip_viejo_valido_hasta) then
	 	 update concepto_transferencia  
		 set valido_desde = p_valido_hasta + interval '1 day', modi_fecha = current_date, modi_usr = p_usr
		 where id = p_id_concepto_afip_viejo;
	end if;

	if (p_valido_desde > p_concepto_afip_viejo_valido_desde and  p_valido_hasta  < p_concepto_afip_viejo_valido_hasta) then
		insert into concepto_transferencia  (concepto_transf, liquidable, concepto_id, valido_desde, valido_hasta, modi_fecha, modi_usr)
		select concepto_transf, liquidable, concepto_id,  p_valido_hasta + interval '1 day', p_concepto_afip_viejo_valido_hasta , current_date, p_usr
		from concepto_transferencia 
		where id = p_id_concepto_afip_viejo;
		idSolapamientoIntermedio=currval('concepto_transferencia_id_seq');
	
	 	update concepto_transferencia  
		set valido_hasta = p_valido_desde - interval '1 day', modi_fecha = current_date, modi_usr = p_usr
		where id = p_id_concepto_afip_viejo;
	 	 
	end if; 
	
	 return nuevoId;
END;
$BODY$;

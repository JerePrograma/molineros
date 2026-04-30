DROP FUNCTION reemplazar_prestacion_concepto(
 p_id_prestacion integer,
 p_viejo_valido_desde date,
 p_viejo_valido_hasta date,
 p_id_nomenclador_concepto_a_reemplazar integer,
 p_id_nuevo_concepto integer,
 p_nuevo_valido_desde date,
 p_nuevo_valido_hasta date,
 p_usr character varying) ;
 
 CREATE OR REPLACE FUNCTION reemplazar_prestacion_concepto(
 p_id_prestacion integer,
 p_viejo_valido_desde date,
 p_viejo_valido_hasta date,
 p_id_nomenclador_concepto_a_reemplazar integer,
 p_id_nuevo_concepto integer,
 p_nuevo_valido_desde date,
 p_nuevo_valido_hasta date,
 p_usr character varying,
 p_tipo_id int) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
declare nuevoId integer;
BEGIN
	
	insert into nomenclador_conceptos (codigo, descripcion, concepto_id, id_prestacion,valido_desde, valido_hasta, tipo_id, alta_fecha, alta_usr, modi_fecha, modi_usr)
	values ((select codigo from nomenclador where id_prestacion = p_id_prestacion),
		(select descripcion from nomenclador where id_prestacion = p_id_prestacion),
		p_id_nuevo_concepto,
		p_id_prestacion, p_nuevo_valido_desde, p_nuevo_valido_hasta,
		p_tipo_id, current_date, p_usr , current_date, p_usr );
	
	nuevoId= currval('nomenclador_conceptos_id_seq');
				
 	--el nuevo concepto se solapa con el fin del concepto anterior
	if (p_nuevo_valido_desde > p_viejo_valido_desde and  p_nuevo_valido_hasta  = p_viejo_valido_hasta) then
		update nomenclador_conceptos set valido_hasta = p_nuevo_valido_desde - interval '1 day' , modi_fecha = current_date, modi_usr = p_usr where id = p_id_nomenclador_concepto_a_reemplazar;
	end if;
	--el concepto nuevo se solapa con el inicio del concepto anterior
	if (p_nuevo_valido_desde = p_viejo_valido_desde and  p_nuevo_valido_hasta  < p_viejo_valido_hasta) then
		update nomenclador_conceptos set valido_desde = p_nuevo_valido_hasta + interval '1 day' , modi_fecha = current_date, modi_usr = p_usr  where id = p_id_nomenclador_concepto_a_reemplazar;
		
	end if;
	--el concepto nuevo se solapa en el medio del periodo de validez del anterior
	if (p_nuevo_valido_desde > p_viejo_valido_desde and  p_nuevo_valido_hasta  < p_viejo_valido_hasta) then
		update nomenclador_conceptos set valido_hasta = p_nuevo_valido_desde - interval '1 day' , modi_fecha = current_date, modi_usr = p_usr  where id = p_id_nomenclador_concepto_a_reemplazar;
		
		insert into nomenclador_conceptos (codigo, descripcion, concepto_id, id_prestacion,valido_desde, valido_hasta, tipo_id, alta_fecha, alta_usr, modi_fecha, modi_usr)
		select codigo, descripcion, concepto_id, id_prestacion,  p_nuevo_valido_hasta + interval '1 day', p_viejo_valido_hasta, tipo_id, current_date, p_usr , current_date, p_usr  
		from nomenclador_conceptos where id = p_id_nomenclador_concepto_a_reemplazar;
	end if;
	
	return nuevoId;
END;
$BODY$;

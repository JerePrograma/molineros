 drop FUNCTION eliminar_concepto(p_id_concepto integer); 
 
 CREATE OR REPLACE FUNCTION eliminar_concepto(p_id_concepto integer, p_valido_desde date, p_valido_hasta date, p_usr character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
declare p_concepto_viejo_valido_desde date;
declare p_concepto_viejo_valido_hasta date;
BEGIN
	
	p_concepto_viejo_valido_desde=valido_desde from conceptos where id_concepto_maestro = p_id_concepto and cast(valido_desde as date) <= cast(p_valido_desde as date) and cast(valido_hasta as date) >= cast(p_valido_hasta as date);
	p_concepto_viejo_valido_hasta=valido_hasta from conceptos where id_concepto_maestro = p_id_concepto and cast(valido_desde as date) <= cast(p_valido_desde as date) and cast(valido_hasta as date) >= cast(p_valido_hasta as date);
	
		--el concepto a borrar se solapa con el fin del concepto original
	if (p_valido_desde = p_concepto_viejo_valido_desde and  p_valido_hasta  = p_concepto_viejo_valido_hasta) then
		delete from conceptos
		 WHERE id_concepto_maestro=p_id_concepto
		 and valido_desde = p_concepto_viejo_valido_desde
		 and valido_hasta = p_concepto_viejo_valido_hasta;
	end if;
	
	--el concepto a borrar se solapa con el fin del concepto original
	if (p_valido_desde > p_concepto_viejo_valido_desde and  p_valido_hasta  = p_concepto_viejo_valido_hasta) then
		UPDATE conceptos
		   SET  valido_hasta = p_valido_desde - interval '1 day', modi_fecha = current_date, modi_usr = p_usr
		 WHERE id_concepto_maestro=p_id_concepto
		 and valido_desde = p_concepto_viejo_valido_desde
		 and valido_hasta = p_concepto_viejo_valido_hasta;
	end if;
	--el concepto a borrar se solapa con el inicio del concepto original
 	if (p_valido_desde = p_concepto_viejo_valido_desde and  p_valido_hasta  < p_concepto_viejo_valido_hasta) then
		UPDATE conceptos
		   SET  valido_desde = p_valido_hasta + interval '1 day', modi_fecha = current_date, modi_usr = p_usr
		 WHERE id_concepto_maestro=p_id_concepto
		 and valido_desde = p_concepto_viejo_valido_desde
		 and valido_hasta = p_concepto_viejo_valido_hasta;
	end if;
	
	--el concepto a borrar se solapa en el medio del periodo de validez del original
 	if (p_valido_desde > p_concepto_viejo_valido_desde and  p_valido_hasta  < p_concepto_viejo_valido_hasta) then
		 --inserto y manejo lo que quedo a la izquierda
		 insert into conceptos (descripcion,  numero_cuenta,  liquidaciones ,   egreso,  ingreso,  cuenta_pasivo,  sub_egreso,  sub_ingreso,  valido_desde,
		  valido_hasta ,  id_plan_cuenta,  id_plan_cuenta_pasivo, alta_fecha, alta_usr, modi_fecha, modi_usr, id_concepto_maestro) 
		  select  descripcion,  numero_cuenta,  liquidaciones ,   egreso,  ingreso,  cuenta_pasivo,  sub_egreso,  sub_ingreso,
		 		 p_valido_hasta + interval '1 day' ,  p_concepto_viejo_valido_hasta, id_plan_cuenta,  id_plan_cuenta_pasivo,
		 		 current_date, p_usr, current_date, p_usr, p_id_concepto
		  from conceptos 
 		 WHERE id_concepto_maestro=p_id_concepto
		 and valido_desde = p_concepto_viejo_valido_desde
		 and valido_hasta = p_concepto_viejo_valido_hasta;
			
 		--manejo lo que quedo a la derecha
		UPDATE conceptos
		   SET  valido_hasta = p_valido_desde - interval '1 day', modi_fecha = current_date, modi_usr = p_usr
		 WHERE id_concepto_maestro=p_id_concepto
		 and valido_desde = p_concepto_viejo_valido_desde
		 and valido_hasta = p_concepto_viejo_valido_hasta;
	end if; 
 	
return 1;
END;
$BODY$;

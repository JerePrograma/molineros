DROP FUNCTION eliminar_cuenta(p_id_cuenta integer);
 CREATE OR REPLACE FUNCTION eliminar_cuenta(p_id_cuenta integer, p_valido_desde date, p_valido_hasta date, p_usr character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
declare p_cuenta_vieja_valido_desde date;
declare p_cuenta_vieja_valido_hasta date;
BEGIN
	p_cuenta_vieja_valido_desde=valido_desde from plan_cuentas where id_cuenta_maestro = p_id_cuenta and cast(valido_desde as date) <= cast(p_valido_desde as date) and cast(valido_hasta as date) >= cast(p_valido_hasta as date);
	p_cuenta_vieja_valido_hasta=valido_hasta from plan_cuentas where id_cuenta_maestro = p_id_cuenta and cast(valido_desde as date) <= cast(p_valido_desde as date) and cast(valido_hasta as date) >= cast(p_valido_hasta as date);
	
		--el concepto a borrar se solapa con el fin del concepto original
	if (p_valido_desde = p_cuenta_vieja_valido_desde and  p_valido_hasta  = p_cuenta_vieja_valido_hasta) then
		delete from plan_cuentas
		 WHERE id_cuenta_maestro=p_id_cuenta
		 and valido_desde = p_cuenta_vieja_valido_desde
		 and valido_hasta = p_cuenta_vieja_valido_hasta;
	end if;
	
	--el concepto a borrar se solapa con el fin del concepto original
	if (p_valido_desde > p_cuenta_vieja_valido_desde and  p_valido_hasta  = p_cuenta_vieja_valido_hasta) then
		UPDATE plan_cuentas
		   SET  valido_hasta = p_valido_desde - interval '1 day', modi_fecha = current_date, modi_usr = p_usr
		 WHERE id_cuenta_maestro=p_id_cuenta
		 and valido_desde = p_cuenta_vieja_valido_desde
		 and valido_hasta = p_cuenta_vieja_valido_hasta;
	end if;
	--el concepto a borrar se solapa con el inicio del concepto original
 	if (p_valido_desde = p_cuenta_vieja_valido_desde and  p_valido_hasta  < p_cuenta_vieja_valido_hasta) then
		UPDATE plan_cuentas
		   SET  valido_desde = p_valido_hasta + interval '1 day', modi_fecha = current_date, modi_usr = p_usr
		 WHERE id_cuenta_maestro=p_id_cuenta
		 and valido_desde = p_cuenta_vieja_valido_desde
		 and valido_hasta = p_cuenta_vieja_valido_hasta;
	end if;
	
	--el concepto a borrar se solapa en el medio del periodo de validez del original
 	if (p_valido_desde > p_cuenta_vieja_valido_desde and  p_valido_hasta  < p_cuenta_vieja_valido_hasta) then
		 --inserto y manejo lo que quedo a la izquierda
			insert into plan_cuentas (cuenta, numero, imputable, alta_fecha, alta_usr, modi_fecha, modi_usr, valido_desde, valido_hasta, id_cuenta_maestro, tipo) 
	  		select cuenta, numero, imputable, localtimestamp, p_usr, localtimestamp, p_usr,
	  				p_valido_hasta + interval '1 day' ,  p_cuenta_vieja_valido_hasta, id_cuenta_maestro, tipo
			  from plan_cuentas 
	 		 WHERE id_cuenta_maestro=p_id_cuenta
			 and valido_desde = p_cuenta_vieja_valido_desde
			 and valido_hasta = p_cuenta_vieja_valido_hasta;
			 
			 
 		--manejo lo que quedo a la derecha
		UPDATE plan_cuentas
		   SET  valido_hasta = p_valido_desde - interval '1 day', modi_fecha = current_date, modi_usr = p_usr
		 WHERE id_cuenta_maestro=p_id_cuenta
		 and valido_desde = p_cuenta_vieja_valido_desde
		 and valido_hasta = p_cuenta_vieja_valido_hasta;
	end if; 
 	

return 1;
END;
$BODY$;

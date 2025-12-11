DROP FUNCTION actualizar_cuenta(p_id_cuenta integer,
 p_cuenta character varying,
 p_numero character varying,
 p_imputable boolean);
 
 DROP FUNCTION actualizar_cuenta(p_id_cuenta integer,
 p_cuenta character varying,
 p_numero character varying,
 p_imputable boolean,
 p_valido_desde date,
 p_valido_hasta date,
 p_usr character varying);
 
 CREATE OR REPLACE FUNCTION actualizar_cuenta(p_id_cuenta integer,
 p_cuenta character varying,
 p_numero character varying,
 p_imputable boolean,
 p_tipo character varying,
 p_valido_desde date,
 p_valido_hasta date,
 p_usr character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
declare p_cuenta_vieja_valido_desde date;
declare p_cuenta_vieja_valido_hasta date;
BEGIN
	
	p_cuenta_vieja_valido_desde=valido_desde from plan_cuentas where id_cuenta_maestro = p_id_cuenta and cast(valido_desde as date) <= cast(p_valido_desde as date) and cast(valido_hasta as date) >= cast(p_valido_hasta as date);
	p_cuenta_vieja_valido_hasta=valido_hasta from plan_cuentas where id_cuenta_maestro = p_id_cuenta and cast(valido_desde as date) <= cast(p_valido_desde as date) and cast(valido_hasta as date) >= cast(p_valido_hasta as date);
	
	--hago un simple update
	if (p_valido_desde = p_cuenta_vieja_valido_desde and  p_valido_hasta  = p_cuenta_vieja_valido_hasta) then
		
		UPDATE plan_cuentas
		   SET numero = p_numero,
		    cuenta = p_cuenta,
		    imputable = p_imputable,
		    modi_fecha = current_date, 
		    modi_usr = p_usr
		 WHERE id_cuenta_maestro=p_id_cuenta 
		 and valido_desde = p_cuenta_vieja_valido_desde
		 and valido_hasta = p_cuenta_vieja_valido_hasta;
	
	else 
		--o inserto un nuevo registro y hago los update correspondientes de acuerdo al solapamiento
		insert into plan_cuentas (cuenta, numero, imputable, alta_fecha, alta_usr, modi_fecha, modi_usr, valido_desde, valido_hasta, id_cuenta_maestro, tipo)
	  	values (p_cuenta, p_numero, p_imputable, localtimestamp, p_usr, localtimestamp, p_usr,	p_valido_desde ,  p_valido_hasta, p_id_cuenta, p_tipo);
			 
			 
			--la nueva cuenta se solapa con el fin de la cuenta anterior
		if (p_valido_desde > p_cuenta_vieja_valido_desde and  p_valido_hasta  = p_cuenta_vieja_valido_hasta) then
			UPDATE plan_cuentas
			   SET  valido_hasta = p_valido_desde - interval '1 day', modi_fecha = current_date, modi_usr = p_usr
			 WHERE id_cuenta_maestro=p_id_cuenta
			 and valido_desde = p_cuenta_vieja_valido_desde
			 and valido_hasta = p_cuenta_vieja_valido_hasta;
		end if;
		--la cuenta nueva se solapa con el inicio de la cuenta  anterior
	 	if (p_valido_desde = p_cuenta_vieja_valido_desde and  p_valido_hasta  < p_cuenta_vieja_valido_hasta) then
			UPDATE plan_cuentas
			   SET  valido_desde = p_valido_hasta + interval '1 day', modi_fecha = current_date, modi_usr = p_usr
			 WHERE id_cuenta_maestro=p_id_cuenta
			 and valido_desde = p_cuenta_vieja_valido_desde
			 and valido_hasta = p_cuenta_vieja_valido_hasta;
		end if;
		
		--la cuenta nueva se solapa en el medio del periodo de validez de la anterior
	 	if (p_valido_desde > p_cuenta_vieja_valido_desde and  p_valido_hasta  < p_cuenta_vieja_valido_hasta) then
			 --inserto y manejo lo que quedo a la izquierda
			insert into plan_cuentas (cuenta, numero, imputable, alta_fecha, alta_usr, modi_fecha, modi_usr, valido_desde, valido_hasta, id_cuenta_maestro, tipo) 
	  		select cuenta, numero, imputable, localtimestamp, p_usr, localtimestamp, p_usr,
	  				p_valido_hasta + interval '1 day' ,  p_cuenta_vieja_valido_hasta, id_cuenta_maestro, p_tipo
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
 	end if;
 	return p_id_cuenta;
 	 
return 1;
END;
$BODY$;

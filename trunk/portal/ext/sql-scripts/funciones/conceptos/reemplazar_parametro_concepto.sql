DROP FUNCTION reemplazar_parametro_concepto(
 p_param character varying, 
 p_concepto_viejo_valido_desde date,
 p_concepto_viejo_valido_hasta date,
 p_valido_desde date,
 p_valido_hasta date,
 p_concepto_id integer);
 
 CREATE OR REPLACE FUNCTION reemplazar_parametro_concepto(
 p_param character varying, 
 p_concepto_viejo_valido_desde date,
 p_concepto_viejo_valido_hasta date,
 p_valido_desde date,
 p_valido_hasta date,
 p_concepto_id integer,
 p_usr character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
declare nuevoId integer;
declare idConceptoSolapamientoIntermedio integer;
BEGIN
	if (p_valido_desde = p_concepto_viejo_valido_desde and  p_valido_hasta  = p_concepto_viejo_valido_hasta) then
		update parametros_conceptos set id_concepto = p_concepto_id, modi_fecha = current_date, modi_usr = p_usr
		where cast(valido_desde as date) =  p_valido_desde
		and cast (valido_hasta as date) = p_valido_hasta
		and parametro = p_param;
	end if;
			
	if (p_valido_desde > p_concepto_viejo_valido_desde and  p_valido_hasta  = p_concepto_viejo_valido_hasta) then
		insert into parametros_conceptos  (parametro, id_concepto,valido_Desde,valido_hasta,observaciones, alta_fecha, alta_usr, modi_fecha, modi_usr)
		select parametro, p_concepto_id,p_valido_desde,p_valido_hasta,observaciones, current_date, p_usr, current_date, p_usr
		from parametros_conceptos
		where  cast(valido_desde as date) =  p_concepto_viejo_valido_desde
		and cast (valido_hasta as date) = p_concepto_viejo_valido_hasta
		and parametro = p_param;
		
		update parametros_conceptos set valido_hasta = p_valido_desde - interval '1 day', modi_fecha = current_date, modi_usr = p_usr
		where cast(valido_desde as date) =  p_concepto_viejo_valido_desde
		and cast (valido_hasta as date) = p_concepto_viejo_valido_hasta
		and parametro = p_param;
	end if;
	
 	if (p_valido_desde = p_concepto_viejo_valido_desde and  p_valido_hasta  < p_concepto_viejo_valido_hasta) then
 		insert into parametros_conceptos  (parametro, id_concepto,valido_Desde,valido_hasta,observaciones, alta_fecha, alta_usr, modi_fecha, modi_usr)
		select parametro, p_concepto_id,p_valido_desde,p_valido_hasta,observaciones, current_date, p_usr, current_date, p_usr
		from parametros_conceptos
		where  cast(valido_desde as date) =  p_concepto_viejo_valido_desde
		and cast (valido_hasta as date) = p_concepto_viejo_valido_hasta
		and parametro = p_param;
		
		update parametros_conceptos set valido_desde = p_valido_hasta + interval '1 day', modi_fecha = current_date, modi_usr = p_usr
		where cast(valido_desde as date) =  p_concepto_viejo_valido_desde
		and cast (valido_hasta as date) = p_concepto_viejo_valido_hasta
		and parametro = p_param;
	end if;
	
 	if (p_valido_desde > p_concepto_viejo_valido_desde and  p_valido_hasta  < p_concepto_viejo_valido_hasta) then
 		insert into parametros_conceptos  (parametro, id_concepto,valido_Desde,valido_hasta,observaciones, alta_fecha, alta_usr, modi_fecha, modi_usr)
		select parametro, p_concepto_id,p_valido_desde,p_valido_hasta,observaciones, current_date, p_usr, current_date, p_usr
		from parametros_conceptos
		where  cast(valido_desde as date) =  p_concepto_viejo_valido_desde
		and cast (valido_hasta as date) = p_concepto_viejo_valido_hasta
		and parametro = p_param;
		
		insert into parametros_conceptos  (parametro, id_concepto,valido_Desde,valido_hasta,observaciones, alta_fecha, alta_usr, modi_fecha, modi_usr)
		select parametro, id_concepto,  p_valido_hasta + interval '1 day', p_concepto_viejo_valido_hasta, observaciones, current_date, p_usr, current_date, p_usr
		from parametros_conceptos
		where  cast(valido_desde as date) =  p_concepto_viejo_valido_desde
		and cast (valido_hasta as date) = p_concepto_viejo_valido_hasta
		and parametro = p_param;
		
		update parametros_conceptos set valido_hasta = p_valido_desde - interval '1 day', modi_fecha = current_date, modi_usr = p_usr
		where cast(valido_desde as date) =  p_concepto_viejo_valido_desde
		and cast (valido_hasta as date) = p_concepto_viejo_valido_hasta
		and parametro = p_param;
		
		
	end if; 
 	
	 
	return 1;
END;
$BODY$;

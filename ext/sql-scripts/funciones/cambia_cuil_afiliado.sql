CREATE OR REPLACE FUNCTION novedades_sss.cambia_cuil_afiliado(cuil_titular_p character varying, inte_p integer, cuil_p character varying, documento_tipo_p character varying, documento_numero_p character varying, vigen_fecha_p timestamp without time zone, cuil_titular_anterior_p character varying, inte_anterior_p integer, cuil_anterior_p character varying, documento_tipo_anterior_p character varying, documento_numero_anterior_p character varying, user_p character varying)
  RETURNS integer AS
$BODY$

declare _record_plan RECORD;
declare id_plan_aux integer;

BEGIN
/*Integrantes: inserto Historico alta nuevo, inserto nuevo Afiliado, inserto domicilio nuevo Afiliado,
	       inserto documentos nuevo Afiliado, actualizo Afiliado anterior, 
	       inserto Historico baja afi anterior, inserto tabla cruce afi_cambio_cuil */

	/*integrantes: inserto Historico afi anterior */
	INSERT INTO afi_estados_histo(
		    cuil_titular, inte, id_ospim, id_uoma, id_amtima, apellido, nombre, 
		    documento_tipo, sexo, cuil, naci_fecha, ingre_fecha, id_seccional, 
		    anterior_os, vigen_fecha, observaciones, pres_ssalud_fecha, alta_fecha, 
		    alta_usr, modi_fecha, modi_usr, baja_fecha, baja_usr, discapacitado, 
		    docu_numero, nacionalidad, aportante_titular, nro_afiliado, id_motivo_baja, 
		    id_ospim_baja_fecha, id_uoma_baja_fecha, id_amtima_baja_fecha, 
		    descripcion_operacion, censo2013, email, id_parentesco_sss, id_estado_civil_sss)
	  select cuil_titular, inte, id_ospim, id_uoma, id_amtima, apellido, nombre, 
		    documento_tipo, sexo, cuil, naci_fecha, ingre_fecha, id_seccional, 
		    anterior_os, vigen_fecha, observaciones, pres_ssalud_fecha, current_date, 
		    user_p, current_date, user_p, baja_fecha, baja_usr, discapacitado, 
		    docu_numero, nacionalidad, aportante_titular, nro_afiliado, id_motivo_baja, 
		    id_ospim_baja_fecha, id_uoma_baja_fecha, id_amtima_baja_fecha, 
		    'CCU', censo2013, email, id_parentesco_sss, id_estado_civil_sss 
		    from afiliado 
		    where cuil_titular = cuil_titular_anterior_p 
		    and case when inte_p > 0 then inte=inte_anterior_p else inte = inte end  ;
	/*integrantes: inserto nuevo Afiliado */
	INSERT INTO afiliado(
		    cuil_titular, inte, id_ospim, id_uoma, id_amtima, apellido, nombre, 
		    documento_tipo, sexo, cuil, naci_fecha, ingre_fecha, id_seccional, 
		    anterior_os, vigen_fecha, observaciones, alta_fecha, alta_usr, 
		    modi_fecha, modi_usr, baja_fecha, baja_usr, discapacitado, docu_numero, 
		    nacionalidad, aportante_titular, nro_afiliado, id_motivo_baja, 
		    id_ospim_baja_fecha, id_uoma_baja_fecha, id_amtima_baja_fecha, 
		    fecha_pres_super, fecha_baja_super, fecha_mod_super, pres_ssalud_fecha, 
		    censo2013, email, id_parentesco_sss, id_estado_civil_sss)
	    select cuil_titular_p, inte, id_ospim, id_uoma, id_amtima, apellido, nombre, 
		    documento_tipo_p, sexo, cuil_p, naci_fecha, ingre_fecha, id_seccional, 
		    anterior_os, vigen_fecha, observaciones, current_date, 
		    user_p, current_date, user_p, baja_fecha, baja_usr, discapacitado, documento_numero_p, 
		    nacionalidad, aportante_titular, nro_afiliado, id_motivo_baja, 
		    id_ospim_baja_fecha, id_uoma_baja_fecha, id_amtima_baja_fecha, 
		    null, null, null, null, --blanqueo fechas SSS
		    censo2013, email, id_parentesco_sss, id_estado_civil_sss 
		    from afiliado
		    where cuil_titular = cuil_titular_anterior_p ;
		    ---and case when inte_p > 0 then inte=inte_anterior_p end  ;
	/* inserto domicilio nuevo Afiliado */	
	INSERT INTO afi_domicilio(
            cuil_titular, inte, vigen_desde, domi_tipo, calle, piso, depto, 
            oficina, postal_codi, barrio, telefono, observaciones, domi_val, 
            alta_fecha, alta_usr, modi_fecha, modi_usr, baja_fecha, baja_usr, 
            provincia, localidad, numero, cod_area_telefono, cod_area_celular, 
            celular, cod_area_tel_laboral, tel_laboral)
	select cuil_titular_p, inte, vigen_desde, domi_tipo, calle, piso, depto, 
            oficina, postal_codi, barrio, telefono, observaciones, domi_val, 
            current_date, user_p, current_date, user_p, baja_fecha, baja_usr, 
            provincia, localidad, numero, cod_area_telefono, cod_area_celular, 
            celular, cod_area_tel_laboral, tel_laboral 
            from afi_domicilio
            where cuil_titular = cuil_titular_anterior_p 
            and inte_p = 0 ;
            --and inte=inte_anterior_p;
	/* inserto documentos nuevo Afiliado */ 
        INSERT INTO afi_documento(
            cuil_titular, inte, id_documento, fecha_ini, fecha_vto, observacion, 
            baja_fecha, alta_fecha, modi_fecha, alta_usr, modi_usr, baja_usr)
	select cuil_titular_p, inte, id_documento, fecha_ini, fecha_vto, observacion, 
            baja_fecha, current_date, current_date, user_p, user_p, baja_usr 
            from afi_documento
            where cuil_titular = cuil_titular_anterior_p 
            and case when inte_p > 0 then inte=inte_anterior_p else inte = inte end  ;
    	    
	/*integrantes: inserto Historico alta nuevo */
	INSERT INTO afi_estados_histo(
		    cuil_titular, inte, id_ospim, id_uoma, id_amtima, apellido, nombre, 
		    documento_tipo, sexo, cuil, naci_fecha, ingre_fecha, id_seccional, 
		    anterior_os, vigen_fecha, observaciones, pres_ssalud_fecha, alta_fecha, 
		    alta_usr, modi_fecha, modi_usr, baja_fecha, baja_usr, discapacitado, 
		    docu_numero, nacionalidad, aportante_titular, nro_afiliado, id_motivo_baja, 
		    id_ospim_baja_fecha, id_uoma_baja_fecha, id_amtima_baja_fecha, 
		    descripcion_operacion, censo2013, email, id_parentesco_sss, id_estado_civil_sss)
	  select cuil_titular, inte, id_ospim, id_uoma, id_amtima, apellido, nombre, 
		    documento_tipo, sexo, cuil, naci_fecha, ingre_fecha, id_seccional, 
		    anterior_os, vigen_fecha, observaciones, pres_ssalud_fecha, current_date, 
		    user_p, current_date, user_p, baja_fecha, baja_usr, discapacitado, 
		    docu_numero, nacionalidad, aportante_titular, nro_afiliado, id_motivo_baja, 
		    id_ospim_baja_fecha, id_uoma_baja_fecha, id_amtima_baja_fecha, 
		    'ALT', censo2013, email, id_parentesco_sss, id_estado_civil_sss 
		    from afiliado 
		    where cuil_titular = cuil_titular_p 
		    and case when inte_p > 0 then inte=inte_anterior_p else inte = inte end  ;		    
		    
	/*integrantes: actualizo Afiliado anterior */	
	UPDATE afiliado SET baja_fecha = current_date,
			    baja_usr = user_p,	
			    id_motivo_baja = 114 --"CAMBIO DE CUIL"
		    where cuil_titular = cuil_titular_anterior_p 
		    and case when inte_p > 0 then inte=inte_anterior_p else inte = inte end  ;

	/*integrantes: inserto tabla cruce afi_cambio_cuil*/
	INSERT INTO afi_cambio_cuil(
            cuil_titular, inte, cuil, documento_tipo, documento_numero, vigen_fecha, 
            cuil_titular_anterior, inte_anterior, cuil_anterior, documento_tipo_anterior, 
            documento_numero_anterior, alta_fecha, alta_usr, modi_fecha, 
            modi_usr)
	VALUES (cuil_titular_p, inte_p, cuil_p, documento_tipo_p, documento_numero_p, vigen_fecha_p, 
            cuil_titular_anterior_p, inte_anterior_p, cuil_anterior_p, documento_tipo_anterior_p, 
            documento_numero_anterior_p, current_date, user_p, current_date, user_p);
	
if(inte_p = 0) then -- es un titular, tenemos que tener cuidado porque debemos actualizar 
--las referencias al grupo fliar, afi_plan, afi_aportes, afi_situ_laboral, afi_tercerizadora_servicio,
-- por que cambia el cuil_titular

/* iteramos sobre todos los planes y aportes que tiene el cuil anterior, para copiarlos al cuil nuevo */
FOR _record_plan IN select id, vigen_hasta from afi_plan 
	where cuil_titular = cuil_titular_anterior_p order by vigen_desde LOOP

	INSERT INTO afi_plan(
		    cuil_titular, inte, id_plan, id_tarifa, vigen_desde, alta_fecha, 
		    alta_usr, modi_fecha, modi_usr, baja_fecha, baja_usr, id_motivo_baja, 
		    id_plan_omint, vigen_hasta)
		select cuil_titular_p, inte_p, id_plan, id_tarifa, vigen_desde, current_date, 
		    user_p, current_date, user_p, baja_fecha, baja_usr, id_motivo_baja, 
		    id_plan_omint, vigen_hasta from afi_plan where id = _record_plan.id;

	id_plan_aux = currval('afi_plan_id_sequence');

        INSERT INTO afi_aportes(
            cuil_titular, inte, id_aporte, fecha_ingre, fecha_egre, alta_usr, 
            baja_usr, baja_fecha, modi_fecha, modi_usr, alta_fecha, id_motivo_baja, 
            id_plan_serial, id_socio, tipo_aporte)
		select cuil_titular_p, inte_p, id_aporte, fecha_ingre, fecha_egre, user_p, 
		baja_usr, baja_fecha, current_date, user_p, current_date, id_motivo_baja, 
		id_plan_aux, id_socio, tipo_aporte from afi_aportes where id_plan_serial = _record_plan.id;

	/*cerramos historia del plan y aportes del cuil anterior */
	if(_record_plan.vigen_hasta is null) then 
		update afi_plan set vigen_hasta = current_date, modi_fecha = current_date, 
				modi_usr = user_p, id_motivo_baja = 114 where id = _record_plan.id;
		update afi_aportes set fecha_egre = current_date, modi_fecha = current_date, 
				modi_usr = user_p, id_motivo_baja = 114 where id_plan_serial = _record_plan.id;		   	
	end if; 	
                
END LOOP;

/* las situac laborales que tiene el cuil anterior, para copiarlos al cuil nuevo */

  INSERT INTO afi_situ_laboral(
            cuil_titular, inte, cuit, sucursal, fecha_ingre, id_puesto, id_revista, 
            fecha_egre, modi_fecha, alta_fecha, baja_fecha, alta_usr, modi_usr, 
            baja_usr, id_categoria, id_motivo_baja, escala_salarial)
    select cuil_titular_p, inte_p, cuit, sucursal, fecha_ingre, id_puesto, id_revista, 
            fecha_egre, current_date, current_date, baja_fecha, user_p, user_p, 
            baja_usr, id_categoria, id_motivo_baja, escala_salarial 
    from afi_situ_laboral 
    where cuil_titular = cuil_titular_anterior_p;

    /* cerramos historia de las situaciones laborales */		
    UPDATE afi_situ_laboral set id_motivo_baja = 114, fecha_egre = current_date, 
				modi_fecha = current_date, modi_usr = user_p
    where cuil_titular = cuil_titular_anterior_p and fecha_egre is null ;  
            
/* las situac laborales que tiene el cuil anterior, para copiarlos al cuil nuevo */

  INSERT INTO afi_tercerizadora_servicio(
            cuil_titular, inte, id_tercerizadora, fecha_inicio_pres, fecha_fin_pres, 
            alta_fecha, modi_fecha, baja_fecha, alta_usr, modi_usr, baja_usr)
    select cuil_titular_p, inte_p, id_tercerizadora, fecha_inicio_pres, fecha_fin_pres, 
           current_date, current_date, baja_fecha, user_p, user_p, baja_usr 
           from afi_tercerizadora_servicio
	   where cuil_titular = cuil_titular_anterior_p;

    /* cerramos historia de las situaciones laborales */		
    UPDATE afi_tercerizadora_servicio set fecha_fin_pres = current_date, 
				modi_fecha = current_date, modi_usr = user_p
    where cuil_titular = cuil_titular_anterior_p and fecha_fin_pres is null ;  
    
END IF;

return 1;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
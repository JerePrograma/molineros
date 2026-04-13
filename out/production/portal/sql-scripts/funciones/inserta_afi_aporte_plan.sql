-- Function: inserta_afi_aporte_plan(character varying, integer, integer, integer, date, date, integer, character varying, boolean, boolean, integer)

-- DROP FUNCTION inserta_afi_aporte_plan(character varying, integer, integer, integer, date, date, integer, character varying, boolean, boolean, integer);

CREATE OR REPLACE FUNCTION inserta_afi_aporte_plan(cuil_p character varying, inte_p integer, id_aporte_p integer, id_plan_p integer, fecha_ingreso_p date, fecha_egreso_p date, id_motivo_baja_p integer, username character varying, esnuevoplan boolean, idyafuegenerado boolean, p_id_plan_omint integer)
  RETURNS integer AS
$BODY$

    declare fecha_actual timestamp without time zone;
    declare fecha_alta_aporte timestamp without time zone;
    declare fecha_alta_plan timestamp without time zone;
    declare aporte_tipo char(1);
	declare existe_plan int;
	declare existe_id_plan_omint int;
	declare genera_tipo_id  char(1);
	declare baja_usr_aux  character varying; 
	declare esnuevoaporte boolean;
  BEGIN
	esnuevoaporte=true;
	fecha_actual = current_timestamp;
	fecha_alta_plan = fecha_actual;
	
    fecha_alta_aporte = alta_fecha from afi_aportes 
	     where cuil_titular=cuil_p 
	     and inte=inte_p 
	     and id_aporte=id_aporte_p 
	     --and fecha_ingre=fecha_ingreso_p
	     and (fecha_egre is null or fecha_egre >fecha_actual);
    
    if fecha_alta_aporte is not null then
    	esnuevoaporte=false;
		UPDATE afi_aportes
		set fecha_ingre=fecha_ingreso_p,
		    fecha_egre=fecha_egreso_p,
		    id_motivo_baja=id_motivo_baja_p,
		    modi_fecha=fecha_actual,
		    modi_usr=username
		where cuil_titular=cuil_p
		and inte=inte_p
		and id_aporte=id_aporte_p		
		and (fecha_egre is null or fecha_egre >fecha_actual);
	else
		fecha_alta_aporte = fecha_actual; 
		INSERT INTO afi_aportes(cuil_titular, inte, id_aporte, fecha_ingre, fecha_egre, id_motivo_baja, alta_usr, alta_fecha)
		VALUES (cuil_p, inte_p, id_aporte_p, fecha_ingreso_p, fecha_egreso_p, id_motivo_baja_p, username, fecha_alta_aporte);    
		update afiliado 
		set aportante_titular=1
		where cuil_titular=cuil_p
		and inte=inte_p;	
    end if;


    existe_plan=id_plan from afi_plan
		where cuil_titular=cuil_p
		and inte=inte_p
		--and id_plan=id_plan_p
		and baja_fecha is null limit 1;	

     existe_id_plan_omint = id_plan_omint from afi_plan
		where cuil_titular=cuil_p
		and inte=inte_p
		--and id_plan=id_plan_p
		and baja_fecha is null limit 1;	
    
--si cambiaron el id_plan omint entra aca
    if existe_id_plan_omint >=0 and (id_plan_p = existe_plan and p_id_plan_omint <> existe_id_plan_omint) then	
		update afi_plan
		set baja_fecha=fecha_actual,
		baja_usr=username
		--id_plan_omint=existe_id_plan_omint
		where cuil_titular=cuil_p
		and inte=inte_p
		--and id_plan<>id_plan_p
		and baja_fecha is null;

		insert into afi_plan(cuil_titular,inte,id_plan,id_tarifa,vigen_desde,alta_fecha,alta_usr,modi_fecha,modi_usr,baja_fecha,baja_usr,id_plan_omint)
		--SE CAMBIA 05/03/2012 values(cuil_p,inte_p,id_plan_p,0,fecha_ingreso_p, fecha_alta_plan,username,null,null,null,null,p_id_plan_omint);    
		values(cuil_p,inte_p,id_plan_p,0,fecha_actual, fecha_actual,username,null,null,null,null,p_id_plan_omint);    

	--chequeo por <> existe_plan, porque si el plan tiene varios aportes, este sp se llama una vez por cada aporte
	-- entonces puede ser que ya haya llamado el sp y el plan ya lo haya cambiado
	--si cambian id plan actualiza plan y id_plan omint
    elseif existe_plan >=0 and (id_plan_p<>existe_plan) then  
		update afi_plan
		set baja_fecha=fecha_actual,
		baja_usr=username
		--id_plan_omint=existe_id_plan_omint
		where cuil_titular=cuil_p
		and inte=inte_p
		--and id_plan<>id_plan_p
		and baja_fecha is null;		
		
		update afi_plan_aporte 
    	set baja_fecha = fecha_actual,
    	baja_usr = username
    	where cuil_titular=cuil_p
		and inte=inte_p
		and id_plan = existe_plan
		and baja_fecha is null;
		
		insert into afi_plan(cuil_titular,inte,id_plan,id_tarifa,vigen_desde,alta_fecha,alta_usr,modi_fecha,modi_usr,baja_fecha,baja_usr,id_plan_omint)
		--SE CAMBIA 05/03/2012 values(cuil_p,inte_p,id_plan_p,0,fecha_ingreso_p, fecha_alta_plan,username,null,null,null,null,p_id_plan_omint);    	
		values(cuil_p,inte_p,id_plan_p,0,fecha_actual, fecha_actual,username,null,null,null,null,p_id_plan_omint);    	
    
	elseif existe_plan is null then
		insert into afi_plan(cuil_titular,inte,id_plan,id_tarifa,vigen_desde,alta_fecha,alta_usr,modi_fecha,modi_usr,baja_fecha,baja_usr,id_plan_omint)
		--SE CAMBIA 05/03/2012 values(cuil_p,inte_p,id_plan_p,0,fecha_ingreso_p, fecha_alta_plan,username,null,null,null,null,p_id_plan_omint);    
		values(cuil_p,inte_p,id_plan_p,0,fecha_actual, fecha_actual,username,null,null,null,null,p_id_plan_omint);    
		    
    end if;

    if esNuevoPlan = true  then
    
    	if existe_plan >= 0 and id_plan_p=existe_plan then
        	fecha_alta_plan=alta_fecha from afi_plan
			where cuil_titular=cuil_p
			and inte=inte_p
			--and id_plan=id_plan_p
			and baja_fecha is null;
	 end if;
    
		if fecha_egreso_p is null then
		    INSERT INTO afi_plan_aporte(
		            cuil_titular, inte, id_plan, id_aporte, plan_alta_fecha, 
		            aporte_alta_fecha, alta_fecha, alta_usr, 
		            modi_fecha, modi_usr)
		    --SE CAMBIA 05/03/2012VALUES (cuil_p, inte_p, id_plan_p, id_aporte_p, fecha_alta_plan, fecha_alta_aporte, fecha_actual, username, fecha_actual, username);
		    VALUES (cuil_p, inte_p, id_plan_p, id_aporte_p, fecha_actual, fecha_alta_aporte, fecha_actual, username, fecha_actual, username);
	    end if;
	else
		 update afi_plan_aporte 
		 set  modi_fecha = fecha_actual,
		 modi_usr = username
		 where cuil_titular = cuil_p
		 and inte = inte_p
		 and id_plan = id_plan_p
		 and id_aporte = id_aporte_p
		 and baja_fecha is null;
	 
	end if;


    return genera_id_socio(cuil_p, inte_p, id_aporte_p, esnuevoplan, esnuevoaporte, idYaFueGenerado, username);
    
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION inserta_afi_aporte_plan(character varying, integer, integer, integer, date, date, integer, character varying, boolean, boolean, integer)
  OWNER TO postgres;


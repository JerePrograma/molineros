CREATE OR REPLACE FUNCTION baja_cascada_sin_situ_laboral(cuil_p character varying, inte_p integer, fecha_egreso_p date, motivo_baja integer, username character varying)
  RETURNS integer AS
$BODY$
declare plan_p integer;
declare plan_base integer;
declare aporte_base integer;
declare plan_omint_p integer;
declare fecha_baja_futura date;
declare fecha_hoy timestamp;
declare baja_futura integer;
declare aporte_p integer;
declare categoria integer;
BEGIN

fecha_hoy=current_timestamp;
categoria=distinct id_categoria from afi_situ_laboral where cuil_titular = cuil_p and inte  =inte_p and (baja_fecha is null or baja_fecha > fecha_hoy) and id_categoria=11;
--DESEMPLEO, DESPIDO; RENUNCIA; FALLECIMIENTO
IF ((motivo_baja=3 or motivo_baja=21 or motivo_baja=1) and categoria not in (12,0)) or (motivo_baja=2 and categoria=11)  then
  baja_futura=1;
END IF;
IF baja_futura=1 then
	fecha_baja_futura=fecha_egreso_p + interval '3 months';
ELSE 
	fecha_baja_futura=fecha_egreso_p;
END IF;

--GUARDO HISTORICO DE ESTADOS AFILIADO
INSERT INTO afi_estados_histo(
            cuil_titular, inte, id_ospim, id_uoma, id_amtima, apellido, nombre, 
            documento_tipo, sexo, cuil, naci_fecha, id_estado_civil_sss, id_parentesco_sss, 
            ingre_fecha, id_seccional, anterior_os, vigen_fecha, observaciones, 
            pres_ssalud_fecha, alta_fecha, alta_usr, modi_fecha, modi_usr, 
            baja_fecha, baja_usr, discapacitado, docu_numero, nacionalidad, 
            aportante_titular, nro_afiliado, id_motivo_baja,
            id_ospim_baja_fecha, id_uoma_baja_fecha, id_amtima_baja_fecha, descripcion_operacion )
select cuil_titular, inte, id_ospim, id_uoma, id_amtima, apellido, nombre, 
            documento_tipo, sexo, cuil, naci_fecha, id_estado_civil_sss, id_parentesco_sss, 
            ingre_fecha, id_seccional, anterior_os, vigen_fecha, observaciones, 
            pres_ssalud_fecha, fecha_hoy, username, modi_fecha, modi_usr, 
            baja_fecha, baja_usr, discapacitado, docu_numero, nacionalidad, 
            aportante_titular, nro_afiliado, id_motivo_baja, id_ospim_baja_fecha, id_uoma_baja_fecha, id_amtima_baja_fecha, 'BTO' 
from afiliado
where cuil_titular=cuil_p
and (baja_fecha is null or baja_fecha>current_date);

RAISE INFO 'baja_futura: %',baja_futura;

IF baja_futura=1 then
--El afiliado queda con plan cobertura (solo id_ospim)
	update afiliado
	set baja_fecha=fecha_baja_futura, 
	baja_usr=username,
	modi_fecha=fecha_hoy, 
	modi_usr=username, 
	id_motivo_baja=motivo_baja
	where cuil_titular=cuil_p
	and (baja_fecha is null or baja_fecha > fecha_hoy);

	/* no vamos a usar mas los id_xxxx_baja fecha
	update afiliado
	set id_uoma_baja_fecha = fecha_egreso_p
	where cuil_titular=cuil_p
	and (baja_fecha is null or baja_fecha > fecha_hoy)
	and (id_uoma_baja_fecha is null or id_uoma_baja_fecha>fecha_hoy);

	update afiliado 
    	set id_amtima_baja_fecha = fecha_egreso_p
    	where cuil_titular=cuil_p
	and (baja_fecha is null or baja_fecha > fecha_hoy)
	and (id_amtima_baja_fecha is null or id_amtima_baja_fecha>fecha_hoy);

    	update afiliado 
    	set id_ospim_baja_fecha = fecha_baja_futura
    	where cuil_titular=cuil_p
	and (baja_fecha is null or baja_fecha > fecha_hoy)
	and (id_ospim_baja_fecha is null or id_ospim_baja_fecha>fecha_hoy);*/
else	
RAISE INFO 'ELLLLSE %', fecha_baja_futura;
	update afiliado
	set baja_fecha=fecha_baja_futura, 
	    baja_usr=username,
	    modi_fecha=fecha_hoy, 
	    modi_usr=username, 
	    id_motivo_baja=motivo_baja
	where cuil_titular=cuil_p
	and (baja_fecha is null or baja_fecha  > fecha_hoy);

	update afiliado
	set id_uoma_baja_fecha = fecha_baja_futura
	where cuil_titular=cuil_p
	and (baja_fecha is null or baja_fecha >= fecha_baja_futura);
	--and (id_uoma_baja_fecha is null or id_uoma_baja_fecha>fecha_hoy);

	update afiliado 
    	set id_amtima_baja_fecha = fecha_baja_futura
    	where cuil_titular=cuil_p
	and (baja_fecha is null or baja_fecha >= fecha_baja_futura);
	--and (id_amtima_baja_fecha is null or id_amtima_baja_fecha>fecha_hoy);

    	update afiliado 
    	set id_ospim_baja_fecha = fecha_baja_futura
    	where cuil_titular=cuil_p
	and (baja_fecha is null or baja_fecha >= fecha_baja_futura);
	--and (id_ospim_baja_fecha is null or id_ospim_baja_fecha>fecha_hoy);
	
end if;

--ACTUALIZO DOMICILIOS
update afi_domicilio
set baja_fecha=fecha_baja_futura,
    baja_usr=username
where cuil_titular=cuil_p
and (baja_fecha is null or baja_fecha > fecha_hoy);


/*

plan_p=id_plan from afi_plan where cuil_titular=cuil_p and inte=0 and (baja_fecha is null or baja_fecha>fecha_hoy) and alta_fecha = (select max(alta_fecha) from  afi_plan where cuil_titular=cuil_p and inte=0 and (baja_fecha is null or baja_fecha>fecha_hoy));

plan_base=id_plan_base from plan where id_plan=plan_p;

plan_omint_p=id_plan_omint from plan_omint where id_plan=plan_p;

aporte_base=a.id_aporte 
	    from plan p, aporte a, plan_aporte pa 
	    where p.id_plan=pa.id_plan
	    and pa.id_aporte=a.id_aporte				  
	    and p.id_plan=plan_p
	    and a.es_os=true;	

--Cambio a plan cobertura
if plan_p<>plan_base then 	
	--baja_plan que no era cobertura
	update afi_plan
	set baja_fecha=fecha_egreso_p,
	    baja_usr=username,
	    id_motivo_baja=motivo_baja
	    where cuil_titular=cuil_p and inte=inte_p and (baja_fecha is null or baja_fecha>fecha_hoy);

	update afi_plan_aporte set baja_fecha=fecha_egreso_p,
	    baja_usr=username
	    where cuil_titular=cuil_p and inte=inte_p and (baja_fecha is null or baja_fecha>fecha_hoy);

	if baja_futura=1 then 
		--alta plan cobertura	
		INSERT INTO afi_plan(
		    cuil_titular, inte, id_plan, id_tarifa, vigen_desde, alta_fecha, 
		    alta_usr, baja_fecha, baja_usr, id_motivo_baja, id_plan_omint)
		VALUES (cuil_p, inte_p, plan_base, 0, fecha_egreso_p, fecha_hoy, username, fecha_baja_futura, username,motivo_baja, plan_omint_p);
		
		--Baja aportes que no sean OS
		update afi_aportes
		set fecha_egre=fecha_egreso_p,	
		modi_usr=username,
		modi_fecha=fecha_hoy,
		id_motivo_baja=motivo_baja
		where cuil_titular=cuil_p
		and inte=inte_p
		and id_aporte in (select a.id_aporte 
				  from plan p, aporte a, plan_aporte pa
				  where p.id_plan=pa.id_plan
			          and pa.id_aporte=a.id_aporte				  
			          and p.id_plan=plan_p
				  and a.es_os=false)
		and (fecha_egre is null or fecha_egre > fecha_hoy)
		and (baja_fecha is null or baja_fecha > fecha_hoy);
		

		aporte_p=id_aporte from afi_aportes where cuil_titular=cuil_p and inte=inte_p and id_aporte=2 and (fecha_egre is null or fecha_egre > fecha_hoy);
		
		if aporte_p is null  then               

		insert into afi_aportes (cuil_titular, inte, id_aporte, fecha_ingre,  fecha_egre,  alta_usr,  alta_fecha , modi_fecha,
    								modi_usr,   id_motivo_baja )
    		values (cuil_p, inte_p, aporte_base, fecha_egreso_p, fecha_baja_futura, username, fecha_hoy,fecha_hoy,username,  motivo_baja );
		else 
			update afi_aportes set fecha_egre = fecha_baja_futura
			where cuil_titular=cuil_p and inte=inte_p and id_aporte=aporte_base and (fecha_egre is null or fecha_egre > fecha_hoy)
																					and (baja_fecha is null or baja_fecha > fecha_hoy);
		end if;
		
		insert into afi_plan_aporte (cuil_titular, inte, id_plan, id_aporte, plan_alta_fecha, aporte_alta_fecha, alta_fecha,
    			alta_usr, modi_fecha, modi_usr, baja_fecha, baja_usr) 
		select cuil_p, inte_p, plan_base, id_aporte, fecha_hoy, alta_fecha, fecha_hoy, username, fecha_hoy, username,fecha_baja_futura, username 
	    from afi_aportes
	    where cuil_titular=cuil_p
		and inte=inte_p
		and id_aporte = aporte_base
		and fecha_egre = fecha_baja_futura
		and (baja_fecha is null or baja_fecha > fecha_hoy);
	    
	    
	    
	else
		--Baja TODOS LOS APORTES 
		update afi_aportes
		set fecha_egre=fecha_egreso_p,	
		modi_usr=username,
		modi_fecha=fecha_hoy,
		id_motivo_baja=motivo_baja
		where cuil_titular=cuil_p
		and inte=inte_p		
		and (fecha_egre is null or fecha_egre > fecha_hoy)
		and (baja_fecha is null or baja_fecha > fecha_hoy);
	end if;
else	
	--Actualizo PLAN OSPIM
	update afi_plan
	set baja_fecha=fecha_baja_futura,
	    baja_usr=username,
	    id_motivo_baja=motivo_baja
	    where cuil_titular=cuil_p and inte=inte_p and id_plan=plan_base and (baja_fecha is null or baja_fecha>fecha_hoy);

	update afi_plan_aporte set baja_fecha=fecha_baja_futura,
	    baja_usr=username
	    where cuil_titular=cuil_p and inte=inte_p and (baja_fecha is null or baja_fecha>fecha_hoy);

	--Actualizo aporte de OSPIM (si tenia)
	update afi_aportes
	set fecha_egre=fecha_baja_futura,
	modi_usr=username,
	modi_fecha=fecha_hoy
	where cuil_titular=cuil_p
	and inte=inte_p
	and (fecha_egre is null or fecha_egre > fecha_hoy)
	and (baja_fecha is null or baja_fecha > fecha_hoy);	
end if;
*/
--Actualizo aporte de OSPIM (si tenia)
/*update afi_aportes
set fecha_egre=fecha_baja_futura,
modi_usr=username,
modi_fecha=fecha_hoy,
id_motivo_baja=motivo_baja
where cuil_titular=cuil_p
and inte=inte_p
and id_aporte = aporte_base
and (fecha_egre is null or fecha_egre > fecha_hoy)
and (baja_fecha is null or baja_fecha > fecha_hoy);
*/
--Actualizo Tercerizadora
update afi_tercerizadora_servicio 
set fecha_fin_pres=fecha_baja_futura,
modi_fecha=fecha_hoy,
modi_usr=username
where cuil_titular=cuil_p
and inte=inte_p
and (baja_fecha is null or baja_fecha > fecha_baja_futura)
and (fecha_fin_pres is null or fecha_fin_pres > fecha_baja_futura);

--SI ES UN FALLECIDO, EL FALLECIDO SE DA DE BAJA A LA FECHA ACTUAL Y SE CARGA EL EMPLEADOR "SUBSIDIO" EL RESTO QUEDA CON COBERTURA X 90 d
if motivo_baja=2 and categoria=11 then
	update afiliado
	set baja_fecha=fecha_egreso_p,
	baja_usr=username,
	id_motivo_baja=motivo_baja
	where cuil_titular=cuil_p
	and (baja_fecha is null or baja_fecha>current_date)
	and inte=inte_p;		
	--INSERTO EL SUBSIDIO
	INSERT INTO afi_situ_laboral(
            cuil_titular, inte, cuit, sucursal, fecha_ingre,  id_revista, 
            fecha_egre, alta_fecha, alta_usr,  id_motivo_baja, id_categoria)
	VALUES (cuil_p, inte_p, '99999999999', '000', fecha_egreso_p, 3, fecha_baja_futura, fecha_hoy,username,2,0);

end if;

RETURN 1;	

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
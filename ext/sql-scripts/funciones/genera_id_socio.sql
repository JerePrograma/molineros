CREATE OR REPLACE FUNCTION genera_id_socio(cuil_p character varying, inte_p integer, id_aporte_p integer, esnuevoplan boolean, esnuevoaporte boolean, idyafuegenerado boolean, username character varying)
  RETURNS integer AS
$BODY$
	declare tiene_uoma int;
	declare tiene_amtima int;
	declare tiene_ospim int;
	declare aporte_ospim char(1);
	declare aporte_uoma char(1);
	declare aporte_amtima char(1);
  BEGIN

	  if idYaFueGenerado = true then
	  	return 0;
	  end if;
	  
--Busco aportes UOMA
    tiene_uoma=max(id_uoma) from afiliado
	     where cuil_titular=cuil_p
	     and id_uoma<>0;
	     --and inte=inte_p;
	     
    if tiene_uoma=0 or tiene_uoma is null or esnuevoplan = true then
		aporte_uoma=genera_id_socio from aporte where id_aporte=id_aporte_p;
        
		if aporte_uoma='U' and esnuevoaporte = true then
			tiene_uoma=max(id_uoma)+1 from afiliado;
		end if;  
	end if;	 
	
	update afiliado
	set id_uoma=tiene_uoma,
		modi_usr=username,
		modi_fecha=current_timestamp
    where cuil_titular=cuil_p;	        
	
    /* Mantener id de socio en los aportes vigentes */
	update afi_plan_aporte apa
	    set id_socio=tiene_uoma, tipo_aporte=aporte_uoma
		where apa.id_aporte=(select id_aporte from aporte a where apa.id_aporte=a.id_aporte
		and a.genera_id_socio='U') --aporte_uoma
		and apa.cuil_titular=cuil_p
		and apa.id_aporte=id_aporte_p
		and apa.baja_fecha is null
		and apa.alta_fecha = (select max(alta_fecha) from afi_plan_aporte apa_
							  where apa.cuil_titular=apa_.cuil_titular
							  and apa.id_aporte=apa_.id_aporte);			
--BUSCO APORTES AMTIMA
    tiene_amtima=max(id_amtima) from afiliado
	     where cuil_titular=cuil_p
	     and id_amtima<>0; 
	     --and inte=inte_p;

    if tiene_amtima=0 or tiene_amtima is null or esnuevoplan = true then

		aporte_amtima=genera_id_socio from aporte where id_aporte=id_aporte_p;
	
        if aporte_amtima='A' and esnuevoaporte = true then
			tiene_amtima=max(id_amtima)+1 from afiliado;
		end if; 
    end if;     
		
    update afiliado
	set id_amtima=tiene_amtima,
		modi_usr=username,
		modi_fecha=current_timestamp
	where cuil_titular=cuil_p;

	 /* Mantener id de socio en los aportes vigentes */
    update afi_plan_aporte apa
    set id_socio=tiene_amtima, tipo_aporte=aporte_amtima
	where apa.id_aporte=(select id_aporte from aporte a where apa.id_aporte=a.id_aporte
	and a.genera_id_socio='A') --aporte_amtima
	and apa.cuil_titular=cuil_p
	and apa.id_aporte=id_aporte_p
	and apa.baja_fecha is null
	and apa.alta_fecha = (select max(alta_fecha) from afi_plan_aporte apa_
						  where apa.cuil_titular=apa_.cuil_titular
						  and apa.id_aporte=apa_.id_aporte);       

--BUSCO APORTES OSPIM
    tiene_ospim=max(id_ospim) from afiliado
	     where cuil_titular=cuil_p
	     and id_ospim<>0;
	     --and inte=inte_p;

    if tiene_ospim=0 or tiene_ospim is null or esnuevoplan = true then

		aporte_ospim=genera_id_socio from aporte where id_aporte=id_aporte_p;
	
        if aporte_ospim='O' and esnuevoaporte = true then
			tiene_ospim=max(id_ospim)+1 from afiliado;
		end if;
	end if;
	
	update afiliado
	set id_ospim=tiene_ospim,
		modi_usr=username,
		modi_fecha=current_timestamp
    where cuil_titular=cuil_p;
	
     /* Mantener id de socio en los aportes vigentes */

    update afi_plan_aporte apa
    set id_socio=tiene_ospim, tipo_aporte=aporte_ospim
	where apa.id_aporte=(select id_aporte from aporte a where apa.id_aporte=a.id_aporte
	and a.genera_id_socio='O') --aporte_ospim
	and apa.cuil_titular=cuil_p
	and apa.id_aporte=id_aporte_p
	and apa.baja_fecha is null
	and apa.alta_fecha = (select max(alta_fecha) from afi_plan_aporte apa_
						  where apa.cuil_titular=apa_.cuil_titular
						  and apa.id_aporte=apa_.id_aporte);
								
    if esnuevoaporte = true then
	INSERT INTO afi_estados_histo(cuil_titular, inte, id_ospim, id_uoma, id_amtima, apellido, nombre, 
					      documento_tipo, sexo, cuil, naci_fecha, id_estado_civil_sss, id_parentesco_sss, 
					      ingre_fecha, id_seccional, anterior_os, vigen_fecha, observaciones, 
					      pres_ssalud_fecha, alta_fecha, alta_usr, modi_fecha, modi_usr, 
					      baja_fecha, baja_usr, discapacitado, docu_numero, nacionalidad, 
					      aportante_titular, nro_afiliado, id_motivo_baja, id_ospim_baja_fecha, 
					      id_uoma_baja_fecha, id_amtima_baja_fecha, descripcion_operacion)
                 select cuil_titular, inte, id_ospim, id_uoma, id_amtima, apellido, nombre, 
					      documento_tipo, sexo, cuil, naci_fecha, id_estado_civil_sss, id_parentesco_sss, 
					      ingre_fecha, id_seccional, anterior_os, vigen_fecha, observaciones, 
					      pres_ssalud_fecha, current_timestamp, username, modi_fecha, modi_usr, 
					      baja_fecha, baja_usr, discapacitado, docu_numero, nacionalidad, 
					      aportante_titular, nro_afiliado, id_motivo_baja, id_ospim_baja_fecha, 
					      id_uoma_baja_fecha, id_amtima_baja_fecha, 'MOD'
		 from afiliado where cuil_titular=cuil_p;
    end if;
	     
    return 0;
    
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
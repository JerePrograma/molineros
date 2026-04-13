CREATE OR REPLACE FUNCTION agrupa_bonos_rendidos(tipo_bono_v integer, seccional_v integer, fecha_desde_v date, fecha_hasta_v date, nro_bono_desde integer, nro_bono_hasta integer)
  RETURNS integer AS
$BODY$
declare _record_bonos RECORD;
declare min int;
declare nro_bono_ant int;
declare max int;
declare tipo_bono_ant int;
declare id_envio_ant int;
declare fecha_envio_ant date;
declare seccional_ant int;
declare primero boolean;
declare fecha_rendido_aux date;

BEGIN



primero=false;
tipo_bono_ant=0;
seccional_ant=0;
id_envio_ant=0;


FOR _record_bonos IN select tipo_bono, nro_bono, id_seccional, id_envio, fecha_envio, fecha_rendido
		     from bonos_seccional b
	             where fecha_rendido is not null 
	             and (tipo_bono_v is null or (tipo_bono_v is not null and b.tipo_bono=tipo_bono_v)) 
		     and (seccional_v  is null or (seccional_v  is not null and b.id_seccional=seccional_v ))	
		     and (fecha_desde_v  is null or (fecha_desde_v  is not null and b.fecha_envio>=fecha_desde_v ))	
		     and (fecha_hasta_v  is null or (fecha_hasta_v  is not null and b.fecha_envio<=fecha_hasta_v ))	
		     and (nro_bono_desde is null or (nro_bono_desde  is not null and b.nro_bono>=nro_bono_desde))	
		     and (nro_bono_hasta is null or (nro_bono_hasta  is not null and b.nro_bono<=nro_bono_hasta))order by tipo_bono, nro_bono, id_seccional, id_envio LOOP

        fecha_rendido_aux=_record_bonos.fecha_rendido;

	if min is null or primero=false or (tipo_bono_ant=0 or tipo_bono_ant<>_record_bonos.tipo_bono) or (seccional_ant=0 or seccional_ant<>_record_bonos.id_seccional) 
		       or (id_envio_ant=0 or id_envio_ant<>_record_bonos.id_envio) then	    
            
            primero=true;	       
            if seccional_ant<>0 and seccional_ant<>_record_bonos.id_seccional then
		if (max=0 or max is null or max<min) then max=min; end if;
		insert into aux_bonos (tipo_bono, id_seccional, descripcion, fecha_envio , min_bono, max_bono, fecha_rendido, id_envio, total) 		
		select cast(tipo_bono_ant as varchar)||'-'||t.descripcion,s.id_seccional,s.descripcion,fecha_envio_ant, min, max, fecha_rendido_aux, id_envio_ant,max+1-min 
	        from tipos_bono t, seccional s
	        where t.tipo_bono=tipo_bono_ant
	        and s.id_seccional=seccional_ant;	    	        
	        --primero=false;
	    elseif id_envio_ant<>0 and  id_envio_ant<>_record_bonos.id_envio then 		
		if (max=0 or max is null or max<min) then max=min; end if;
		insert into aux_bonos (tipo_bono, id_seccional, descripcion, fecha_envio , min_bono, max_bono, fecha_rendido, id_envio, total) 
		select cast(tipo_bono_ant as varchar)||'-'||t.descripcion,s.id_seccional,s.descripcion,fecha_envio_ant,min,max,fecha_rendido_aux,id_envio_ant,max+1-min 
	        from tipos_bono t, seccional s
	        where t.tipo_bono=tipo_bono_ant
	        and s.id_seccional=seccional_ant;
		--primero=false;
            end if;            		       
	    nro_bono_ant=_record_bonos.nro_bono;
            tipo_bono_ant=_record_bonos.tipo_bono;
            id_envio_ant=_record_bonos.id_envio;
            fecha_envio_ant=_record_bonos.fecha_envio;
            seccional_ant=_record_bonos.id_seccional;
	    min=_record_bonos.nro_bono;
	    
	else
	    if _record_bonos.nro_bono=nro_bono_ant+1 then 		
		max=_record_bonos.nro_bono;
		nro_bono_ant=_record_bonos.nro_bono;
		
	    else			
		if (max=0 or max is null or max<min) then max=min; end if;
	        insert into aux_bonos (tipo_bono, id_seccional, descripcion, fecha_envio , min_bono, max_bono, fecha_rendido, id_envio, total) 	        
	        select cast(tipo_bono_ant as varchar)||'-'||t.descripcion,s.id_seccional,s.descripcion,fecha_envio_ant,min,max ,fecha_rendido_aux,id_envio_ant ,max+1-min 
	        from tipos_bono t, seccional s
	        where t.tipo_bono=tipo_bono_ant
	        and s.id_seccional=seccional_ant;
	        nro_bono_ant=_record_bonos.nro_bono;
		tipo_bono_ant=_record_bonos.tipo_bono;
		min=_record_bonos.nro_bono;	        
            end if;
        end if;        
END LOOP;      

insert into aux_bonos (tipo_bono, id_seccional, descripcion, fecha_envio , min_bono, max_bono, fecha_rendido, id_envio, total)  
select cast(tipo_bono_ant as varchar)||'-'||t.descripcion as aaa,s.id_seccional,s.descripcion,fecha_envio_ant,min,max,fecha_rendido_aux,id_envio_ant,max+1-min	        
	        from tipos_bono t, seccional s
	        where t.tipo_bono=tipo_bono_ant
	        and s.id_seccional=seccional_ant;
	        nro_bono_ant=_record_bonos.nro_bono;
		tipo_bono_ant=_record_bonos.tipo_bono;
		min=_record_bonos.nro_bono;	           
	             

return 1;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

CREATE OR REPLACE FUNCTION agrupa_bonos_sin_enviar(tipo_bono_v integer, fecha_desde_v date, fecha_hasta_v date, nro_bono_desde integer, nro_bono_hasta integer)
  RETURNS integer AS
$BODY$
declare _record_bonos RECORD;
declare min int;
declare nro_bono_ant int;
declare max int;
declare tipo_bono_ant int;
declare primero boolean;
BEGIN

primero=false;
tipo_bono_ant=0;

FOR _record_bonos IN select tipo_bono, nro_bono
		     from bonos b
	             where not exists (select 1 from bonos_seccional s where b.tipo_bono=s.tipo_bono and b.nro_bono=s.nro_bono) 
	             and (tipo_bono_v is null or (tipo_bono_v is not null and b.tipo_bono=tipo_bono_v)) 		     	
		     and (fecha_desde_v  is null or (fecha_desde_v  is not null and b.alta_fecha>=fecha_desde_v ))	
		     and (fecha_hasta_v  is null or (fecha_hasta_v  is not null and b.alta_fecha<=fecha_hasta_v ))	
		     and (nro_bono_desde is null or (nro_bono_desde  is not null and b.nro_bono>=nro_bono_desde))	
		     and (nro_bono_hasta is null or (nro_bono_hasta  is not null and b.nro_bono<=nro_bono_hasta))
	             order by tipo_bono, nro_bono LOOP
        	             
	if min is null or primero=false or (tipo_bono_ant=0 or tipo_bono_ant<>_record_bonos.tipo_bono) then	    
	    nro_bono_ant=_record_bonos.nro_bono;
            tipo_bono_ant=_record_bonos.tipo_bono;
	    min=_record_bonos.nro_bono;	    
	    primero=true;
	else
	    if _record_bonos.nro_bono=nro_bono_ant+1 then 
		max=_record_bonos.nro_bono;
		nro_bono_ant=_record_bonos.nro_bono;		
	    else
		if (max=0 or max is null) then max=min; end if;
	        insert into aux_bonos (tipo_bono, id_seccional, descripcion, fecha_envio , min_bono, max_bono, fecha_rendido, id_envio, total) 
	        select cast(tipo_bono_ant as varchar)||'-'||t.descripcion,null,null,null,min,max,null,null,max+1-min	        
	        from tipos_bono t
	        where t.tipo_bono=tipo_bono_ant;
	        nro_bono_ant=_record_bonos.nro_bono;
		tipo_bono_ant=_record_bonos.tipo_bono;
		min=_record_bonos.nro_bono;	        
            end if;
        end if;        
END LOOP;      
insert into aux_bonos (tipo_bono, id_seccional, descripcion, fecha_envio , min_bono, max_bono, fecha_rendido, id_envio, total)  
select cast(tipo_bono_ant as varchar)||'-'||t.descripcion,null,null,null,min,max,null,null,max+1-min	        
	        from tipos_bono t
	        where t.tipo_bono=tipo_bono_ant;
	        nro_bono_ant=_record_bonos.nro_bono;
		tipo_bono_ant=_record_bonos.tipo_bono;
		min=_record_bonos.nro_bono;	           
	             

return 1;

END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE

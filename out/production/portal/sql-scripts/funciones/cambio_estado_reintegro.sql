drop function cambio_estado_reintegro(id_reintegro integer, estado integer, username character varying)

CREATE OR REPLACE FUNCTION cambio_estado_reintegro(id_reintegro_ integer, estado_ integer, username character varying, tipo_reintegro character varying)
  RETURNS integer AS  
$BODY$	
	declare estado_anterior integer;
	begin
		
	estado_anterior = estado from reintegro where id_reintegro = id_reintegro_;
	
    update reintegro
    set estado = estado_,
    modi_fecha = localtimestamp,
    modi_usr = username
    where id_reintegro=id_reintegro_;
        
    --si el reintegreo es de protesis entonces debo evaluar la siguiente condicion
    if tipo_reintegro='pro' then
    --si el reintegro debe dejar de ser pendiente, entonces generar el numero de reintegro con el siguiente número de reintegro posible
	    if estado_anterior is not null and estado_anterior = 4 then
	    	update reintegro r set id_reintegro_user = nextval('reintegro_protesis_id_seq') where r.id_reintegro = id_reintegro_;
	    end if;	
	end if;
    return 1;
    
end;    
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
CREATE OR REPLACE FUNCTION cambio_estado_cuota(

id_reintegro_ integer,
cuota_ integer,
estado_ integer, 
username character varying

)
  RETURNS integer AS  
$BODY$
	declare estado_anterior integer;
	begin
	
	estado_anterior = estado from detalle_cuota where id_reintegro = id_reintegro_ and nro_cuota = cuota_;
	
    update detalle_cuota
    set estado = estado_
    where id_reintegro = id_reintegro_
    and nro_cuota = cuota_;
    
    if estado_ = 3 then
    	update detalle_cuota set id_cuota = nextval('reintegro_protesis_id_seq') where id_reintegro = id_reintegro_ and nro_cuota = cuota_;
    end if;
    if estado_anterior = 3 and estado_ = 5 then
    	update detalle_cuota set id_cuota = 0 where id_reintegro = id_reintegro_ and nro_cuota = cuota_;
    end if;
    return 1;
end;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
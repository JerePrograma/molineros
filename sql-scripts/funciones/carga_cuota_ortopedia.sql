CREATE OR REPLACE FUNCTION carga_cuota_ortopedia(id_reintegro_ integer, nro_cuota_ integer, porcentaje_ integer, importe_ numeric, username character varying)
  RETURNS integer AS  
$BODY$
	
	declare estado_ integer;
	
	begin		  
	
	estado_ = estado from reintegro where id_reintegro = id_reintegro_;
		
	INSERT INTO detalle_cuota(id_cuota, 
            id_reintegro, nro_cuota, porcentaje, importe, estado)
    VALUES (0, id_reintegro_, nro_cuota_, porcentaje_, importe_, estado_);
    
    return 1;
end;    
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
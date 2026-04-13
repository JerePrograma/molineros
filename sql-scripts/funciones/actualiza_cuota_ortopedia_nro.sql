CREATE OR REPLACE FUNCTION actualiza_cuota_ortopedia_nro(id_reintegro_ integer, nro_cuota_ integer, porcentaje_ integer, importe_ numeric, username character varying)
  RETURNS integer AS  
$BODY$

	begin		  		
		
	update detalle_cuota set importe = importe_
	where id_reintegro = id_reintegro_ and nro_cuota = nro_cuota_;
    
	return 1;
end;    
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
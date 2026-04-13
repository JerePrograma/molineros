CREATE OR REPLACE FUNCTION inserta_medicamento_reintegro_farmacia(id_reintegro_p integer, id_medicamento_p integer, 
			fecha_p date, nro_receta_p integer, profesional_p character varying, cantidad_p integer, 
			troquel_p integer, cober_sss_p double precision, cober_amtima_p double precision, cober_ospim_p double precision, 
			monto_ospim_p double precision, monto_amtima_p double precision, precio_al_publico_p double precision, 
			precio_ospim_p double precision, total_cober double precision, total_p double precision, total_med_p double precision, 
			fecha_receta_p date, user_p character varying)
  RETURNS integer AS
$BODY$
  begin
  insert into medicamento_reintegro_farmacia (id_reintegro,id_medicamento, fecha, nro_receta, profesional, cantidad, troquel, cober_sss, cober_amtima, cober_ospim, monto_ospim, monto_amtima, precio_al_publico, precio_ospim, 
	total_med, total_cobertura, total, fecha_receta,
	alta_fecha, alta_usr, mod_fecha, modi_usr)  
  values (id_reintegro_p, id_medicamento_p, fecha_p, nro_receta_p, '', cantidad_p, troquel_p, cober_sss_p, cober_amtima_p, cober_ospim_p, monto_ospim_p, monto_amtima_p, precio_al_publico_p, precio_ospim_p, 
          total_med_p, total_cober, total_p, fecha_receta_p,
  current_date,  user_p, current_date, user_p); 
   
  return 1;
  end;  
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
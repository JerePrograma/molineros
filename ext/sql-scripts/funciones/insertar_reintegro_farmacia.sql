-- Function: insertar_reintegro_farmacia(date, date, integer, character varying, integer, character varying)

-- DROP FUNCTION insertar_reintegro_farmacia(date, date, integer, character varying, integer, character varying);

CREATE OR REPLACE FUNCTION insertar_reintegro_farmacia(fecha_p date, periodo_p date, id_seccional_p integer, cuil_p character varying, inte_p integer, user_p character varying)
  RETURNS integer AS
$BODY$
  begin

  insert into reintegro_farmacia (fecha, periodo, id_seccional, cuil_titular, inte, alta_fecha, alta_usr, modi_fecha, modi_usr)  
  values (fecha_p, periodo_p, id_seccional_p, cuil_p, inte_p, current_date, user_p, current_date, user_p ); 
   
  return currval('reintegro_farmacia_id_reintegro_seq');
  end;  
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION insertar_reintegro_farmacia(date, date, integer, character varying, integer, character varying) OWNER TO postgres;

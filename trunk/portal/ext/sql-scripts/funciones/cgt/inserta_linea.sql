CREATE OR REPLACE FUNCTION inserta_linea(tipo_linea_p character varying, linea_p character varying, id_organismo_p integer, p_user character varying, origen_p integer)
  RETURNS integer AS
$BODY$
BEGIN
if origen_p=1 then
	INSERT INTO organismo_linea(
		    id_organismo, tipo_linea, linea, alta_fecha, modi_fecha, alta_user, modi_user)
	values(id_organismo_p, tipo_linea_p, linea_p, current_timestamp, current_timestamp, p_user,p_user);
else 	if origen_p=2 then
		INSERT INTO area_linea(
			    id_area, tipo_linea, linea, alta_fecha, modi_fecha, alta_user, modi_user)
		values(id_organismo_p, tipo_linea_p, linea_p, current_timestamp, current_timestamp, p_user,p_user);
	end if;
end if;	
		

	

return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE

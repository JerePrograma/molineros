CREATE OR REPLACE FUNCTION inserta_comentario(fecha_p date, comentario_p character varying, id_organismo_p integer, p_user character varying, origen_p integer)
  RETURNS integer AS
$BODY$
declare organismo_int integer;
BEGIN

if origen_p=1 then
	INSERT INTO organismo_comentario(
		    id_organismo, fecha, comentario, alta_fecha, alta_user, 
		    modi_fecha, modi_user)
	values(id_organismo_p, fecha_p, comentario_p, LOCALTIMESTAMP, p_user, LOCALTIMESTAMP, p_user);
else 	if origen_p=2 then
		INSERT INTO area_comentario(
			    id_area, fecha, comentario, alta_fecha, alta_user, 
			    modi_fecha, modi_user)
		values(id_organismo_p, fecha_p, comentario_p, LOCALTIMESTAMP, p_user, LOCALTIMESTAMP, p_user);
	end if;
end if;	

return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE

CREATE OR REPLACE FUNCTION actualiza_area(id_area_p integer, nombre_p character varying, telefono_p character varying, web_p character varying, observaciones_p character varying, p_user character varying)
  RETURNS integer AS
$BODY$
declare resultDom integer;
BEGIN

update area
set denominacion=nombre_p,
telefono=telefono_p,
web=web_p,
observaciones=observaciones_p,
modi_usr=p_user,
modi_fecha=current_timestamp
where id_area=id_area_p;

return id_area_p;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION actualiza_area(integer, character varying, character varying, character varying, character varying, character varying)
  OWNER TO postgres;


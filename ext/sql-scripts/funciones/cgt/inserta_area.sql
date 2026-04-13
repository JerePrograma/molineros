CREATE OR REPLACE FUNCTION inserta_area(id_organismo_p integer, nombre_p character varying, telefono_p character varying, web_p character varying, observaciones_p character varying, p_user character varying)
  RETURNS integer AS
$BODY$
declare resultDom integer;
BEGIN
INSERT INTO area(id_organismo, denominacion, observaciones, telefono, web, alta_fecha, alta_usr, 
            modi_fecha, modi_usr)
values(id_organismo_p, nombre_p, observaciones_p,  telefono_p, web_p, LOCALTIMESTAMP, p_user, LOCALTIMESTAMP, p_user);


return currval('area_id_seq');
END;
$BODY$
  LANGUAGE plpgsql VOLATILE

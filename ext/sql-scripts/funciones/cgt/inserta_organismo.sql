CREATE OR REPLACE FUNCTION inserta_organismo(nombre_p character varying, ambito_p character varying, telefono_p character varying, web_p character varying, observaciones_p character varying, sigla_p character varying, orbita_p character varying, p_user character varying)
  RETURNS integer AS
$BODY$
declare resultDom integer;
BEGIN
INSERT INTO organismo(
            denominacion, sigla, observaciones, ambito, telefono, web, orbita, alta_fecha, alta_usr, 
            modi_fecha, modi_usr)
values(nombre_p, sigla_p, observaciones_p, ambito_p, telefono_p, web_p, orbita_p, LOCALTIMESTAMP, p_user, LOCALTIMESTAMP, p_user);


return currval('organismo_id_seq');
END;
$BODY$
  LANGUAGE plpgsql VOLATILE

-- Function: inserta_recibo(character varying, numeric, character varying, character varying, character varying, timestamp without time zone, character varying, boolean)

-- DROP FUNCTION inserta_recibo(character varying, numeric, character varying, character varying, character varying, timestamp without time zone, character varying, boolean);

CREATE OR REPLACE FUNCTION inserta_recibo(p_numero character varying, p_importe numeric, p_obs character varying, p_cuit character varying, p_sucu character varying, p_seccional integer, p_fecha timestamp without time zone, p_user character varying, p_dar_de_baja boolean)
  RETURNS integer AS
$BODY$
declare resultDom integer;
BEGIN
	
INSERT INTO recibo(numero, importe, cuit, sucursal, fecha, descripcion, alta_fecha, alta_usr, 
            modi_fecha, modi_usr, baja_fecha, baja_usr, id_seccional)
    VALUES (p_numero, p_importe, p_cuit, p_sucu,  p_fecha, p_obs, LOCALTIMESTAMP, p_user, LOCALTIMESTAMP, 
            p_user, case when p_dar_de_baja then p_fecha else null end, case when p_dar_de_baja then p_user else null end, p_seccional);

return currval('recibo_id_seq');
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- Function: uoma.inserta_domicilio_correspondencia(integer, integer, character varying, character varying, character varying, character varying, character varying, character varying, character varying)

-- DROP FUNCTION uoma.inserta_domicilio_correspondencia(integer, integer, character varying, character varying, character varying, character varying, character varying, character varying, character varying);

CREATE OR REPLACE FUNCTION uoma.inserta_domicilio_correspondencia(id_localidad_v integer, id_provincia_v integer, calle_v character varying, numero_v character varying, piso_v character varying, depto_v character varying, postal_codi_v character varying, observaciones_v character varying, username character varying)
  RETURNS integer AS
$BODY$
declare id_domicilio_p integer;
declare id_incidente_p integer;
BEGIN
INSERT INTO uoma.domicilio_correspondencia(
            calle, piso, depto,  postal_codi, observaciones, domi_val, alta_fecha, alta_usr, modi_fecha, 
            modi_usr, provincia, localidad, numero)
    VALUES (calle_v, piso_v, depto_v, postal_codi_v, observaciones_v, '0', current_date, username, current_date, username, 
	    id_provincia_v, id_localidad_v, numero_v);            
id_domicilio_p=currval('uoma.domicilio_uoma_corr_id_seq');

return id_domicilio_p;


END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION uoma.inserta_domicilio_correspondencia(integer, integer, character varying, character varying, character varying, character varying, character varying, character varying, character varying)
  OWNER TO postgres;


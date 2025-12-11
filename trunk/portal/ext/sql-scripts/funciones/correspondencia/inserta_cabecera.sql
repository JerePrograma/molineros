CREATE OR REPLACE FUNCTION correo.inserta_cabecera(p_lugar_recep_emision character varying, p_fecha timestamp without time zone, 
p_tipo_registro character varying, p_tipo_envio character varying, p_oblea character varying, p_usuario character varying)
  RETURNS integer AS
$BODY$

BEGIN

INSERT INTO correo.cabecera_correspondencia(
            lugar_recep_emision, fecha, tipo_registro, tipo_envio, oblea,
            alta_fecha, alta_usr, modi_fecha, modi_usr, baja_fecha, baja_usr            
            )
    
    VALUES (p_lugar_recep_emision, p_fecha, p_tipo_registro, p_tipo_envio, p_oblea,   		
    		LOCALTIMESTAMP, p_usuario, LOCALTIMESTAMP, p_usuario, null, null);

return currval('correo.correspondencia_id_seq');
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
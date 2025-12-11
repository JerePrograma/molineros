CREATE OR REPLACE FUNCTION inserta_contrato

(IN p_id_prestador integer, IN p_estado integer, IN p_dia_recepcion integer,
IN p_condicion_de_pago character varying, IN p_id_tipo_pago integer, IN p_usuario character varying)

  RETURNS integer AS
$BODY$

BEGIN

INSERT INTO contrato(
            id_prestador, estado, dia_recepcion, condicion_de_pago, 
            id_tipo_pago, alta_fecha, alta_usr, modi_fecha, modi_usr, baja_fecha, 
            baja_usr)
    
    VALUES (p_id_prestador, p_estado, p_dia_recepcion, 
    		p_condicion_de_pago, p_id_tipo_pago, 
    		LOCALTIMESTAMP, p_usuario, LOCALTIMESTAMP, p_usuario, null, null);

return currval('contrato_id_seq');
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
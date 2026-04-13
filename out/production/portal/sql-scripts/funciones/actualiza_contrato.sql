CREATE OR REPLACE FUNCTION actualiza_contrato
(
IN p_id_prestador integer, IN p_estado integer, IN p_dia_recepcion integer,
IN p_condicion_de_pago character varying, IN p_id_tipo_pago integer, IN p_usuario character varying
)
 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
 begin 

 UPDATE contrato
   SET id_prestador=p_id_prestador, estado=p_estado, dia_recepcion=p_dia_recepcion, condicion_de_pago=p_condicion_de_pago, 
       id_tipo_pago=p_id_tipo_pago, alta_fecha=LOCALTIMESTAMP, alta_usr=p_usuario, modi_fecha=LOCALTIMESTAMP, modi_usr=p_usuario        
 WHERE id_contrato=p_id_contrato;

 return 1;
 end;

$BODY$;
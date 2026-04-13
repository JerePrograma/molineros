drop sequence orden_pago_ospim_id_seq cascade;

CREATE OR REPLACE FUNCTION insertar_orden_pago_ospim(p_importe numeric, p_prestador boolean, p_farmacia boolean, p_cuit_acreedor character varying, p_sucu_acreedor character varying, p_seccional integer, p_obs character varying, p_dto numeric, p_dto_drog numeric, p_fecha_desde date, p_usuario character varying)
  RETURNS integer AS
$BODY$    
    declare v_id_orden_pago integer;
begin

	  v_id_orden_pago=max(id_orden_pago) from orden_pago_ospim;
	  v_id_orden_pago=v_id_orden_pago+1;
	  
	  INSERT INTO orden_pago_ospim(id_orden_pago, importe, prestador, farmacia, cuit_acreedor, sucu_acreedor, id_seccional, observaciones,
		descuento, descuento_por_drogueria, fecha_desde, alta_fecha, alta_usr, modi_fecha, modi_usr )
	  VALUES (v_id_orden_pago, p_importe, p_prestador, p_farmacia, p_cuit_acreedor, p_sucu_acreedor, p_seccional, p_obs,p_dto, p_dto_drog, 
	    p_fecha_desde, LOCALTIMESTAMP, p_usuario , LOCALTIMESTAMP, p_usuario);
	  
      return  v_id_orden_pago;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE


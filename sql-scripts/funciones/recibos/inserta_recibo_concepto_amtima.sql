CREATE OR REPLACE FUNCTION inserta_recibo_concepto_amtima(p_recibo_id integer, p_acta_id integer, p_convenio_id integer, p_nro_cheque_no_depositado numeric, p_id_banco_no_depositado integer, p_nro_cheque_rechazado numeric, p_id_banco_rechazado integer, p_caja_concepto_id integer, p_importe_por_cheques numeric, p_importe_adicional numeric, p_user character varying, p_importe_remun_total numeric, p_periodo date, p_cant_empleados integer )
  RETURNS integer AS
$BODY$
declare resultDom integer;
BEGIN
	
INSERT INTO recibo_conceptos_amtima(recibo_id, acta_id, convenio_id, nro_cheque_no_depositado, 
            id_banco_no_depositado, nro_cheque_rechazado, id_banco_rechazado, caja_concepto_id,
            concepto_importe_por_cheques, concepto_importe_adicional, alta_fecha, 
            alta_usr, modi_fecha, modi_usr, importe_remuneracion_total, periodo, cantidad_empleados)
    VALUES (p_recibo_id, p_acta_id, p_convenio_id, p_nro_cheque_no_depositado, p_id_banco_no_depositado,
 			p_nro_cheque_rechazado, p_id_banco_rechazado, p_caja_concepto_id, p_importe_por_cheques, p_importe_adicional,
 			localtimestamp, p_user, localtimestamp, p_user, p_importe_remun_total, p_periodo, p_cant_empleados);

/*if p_acta_id is not null then
	update acta_pagos set recibo_id = currval('recibo_id_seq') where acta_id = p_acta_id and recibo_id is null and tipo = 'PGO' and forma = 'C' and baja_fecha is null;
end if;

if p_convenio_id is not null then
	update convenio_pagos set recibo_id = currval('recibo_id_seq') where convenio_id = p_convenio_id and recibo_id is null and tipo = 'PGO' and nro_cheque is not null and baja_fecha is null;
end if;*/

return currval('recibo_conceptos_amtima_id_seq');
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
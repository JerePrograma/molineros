CREATE OR REPLACE FUNCTION actualiza_acta_no_os_pagos(p_actapago_id integer, p_tipo character, p_fecha_pago timestamp without time zone, p_importe numeric, p_user character varying, p_nro_cheque numeric, p_banco_cheque integer, p_forma character)
  RETURNS integer AS
$BODY$
declare resultDom integer;
BEGIN
	
update acta_no_os_pagos set tipo = p_tipo, fecha_pago = p_fecha_pago, importe = p_importe,  modi_fecha = localtimestamp, 
	modi_usr = p_user, nro_cheque = p_nro_cheque, 	banco_cheque = p_banco_cheque, forma = p_forma
where id = p_actapago_id;

return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE


CREATE OR REPLACE FUNCTION inserta_recibo_concepto_pago_amtima(p_recibo_concepto_id integer, p_recibo_ingreso_id integer, p_importe numeric, p_user character varying)
  RETURNS integer AS
$BODY$
declare resultDom integer;
BEGIN
	
INSERT INTO recibo_conceptos_pagos_amtima( recibo_concepto_id ,	recibo_ingreso_id,
			importe, alta_fecha,  alta_usr, modi_fecha, modi_usr, pendiente_derivar)
    VALUES (p_recibo_concepto_id, p_recibo_ingreso_id, 	p_importe,	localtimestamp, p_user, localtimestamp, p_user, p_importe);


return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE

drop  FUNCTION sacar_anulacion_cheques_op(p_orden_pago_ospim_id integer, p_date date);
CREATE OR REPLACE FUNCTION sacar_anulacion_cheques_op(p_orden_pago_ospim_id integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
BEGIN

update cheque  c set baja_fecha = null, baja_usr = null
from orden_pago_ospim_pagos opop
where c.nro_cheque = opop.nro_cheque
and c.id_banco = opop.id_banco_cheque
and opop.id_orden_pago = p_orden_pago_ospim_id;

return 1;
END;
$BODY$;
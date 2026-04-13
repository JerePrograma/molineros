CREATE OR REPLACE FUNCTION reactivar_orden_pago_ospim(p_orden_pago_ospim_id integer)
  RETURNS integer AS
$BODY$
BEGIN

update orden_pago_ospim set baja_fecha = null, baja_usr = null,
	concepto = replace(concepto, '- ANULADA', '')
where id_orden_pago = p_orden_pago_ospim_id;

update orden_pago_ospim_liquidaciones set baja_fecha = null, baja_usr = null where id_orden_pago_ospim = p_orden_pago_ospim_id;
update orden_pago_ospim_lista_reintegros set baja_fecha = null, baja_usr = null where id_orden_pago_ospim = p_orden_pago_ospim_id;
update orden_pago_ospim_lista_reintegros_farmacia set baja_fecha = null, baja_usr = null where id_orden_pago_ospim = p_orden_pago_ospim_id;

update comprobante c set baja_fecha = null, baja_usr = null
from comprobante_orden_pago_ospim copo
where c.cuit = copo.cuit
and c.compro_nro = copo.compro_nro
and c.compro_tipo = copo.compro_tipo
and c.compro_sucu = copo.compro_sucu
and c.compro_letra = copo.compro_letra
and c.id_punto_venta = copo.id_punto_venta
and copo.id_orden_pago_ospim = p_orden_pago_ospim_id
and c.compro_tipo = 'REI'
and (exists (select 1 from orden_pago_ospim_lista_reintegros where id_orden_pago_ospim = copo.id_orden_pago_ospim)
or exists (select 1 from orden_pago_ospim_lista_reintegros_farmacia where id_orden_pago_ospim = copo.id_orden_pago_ospim)) ;

update cheque  c set baja_fecha = null, baja_usr = null
from orden_pago_ospim_pagos opop
where c.nro_cheque = opop.nro_cheque
and c.id_banco = opop.id_banco_cheque
and opop.id_orden_pago = p_orden_pago_ospim_id;

return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE

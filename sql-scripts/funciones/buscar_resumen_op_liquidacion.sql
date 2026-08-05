CREATE OR REPLACE FUNCTION buscar_resumen_op_liquidacion 
(IN id_liquidacion integer)
	
RETURNS TABLE(l__id_orden_pago integer, l__nro_cheque numeric, l__fecha_op timestamp without time zone) AS
	$BODY$
	
	select

	opo.id_orden_pago,
	opos.nro_cheque,
	opo.alta_fecha as fecha_op	
	
	from liquidacion l

	inner join orden_pago_ospim_liquidaciones opol
	on l.id_liquidacion = opol.id_liquidacion
	and l.id_liquidacion = $1 and
	
	opol.baja_fecha is null
	left outer join orden_pago_ospim opo
	on opol.id_orden_pago_ospim = opo.id_orden_pago
	and opo.baja_fecha is null
	left outer join orden_pago_ospim_pagos opos
	on opo.id_orden_pago = opos.id_orden_pago
	
	--limit 1;
	$BODY$
	  LANGUAGE 'sql' VOLATILE
	  COST 100
	  ROWS 1000;

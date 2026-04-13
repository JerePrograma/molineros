CREATE OR REPLACE FUNCTION buscar_resumen_op_reintegros 
(IN id_reintegro integer, IN tipo_reintegro character varying)
	
RETURNS TABLE(r__id_orden_pago integer, r__nro_cheque numeric, r__fecha_op timestamp without time zone, opor_id_lista_reintegro_pago integer) AS
	$BODY$
	
	select

	opo.id_orden_pago,
	opos.nro_cheque,
	opo.alta_fecha as fecha_op,
	opor.id_lista_reintegro_pago
	
	from reintegro r

	inner join lista_reintegro_pago_detalle opor
	on r.id_reintegro = opor.id_reintegro
	and $2 != 'ort' and
	r.id_reintegro =$1 and
	r.tipo_reintegro = $2 and 
	opor.tipo_reintegro != 'ort'	
	
	left outer join orden_pago_ospim_lista_reintegros opol
	on opor.id_lista_reintegro_pago = opol.id_lista_reintegro_pago

	and opol.baja_fecha is null
	left outer join orden_pago_ospim opo
	on opol.id_orden_pago_ospim = opo.id_orden_pago
	and opo.baja_fecha is null
	left outer join orden_pago_ospim_pagos opos
	on opo.id_orden_pago = opos.id_orden_pago



		union


	select

	opo.id_orden_pago,
	opos.nro_cheque,
	opo.alta_fecha as fecha_op,
	opor.id_lista_reintegro_pago

	from reintegro r
	
	inner join detalle_cuota dc
	on r.id_reintegro = dc.id_reintegro
	and dc.id_cuota=$1 and
	$2 = 'ort'
	and r.tipo_reintegro = 'ort'
		
	inner join lista_reintegro_pago_detalle opor
	on dc.id_cuota = opor.id_reintegro --en este caso el join se hace por detalle_cuenta
	and  opor.tipo_reintegro = 'ort'
	
	left outer join orden_pago_ospim_lista_reintegros opol
	on opor.id_lista_reintegro_pago = opol.id_lista_reintegro_pago	

	and opol.baja_fecha is null
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

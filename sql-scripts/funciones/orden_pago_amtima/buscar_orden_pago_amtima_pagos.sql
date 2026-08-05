
CREATE OR REPLACE FUNCTION buscar_orden_pago_amtima_pagos(IN p_id_orden_pago integer)
  RETURNS TABLE(opp__id integer, opp__id_orden_pago integer, opp__id_punto_venta_antic smallint, opp__compro_tipo_antic character varying, 
  opp__compro_nro_antic character varying, opp__cuit_antic character, opp__compro_letra_antic character varying, opp__compro_sucu_antic integer, 
  opp__nro_cheque numeric, opp__id_banco_cheque integer, opp__id_cta_bcria_cheque integer, opp__id_cta_bcria_retencion integer, 
  opp__importe_retencion numeric, opp__id_cta_bcria_debito_crio integer, opp__importe_debito_bcrio numeric, opp__nro_debito_bcrio character varying,
  ch__nro_cheque numeric, ch__cuit character varying, ch__a_nombre_de character varying, ch__fecha timestamp without time zone, ch__importe numeric, 
  ch__alta_fecha timestamp without time zone, ch__alta_usr character varying, ch__alta_ip character varying, ch__modi_fecha timestamp without time zone, 
  ch__modi_usr character varying, ch__modi_ip character varying, ch__baja_fecha timestamp without time zone, ch__baja_usr character varying, 
  ch__baja_ip character varying, ch__concepto character varying, ch__id_cta_bcria integer, ch__debito_credito character, ch__id_banco integer,
  es__id integer, es__descripcion character varying, ba__id_banco integer, ba__descripcion character varying, cb__nro_cuenta_cheque integer, 
  cb__sucursal_cheque integer, cb__descripcion_cheque character varying, cb__numero_plan_cuenta_asociada_cheque character varying, 
  cb__cuenta_asociada_cheque character varying, CB__cuenta_asociada_id integer, cb2__nro_cuenta_debito integer, cb2__sucursal_debito integer, cb2__descripcion_debito character varying,
  cb2__numero_plan_cuenta_asociada_debito character varying, cb2__cuenta_asociada_debito character varying, CB2__cuenta_asociada_debito_id integer, 
  tpp__id_tipo_pago integer, 
  tpp__descripcion character varying, cheque_otra_id_orden_pago integer,
   opOrigenAnticipo integer,
  fechaOPOrigenAnticipo date,
  op_alta_fecha date) AS
$BODY$

select opop.id,
	opop.id_orden_pago,
	opop.id_punto_venta_antic,
	opop.compro_tipo_antic,
	opop.compro_nro_antic,
	opop.cuit_antic,
	opop.compro_letra_antic,
	opop.compro_sucu_antic,
	opop.nro_cheque,
	opop.id_banco_cheque,
	opop.id_cta_bcria_cheque,
	opop.id_cta_bcria_retencion,
	opop.importe_retencion,
	opop.id_cta_bcria_debito_crio,
	opop.importe_debito_bcrio,
	opop.nro_debito_bcrio,
	c.nro_cheque,
	 c.cuit,
	 c.a_nombre_de,
	 c.fecha,
	 c.importe,
	 c.alta_fecha ,
	 c.alta_usr ,
	 cast(null as character varying),
	 c.modi_fecha,
	 c.modi_usr,
	 cast(null as character varying),
	 c.baja_fecha,
	 c.baja_usr ,
	 cast(null as character varying),
	 c.concepto,
	 c.id_cta_bcria,
	 c.debito_credito,
	 c.id_banco,
	 ce.id,
	 ce.descripcion,
	 b.id_banco,
     b.descripcion,
      cb.nro_cuenta,
     cb.sucursal,
     cb.descripcion,
     pc.numero,
     pc.cuenta,
     1,
      cb2.nro_cuenta,
     cb2.sucursal,
     cb2.descripcion,
     pc2.numero,
     pc2.cuenta,
     1,
     tp.id_tipo_pago,
     tp.descripcion,
     (select  opo2.id_orden_pago
			from orden_pago_amtima_pagos opop2, 
				orden_pago_amtima opo2 
			where opop2.id_orden_pago = opo2.id_orden_pago 
			and opo2.baja_fecha is null 
			and opop2.nro_cheque = c.nro_cheque 
			and opop2.id_banco_cheque = c.id_banco 
			and opo2.id_orden_pago <> opop.id_orden_pago
			limit 1) as cheque_otra_id_orden_pago ,
	opo2.id_orden_pago,
	cast(opo2.alta_fecha as date),
	cast(opo.alta_fecha as date)
from  orden_pago_amtima_pagos opop
inner join orden_pago_amtima opo
on opop.id_orden_pago = opo.id_orden_pago
left outer join cheque_amtima c
on opop.nro_cheque = c.nro_cheque
and opop.id_banco_cheque = c.id_banco
left outer join cheque_amtima_estado ce
on c.id_estado = ce.id
left outer join banco b
on c.id_banco = b.id_banco 
left outer join cuenta_bcria cb
on cb.id_cuenta_bcria = opop.id_cta_bcria_cheque
left outer join plan_cuentas_amtima pc
on cb.cuenta_asociada = pc.numero
left outer join cuenta_bcria cb2
on cb2.id_cuenta_bcria = opop.id_cta_bcria_debito_crio
left outer join plan_cuentas_amtima pc2
on cb2.cuenta_asociada = pc2.numero
left outer join tipo_pago tp
on opop.tipo_pago = tp.id_tipo_pago
left outer join comprobante_orden_pago_amtima copo
on opop.id_punto_venta_antic =  copo.id_punto_venta
and opop.compro_tipo_antic =copo.compro_tipo
and opop.compro_letra_antic  =copo.compro_letra
and opop.compro_sucu_antic =copo.compro_sucu
and opop.compro_nro_antic =copo.compro_nro
and opop.cuit_antic = copo.cuit
left outer join orden_pago_amtima opo2
on copo.id_orden_pago_amtima = opo2.id_orden_pago
and opo2.baja_fecha is null
where opop.id_orden_pago = $1;
 
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;

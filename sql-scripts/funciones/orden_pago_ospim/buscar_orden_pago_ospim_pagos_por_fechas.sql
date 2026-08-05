
DROP FUNCTION buscar_orden_pago_ospim_pagos_por_fechas(p_date_ini date, p_date_fin date);
CREATE OR REPLACE FUNCTION buscar_orden_pago_ospim_pagos_por_fechas(p_date_ini date, p_date_fin date)
  RETURNS TABLE(
  OP__id_orden_pago integer,
OPP__id integer,
OPP__id_orden_pago integer,
OPP__id_punto_venta_antic smallint ,
OPP__compro_tipo_antic character varying(3) ,
OPP__compro_nro_antic character varying(50) ,
OPP__cuit_antic character(11),
OPP__compro_letra_antic character varying(1) ,
OPP__compro_sucu_antic integer ,
OPP__nro_cheque numeric(15,0),
OPP__id_banco_cheque integer,
OPP__id_cta_bcria_cheque integer,
OPP__id_cta_bcria_retencion integer,
OPP__importe_retencion numeric(12,2),
OPP__id_cta_bcria_debito_crio integer,
OPP__importe_debito_bcrio numeric(12,2),
OPP__nro_debito_bcrio character varying(50),
ch__nro_cheque numeric,
 ch__cuit character varying,
 ch__a_nombre_de character varying,
 ch__fecha timestamp without time zone,
 ch__importe numeric,
 ch__alta_fecha timestamp without time zone,
 ch__alta_usr character varying,
 ch__alta_ip character varying,
 ch__modi_fecha timestamp without time zone,
 ch__modi_usr character varying,
 ch__modi_ip character varying,
 ch__baja_fecha timestamp without time zone,
 ch__baja_usr character varying,
 ch__baja_ip character varying,
 ch__concepto character varying,
 ch__id_cta_bcria integer,
 ch__debito_credito char(1),
 ch__id_banco integer,
 es__id integer,
 es__descripcion character varying (50),
 ba__id_banco integer,
 ba__descripcion character varying,
 CB__nro_cuenta_cheque integer ,
 CB__sucursal_cheque integer ,
 CB__descripcion_cheque character varying,
 CB__numero_plan_cuenta_asociada_cheque character varying,
 CB__cuenta_asociada_cheque  character varying,
 CB__cuenta_asociada_id integer,
 CB2__nro_cuenta_debito integer ,
 CB2__sucursal_debito integer ,
 CB2__descripcion_debito character varying,
 CB2__numero_plan_cuenta_asociada_debito character varying,
 CB2__cuenta_asociada_debito  character varying,
 CB2__cuenta_asociada_debito_id integer,
 TPP__id_tipo_pago integer,
 TPP__descripcion character varying,
   opOrigenAnticipo integer,
  fechaOPOrigenAnticipo date,
    op_alta_fecha date) AS
$BODY$

select opo.id_orden_pago,
	opop.id,
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
	 c.alta_ip,
	 c.modi_fecha,
	 c.modi_usr,
	 c.modi_ip ,
	 c.baja_fecha,
	 c.baja_usr ,
	 c.baja_ip,
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
     pc.id_cuenta_maestro,
     cb2.nro_cuenta,
     cb2.sucursal,
     cb2.descripcion,
      case when (id_tipo_pago = 2) then  (select pcuentas.numero from parametros_contabilidad pc, plan_cuentas pcuentas
											where pc.id_plan_cuenta = pcuentas.id_cuenta_maestro
											and pc.parametro = 'creditos_autogestion' 
											and pc.valido_desde <= cast(opo.alta_fecha as date) and pc.valido_hasta >= cast(opo.alta_fecha as date)
											and pcuentas.valido_desde <= cast(opo.alta_fecha as date) and pcuentas.valido_hasta >= cast(opo.alta_fecha as date)) else  pc2.numero end,
      case when (id_tipo_pago = 2) then (select pcuentas.cuenta from parametros_contabilidad pc, plan_cuentas pcuentas
											where pc.id_plan_cuenta = pcuentas.id_cuenta_maestro
											and pc.parametro = 'creditos_autogestion' 
											and pc.valido_desde <= cast( opo.alta_fecha as date) and pc.valido_hasta >= cast(opo.alta_fecha as date)
											and pcuentas.valido_desde <= cast(opo.alta_fecha as date) and pcuentas.valido_hasta >= cast(opo.alta_fecha as date)) else  pc2.cuenta end,
  case when (id_tipo_pago = 2) then (select pcuentas.id_cuenta_maestro from parametros_contabilidad pc, plan_cuentas pcuentas
											where pc.id_plan_cuenta = pcuentas.id_cuenta_maestro
											and pc.parametro = 'creditos_autogestion' 
											and pc.valido_desde <= cast( opo.alta_fecha as date) and pc.valido_hasta >= cast(opo.alta_fecha as date)
											and pcuentas.valido_desde <= cast(opo.alta_fecha as date) and pcuentas.valido_hasta >= cast(opo.alta_fecha as date)) else  pc2.id_cuenta_maestro end,
      tp.id_tipo_pago,
     tp.descripcion,
     opo2.id_orden_pago,
	cast(opo2.alta_fecha as date),
	cast(opo.alta_fecha as date)
from  orden_pago_ospim_pagos opop
inner join orden_pago_ospim opo
on opop.id_orden_pago = opo.id_orden_pago
left outer join cheque c
on opop.nro_cheque = c.nro_cheque
and opop.id_banco_cheque = c.id_banco
left outer join cheque_estado ce
on c.id_estado = ce.id
left outer join banco b
on c.id_banco = b.id_banco 
left outer join cuenta_bcria cb
on cb.id_cuenta_bcria = opop.id_cta_bcria_cheque
left outer join plan_cuentas pc
on cb.id_plan_cuenta = pc.id_cuenta_maestro
and pc.valido_desde <= cast(opo.alta_fecha as date) and pc.valido_hasta >= cast(opo.alta_fecha as date)
left outer join cuenta_bcria cb2
on cb2.id_cuenta_bcria = opop.id_cta_bcria_debito_crio
left outer join plan_cuentas pc2
on cb2.id_plan_cuenta = pc2.id_cuenta_maestro
and pc2.valido_desde <= cast(opo.alta_fecha as date) and pc2.valido_hasta >= cast(opo.alta_fecha as date)
left outer join tipo_pago tp
on opop.tipo_pago = tp.id_tipo_pago
left outer join comprobante_orden_pago_ospim copo
on opop.id_punto_venta_antic =  copo.id_punto_venta
and opop.compro_tipo_antic =copo.compro_tipo
and opop.compro_letra_antic  =copo.compro_letra
and opop.compro_sucu_antic =copo.compro_sucu
and opop.compro_nro_antic =copo.compro_nro
and opop.cuit_antic = copo.cuit
left outer join orden_pago_ospim opo2
on copo.id_orden_pago_ospim = opo2.id_orden_pago
where ((cast(opo.alta_fecha  as date)>=$1
and cast(opo.alta_fecha as date)<=$2)
or (cast(opo.baja_fecha  as date)>=$1
and cast(opo.baja_fecha as date)<=$2))
and opo2.baja_fecha is null;
 
 
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;



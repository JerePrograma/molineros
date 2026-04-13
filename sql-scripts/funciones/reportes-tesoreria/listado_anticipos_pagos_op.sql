create or replace function listado_anticipos_pagos_op(p_fecha_ini date, p_fecha_fin date) 
RETURNS TABLE(
  cuit_acreedor character varying,
  sucu_acreedor character varying,
  id_seccional integer,
  razon_soc character varying,
  seccional character varying,
  fecha_recepcion timestamp without time zone,
  periodo_prestacion date,
  descripcion character varying,
  total numeric,
  op integer,
  fecha_pago date,
  debito_para_egreso boolean,
  op_aplicacion integer,
  fecha_pago_aplicacion date,
  importe_aplicado numeric)
LANGUAGE sql
AS $BODY$


select  aux.cuit_acreedor, 
	aux.sucu_acreedor, 
	aux.seccional, 
	e.razon_soc , 
	aux.descripcion_seccional, 
	aux.fecha_recepcion, 
	aux.periodo_prestacion, 
	aux.descripcion, 
	aux.total,
	aux.op , 
	cast(aux.fecha_pago as date), 
	aux.debito_para_egreso,
	pagos.id_orden_pago,
	cast(pagos.alta_fecha as date),
	aux.total
from (
	select  c.compro_tipo, 
		c.compro_nro, 
		c.compro_sucu, 
		c.compro_letra, 
		c.id_punto_venta, 
		c.cuit, 
		c.cuit_acreedor, 
		c.sucu_acreedor, 
		c.seccional, 
		s.descripcion  as descripcion_seccional, 
		c.fecha_recepcion, 
		c.periodo_prestacion, 
		cast(c.compro_tipo || '-' || c.id_punto_venta || '-' || c.compro_nro as character varying) as descripcion,  
		c.total, 
		c.debito_para_egreso,
		opo.id_orden_pago as op,
		opo.alta_fecha as fecha_pago
		from orden_pago_ospim opo
		inner join comprobante_orden_pago_ospim copo
		on opo.id_orden_pago = copo.id_orden_pago_ospim
		inner join comprobante c
		on c.cuit = copo.cuit
		and c.compro_letra = copo.compro_letra
		and c.compro_sucu = copo.compro_sucu
		and c.compro_tipo = copo.compro_tipo
		and c.compro_nro = copo.compro_nro
		and c.id_punto_venta = copo.id_punto_venta
		left outer join seccional s
		on c.seccional = s.id_seccional
		where  (cast(opo.alta_fecha as date)  >= $1 and  cast(opo.alta_fecha as date)  <= $2)
		and c.compro_tipo = 'ANT'
		and trim(c.compro_letra ) = ''
		union all
		select  c.compro_tipo, 
		c.compro_nro, 
		c.compro_sucu, 
		c.compro_letra, 
		c.id_punto_venta, 
		c.cuit, 
		c.cuit_acreedor, 
		c.sucu_acreedor, 
		c.seccional, 
		s.descripcion  as descripcion_seccional, 
		c.fecha_recepcion, 
		c.periodo_prestacion, 
		cast(c.compro_tipo || '-' || c.id_punto_venta || '-' || c.compro_nro as character varying) as descripcion,  
		-1*c.total, 
		c.debito_para_egreso,
		opo.id_orden_pago as op,
		opo.baja_fecha as fecha_pago
		from orden_pago_ospim opo
		inner join comprobante_orden_pago_ospim copo
		on opo.id_orden_pago = copo.id_orden_pago_ospim
		inner join comprobante c
		on c.cuit = copo.cuit
		and c.compro_letra = copo.compro_letra
		and c.compro_sucu = copo.compro_sucu
		and c.compro_tipo = copo.compro_tipo
		and c.compro_nro = copo.compro_nro
		and c.id_punto_venta = copo.id_punto_venta
		left outer join seccional s
		on c.seccional = s.id_seccional
		where  (cast(opo.baja_fecha as date)  >= $1 and  cast(opo.baja_fecha as date)  <= $2)
		and c.compro_tipo = 'ANT'
		and trim(c.compro_letra ) = ''
	)aux
left outer join 
	(select cuit_antic, id_punto_venta_antic, compro_nro_antic, compro_tipo_antic, compro_letra_antic, compro_sucu_antic, opo.id_orden_pago, opo.alta_fecha from orden_pago_ospim_pagos opop
	  inner join orden_pago_ospim opo
	  on opop.id_orden_pago = opo.id_orden_pago
	  where cast(opo.alta_fecha as date)  >= $1 
		and opo.baja_fecha is null
		and cuit_antic is not null
	) pagos
on aux.cuit = pagos.cuit_antic
and aux.id_punto_venta = pagos.id_punto_venta_antic
and aux.compro_nro = pagos.compro_nro_antic
and aux.compro_tipo = pagos.compro_tipo_antic
and aux.compro_letra = pagos.compro_letra_antic
and aux.compro_sucu = pagos.compro_sucu_antic
left outer join empresa e
on aux.cuit_acreedor = e.cuit
and aux.sucu_acreedor = e.sucursal
where op is not null
order by aux.op asc, aux.fecha_pago asc;

$BODY$;

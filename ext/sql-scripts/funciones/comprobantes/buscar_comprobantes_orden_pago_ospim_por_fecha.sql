-- Function: buscar_comprobantes_orden_pago_ospim_por_fecha(date, date)

-- DROP FUNCTION buscar_comprobantes_orden_pago_ospim_por_fecha(date, date);
/*
 drop type tipo_comprobantes_subdiario_egreso cascade; 
create type tipo_comprobantes_subdiario_egreso as (
c__fecha_emision timestamp without time zone, 
c__fecha_recepcion timestamp without time zone, 
c__importe_comprobante numeric, 
c__nro character varying, 
c__tipo character varying, 
c__id_punto_venta smallint,
c__cuit character(11), 
c__alta_fecha timestamp without time zone, 
c__alta_usr character varying, 
c__modi_fecha timestamp without time zone, 
c__modi_usr character varying, 
c__baja_fecha timestamp without time zone, 
c__baja_usr character varying, 
c__compro_letra character varying, 
c__compro_sucu integer, 
c__cuit_acreedor character varying, 
c__sucu_acreedor character varying, 
c__seccional integer, 
c__observaciones character varying, 
c__vto timestamp without time zone, 
c__periodo_prestacion date, 
c__debito_para_egreso boolean, 
op__id_orden_pago integer, 
e__razon_soc character varying,
fecha_primer_op  timestamp without time zone);

*/

CREATE OR REPLACE FUNCTION buscar_comprobantes_orden_pago_ospim_por_fecha(IN p_date_ini date, IN p_date_fin date)
  RETURNS setof tipo_comprobantes_subdiario_egreso AS
$BODY$

begin 
	
drop table if exists comps_fecha;

CREATE temp table comps_fecha AS 


select min(opo2.alta_fecha) as fecha_primer_op, copo2.cuit, copo2.id_punto_venta, copo2.compro_nro, 
	copo2.compro_sucu, copo2.compro_tipo, copo2.compro_letra
from 	
	(select cuit, id_punto_venta, compro_nro, compro_sucu, compro_tipo, compro_letra
		from comprobante_orden_pago_ospim c
		inner join orden_pago_ospim opo
		on c.id_orden_pago_ospim = opo.id_orden_pago
		and ((cast(opo.alta_fecha  as date)>= $1 and cast(opo.alta_fecha as date)<=$2 )
		or (cast(opo.baja_fecha  as date)>= $1  and cast(opo.baja_fecha as date)<=$2 ) )
		
	union all
	
	select c.cuit, c.id_punto_venta, c.compro_nro, c.compro_sucu, c.compro_tipo, c.compro_letra
		from orden_pago_ospim_pagos opop
		inner join orden_pago_ospim opo
		on opop.id_orden_pago= opo.id_orden_pago
		inner join comprobante c
		on opop.id_punto_venta_antic =  c.id_punto_venta
		and opop.compro_tipo_antic =c.compro_tipo
		and opop.compro_letra_antic  =c.compro_letra
		and opop.compro_sucu_antic =c.compro_sucu
		and opop.compro_nro_antic =c.compro_nro
		and opop.cuit_antic = c.cuit
		where opop.id_punto_venta_antic is not null
		and (cast(opo.alta_fecha  as date)>=$1 and cast(opo.alta_fecha as date)<=$2) 
		or (cast(opo.baja_fecha  as date)>=$1 and cast(opo.baja_fecha as date)<=$2)
	 ) c
inner join comprobante_orden_pago_ospim copo2
on   c.compro_nro = copo2.compro_nro
and c.compro_tipo = copo2.compro_tipo
and c.id_punto_venta = copo2.id_punto_venta
and c.cuit = copo2.cuit
and c.compro_letra = copo2.compro_letra
and c.compro_sucu = copo2.compro_sucu
inner join orden_pago_ospim opo2
on copo2.id_orden_pago_ospim = opo2.id_orden_pago
group by copo2.cuit, copo2.id_punto_venta, copo2.compro_nro, 
	copo2.compro_sucu, copo2.compro_tipo, copo2.compro_letra;
	
	
	
return query select  
  c.fecha_emision,
  c.fecha_recepcion,
  c.total,
  c.compro_nro,
  c.compro_tipo,
  c.id_punto_venta,
  c.cuit,
  c.alta_fecha,
  c.alta_usr,
  c.modi_fecha,
  c.modi_usr,
  c.baja_fecha,
  c.baja_usr,
  c.compro_letra,
  c.compro_sucu,
 c.cuit_acreedor ,
  c.sucu_acreedor,
  c.seccional, 
  c.observaciones,
  c.vto,
  c.periodo_prestacion,
    c.debito_para_egreso,
  opo.id_orden_pago,
  e.razon_soc,
  cf.fecha_primer_op
from comprobante c
inner join comprobante_orden_pago_ospim copo
on   c.compro_nro = copo.compro_nro
and c.compro_tipo = copo.compro_tipo
and c.id_punto_venta = copo.id_punto_venta
and c.cuit = copo.cuit
and c.compro_letra = copo.compro_letra
and c.compro_sucu = copo.compro_sucu
inner join orden_pago_ospim opo
on copo.id_orden_pago_ospim = opo.id_orden_pago
left outer join empresa e
on opo.cuit_acreedor = e.cuit
and opo.sucu_acreedor = e.sucursal
left outer join comps_fecha cf
on   c.compro_nro = cf.compro_nro
and c.compro_tipo = cf.compro_tipo
and c.id_punto_venta = cf.id_punto_venta
and c.cuit = cf.cuit
and c.compro_letra = cf.compro_letra
and c.compro_sucu = cf.compro_sucu
where (cast(opo.alta_fecha  as date)>=$1
and cast(opo.alta_fecha as date)<=$2) 
or (cast(opo.baja_fecha  as date)>=$1
and cast(opo.baja_fecha as date)<=$2)
union all
select  c.fecha_emision,
  c.fecha_recepcion,
  (-1*c.total),
  c.compro_nro,
  c.compro_tipo,
  c.id_punto_venta,
  c.cuit,
  c.alta_fecha,
  c.alta_usr,
  c.modi_fecha,
  c.modi_usr,
  c.baja_fecha,
  c.baja_usr,
  c.compro_letra,
  c.compro_sucu,
 c.cuit_acreedor ,
  c.sucu_acreedor,
  c.seccional, 
  c.observaciones,
  c.vto,
  c.periodo_prestacion,
    c.debito_para_egreso,
  opo.id_orden_pago,
  e.razon_soc,
  cf.fecha_primer_op
from orden_pago_ospim_pagos opop
inner join orden_pago_ospim opo
on opop.id_orden_pago= opo.id_orden_pago
inner join comprobante c
on opop.id_punto_venta_antic =  c.id_punto_venta
and opop.compro_tipo_antic =c.compro_tipo
and opop.compro_letra_antic  =c.compro_letra
and opop.compro_sucu_antic =c.compro_sucu
and opop.compro_nro_antic =c.compro_nro
and opop.cuit_antic = c.cuit
left outer join empresa e
on opo.cuit_acreedor = e.cuit
and opo.sucu_acreedor = e.sucursal
left outer join comps_fecha cf
on   c.compro_nro = cf.compro_nro
and c.compro_tipo = cf.compro_tipo
and c.id_punto_venta = cf.id_punto_venta
and c.cuit = cf.cuit
and c.compro_letra = cf.compro_letra
and c.compro_sucu = cf.compro_sucu
where opop.id_punto_venta_antic is not null
and (cast(opo.alta_fecha  as date)>=$1 and cast(opo.alta_fecha as date)<=$2) 
or (cast(opo.baja_fecha  as date)>=$1 and cast(opo.baja_fecha as date)<=$2);

end;  
$BODY$
  LANGUAGE 'plpgsql' VOLATILE

 
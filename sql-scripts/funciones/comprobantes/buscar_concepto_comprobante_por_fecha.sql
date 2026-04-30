
create type concepto_comprobante_por_fecha_result as (ccc__alta_fecha timestamp without time zone, ccc__alta_usr character varying, ccc__modi_fecha timestamp without time zone, ccc__modi_usr character varying, ccc__baja_fecha timestamp without time zone, ccc__baja_usr character varying, ccc__concepto_id integer, cc__id integer, cc__descripcion character varying, cc__numero character varying, ccc__importe numeric, c__id_punto_venta smallint, c__compro_tipo character varying, c__compro_nro character varying, c__cuit character, c__compro_letra character varying, c__compro_sucu integer, cc__cuenta character varying) 
-- Function: buscar_concepto_comprobante_por_fecha(date, date)

-- DROP FUNCTION buscar_concepto_comprobante_por_fecha(date, date);

CREATE OR REPLACE FUNCTION buscar_concepto_comprobante_por_fecha(IN p_date_ini date, IN p_date_fin date)
  RETURNS TABLE(ccc__alta_fecha timestamp without time zone, ccc__alta_usr character varying, ccc__modi_fecha timestamp without time zone, ccc__modi_usr character varying, ccc__baja_fecha timestamp without time zone, ccc__baja_usr character varying, ccc__concepto_id integer, cc__id integer, cc__descripcion character varying, cc__numero character varying, ccc__importe numeric, c__id_punto_venta smallint, c__compro_tipo character varying, c__compro_nro character varying, c__cuit character, c__compro_letra character varying, c__compro_sucu integer, cc__cuenta character varying) AS
$BODY$


select
  c.alta_fecha, 
  c.alta_usr,
  c.modi_fecha,
  c.modi_usr,
  c.baja_fecha,
  c.baja_usr,
  c.concepto_id,
  cc.id_concepto_maestro,
  cc.descripcion,
  cc.numero_cuenta,
  c.importe,
  c.id_punto_venta ,
  c.compro_tipo ,
  c.compro_nro ,
  c.cuit ,
  c.compro_letra ,
  c.compro_sucu ,
  pc.cuenta
 from (   
	select distinct c.alta_fecha, c.alta_usr,
	  c.modi_fecha,
	  c.modi_usr,
	  c.baja_fecha,
	  c.baja_usr,
	  c.concepto_id,
	  c.importe,
	  c.id_punto_venta ,
	 c.compro_tipo ,
	    c.compro_nro ,
	    c.cuit ,
	    c.compro_letra ,
	    c.compro_sucu 
	from  concepto_comprobante c
	inner join comprobante_orden_pago_ospim copo
	on c.id_punto_venta = copo.id_punto_venta
	   and c.compro_tipo = copo.compro_tipo
	   and c.compro_nro = copo.compro_nro
	   and c.cuit = copo.cuit
	   and c.compro_letra = copo.compro_letra
	   and c.compro_sucu = copo.compro_sucu
	inner join orden_pago_ospim opo
	on copo.id_orden_pago_ospim = opo.id_orden_pago
	where (cast(opo.alta_fecha  as date)>=$1 and cast(opo.alta_fecha as date)<=$2)
	or (cast(opo.baja_fecha  as date)>=$1 and cast(opo.baja_fecha as date)<=$2)
	or exists (select 1 from orden_pago_ospim opo2, orden_pago_ospim_pagos opop2
		where opo2.id_orden_pago = opop2.id_orden_pago 
		and ((cast(opo2.alta_fecha  as date)>=$1 and cast(opo2.alta_fecha as date)<=$2)
			or (cast(opo2.baja_fecha  as date)>=$1 and cast(opo2.baja_fecha as date)<=$2))
		   and c.id_punto_venta = opop2.id_punto_venta_antic
		   and c.compro_tipo = opop2.compro_tipo_antic
		   and c.compro_nro = opop2.compro_nro_antic
		   and c.cuit = opop2.cuit_antic
		   and c.compro_letra = opop2.compro_letra_antic
		   and c.compro_sucu = opop2.compro_sucu_antic)
) c
inner join comprobante comp
on c.id_punto_venta = comp.id_punto_venta
   and c.compro_tipo = comp.compro_tipo
   and c.compro_nro = comp.compro_nro
   and c.cuit = comp.cuit
   and c.compro_letra = comp.compro_letra
   and c.compro_sucu = comp.compro_sucu
inner join conceptos cc
on c.concepto_id = cc.id_concepto_maestro
and cast(cc.valido_desde as date)  <= cast(comp.fecha_recepcion as date)
and cast(cc.valido_hasta as date)  >= cast(comp.fecha_recepcion as date)
inner join plan_cuentas pc
on cc.id_plan_cuenta = pc.id_cuenta_maestro
and cast(pc.valido_desde as date)  <= cast(comp.fecha_recepcion as date)
and cast(pc.valido_hasta as date)  >= cast(comp.fecha_recepcion as date);


$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION buscar_concepto_comprobante_por_fecha(date, date) OWNER TO postgres;

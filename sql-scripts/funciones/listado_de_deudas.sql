--ORDENAR LAS NDB AL FINAL
--comprobante creado y anulado en el mismo mes -> ni aparece
--comprobante creado, sin pagar, y anulado al mes siguiente -> aparece en el mes original y aparece en el mes de anulacion en forma inversa
--comprobante creado, pago en el mismo mes, y anulado al mes siguiente -> no aparece en el mes original, y entonces no tiene q aparecer en el mes de anulacion como asiento inverso, pero si tiene q aparecer como deuda en ese 2do mes: bug?
--comprobante creado, pago en el mes siguiente, y anulado ese mes -> en el mes original aparece, y en el mes de anulacion de op no aparece porque ya aparecio en el mes original
  
------------ CUIDADO!!!!! ----------------------
/*
 * El estado de las liquidaciones esta harcodeado en java. Por lo tanto
 * si se crea un nuevo estado que represente una liquidacion cerrada,
 * y que por ese motivo deba aparecer en este listado, hay que agregarlo 
 * en los lugares correspondientes en esta query.
 */
------------ CUIDADO!!!! -------------------------

/*

drop type tipo_listado_de_deuda cascade;

create type tipo_listado_de_deuda as (
fecha_recepcion date,
total numeric,
compro_nro character varying,
compro_letra character varying,
compro_tipo character varying,
compro_sucu integer,
id_punto_venta smallint,			
cuit character(11), 
numero character varying,
cuenta character varying,
cuenta_id integer,
importe numeric,
numero_pasivo character varying,
cuenta_pasivo character varying,
cuenta_pasivo_id integer,
importe_pasivo numeric,
cuit_acreedor character varying, 
sucu_acreedor character varying,
id_seccional integer, 
seccional character varying,  
razon_social character varying,
id_liquidaciones integer,
debito_para_egreso boolean
);
*/
-- Function: listado_de_deudas(character varying, character varying, integer, date, date, date)

-- DROP FUNCTION listado_de_deudas(character varying, character varying, integer, date, date, date);

CREATE OR REPLACE FUNCTION listado_de_deudas(p_cuit character varying, p_sucu character varying, p_seccional integer, p_fecha_ini date, 
	p_fecha_fin date, p_fecha_pago date, p_incluirProveedores boolean , p_incluirLiquidaciones boolean , p_incluirReintegros boolean)
  RETURNS SETOF tipo_listado_de_deuda AS
$BODY$
BEGIN

drop table if exists comp_liquidaciones_tmp;
drop table if exists comp_tmp;
drop table if exists comp_anulado_tmp;
drop table if exists notas_debito_tmp;

CREATE temp table comp_liquidaciones_tmp AS 
select  distinct alta_fecha, alta_usr,   modi_fecha,  modi_usr,  baja_fecha,  baja_usr,  total,  id_punto_venta ,   compro_tipo ,
                 compro_nro, cuit, compro_letra, compro_sucu, todas_liq.id_prestacion, 
                 amb_imp_honorarios,amb_imp_gast,amt_imp_hon, amt_imp_gast, orden, cuit_acreedor, sucu_acreedor,  seccional, fecha_recepcion,id_liquidacion,debito_para_egreso 
from (
	(select 
		c.*,
		n.id_prestacion,
		(lp.importe * lp.cantidad) * n.coef_honorarios as amb_imp_honorarios,
		(lp.importe * lp.cantidad) * n.coef_gastos as amb_imp_gast,
		0 as amt_imp_hon, 
		0 as amt_imp_gast,
		cast(lp.id_liquidacion as character varying) || cast(lp.orden as character varying) as orden,
		(select cast(max(alta_fecha) as date)
			from orden_pago_ospim opo, comprobante_orden_pago_ospim copo
			where opo.id_orden_pago = copo.id_orden_pago_ospim 
			and copo.cuit= c.cuit 
			and copo.id_punto_venta = c.id_punto_venta 
			and  copo.compro_tipo = c.compro_tipo 
			and copo.compro_sucu = c.compro_sucu 
			and copo.compro_letra = c.compro_letra 
			and copo.compro_nro = c.compro_nro
			and cast(opo.alta_fecha as date)>=cast(c.fecha as date) 
			and cast(opo.alta_fecha as date) <=   p_fecha_pago
			and (opo.baja_fecha is null or cast(opo.baja_fecha as date) >= p_fecha_pago)
		) as fecha_pagado,
		lp.id_liquidacion
	from comprobante c 		
	inner join comprobante_liquidacion cl
	on c.id_punto_venta = cl.id_punto_venta
	and c.compro_tipo = cl.compro_tipo
	and c.compro_nro = cl.compro_nro
	and c.cuit = cl.cuit
	and c.compro_letra = cl.compro_letra
	and c.compro_sucu = cl.compro_sucu
	and c.compro_tipo != 'NDB'
	inner join liquidacion l	
	on l.id_liquidacion = cl.id_liquidacion
	inner join liquidacion_prestacion lp
	on l.id_liquidacion = lp.id_liquidacion
	inner join nomenclador n
	on lp.id_prestacion = n.id_prestacion
	-------------------------------------
	where p_incluirLiquidaciones = true 
	and lp.tercerizado <> '1' 
	and lp.servicio != 'INTERNACIÓN CLÍNICA' 
	and l.estado in (2,10,11)
	and (p_cuit is null or c.cuit_acreedor = p_cuit)
	and (p_sucu is null or (c.sucu_acreedor = p_sucu and ((p_seccional is null and c.seccional is null) or c.seccional = p_seccional)))
	and (cast(c.fecha_recepcion as date)>= p_fecha_ini and cast(c.fecha_recepcion as date)<=p_fecha_fin)
	and (c.anulado_fecha is null or cast(c.anulado_fecha as date) >= p_fecha_pago)
	and c.baja_fecha is null
	)
  union all
	(select c.*,
		n.id_prestacion,
		0 as amb_imp_honorarios, 
		0 as amb_imp_gast,
		(lp.importe * lp.cantidad) * n.coef_honorarios as amt_imp_hon,
		(lp.importe * lp.cantidad) * n.coef_gastos as amt_imp_gast,
		cast(lp.id_liquidacion as character varying) || cast(lp.orden as character varying) as orden,
		(select cast(max(alta_fecha) as date)
			from orden_pago_ospim opo, comprobante_orden_pago_ospim copo
			where opo.id_orden_pago = copo.id_orden_pago_ospim 
			and copo.cuit= c.cuit 
			and copo.id_punto_venta = c.id_punto_venta 
			and  copo.compro_tipo = c.compro_tipo 
			and copo.compro_sucu = c.compro_sucu 
			and copo.compro_letra = c.compro_letra 
			and copo.compro_nro = c.compro_nro
			and cast(opo.alta_fecha as date)>=cast(c.fecha as date) 
			and cast(opo.alta_fecha as date) <=   p_fecha_pago
			and (opo.baja_fecha is null or cast(opo.baja_fecha as date) >= p_fecha_pago)
		) as fecha_pagado,
		lp.id_liquidacion
	from comprobante c 		
	inner join comprobante_liquidacion cl
	on c.id_punto_venta = cl.id_punto_venta
	and c.compro_tipo = cl.compro_tipo
	and c.compro_nro = cl.compro_nro
	and c.cuit = cl.cuit
	and c.compro_letra = cl.compro_letra
	and c.compro_sucu = cl.compro_sucu
	and c.compro_tipo != 'NDB'
	inner join liquidacion l	
	on l.id_liquidacion = cl.id_liquidacion
	inner join liquidacion_prestacion lp
	on l.id_liquidacion = lp.id_liquidacion
	inner join nomenclador n
	on lp.id_prestacion = n.id_prestacion
	-------------------------------------
	where p_incluirLiquidaciones = true 
	and lp.tercerizado <> '1' 
	and lp.servicio = 'INTERNACIÓN CLÍNICA'
	and l.estado in (2,10,11)
	and (p_cuit is null or c.cuit_acreedor = p_cuit)
	and (p_sucu is null or (c.sucu_acreedor = p_sucu and ((p_seccional is null and c.seccional is null) or c.seccional = p_seccional)))
	and (cast(c.fecha_recepcion as date)>= p_fecha_ini and cast(c.fecha_recepcion as date)<=p_fecha_fin)
	and (c.anulado_fecha is null or cast(c.anulado_fecha as date) >= p_fecha_pago)
	and c.baja_fecha is null
	) 
) todas_liq
where fecha_pagado is null;

CREATE temp table comp_tmp AS 
select    todo_liq_final.concepto_id,
	  todo_liq_final.importe,
	  todo_liq_final.id_punto_venta ,
	  todo_liq_final.compro_tipo ,
	  todo_liq_final.compro_nro ,
	  todo_liq_final.cuit ,
	  todo_liq_final.compro_letra ,
	  todo_liq_final.compro_sucu,
	  todo_liq_final.cuit_acreedor, 
	  todo_liq_final.sucu_acreedor, 
	  todo_liq_final.seccional,
	  todo_liq_final.fecha_recepcion,
	  todo_liq_final.total,
	  todo_liq_final.id_liquidacion,
	  todo_liq_final.debito_para_egreso
	    from (
	  select id_punto_venta, compro_tipo, compro_nro, cuit, compro_letra, compro_sucu,
	         t.id_prestacion, amb_imp_honorarios as importe, nc.concepto_id, orden,
	         t.cuit_acreedor, t.sucu_acreedor, t.seccional, fecha_recepcion, total, id_liquidacion,debito_para_egreso
	  from comp_liquidaciones_tmp t
	  left outer join nomenclador_conceptos nc
	  on t.id_prestacion = nc.id_prestacion
	  and nc.valido_desde <= fecha_recepcion and nc.valido_hasta >= fecha_recepcion 
	  where amb_imp_honorarios <> 0
	  and nc.tipo_id = 1
	union all
	 select  id_punto_venta, compro_tipo, compro_nro, cuit, compro_letra, compro_sucu,
	         t.id_prestacion, amb_imp_gast as importe, nc.concepto_id, orden,
	         t.cuit_acreedor, t.sucu_acreedor, t.seccional, fecha_recepcion, total, id_liquidacion,debito_para_egreso
	  from comp_liquidaciones_tmp t
	  left outer join nomenclador_conceptos nc
	  on t.id_prestacion = nc.id_prestacion
	  and nc.valido_desde <= fecha_recepcion and nc.valido_hasta >= fecha_recepcion
	  where amb_imp_gast <> 0
	   and nc.tipo_id = 2
	union all
	  select id_punto_venta, compro_tipo, compro_nro, cuit, compro_letra, compro_sucu, 
	         t.id_prestacion, amt_imp_hon as importe, nc.concepto_id, orden,
	         t.cuit_acreedor, t.sucu_acreedor, t.seccional, fecha_recepcion, total, id_liquidacion,debito_para_egreso
	  from comp_liquidaciones_tmp t
	  left outer join nomenclador_conceptos nc
	  on t.id_prestacion = nc.id_prestacion
	  and nc.valido_desde <= fecha_recepcion and nc.valido_hasta >= fecha_recepcion
	  where amt_imp_hon <> 0
	   and nc.tipo_id = 3
	union all
	  select id_punto_venta, compro_tipo, compro_nro, cuit, compro_letra, compro_sucu, 
	         t.id_prestacion, amt_imp_gast as importe, nc.concepto_id, orden,
	         t.cuit_acreedor, t.sucu_acreedor, t.seccional, fecha_recepcion, total, id_liquidacion,debito_para_egreso
	  from comp_liquidaciones_tmp t
	  left outer join nomenclador_conceptos nc
	  on t.id_prestacion = nc.id_prestacion
	  and nc.valido_desde <= fecha_recepcion and nc.valido_hasta >= fecha_recepcion
	  where amt_imp_gast <> 0 
	   and nc.tipo_id = 4
	 ) todo_liq_final;


insert into comp_tmp
select cc.concepto_id, cc.importe, comp.id_punto_venta,  comp.compro_tipo, comp.compro_nro, comp.cuit, comp.compro_letra , comp.compro_sucu, 
       comp.cuit_acreedor, comp.sucu_acreedor, comp.seccional, comp.fecha_recepcion, comp.total, cl.id_liquidacion,comp.debito_para_egreso
from ( select c.id_punto_venta,  c.compro_tipo, c.compro_nro, c.cuit, c.compro_letra , c.compro_sucu, 
	      c.cuit_acreedor, c.sucu_acreedor, c.seccional, c.fecha_recepcion, c.total,
			(select cast(max(alta_fecha) as date)
			from orden_pago_ospim opo, comprobante_orden_pago_ospim copo
			where opo.id_orden_pago = copo.id_orden_pago_ospim 
			and copo.cuit= c.cuit 
			and copo.id_punto_venta = c.id_punto_venta 
			and  copo.compro_tipo = c.compro_tipo 
			and copo.compro_sucu = c.compro_sucu 
			and copo.compro_letra = c.compro_letra 
			and copo.compro_nro = c.compro_nro
			and cast(opo.alta_fecha as date)>=cast(c.fecha as date) 
			and cast(opo.alta_fecha as date) <=   p_fecha_pago
			and (opo.baja_fecha is null or cast(opo.baja_fecha as date) >= p_fecha_pago)
			) as fecha_pagado,
			debito_para_egreso
	from comprobante c 
	where (p_cuit is null or c.cuit_acreedor = p_cuit)
	and (p_sucu is null or (c.sucu_acreedor = p_sucu and ((p_seccional is null and c.seccional is null) or c.seccional = p_seccional)))
	and cast(c.fecha_recepcion as date) >= p_fecha_ini 
	and cast(c.fecha_recepcion as date) <= p_fecha_fin
	and (c.anulado_fecha is null or cast(c.anulado_fecha as date) >= p_fecha_pago)
	and c.baja_fecha is null
) comp
left outer join concepto_comprobante cc
on comp.cuit= cc.cuit 
and comp.id_punto_venta = cc.id_punto_venta 
and comp.compro_tipo = cc.compro_tipo 
and comp.compro_sucu = cc.compro_sucu 
and comp.compro_letra = cc.compro_letra 
and comp.compro_nro = cc.compro_nro
left outer join comprobante_liquidacion cl
on comp.id_punto_venta = cl.id_punto_venta
and comp.compro_tipo = cl.compro_tipo
and comp.compro_nro = cl.compro_nro
and comp.cuit = cl.cuit
and comp.compro_letra = cl.compro_letra
and comp.compro_sucu = cl.compro_sucu
	
where comp.fecha_pagado is null
and (p_incluirLiquidaciones = true or (p_incluirLiquidaciones = false and cl.id_liquidacion is null))
and (exists (select 1 from comprobante_liquidacion cl, liquidacion l
				   where cl.id_punto_venta = comp.id_punto_venta
				   and cl.compro_tipo = comp.compro_tipo
				   and cl.compro_nro = comp.compro_nro
				   and cl.cuit = comp.cuit
				   and cl.compro_letra = comp.compro_letra
				   and cl.compro_sucu = comp.compro_sucu 
				   and cl.id_liquidacion = l.id_liquidacion
				   and l.estado in (2,10,11)
				   and cc.concepto_id not in (select id_concepto from parametros_conceptos   where parametro = 'prestaciones_medicas'))
	  or not exists  (select 1 from comprobante_liquidacion cl
				   where cl.id_punto_venta = comp.id_punto_venta
				   and cl.compro_tipo = comp.compro_tipo
				   and cl.compro_nro = comp.compro_nro
				   and cl.cuit = comp.cuit
				   and cl.compro_letra = comp.compro_letra
				   and cl.compro_sucu = comp.compro_sucu)
	);

--comprobantes anulados de los cuales tiene que figurar su inversa
CREATE temp table comp_anulado_tmp AS 
select cc.concepto_id, cc.importe, comp.id_punto_venta,  comp.compro_tipo, comp.compro_nro, comp.cuit, comp.compro_letra , comp.compro_sucu, 
       comp.cuit_acreedor, comp.sucu_acreedor, comp.seccional, comp.anulado_fecha , comp.total, comp.id_liquidacion, comp.debito_para_egreso,
       comp.fecha_recepcion
from ( 	select c.id_punto_venta,  c.compro_tipo, c.compro_nro, c.cuit, c.compro_letra , c.compro_sucu, 
		      c.cuit_acreedor, c.sucu_acreedor, c.seccional, c.anulado_fecha, c.total, c.fecha_recepcion,
		(select cast(max(alta_fecha) as date)
				from orden_pago_ospim opo, comprobante_orden_pago_ospim copo
				where opo.id_orden_pago = copo.id_orden_pago_ospim 
				and copo.cuit= c.cuit 
				and copo.id_punto_venta = c.id_punto_venta 
				and  copo.compro_tipo = c.compro_tipo 
				and copo.compro_sucu = c.compro_sucu 
				and copo.compro_letra = c.compro_letra 
				and copo.compro_nro = c.compro_nro
				and cast(opo.alta_fecha as date)>=  date_trunc('month', c.fecha_recepcion )
				and cast(opo.alta_fecha as date) <   (date_trunc('month', c.fecha_recepcion ) + interval '1 month') --fecha de pago si es que estuvo pago en el mes de recepcion
				and (opo.baja_fecha is null or cast(opo.baja_fecha as date) >= (date_trunc('month', c.fecha_recepcion ) + interval '1 month'))
				) as fecha_pagado, compl.id_liquidacion,debito_para_egreso
	from comprobante c
	left outer join comprobante_liquidacion compl
	on compl.id_punto_venta = c.id_punto_venta
	 and compl.compro_tipo = c.compro_tipo
	 and compl.compro_nro = c.compro_nro
	 and compl.cuit = c.cuit
	 and compl.compro_letra = c.compro_letra
	 and compl.compro_sucu = c.compro_sucu
	where c.anulado_fecha is not null 
	and c.baja_fecha is null	
	and cast(c.anulado_fecha as date) >= p_fecha_ini and cast(c.anulado_fecha as date) <= p_fecha_fin  --los anulados para las fechas del reporte
	and c.fecha_recepcion < p_fecha_ini --que fueron creados previos a la fecha inicial del reporte
	and (p_cuit is null or c.cuit_acreedor = p_cuit)
	and (p_sucu is null or (c.sucu_acreedor = p_sucu and ((p_seccional is null and c.seccional is null) or c.seccional = p_seccional)))
	and (p_incluirLiquidaciones = true or (p_incluirLiquidaciones = false and compl.id_liquidacion is null))
) comp
left outer join concepto_comprobante cc
on comp.cuit= cc.cuit 
and comp.id_punto_venta = cc.id_punto_venta 
and comp.compro_tipo = cc.compro_tipo 
and comp.compro_sucu = cc.compro_sucu 
and comp.compro_letra = cc.compro_letra 
and comp.compro_nro = cc.compro_nro
where comp.fecha_pagado is null --si estaba pagado en el mes de recepcion del comprobante, NO tiene q aparecer
and (exists (select 1 from comprobante_liquidacion cl, liquidacion l--si pertenece a una liq cerrada
				   where cl.id_punto_venta = comp.id_punto_venta
				   and cl.compro_tipo = comp.compro_tipo
				   and cl.compro_nro = comp.compro_nro
				   and cl.cuit = comp.cuit
				   and cl.compro_letra = comp.compro_letra
				   and cl.compro_sucu = comp.compro_sucu 
				   and cl.id_liquidacion = l.id_liquidacion
				   and l.estado in (2,10,11)
				   and cc.concepto_id not in (select id_concepto from parametros_conceptos   where parametro = 'prestaciones_medicas'))
	  or not exists  (select 1 from comprobante_liquidacion cl --o si no pertenece a una liq
				   where cl.id_punto_venta = comp.id_punto_venta
				   and cl.compro_tipo = comp.compro_tipo
				   and cl.compro_nro = comp.compro_nro
				   and cl.cuit = comp.cuit
				   and cl.compro_letra = comp.compro_letra
				   and cl.compro_sucu = comp.compro_sucu)
	);
	
	
create temp table notas_debito_tmp as
select  null, (total * -1) as importe, id_punto_venta,  compro_tipo, compro_nro, cuit, compro_letra , compro_sucu, 
       cuit_acreedor, sucu_acreedor, seccional, fecha_recepcion, (total * -1) as total, id_liquidacion,debito_para_egreso
       from (select 
		c.*,
		cl.id_liquidacion
	from comprobante c 		
	inner join comprobante_liquidacion cl
	on c.id_punto_venta = cl.id_punto_venta
	and c.compro_tipo = cl.compro_tipo
	and c.compro_nro = cl.compro_nro
	and c.cuit = cl.cuit
	and c.compro_letra = cl.compro_letra
	and c.compro_sucu = cl.compro_sucu
	and c.compro_tipo = 'NDB'
	/*inner join liquidacion l	
	on l.id_liquidacion = cl.id_liquidacion
	-------------------------------------
	where l.estado in (2,10,11)
	and (p_cuit is null or c.cuit_acreedor = p_cuit)
	and (p_sucu is null or (c.sucu_acreedor = p_sucu and ((p_seccional is null and c.seccional is null) or c.seccional = p_seccional)))
	and (cast(c.fecha_recepcion as date)>= p_fecha_ini and cast(c.fecha_recepcion as date)<=p_fecha_fin)
	and (c.anulado_fecha is null or cast(c.anulado_fecha as date) >= p_fecha_pago)
	and c.baja_fecha is null*/
	WHERE (cl.id_liquidacion in (select id_liquidacion from comp_anulado_tmp) 
			or cl.id_liquidacion in (select id_liquidacion from comp_tmp) )
	and (p_incluirLiquidaciones = true)
) notasdb;
	
return query 
select fecha,
	total, compro_nro, compro_letra, compro_tipo, compro_sucu, id_punto_venta,		
	cuit, numero, cuenta, cuenta_id, importe, nro_pasivo, cta_pasivo,cta_pasivo_id, imp_pasivo, 
	cuit_acreedor, sucu_acreedor, id_seccional, descripcion, razon_soc, id_liquidacion,debito_para_egreso
from (select cast(cc.fecha_recepcion as date) as fecha,
	cc.total, cc.compro_nro, cc.compro_letra, cc.compro_tipo, cc.compro_sucu, cc.id_punto_venta,		
	cc.cuit, pc.numero, pc.cuenta, pc.id_cuenta_maestro as cuenta_id, cc.importe, pc2.numero as nro_pasivo, pc2.cuenta as cta_pasivo, pc2.id_cuenta_maestro as cta_pasivo_id, cc.importe as imp_pasivo, 
	e.cuit as cuit_acreedor, e.sucursal as sucu_acreedor, s.id_seccional, s.descripcion, e.razon_soc, id_liquidacion, 1 as orden,debito_para_egreso
from comp_tmp cc
left outer join conceptos conc
on cc.concepto_id = conc.id_concepto_maestro
and cast(conc.valido_desde as date)  <= cast(cc.fecha_recepcion as date)
and cast(conc.valido_hasta as date)  >= cast(cc.fecha_recepcion as date)
left outer join plan_cuentas pc
on conc.id_plan_cuenta = pc.id_cuenta_maestro
and pc.valido_desde <= cast(cc.fecha_recepcion as date) and pc.valido_hasta >= cast(cc.fecha_recepcion as date)
left outer join plan_cuentas pc2
on conc.id_plan_cuenta_pasivo = pc2.id_cuenta_maestro
and pc2.valido_desde <= cast(cc.fecha_recepcion as date) and pc2.valido_hasta >= cast(cc.fecha_recepcion as date)
left outer join seccional s
on cc.seccional = s.id_seccional
left outer join empresa e
on cc.cuit_acreedor = e.cuit
and cc.sucu_acreedor = e.sucursal
where cc.concepto_id not in (select id from concepto_maestro where descripcion_original = 'AJUSTE')
union all 
select cast(cc.anulado_fecha as date) as fecha,
	cc.total, cc.compro_nro, cc.compro_letra, cc.compro_tipo, cc.compro_sucu, cc.id_punto_venta,		
	cc.cuit, pc2.numero, pc2.cuenta, pc2.id_cuenta_maestro, cc.importe, pc.numero  as nro_pasivo, pc.cuenta as cta_pasivo, pc.id_cuenta_maestro, cc.importe as imp_pasivo,
	e.cuit as cuit_acreedor, e.sucursal as sucu_acreedor, s.id_seccional, s.descripcion, e.razon_soc, id_liquidacion, 1 as orden,debito_para_egreso
from comp_anulado_tmp cc
left outer join conceptos conc
on cc.concepto_id = conc.id_concepto_maestro
and cast(conc.valido_desde as date)  <= cast(cc.fecha_recepcion as date)
and cast(conc.valido_hasta as date)  >= cast(cc.fecha_recepcion as date)
left outer join plan_cuentas pc
on conc.id_plan_cuenta = pc.id_cuenta_maestro
and pc.valido_desde <= cast(cc.fecha_recepcion as date) and pc.valido_hasta >= cast(cc.fecha_recepcion as date)
left outer join plan_cuentas pc2
on conc.id_plan_cuenta_pasivo = pc2.id_cuenta_maestro
and pc2.valido_desde <= cast(cc.fecha_recepcion as date) and pc2.valido_hasta >= cast(cc.fecha_recepcion as date)
left outer join seccional s
on cc.seccional = s.id_seccional
left outer join empresa e
on cc.cuit_acreedor = e.cuit
and cc.sucu_acreedor = e.sucursal
where cc.concepto_id not in (select id from concepto_maestro where descripcion_original = 'AJUSTE')
union all
select  cast(cc.fecha_recepcion as date) as fecha,
	cc.total, cc.compro_nro, cc.compro_letra, cc.compro_tipo, cc.compro_sucu, cc.id_punto_venta,		
	cc.cuit, null, null, null, cc.total, null  as nro_pasivo, null as cta_pasivo, null, cc.total  as imp_pasivo,
	e.cuit as cuit_acreedor, e.sucursal as sucu_acreedor, s.id_seccional, s.descripcion, e.razon_soc, id_liquidacion , 2 as orden,debito_para_egreso
	from notas_debito_tmp cc
	left outer join empresa e
	on cc.cuit_acreedor = e.cuit
	and cc.sucu_acreedor = e.sucursal
	left outer join seccional s
	on cc.seccional = s.id_seccional
) res
where p_incluirProveedores =  true or (p_incluirProveedores = false and id_liquidacion is not null)
order by orden asc, cuit asc,1 asc;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;

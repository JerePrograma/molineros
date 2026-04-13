/*
drop type tipo_conceptos_subdiario cascade;

create type tipo_conceptos_subdiario as (
ccc__alta_fecha timestamp without time zone, 
ccc__alta_usr character varying, 
ccc__modi_fecha timestamp without time zone, 
ccc__modi_usr character varying, 
ccc__baja_fecha timestamp without time zone, 
ccc__baja_usr character varying, 
ccc__concepto_id integer, 
cc__id integer, 
cc__descripcion character varying, 
cc__numero character varying, 
ccc__importe numeric, 
c__id_punto_venta smallint, 
c__compro_tipo character varying, 
c__compro_nro character varying, 
c__cuit character(11), 
c__compro_letra character varying, 
c__compro_sucu integer, 
cc__cuenta character varying,
cc__cuenta_id integer,
cc_numero_pasivo character varying,
cc_cuenta_pasivo character varying,
cc_cuenta_pasivo_id integer,
id_aux text);*/

drop function buscar_concepto_comprobante_por_fecha_subdiario(IN p_date_ini date, IN p_date_fin date);
-- Function: buscar_concepto_comprobante_por_fecha_subdiario(date, date)

-- DROP FUNCTION buscar_concepto_comprobante_por_fecha_subdiario(date, date);

CREATE OR REPLACE FUNCTION buscar_concepto_comprobante_por_fecha_subdiario(p_date_ini date, p_date_fin date)
  RETURNS SETOF tipo_conceptos_subdiario AS
$BODY$
BEGIN

drop table if exists reintes_tmp;
drop table if exists liquidaciones_tmp;
 
--PRESTACIONES DE REINTEGROS!!!
CREATE temp table reintes_tmp AS 
select  distinct alta_fecha, alta_usr,   modi_fecha,  modi_usr,  baja_fecha,  baja_usr,  total,  id_punto_venta ,   compro_tipo ,
    compro_nro ,    cuit ,    compro_letra ,    compro_sucu ,    todas.id_prestacion,amb_imp_honorarios,amb_imp_gast,amt_imp_hon, amt_imp_gast, id_reintegro, id_aux 
 from
(
--reintegros farmacia
	(select
	c.*,
	--rp.*,
	mrf.id_medicamento as id_prestacion,
	1 as amb_imp_honorarios,
	1 as amb_imp_gast,
	0 as amt_imp_hon, 
	0 as amt_imp_gast,
	r.id_reintegro,
	cast (mrf.id_reintegro as character varying) || cast (mrf.id_medicamento  as character varying)
	|| cast (mrf.alta_fecha  as character varying)as id_aux
	
	from comprobante c inner join comprobante_orden_pago_ospim copo
	
	on c.id_punto_venta = copo.id_punto_venta
	and c.compro_tipo = copo.compro_tipo
	and c.compro_nro = copo.compro_nro
	and c.cuit = copo.cuit
	and c.compro_letra = copo.compro_letra
	and c.compro_sucu = copo.compro_sucu
	
	inner join orden_pago_ospim opo
	on copo.id_orden_pago_ospim = opo.id_orden_pago
	
	inner join orden_pago_ospim_lista_reintegros_farmacia opol
	on opo.id_orden_pago = opol.id_orden_pago_ospim
	
	inner join lista_reintegro_farmacia_pago_detalle opor
	on opol.id_lista_reintegro_pago = opor.id_lista_reintegro_pago
	
	inner join reintegro_farmacia r
	on r.id_reintegro = opor.id_reintegro 		

	inner join medicamento_reintegro_farmacia mrf
	on mrf.id_reintegro=r.id_reintegro

	inner join medicamentos m
	on m.id_medicamento=mrf.id_medicamento
	
	-------------------------------------
	where c.compro_nro like '%FAR-%'
	and ((cast(opo.alta_fecha as date)>=$1 and cast(opo.alta_fecha as date)<=$2)
	or (cast(opo.baja_fecha as date)>=$1 and cast(opo.baja_fecha as date)<=$2)))
	union all
	-------------------------------------
	--reintegros prestacionales
	(select
	c.*,
	--rp.*,
	n.id_prestacion,
	(rp.importe * rp.cantidad) * n.coef_honorarios as amb_imp_honorarios,
	(rp.importe * rp.cantidad) * n.coef_gastos as amb_imp_gast,
	0 * n.coef_honorarios as amt_imp_hon, 
	0 * n.coef_gastos as amt_imp_gast,
	r.id_reintegro,
	cast (rp.id_reintegro as character varying) || cast (rp.id_prestacion  as character varying)
	|| cast (rp.id_plan  as character varying)|| cast (rp.alta_fecha  as character varying)as id_aux
	
	from comprobante c inner join comprobante_orden_pago_ospim copo
	
	on c.id_punto_venta = copo.id_punto_venta
	and c.compro_tipo = copo.compro_tipo
	and c.compro_nro = copo.compro_nro
	and c.cuit = copo.cuit
	and c.compro_letra = copo.compro_letra
	and c.compro_sucu = copo.compro_sucu
	
	inner join orden_pago_ospim opo
	on copo.id_orden_pago_ospim = opo.id_orden_pago
	
	inner join orden_pago_ospim_lista_reintegros opol
	on opo.id_orden_pago = opol.id_orden_pago_ospim
	
	inner join lista_reintegro_pago_detalle opor
	on opol.id_lista_reintegro_pago = opor.id_lista_reintegro_pago
	
	inner join reintegro r
	on r.id_reintegro = opor.id_reintegro 
	and r.tipo_reintegro = 'pre'
	
	inner join reintegro_prestacion rp
	on r.id_reintegro = rp.id_reintegro
	
	inner join plan_prestacion pp
	on 
	rp.id_prestacion = pp.id_prestacion and
	rp.id_plan = pp.id_plan
	
	inner join nomenclador n
	on pp.id_prestacion = n.id_prestacion
	-------------------------------------
	where c.compro_nro not like 'FAR-%'
	and (rp.tercerizado is null or rp.tercerizado = '0' or trim(rp.tercerizado) = '')
	and ((cast(opo.alta_fecha as date)>=$1 and cast(opo.alta_fecha as date)<=$2)
	or (cast(opo.baja_fecha as date)>=$1 and cast(opo.baja_fecha as date)<=$2))) 
	
	union all
	--reintegro protesis
	(select
	
	c.*,
	--rp.*,
	n.id_prestacion,
	(rp.importe * rp.cantidad) * n.coef_honorarios as amb_imp_honorarios,
	(rp.importe * rp.cantidad) * n.coef_gastos as amb_imp_gast,
	0 * n.coef_honorarios as amt_imp_hon, 
	0 * n.coef_gastos as amt_imp_gast,
	r.id_reintegro,
	cast(rp.id  as character varying) as id_aux
	
	
	from comprobante c inner join comprobante_orden_pago_ospim copo
	
	on c.id_punto_venta = copo.id_punto_venta
	and c.compro_tipo = copo.compro_tipo
	and c.compro_nro = copo.compro_nro
	and c.cuit = copo.cuit
	and c.compro_letra = copo.compro_letra
	and c.compro_sucu = copo.compro_sucu
	
	inner join orden_pago_ospim opo
	on copo.id_orden_pago_ospim = opo.id_orden_pago
	
	inner join orden_pago_ospim_lista_reintegros opol
	on opo.id_orden_pago = opol.id_orden_pago_ospim
	
	inner join lista_reintegro_pago_detalle opor
	on opol.id_lista_reintegro_pago = opor.id_lista_reintegro_pago
	
	inner join reintegro r
	on r.id_reintegro = opor.id_reintegro 
	and r.tipo_reintegro = 'pro'
	
	inner join reintegro_prestacion_odo_protesis rp
	on r.id_reintegro = rp.id_reintegro
	
	inner join plan_prestacion pp
	on 
	rp.id_prestacion = pp.id_prestacion and
	rp.id_plan = pp.id_plan
	
	inner join nomenclador n
	on pp.id_prestacion = n.id_prestacion
	-------------------------------------
	where  c.compro_nro not like 'FAR-%'	
	and (rp.tercerizado is null or rp.tercerizado = '0' or trim(rp.tercerizado) = '') 
	and ((cast(opo.alta_fecha as date)>=$1 and cast(opo.alta_fecha as date)<=$2)
	or (cast(opo.baja_fecha as date)>=$1 and cast(opo.baja_fecha as date)<=$2))) 
	
	union all
	--ortopedias y ortodoncias
	(select
	
	c.*,
	--rp.*,
	n.id_prestacion,
	dc.importe * n.coef_honorarios as amb_imp_honorarios, --cantidad siempre es 1
	dc.importe * n.coef_gastos as amb_imp_gast, --cantidad siempre es 1
	0 * n.coef_honorarios as amt_imp_hon, 
	0 * n.coef_gastos as amt_imp_gast,
	r.id_reintegro,
	cast (dc.id_cuota as character varying) as id_aux
	
	from comprobante c inner join comprobante_orden_pago_ospim copo
	
	on c.id_punto_venta = copo.id_punto_venta
	and c.compro_tipo = copo.compro_tipo
	and c.compro_nro = copo.compro_nro
	and c.cuit = copo.cuit
	and c.compro_letra = copo.compro_letra
	and c.compro_sucu = copo.compro_sucu
	
	inner join orden_pago_ospim opo
	on copo.id_orden_pago_ospim = opo.id_orden_pago
	
	inner join orden_pago_ospim_lista_reintegros opol
	on opo.id_orden_pago = opol.id_orden_pago_ospim
	
	inner join lista_reintegro_pago_detalle opor
	on opol.id_lista_reintegro_pago = opor.id_lista_reintegro_pago
	
	inner join detalle_cuota dc
	on dc.id_cuota = opor.id_reintegro --en este caso el join se hace por detalle_cuenta
	
	inner join reintegro r
	on r.id_reintegro = dc.id_reintegro
	and r.tipo_reintegro = 'ort'
	
	inner join reintegro_prestacion_odo_ort rp
	on r.id_reintegro = rp.id_reintegro
	
	inner join plan_prestacion pp
	on 
	rp.id_prestacion = pp.id_prestacion and
	rp.id_plan = pp.id_plan
	
	inner join nomenclador n
	on pp.id_prestacion = n.id_prestacion
	-------------------------------------
	where  c.compro_nro not like 'FAR-%'
	and (rp.tercerizado is null or rp.tercerizado = '0' or trim(rp.tercerizado) = '') 
	and ((cast(opo.alta_fecha as date)>=$1 and cast(opo.alta_fecha as date)<=$2) --SIEMPRE TRAIGO ALTAS Y
	or (cast(opo.baja_fecha as date)>=$1 and cast(opo.baja_fecha as date)<=$2))) --BAJAS PARA LA CONTABILIDAD
) todas
--Para evitar un bug de problemas de datos (comprobantes REI que no son reintegros (mala migración)
where (alta_fecha>='20130701' or (alta_fecha <'20130701' and not exists (
select 1 from comprobante_orden_pago_ospim copo
where id_orden_pago_ospim in (
				select id_orden_pago_ospim
				from comprobante_orden_pago_ospim   copo
				where compro_tipo = 'REI' 
				group by id_orden_pago_ospim
				having count(*) >1
				)
and compro_nro = (select min(compro_nro) from comprobante_orden_pago_ospim where id_orden_pago_ospim = copo.id_orden_pago_ospim)
and compro_nro = todas.compro_nro
and compro_tipo = todas.compro_tipo
and compro_sucu = todas.compro_sucu
and compro_letra = todas.compro_letra
and cuit = todas.cuit
and id_punto_venta = todas.id_punto_venta ))) ;



--LIQUIDACIONES -- PRESTACIONES NO ESTADISTICAS
CREATE temp table liquidaciones_tmp AS 
select  distinct alta_fecha, alta_usr,   modi_fecha,  modi_usr,  baja_fecha,  baja_usr,  total,  id_punto_venta ,   compro_tipo ,
    compro_nro ,    cuit ,    compro_letra ,    compro_sucu , todas_liq.id_prestacion,amb_imp_honorarios,amb_imp_gast,amt_imp_hon, amt_imp_gast , orden
from (
	--ESTAS SON PRESTACIONES PARA AMBULATORIAS
	(select 
		c.*,
		n.id_prestacion,
		(lp.importe * lp.cantidad) * n.coef_honorarios as amb_imp_honorarios,--AMBULATORIO
		(lp.importe * lp.cantidad) * n.coef_gastos as amb_imp_gast,--AMBULATORIO
		0 as amt_imp_hon, --INTERNACION
		0 as amt_imp_gast,--INTERNACION
		cast(lp.id_liquidacion as character varying) || cast(lp.orden as character varying) as orden
	
	from comprobante c inner join comprobante_orden_pago_ospim copo
	
	on c.id_punto_venta = copo.id_punto_venta
	and c.compro_tipo = copo.compro_tipo
	and c.compro_nro = copo.compro_nro
	and c.cuit = copo.cuit
	and c.compro_letra = copo.compro_letra
	and c.compro_sucu = copo.compro_sucu
	
	inner join orden_pago_ospim opo
	on copo.id_orden_pago_ospim = opo.id_orden_pago
		
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
	
	where (lp.tercerizado <> '1' or lp.tercerizado is null) --ACA DEFINE SI ES ESTADISTICO O TERCERIZADO
	and (lp.servicio != 'INTERNACIÓN CLÍNICA' or lp.servicio is null)  
	and ((cast(opo.alta_fecha as date)>= $1 and cast(opo.alta_fecha as date)<=$2)
	or (cast(opo.baja_fecha as date)>=$1 and cast(opo.baja_fecha as date)<=$2))
	)
	
	union all
	--ESTO ES PARA PRESTACIONES DE INTERNACION
	(select
	c.*,
	n.id_prestacion,
	0 as amb_imp_honorarios, 
	0 as amb_imp_gast,
	(lp.importe * lp.cantidad) * n.coef_honorarios as amt_imp_hon,
	(lp.importe * lp.cantidad) * n.coef_gastos as amt_imp_gast,
	cast(lp.id_liquidacion as character varying) || cast(lp.orden as character varying) as orden
	
	from comprobante c inner join comprobante_orden_pago_ospim copo
	
	on c.id_punto_venta = copo.id_punto_venta
	and c.compro_tipo = copo.compro_tipo
	and c.compro_nro = copo.compro_nro
	and c.cuit = copo.cuit
	and c.compro_letra = copo.compro_letra
	and c.compro_sucu = copo.compro_sucu
	
	inner join orden_pago_ospim opo
	on copo.id_orden_pago_ospim = opo.id_orden_pago
		
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
	
	where
	
	(lp.tercerizado <> '1' or lp.tercerizado is null) and  --ACA DEFINE SI ES ESTADISTICO O TERCERIZADO
	lp.servicio = 'INTERNACIÓN CLÍNICA'
	and ((cast(opo.alta_fecha as date)>=$1 and cast(opo.alta_fecha as date)<=$2)
	or (cast(opo.baja_fecha as date)>=$1 and cast(opo.baja_fecha as date)<=$2)) 
	) 
) todas_liq;


	 
--LA PARTE ANTES DEL UNION BUSCA LOS CONCEPTOS DE LAS OP NO REINTEGROS Y NO PRESTACIONES DE LIQUIDACIONES
 return query 
	select distinct c.alta_fecha, c.alta_usr,
	  c.modi_fecha,
	  c.modi_usr,
	  c.baja_fecha,
	  c.baja_usr,
	  c.concepto_id,
	  cc.id_concepto_maestro,
	  cc.descripcion,
	  pc.numero,
	  c.importe,
	  c.id_punto_venta ,
	  c.compro_tipo ,
	    c.compro_nro ,
	    c.cuit ,
	    c.compro_letra ,
	    c.compro_sucu ,
	     pc.cuenta,
	     pc.id_cuenta_maestro,
	     pc2.numero,
	     pc2.cuenta,
	     pc2.id_cuenta_maestro,
	     null as id_aux
	from  concepto_comprobante c
	inner join comprobante comp
	on c.id_punto_venta = comp.id_punto_venta
	   and c.compro_tipo = comp.compro_tipo
	   and c.compro_nro = comp.compro_nro
	   and c.cuit = comp.cuit
	   and c.compro_letra = comp.compro_letra
	   and c.compro_sucu = comp.compro_sucu
	inner join concepto_maestro cm
	on c.concepto_id = cm.id
	inner join conceptos cc
	on cm.id = cc.id_concepto_maestro
	and cast(cc.valido_desde as date)  <= cast(comp.fecha_recepcion as date)
	and cast(cc.valido_hasta as date)  >= cast(comp.fecha_recepcion as date)
	inner join comprobante_orden_pago_ospim copo
	on c.id_punto_venta = copo.id_punto_venta
	   and c.compro_tipo = copo.compro_tipo
	   and c.compro_nro = copo.compro_nro
	   and c.cuit = copo.cuit
	   and c.compro_letra = copo.compro_letra
	   and c.compro_sucu = copo.compro_sucu
	inner join orden_pago_ospim opo
	on copo.id_orden_pago_ospim = opo.id_orden_pago
	left outer join plan_cuentas pc
	on cc.id_plan_cuenta = pc.id_cuenta_maestro
	and cast(pc.valido_desde as date)  <= cast(comp.fecha_recepcion as date)
	and cast(pc.valido_hasta as date)  >= cast(comp.fecha_recepcion as date)
	left outer join plan_cuentas pc2
	on cc.id_plan_cuenta_pasivo = pc2.id_cuenta_maestro
	and cast(pc2.valido_desde as date)  <= cast(comp.fecha_recepcion as date)
	and cast(pc2.valido_hasta as date)  >= cast(comp.fecha_recepcion as date)
	where c.compro_nro not like 'FAR-%'
	and ((cast(opo.alta_fecha  as date)>=$1
	and cast(opo.alta_fecha as date)<=$2)
	or (cast(opo.baja_fecha  as date)>=$1
	and cast(opo.baja_fecha as date)<=$2))
	--QUE NO SEAN PRESTACIONES MEDICAS DE LIQUIDACIONES (SE INCLUYEN CONVENIOS GLOBALES)
	and not exists (select 1 from comprobante_liquidacion where   id_punto_venta = c.id_punto_venta
															   and compro_tipo = c.compro_tipo
															   and compro_nro = c.compro_nro
															   and cuit = c.cuit
															   and compro_letra = c.compro_letra
															   and compro_sucu = c.compro_sucu 
															   and c.concepto_id = (select id_concepto from parametros_conceptos   where parametro = 'prestaciones_medicas' and valido_desde <= comp.fecha_recepcion and valido_hasta >= comp.fecha_recepcion))
	--QUE NO SEAN DE REINTEGROS															
	and not exists (select 1 from orden_pago_ospim_lista_reintegros where id_orden_pago_ospim = opo.id_orden_pago)
---- UNION!!! ----
union   -- UNION CON LA QUERY DE ANTICIPOS USADOS COMO PAGOS
---- UNION!!! ----		
--REINTEGROS DE FARMACIA.....
select distinct c.alta_fecha, c.alta_usr,
	  c.modi_fecha,
	  c.modi_usr,
	  c.baja_fecha,
	  c.baja_usr,
	  c.concepto_id,
	  cc.id_concepto_maestro,
	  cc.descripcion,
	  pc.numero,
	  c.importe,
	  c.id_punto_venta ,
	  c.compro_tipo ,
	    c.compro_nro ,
	    c.cuit ,
	    c.compro_letra ,
	    c.compro_sucu ,
	     pc.cuenta,
	     pc.id_cuenta_maestro,
	     pc2.numero,
	     pc2.cuenta,
	     pc2.id_cuenta_maestro,
	     null as id_aux
	from reintes_tmp tmp
	inner join concepto_comprobante c
	on tmp.compro_tipo=c.compro_tipo
	and tmp.compro_nro=c.compro_nro
	inner join comprobante comp
	on c.id_punto_venta = comp.id_punto_venta
	   and c.compro_tipo = comp.compro_tipo
	   and c.compro_nro = comp.compro_nro
	   and c.cuit = comp.cuit
	   and c.compro_letra = comp.compro_letra
	   and c.compro_sucu = comp.compro_sucu
	inner join concepto_maestro cm
	on c.concepto_id = cm.id
	inner join conceptos cc
	on cm.id = cc.id_concepto_maestro
	and cast(cc.valido_desde as date)  <= cast(comp.fecha_recepcion as date)
	and cast(cc.valido_hasta as date)  >= cast(comp.fecha_recepcion as date)
	inner join comprobante_orden_pago_ospim copo
	on c.id_punto_venta = copo.id_punto_venta
	   and c.compro_tipo = copo.compro_tipo
	   and c.compro_nro = copo.compro_nro
	   and c.cuit = copo.cuit
	   and c.compro_letra = copo.compro_letra
	   and c.compro_sucu = copo.compro_sucu
	inner join orden_pago_ospim opo
	on copo.id_orden_pago_ospim = opo.id_orden_pago
	left outer join plan_cuentas pc
	on cc.id_plan_cuenta = pc.id_cuenta_maestro
	and cast(pc.valido_desde as date)  <= cast(comp.fecha_recepcion as date)
	and cast(pc.valido_hasta as date)  >= cast(comp.fecha_recepcion as date)
	left outer join plan_cuentas pc2
	on cc.id_plan_cuenta_pasivo = pc2.id_cuenta_maestro
	and cast(pc2.valido_desde as date)  <= cast(comp.fecha_recepcion as date)
	and cast(pc2.valido_hasta as date)  >= cast(comp.fecha_recepcion as date)
	where c.compro_nro like 'FAR-%' 
	and ((cast(opo.alta_fecha  as date)>=$1
	and cast(opo.alta_fecha as date)<=$2)
	or (cast(opo.baja_fecha  as date)>=$1
	and cast(opo.baja_fecha as date)<=$2))
	--QUE NO SEAN PRESTACIONES MEDICAS DE LIQUIDACIONES (SE INCLUYEN CONVENIOS GLOBALES)
	and not exists (select 1 from comprobante_liquidacion where   id_punto_venta = c.id_punto_venta
															   and compro_tipo = c.compro_tipo
															   and compro_nro = c.compro_nro
															   and cuit = c.cuit
															   and compro_letra = c.compro_letra
															   and compro_sucu = c.compro_sucu 
															   and c.concepto_id = (select id_concepto from parametros_conceptos   where parametro = 'prestaciones_medicas' and valido_desde <= comp.fecha_recepcion and valido_hasta >= comp.fecha_recepcion))
	--QUE SEAN DE REINTEGROS FARMACIA															
	and exists (select 1 from orden_pago_ospim_lista_reintegros_farmacia where id_orden_pago_ospim = opo.id_orden_pago)	
---- UNION!!! ----
union   -- UNION CON LA QUERY DE ANTICIPOS USADOS COMO PAGOS
---- UNION!!! ----	
	select distinct c.alta_fecha, c.alta_usr,
	  c.modi_fecha,
	  c.modi_usr,
	  c.baja_fecha,
	  c.baja_usr,
	  cc.concepto_id,
	  conc.id_concepto_maestro,
	  conc.descripcion,
	  pc.numero,
	  cc.importe,
	  c.id_punto_venta ,
	 c.compro_tipo ,
	    c.compro_nro ,
	    c.cuit ,
	    c.compro_letra ,
	    c.compro_sucu ,
	     pc.cuenta,
	     pc.id_cuenta_maestro,
	     pc2.numero,
	     pc2.cuenta,
	     pc2.id_cuenta_maestro,
		null as id_aux
from orden_pago_ospim_pagos opop --SI FIGURAN ACA FUERON USADOS COMO PAGOS
inner join orden_pago_ospim opo
on opop.id_orden_pago= opo.id_orden_pago
inner join comprobante c
on opop.id_punto_venta_antic =  c.id_punto_venta
and opop.compro_tipo_antic =c.compro_tipo
and opop.compro_letra_antic  =c.compro_letra
and opop.compro_sucu_antic =c.compro_sucu
and opop.compro_nro_antic =c.compro_nro
and opop.cuit_antic = c.cuit
inner join concepto_comprobante cc
on  c.id_punto_venta =  cc.id_punto_venta
and c.compro_tipo  =cc.compro_tipo
and c.compro_letra  =cc.compro_letra
and c.compro_sucu =cc.compro_sucu
and c.compro_nro =cc.compro_nro
and c.cuit = cc.cuit
inner join concepto_maestro cm
on cc.concepto_id = cm.id
inner join conceptos conc
on cm.id = conc.id_concepto_maestro
and cast(conc.valido_desde as date)  <= cast(c.fecha_recepcion as date)
and cast(conc.valido_hasta as date)  >= cast(c.fecha_recepcion as date)
left outer join plan_cuentas pc
on conc.id_plan_cuenta = pc.id_cuenta_maestro
and cast(pc.valido_desde as date)  <= cast(c.fecha_recepcion as date)
and cast(pc.valido_hasta as date)  >= cast(c.fecha_recepcion as date)
left outer join plan_cuentas pc2
on conc.id_plan_cuenta_pasivo = pc2.id_cuenta_maestro
and cast(pc2.valido_desde as date)  <= cast(c.fecha_recepcion as date)
and cast(pc2.valido_hasta as date)  >= cast(c.fecha_recepcion as date)
left outer join empresa e
on opo.cuit_acreedor = e.cuit
and opo.sucu_acreedor = e.sucursal
where opop.id_punto_venta_antic is not null
and ((cast(opo.alta_fecha  as date)>=$1 and cast(opo.alta_fecha as date)<=$2) 
or (cast(opo.baja_fecha  as date)>=$1 and cast(opo.baja_fecha as date)<=$2))
and not exists (select 1 from comprobante_orden_pago_ospim  copo1, orden_pago_ospim opo1
	where copo1.id_punto_venta = opop.id_punto_venta_antic
	   and copo1.compro_tipo = opop.compro_tipo_antic
	   and copo1.compro_nro = opop.compro_nro_antic
	   and copo1.cuit = opop.cuit_antic
	   and copo1.compro_letra = opop.compro_letra_antic
	   and copo1.compro_sucu = opop.compro_sucu_antic
	   and copo1.id_orden_pago_ospim = opo1.id_orden_pago
	   and ( (cast(opo1.alta_fecha  as date)>=$1 and cast(opo1.alta_fecha as date)<=$2)
			  or (cast(opo1.baja_fecha  as date)>=$1and cast(opo1.baja_fecha as date)<=$2)) 
			)
---- UNION!!! ----
union   -- UNION CON LA QUERY DE REINTEGROS NO TERCERIZADOS
---- UNION!!! ----
	select todo_final.alta_fecha, todo_final.alta_usr,
	  todo_final.modi_fecha,
	  todo_final.modi_usr,
	  todo_final.baja_fecha,
	  todo_final.baja_usr,
	  todo_final.concepto_id,
	  c.id_concepto_maestro,
	  c.descripcion,
	  pc.numero,
	  todo_final.importe,
	  todo_final.id_punto_venta ,
	 todo_final.compro_tipo ,
	    todo_final.compro_nro ,
	    todo_final.cuit ,
	    todo_final.compro_letra ,
	    todo_final.compro_sucu ,
	     pc.cuenta,
	     pc.id_cuenta_maestro,
	     pc2.numero,
	     pc2.cuenta,
	     pc2.id_cuenta_maestro,
	     id_aux
	     from (
	--REINTEGROS AMBULATORIOS HONORARIOS TERCERIZADOS 
	select  t.alta_fecha, t.alta_usr,   t.modi_fecha,  t.modi_usr,  t.baja_fecha,  t.baja_usr,   id_punto_venta ,   compro_tipo ,
	 compro_nro ,    cuit ,    compro_letra ,    compro_sucu ,    t.id_prestacion,amb_imp_honorarios as importe, nc.concepto_id, id_aux
	  from reintes_tmp t
	  left outer join nomenclador_conceptos nc
	  on t.id_prestacion = nc.id_prestacion
	  and nc.valido_desde <= t.alta_fecha and nc.valido_hasta >= t.alta_fecha 
	  where amb_imp_honorarios <> 0
	  and nc.tipo_id = 1
	union all
	--REINTEGROS AMBULATORIOS GASTOS TERCERIZADOS 
	select  t.alta_fecha, t.alta_usr,  t.modi_fecha,  t.modi_usr,  t.baja_fecha,  t.baja_usr,   id_punto_venta ,   compro_tipo ,
	  compro_nro ,    cuit ,    compro_letra ,    compro_sucu ,    t.id_prestacion,amb_imp_gast as importe, nc.concepto_id, id_aux
	  from reintes_tmp t
	  left outer join nomenclador_conceptos nc
	  on t.id_prestacion = nc.id_prestacion
	  and nc.valido_desde <= t.alta_fecha and nc.valido_hasta >= t.alta_fecha 
	  where amb_imp_gast <> 0
	  and nc.tipo_id = 2
	union all
	--REINTEGROS INTERNACION HONORARIOS TERCERIZADOS 
	select  t.alta_fecha, t.alta_usr,   t.modi_fecha,  t.modi_usr,  t.baja_fecha,  t.baja_usr,   id_punto_venta ,   compro_tipo ,
	  compro_nro ,    cuit ,    compro_letra ,    compro_sucu ,    t.id_prestacion,amt_imp_hon as importe, nc.concepto_id, id_aux
	  from reintes_tmp t
	  left outer join nomenclador_conceptos nc
	  on t.id_prestacion = nc.id_prestacion
	  and nc.valido_desde <= t.alta_fecha and nc.valido_hasta >= t.alta_fecha 
	  where amt_imp_hon <> 0
	  and nc.tipo_id = 3
	union all
	--REINTEGROS INTERNACION GASTOS TERCERIZADOS 
	select  t.alta_fecha, t.alta_usr,   t.modi_fecha,  t.modi_usr,  t.baja_fecha,  t.baja_usr,    id_punto_venta ,   compro_tipo ,
	  compro_nro ,    cuit ,    compro_letra ,    compro_sucu ,    t.id_prestacion,amt_imp_gast as importe, nc.concepto_id, id_aux
	  from reintes_tmp t
	  left outer join nomenclador_conceptos nc
	  on t.id_prestacion = nc.id_prestacion
	  and nc.valido_desde <= t.alta_fecha and nc.valido_hasta >= t.alta_fecha 
	  where amt_imp_gast <> 0
	  and nc.tipo_id = 4
	 ) todo_final 
	 left outer join concepto_maestro cm
	 on todo_final.concepto_id = cm.id
	 left outer join conceptos c
	 on cm.id = c.id_concepto_maestro
	 and cast(c.valido_desde as date)  <= cast(todo_final.alta_fecha as date)
	 and cast(c.valido_hasta as date)  >= cast(todo_final.alta_fecha as date)
	 left outer join plan_cuentas pc
	 on c.id_plan_cuenta = pc.id_cuenta_maestro
	 and cast(pc.valido_desde as date)  <= cast(todo_final.alta_fecha as date)
	 and cast(pc.valido_hasta as date)  >= cast(todo_final.alta_fecha as date)
	 left outer join plan_cuentas pc2
	 on c.id_plan_cuenta_pasivo = pc2.id_cuenta_maestro
	 and cast(pc2.valido_desde as date)  <= cast(todo_final.alta_fecha as date)
	 and cast(pc2.valido_hasta as date)  >= cast(todo_final.alta_fecha as date)
	 where todo_final.compro_nro not like 'FAR-%'
---- UNION!!! ----
union   -- UNION CON LA QUERY DE REINTEGROS TERCERIZADOS
---- UNION!!! ----
	select todas_reint_ter.alta_fecha, todas_reint_ter.alta_usr,
	  todas_reint_ter.modi_fecha,
	  todas_reint_ter.modi_usr,
	  todas_reint_ter.baja_fecha,
	  todas_reint_ter.baja_usr,
	  pc.id_concepto,
	  pc.id_concepto,
	 	planc.cuenta,
	 	planc.numero,
	  	importe,
	  	id_punto_venta ,
	 	compro_tipo ,
	    compro_nro ,
	    cuit ,
	    compro_letra ,
	    compro_sucu ,
	 	planc.cuenta,
	 	planc.id_cuenta_maestro,
	 	planc.numero,
		planc.cuenta,
		planc.id_cuenta_maestro,
		id_aux
	   from
	(
	--prestacionales
	(select
	c.*,
	--rp.*,
	n.id_prestacion,
	(rp.importe * rp.cantidad) as importe,
		cast (rp.id_reintegro as character varying) || cast (rp.id_prestacion  as character varying)
	|| cast (rp.id_plan  as character varying)|| cast (rp.alta_fecha  as character varying)as id_aux
	
	from comprobante c inner join comprobante_orden_pago_ospim copo

	on c.id_punto_venta = copo.id_punto_venta
	and c.compro_tipo = copo.compro_tipo
	and c.compro_nro = copo.compro_nro
	and c.cuit = copo.cuit
	and c.compro_letra = copo.compro_letra
	and c.compro_sucu = copo.compro_sucu

	inner join orden_pago_ospim opo
	on copo.id_orden_pago_ospim = opo.id_orden_pago

	inner join orden_pago_ospim_lista_reintegros opol
	on opo.id_orden_pago = opol.id_orden_pago_ospim		

	inner join lista_reintegro_pago_detalle opor
	on opol.id_lista_reintegro_pago = opor.id_lista_reintegro_pago

	inner join reintegro r
	on r.id_reintegro = opor.id_reintegro 
	and r.tipo_reintegro = 'pre'

	inner join reintegro_prestacion rp
	on r.id_reintegro = rp.id_reintegro

	inner join plan_prestacion pp
	on 
	rp.id_prestacion = pp.id_prestacion and
	rp.id_plan = pp.id_plan

	inner join nomenclador n
	on pp.id_prestacion = n.id_prestacion
	-------------------------------------
	where c.compro_nro not like 'FAR-%'

	and ( rp.tercerizado = '1')
	and ((cast(opo.alta_fecha as date)>=$1 and cast(opo.alta_fecha as date)<=$2)
	or (cast(opo.baja_fecha as date)>=$1 and cast(opo.baja_fecha as date)<=$2))) 

	union all
	--protesis
	(select

	c.*,
	--rp.*,
	n.id_prestacion,
	(rp.importe * rp.cantidad) as importe,
	cast(rp.id  as character varying) as id_aux
	
	from comprobante c inner join comprobante_orden_pago_ospim copo

	on c.id_punto_venta = copo.id_punto_venta
	and c.compro_tipo = copo.compro_tipo
	and c.compro_nro = copo.compro_nro
	and c.cuit = copo.cuit
	and c.compro_letra = copo.compro_letra
	and c.compro_sucu = copo.compro_sucu

	inner join orden_pago_ospim opo
	on copo.id_orden_pago_ospim = opo.id_orden_pago

	inner join orden_pago_ospim_lista_reintegros opol
	on opo.id_orden_pago = opol.id_orden_pago_ospim

	inner join lista_reintegro_pago_detalle opor
	on opol.id_lista_reintegro_pago = opor.id_lista_reintegro_pago

	inner join reintegro r
	on r.id_reintegro = opor.id_reintegro 
	and r.tipo_reintegro = 'pro'

	inner join reintegro_prestacion_odo_protesis rp
	on r.id_reintegro = rp.id_reintegro

	inner join plan_prestacion pp
	on 
	rp.id_prestacion = pp.id_prestacion and
	rp.id_plan = pp.id_plan

	inner join nomenclador n
	on pp.id_prestacion = n.id_prestacion
	-------------------------------------
	where c.compro_nro not like 'FAR-%'
	and ( rp.tercerizado = '1')
	and ((cast(opo.alta_fecha as date)>=$1 and cast(opo.alta_fecha as date)<=$2)
	or (cast(opo.baja_fecha as date)>=$1 and cast(opo.baja_fecha as date)<=$2))) 

	union all
	--ortopedias y ortodoncias
	(select

	c.*,
	--rp.*,
	n.id_prestacion,
	dc.importe as importe, --cantidad siempre es 1
	cast(rp.id  as character varying) as id_aux
	
	from comprobante c inner join comprobante_orden_pago_ospim copo

	on c.id_punto_venta = copo.id_punto_venta
	and c.compro_tipo = copo.compro_tipo
	and c.compro_nro = copo.compro_nro
	and c.cuit = copo.cuit
	and c.compro_letra = copo.compro_letra
	and c.compro_sucu = copo.compro_sucu

	inner join orden_pago_ospim opo
	on copo.id_orden_pago_ospim = opo.id_orden_pago

	inner join orden_pago_ospim_lista_reintegros opol
	on opo.id_orden_pago = opol.id_orden_pago_ospim

	inner join lista_reintegro_pago_detalle opor
	on opol.id_lista_reintegro_pago = opor.id_lista_reintegro_pago

	inner join detalle_cuota dc
	on dc.id_cuota = opor.id_reintegro --en este caso el join se hace por detalle_cuenta

	inner join reintegro r
	on r.id_reintegro = dc.id_reintegro
	and r.tipo_reintegro = 'ort'

	inner join reintegro_prestacion_odo_ort rp
	on r.id_reintegro = rp.id_reintegro

	inner join plan_prestacion pp
	on 
	rp.id_prestacion = pp.id_prestacion and
	rp.id_plan = pp.id_plan

	inner join nomenclador n
	on pp.id_prestacion = n.id_prestacion
	-------------------------------------

	and ( rp.tercerizado = '1')
	and ((cast(opo.alta_fecha as date)>=$1 and cast(opo.alta_fecha as date)<=$2)
	or (cast(opo.baja_fecha as date)>=$1 and cast(opo.baja_fecha as date)<=$2)) 
	where c.compro_nro not like 'FAR-%')

	) todas_reint_ter , parametros_conceptos pc, concepto_maestro cm, conceptos c, plan_cuentas planc
	where pc.parametro = 'convenios_globales' 
	and  todas_reint_ter.alta_fecha >=  pc.valido_desde 
	and todas_reint_ter.alta_fecha <= pc.valido_hasta
	and pc.id_concepto = cm.id
	and cm.id = c.id_concepto_maestro
	and cast(c.valido_desde as date)  <= cast(todas_reint_ter.alta_fecha as date)
	and cast(c.valido_hasta as date)  >= cast(todas_reint_ter.alta_fecha as date)
	and c.id_plan_cuenta = planc.id_cuenta_maestro
	and cast(planc.valido_desde as date)  <= cast(todas_reint_ter.alta_fecha as date)
	and cast(planc.valido_hasta as date)  >= cast(todas_reint_ter.alta_fecha as date)
	
---- UNION!!! ----
union   -- UNION CON LA QUERY DE PRESTACIONES - LIQ 
---- UNION!!! ----
	select todo_liq_final.alta_fecha, todo_liq_final.alta_usr,
	  todo_liq_final.modi_fecha,
	  todo_liq_final.modi_usr,
	  todo_liq_final.baja_fecha,
	  todo_liq_final.baja_usr,
	  todo_liq_final.concepto_id,
	  c.id_concepto_maestro,
	  c.descripcion,
	  pc.numero,
	  todo_liq_final.importe,
	  todo_liq_final.id_punto_venta ,
	 todo_liq_final.compro_tipo ,
	    todo_liq_final.compro_nro ,
	    todo_liq_final.cuit ,
	    todo_liq_final.compro_letra ,
	    todo_liq_final.compro_sucu ,
	     pc.cuenta,
	     pc.id_cuenta_maestro,
	     pc2.numero,
	     pc2.cuenta,
	     pc2.id_cuenta_maestro,
	     orden as id_aux from (
	select  t.alta_fecha, t.alta_usr,   t.modi_fecha,  t.modi_usr,  t.baja_fecha,  t.baja_usr,   id_punto_venta ,   compro_tipo ,
	 compro_nro ,    cuit ,    compro_letra ,    compro_sucu ,    t.id_prestacion,amb_imp_honorarios as importe, nc.concepto_id, orden
	  from liquidaciones_tmp t
	  left outer join nomenclador_conceptos nc
	  on t.id_prestacion = nc.id_prestacion
	  and nc.valido_desde <= t.alta_fecha and nc.valido_hasta >= t.alta_fecha 
	  where amb_imp_honorarios <> 0
	  and nc.tipo_id = 1
	union all
	select  t.alta_fecha, t.alta_usr,   t.modi_fecha,  t.modi_usr,  t.baja_fecha,  t.baja_usr,   id_punto_venta ,   compro_tipo ,
	  compro_nro ,    cuit ,    compro_letra ,    compro_sucu ,    t.id_prestacion,amb_imp_gast as importe, nc.concepto_id, orden
	  from liquidaciones_tmp t
	  left outer join nomenclador_conceptos nc
	  on t.id_prestacion = nc.id_prestacion
	  and nc.valido_desde <= t.alta_fecha and nc.valido_hasta >= t.alta_fecha 
	  where amb_imp_gast <> 0
	  and nc.tipo_id = 2
	union all
	select  t.alta_fecha, t.alta_usr,   t.modi_fecha,  t.modi_usr,  t.baja_fecha,  t.baja_usr,   id_punto_venta ,   compro_tipo ,
	  compro_nro ,    cuit ,    compro_letra ,    compro_sucu ,    t.id_prestacion,amt_imp_hon as importe, nc.concepto_id, orden
	  from liquidaciones_tmp t
	  left outer join nomenclador_conceptos nc
	  on t.id_prestacion = nc.id_prestacion
	  and nc.valido_desde <= t.alta_fecha and nc.valido_hasta >= t.alta_fecha 
	  where amt_imp_hon <> 0
	  and nc.tipo_id = 3
	union all
	select  t.alta_fecha, t.alta_usr,   t.modi_fecha,  t.modi_usr,  t.baja_fecha,  t.baja_usr,    id_punto_venta ,   compro_tipo ,
	  compro_nro ,    cuit ,    compro_letra ,    compro_sucu ,    t.id_prestacion,amt_imp_gast as importe, nc.concepto_id, orden
	  from liquidaciones_tmp t
	  left outer join nomenclador_conceptos nc
	  on t.id_prestacion = nc.id_prestacion
	  and nc.valido_desde <= t.alta_fecha and nc.valido_hasta >= t.alta_fecha 
	  where amt_imp_gast <> 0 
	  and nc.tipo_id = 4
	 ) todo_liq_final
	 left outer join concepto_maestro cm
	 on todo_liq_final.concepto_id = cm.id
	 left outer join conceptos c
	 on cm.id = c.id_concepto_maestro
	 and cast(c.valido_desde as date)  <= cast(todo_liq_final.alta_fecha as date)
	and cast(c.valido_hasta as date)  >= cast(todo_liq_final.alta_fecha as date)
	 left outer join plan_cuentas pc
	 on c.id_plan_cuenta = pc.id_cuenta_maestro
	 and cast(pc.valido_desde as date)  <= cast(todo_liq_final.alta_fecha as date)
	 and cast(pc.valido_hasta as date)  >= cast(todo_liq_final.alta_fecha as date)
	 left outer join plan_cuentas pc2
	 on c.id_plan_cuenta_pasivo = pc2.id_cuenta_maestro
	 and cast(pc2.valido_desde as date)  <= cast(todo_liq_final.alta_fecha as date)
	 and cast(pc2.valido_hasta as date)  >= cast(todo_liq_final.alta_fecha as date);

END;
$BODY$
  LANGUAGE plpgsql VOLATILE

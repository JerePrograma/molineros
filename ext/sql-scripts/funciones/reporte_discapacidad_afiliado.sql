select * from reporte_discapacidad (null, null, '20120101' , '20121001', '', 100, 23586, '20216992661', 3, '', '', '1', '1', '', '')

drop type reporte_discapacidad_afiliado_type

drop FUNCTION reporte_discapacidad(p_fecha_desde timestamp without time zone, p_fecha_hasta timestamp without time zone, 
p_pediodo_desde timestamp without time zone, p_periodo_hasta timestamp without time zone,

p_codigo character varying, p_estado integer, p_nro_afiliado integer,
p_cuil character varying, p_inte integer, 
p_cuit character varying, p_sucu character varying, 
p_prestac character varying, 
p_liquidaciones character varying, 
p_diagnositco character varying,
p_ciex character varying

)

create type reporte_discapacidad_afiliado_type as 

(tipo_consumo varchar, id_liquidacion integer, fecha_prestacion timestamp without time zone,
apellido varchar, nombre varchar, docu_numero varchar, secciona varchar, cuit varchar, razon_soc varchar, codigo varchar,
descripcion varchar, presentacion varchar, laboratorio varchar, pieza varchar, cara varchar, importe_total numeric, nro_cuota smallint,
porcentaje_cuota smallint, cantidad numeric, importe numeric, ospim numeric, amtima numeric, receta numeric, porcentaje numeric,
localidad_prestador varchar, prov_prestador varchar, debitado_omint numeric, id_orden_pago integer, discapacitado varchar,
cta integer, periodo timestamp without time zone, cuil_titular varchar, inte integer , fecha timestamp without time zone, 
fecha_comprobante timestamp without time zone ,importe_comprobante numeric, tercerizado varchar, 
fecha_op timestamp without time zone, comprobante varchar, diagnostico varchar, ciex varchar);


drop FUNCTION reporte_discapacidad(p_fecha_desde timestamp without time zone, p_fecha_hasta timestamp without time zone, 
p_pediodo_desde timestamp without time zone, p_periodo_hasta timestamp without time zone,

p_codigo character varying, p_estado integer, p_nro_afiliado integer,
p_cuil character varying, p_inte integer, 
p_cuit character varying, p_sucu character varying, 
p_prestac character varying, 
p_liquidaciones character varying, 
p_diagnositco character varying,
p_ciex character varying

)

CREATE OR REPLACE FUNCTION reporte_discapacidad(

p_fecha_desde timestamp without time zone, p_fecha_hasta timestamp without time zone, 
p_periodo_desde timestamp without time zone, p_periodo_hasta timestamp without time zone,

p_codigo character varying, p_estado integer, p_nro_afiliado integer,
p_cuil character varying, p_inte integer, 
p_cuit character varying, p_sucu character varying, 
p_prestac character varying, 
p_liquidaciones character varying, 
p_diagnositco character varying,
p_ciex character varying

)
 
RETURNS SETOF reporte_discapacidad_afiliado_type AS
$BODY$

declare fecha_desde_ timestamp without time zone;
declare fecha_hasta_ timestamp without time zone;
declare periodo_desde_ timestamp without time zone;
declare periodo_hasta_ timestamp without time zone;


BEGIN

drop table if exists reporte_discapacidad_result;

fecha_desde_ = case when (p_fecha_desde is null) then '19000101' else p_fecha_desde end;
fecha_hasta_ = case when (p_fecha_hasta is null) then '99990101' else p_fecha_hasta end;
periodo_desde_ = case when (p_periodo_desde is null) then '19000101' else p_periodo_desde end;
periodo_hasta_ = case when (p_periodo_hasta is null) then '99990101' else p_periodo_hasta end;

create table reporte_discapacidad_result as

--prestaciones medicas
select cast('LIQUIDACION PRESTACIONAL' as character varying) as tipo_consumo,
 l.id_liquidacion,
 lp.fecha_prestacion as fecha_prestacion, a.apellido, a.nombre, a.docu_numero, s.descripcion as secciona,  p.cuit, p.descripcion as razon_soc,
n.codigo,n.descripcion, cast(null as character varying) as presentacion, 
cast(null as  character varying) as laboratorio ,
cast(null as  character varying) as pieza,
 cast(null as  character varying) as cara, 
 -1* (lp.importe * lp.cantidad) as importe_total, 
 cast (null as smallint) as nro_cuota, 
 cast(null as smallint) as porcentaje_cuota,
  lp.cantidad ,
   -1 * lp.importe, 
  cast(null as numeric) as ospim,
  cast(null as numeric) as amtima,
cast (null as numeric) as receta, 
cast(null as numeric) as porcentaje,
cast(null as varchar) as localidad_prestador, 
cast(null as varchar) as prov_prestador, 
case when l.debitado is not null and l.debitado<>0 then -1* (lp.importe * lp.cantidad) else 0 end as debitado_omint, os.id_orden_pago, a.discapacitado, (select distinct 2 from orden_pago_ospim_pagos opp
																	    where opp.id_orden_pago=os.id_orden_pago
																	    and (id_cta_bcria_cheque=2
																	    or id_cta_bcria_retencion=2
																	    or id_cta_bcria_transf_bcria=2)) as cta,
lp.periodo as periodo,
lp.cuil_titular as cuil_titular,
lp.inte as inte,
l.fecha as fecha,
l.fecha_emitido as fecha_comprobante,
l.importe as importe_comprobante,
l.tercerizado as tercerizado,
os.alta_fecha as fecha_op, 
cast(l.compro_a_debitar_tipo || '-' || l.compro_a_debitar_letra  || '-' || l.compro_a_debitar_numero || '-' ||  l.sucu as character varying) as comprobante

from afiliado a, liquidacion l, liquidacion_prestacion lp, nomenclador n, prestador p, seccional s, orden_pago_ospim_liquidaciones op, orden_pago_ospim os
where  
a.cuil_titular=lp.cuil_titular
and a.inte=lp.inte
and a.id_seccional=s.id_seccional
and a.discapacitado  = '1'
and lp.id_liquidacion=l.id_liquidacion
and lp.id_prestacion=n.id_prestacion
and l.id_prestador=p.id_prestador
and cast(os.alta_fecha as date)< fecha_desde_
and cast(os.baja_fecha as date)<= fecha_hasta_
and cast(os.baja_fecha as date)>= fecha_desde_
and cast(lp.periodo as date)<= periodo_hasta_
and cast(lp.periodo as date)>= periodo_desde_

and l.baja_fecha is null
and lp.baja_fecha is null
and l.id_liquidacion=op.id_liquidacion
and os.id_orden_pago=op.id_orden_pago_ospim
and (p_cuil is null or p_cuil = '' or (p_cuil is not null and p_cuil = a.cuil_titular and p_inte = a.inte))
and (p_cuit is null or p_cuit = '' or (p_cuit is not null and p_cuit = p.cuit))
and (p_codigo is null or p_codigo = '' or (p_codigo is not null and p_codigo = n.codigo))
and (p_estado is null or p_estado = 100 or 
	(p_estado is not null and (p_estado = lp.motivo_alta_discapacidad or (p_estado in (1, 4) and lp.motivo_alta_discapacidad = 5))))
and p_liquidaciones = '1'

union all

select 'LIQUIDACION PRESTACIONAL' as tipo_consumo,
 l.id_liquidacion,
 lp.fecha_prestacion as fecha_prestacion, a.apellido, a.nombre, a.docu_numero, s.descripcion as secciona, p.cuit, p.descripcion as razon_soc,
n.codigo,n.descripcion, null as presentacion, null as laboratorio ,null as pieza, null as cara, (lp.importe * lp.cantidad) as importe_total, cast (null as smallint) as nro_cuota, cast(null as smallint) as porcentaje_cuota, lp.cantidad, lp.importe, cast(null as numeric) as ospim,cast(null as numeric) as amtima,
cast (null as numeric) as receta, cast(null as numeric) as porcentaje,cast(null as varchar) as localidad_prestador, cast(null as varchar) as prov_prestador, 
case when l.debitado is not null and l.debitado<>0 then (lp.importe * lp.cantidad) else 0 end as debitado_omint, os.id_orden_pago, a.discapacitado,  (select distinct 2 from orden_pago_ospim_pagos opp
																	    where opp.id_orden_pago=os.id_orden_pago
																	    and (id_cta_bcria_cheque=2
																	    or id_cta_bcria_retencion=2
																	    or id_cta_bcria_transf_bcria=2
																	    or id_cta_bcria_debito_crio=2)) as cta, 
																	    lp.periodo as periodo,
lp.cuil_titular as cuil_titular,
lp.inte as inte,
l.fecha as fecha,
l.fecha_emitido as fecha_comprobante,
l.importe as importe_comprobante,
l.tercerizado as tercerizado,
os.alta_fecha as fecha_op, 
cast(l.compro_a_debitar_tipo || '-' || l.compro_a_debitar_letra  || '-' || l.compro_a_debitar_numero || '-' ||  l.sucu as character varying) as comprobante
																	    
																	    
from afiliado a, liquidacion l, liquidacion_prestacion lp, nomenclador n, prestador p, seccional s, orden_pago_ospim_liquidaciones op, orden_pago_ospim os
where 
a.cuil_titular=lp.cuil_titular
and a.inte=lp.inte
and a.id_seccional=s.id_seccional 
and a.discapacitado  = '1'
and lp.id_liquidacion=l.id_liquidacion
and lp.id_prestacion=n.id_prestacion
and l.id_prestador=p.id_prestador
and cast(os.alta_fecha as date)<= fecha_hasta_
and cast(os.alta_fecha as date)>= fecha_desde_

and cast(lp.periodo as date)<= periodo_hasta_
and cast(lp.periodo as date)>= periodo_desde_

and (os.baja_fecha is null or (os.baja_fecha is not null and date_trunc('month',os.baja_fecha) > date_trunc('month',os.alta_fecha)))

and l.baja_fecha is null 
and lp.baja_fecha is null
and l.id_liquidacion=op.id_liquidacion
and os.id_orden_pago=op.id_orden_pago_ospim
and (p_cuil is null or p_cuil = '' or (p_cuil is not null and p_cuil = a.cuil_titular and p_inte = a.inte))
and (p_cuit is null or p_cuit = '' or (p_cuit is not null and p_cuit = p.cuit))
and (p_codigo is null or p_codigo = '' or (p_codigo is not null and p_codigo = n.codigo))
and (p_estado is null or p_estado = 100 or 
	(p_estado is not null and (p_estado = lp.motivo_alta_discapacidad or (p_estado in (1, 4) and lp.motivo_alta_discapacidad = 5))))
and p_liquidaciones = '1'


union all


select 'REINTEGRO PRESTACIONAL', r.id_reintegro_user, rp.fecha_prestacion as fecha_prestacion, a.apellido, a.nombre, a.docu_numero,  s.descripcion as secciona, 
case when rp.cuit is null or rp.cuit = '' then rp.cuit_entidad else rp.cuit end, rp.descripcion, rp.codigo, n.descripcion,  cast (null as character varying), cast (null as character varying), cast (null as character varying), 
cast (null as character varying), (rp.importe * rp.cantidad) as importe_total, cast(null as smallint), cast(null as smallint),  rp.cantidad, rp.importe, 
cast(null as numeric), cast(null as numeric), cast(null as numeric), cast(null as numeric),null, null,
 case when rp.tercerizado='1' then rp.importe*rp.cantidad else 0 end as debitado_omint, os.id_orden_pago, a.discapacitado,
  (select distinct 2 from orden_pago_ospim_pagos opp
    where opp.id_orden_pago=os.id_orden_pago
    and (id_cta_bcria_cheque=2
    or id_cta_bcria_retencion=2
    or id_cta_bcria_transf_bcria=2
    or id_cta_bcria_debito_crio=2)) as cta, rp.periodo as periodo,
r.cuil_titular as cuil_titular,
r.inte as inte,
r.fecha as fecha,
rp.fecha_comprobante as fecha_comprobante,
rp.importe_comprobante as importe_comprobante,
rp.tercerizado as tercerizado,
os.alta_fecha as fecha_op, 
cast (rp.compro_a_debitar_tipo || '-' || rp.compro_a_debitar_numero  as character varying) as comprobante
    
    
from afiliado a, reintegro r, reintegro_prestacion rp, nomenclador n, seccional s, lista_reintegro_pago lr, 
	lista_reintegro_pago_detalle lrp, orden_pago_ospim_lista_reintegros op, orden_pago_ospim os
where 
r.id_reintegro=rp.id_reintegro
and r.tipo_reintegro='pre' 
and r.cuil_titular=a.cuil_titular
and r.inte=a.inte
and a.discapacitado  = '1'
and r.id_seccional=s.id_seccional
and cast(os.alta_fecha as date)<= fecha_hasta_
and cast(os.alta_fecha as date)>= fecha_desde_
and (os.baja_fecha is null or (os.baja_fecha is not null and date_trunc('month',os.alta_fecha) < date_trunc('month',os.baja_fecha) ))

and cast(rp.periodo as date)<= periodo_hasta_
and cast(rp.periodo as date)>= periodo_desde_

and rp.id_prestacion=n.id_prestacion
and n.id_tipo_nomenclador<>1
and r.id_reintegro=lrp.id_reintegro
and lrp.id_lista_reintegro_pago=lr.id
and lrp.id_lista_reintegro_pago=op.id_lista_reintegro_pago
and op.id_orden_pago_ospim=os.id_orden_pago
and p_prestac = '1'

and (p_cuil is null or p_cuil = '' or (p_cuil is not null and p_cuil = r.cuil_titular and p_inte = r.inte))
and (p_cuit is null or p_cuit = '' or (p_cuit is not null and (p_cuit = rp.cuit or p_cuit = rp.cuit_entidad)))
and (p_codigo is null or p_codigo = '' or (p_codigo is not null and p_codigo = rp.codigo))
and (p_estado is null or p_estado = 100 or 
	(p_estado is not null and (p_estado = rp.motivo_alta_discapacidad or (p_estado in (1, 4) and rp.motivo_alta_discapacidad = 5))))


union all

select 'REINTEGRO PRESTACIONAL', r.id_reintegro_user, rp.fecha_prestacion as fecha_prestacion, a.apellido, a.nombre, a.docu_numero,  s.descripcion as secciona, 
case when rp.cuit is null or rp.cuit = '' then rp.cuit_entidad else rp.cuit end, rp.descripcion, rp.codigo, n.descripcion,  cast (null as character varying), cast (null as character varying), cast (null as character varying), 
cast (null as character varying), -1* (rp.importe * rp.cantidad), cast(null as smallint), cast(null as smallint),  rp.cantidad, rp.importe, 
cast(null as numeric), cast(null as numeric), cast(null as numeric), cast(null as numeric),null, null,
 case when rp.tercerizado='1' then rp.importe*rp.cantidad else 0 end as debitado_omint, os.id_orden_pago, a.discapacitado,
  (select distinct 2 from orden_pago_ospim_pagos opp
    where opp.id_orden_pago=os.id_orden_pago
    and (id_cta_bcria_cheque=2
    or id_cta_bcria_retencion=2
    or id_cta_bcria_transf_bcria=2
    or id_cta_bcria_debito_crio=2))as cta, rp.periodo as periodo,
r.cuil_titular as cuil_titular,
r.inte as inte,
r.fecha as fecha,
rp.fecha_comprobante as fecha_comprobante,
rp.importe_comprobante as importe_comprobante,
rp.tercerizado as tercerizado,
os.alta_fecha as fecha_op, 
cast (rp.compro_a_debitar_tipo || '-' || rp.compro_a_debitar_numero as character varying) as comprobante
    
    
from afiliado a, reintegro r, reintegro_prestacion rp, nomenclador n, seccional s, lista_reintegro_pago lr, 
	lista_reintegro_pago_detalle lrp, orden_pago_ospim_lista_reintegros op, orden_pago_ospim os
where 
r.id_reintegro=rp.id_reintegro
and r.tipo_reintegro='pre' 
and r.cuil_titular=a.cuil_titular
and r.inte=a.inte
and a.discapacitado  = '1'
and r.id_seccional=s.id_seccional
and cast(os.alta_fecha as date)< fecha_desde_
and cast(os.baja_fecha as date)<= fecha_hasta_
and cast(os.baja_fecha as date)>= fecha_desde_

and cast(rp.periodo as date)<= periodo_hasta_
and cast(rp.periodo as date)>= periodo_desde_

and rp.id_prestacion=n.id_prestacion
and n.id_tipo_nomenclador<>1
and r.id_reintegro=lrp.id_reintegro
and lrp.id_lista_reintegro_pago=lr.id
and lrp.id_lista_reintegro_pago=op.id_lista_reintegro_pago
and op.id_orden_pago_ospim=os.id_orden_pago
and p_prestac = '1'
and (p_cuil is null or p_cuil = '' or (p_cuil is not null and p_cuil = r.cuil_titular and p_inte = r.inte))
and (p_cuit is null or p_cuit = '' or (p_cuit is not null and (p_cuit = rp.cuit or p_cuit = rp.cuit_entidad)))
and (p_codigo is null or p_codigo = '' or (p_codigo is not null and p_codigo = rp.codigo))
and (p_estado is null or p_estado = 100 or 
	(p_estado is not null and (p_estado = rp.motivo_alta_discapacidad or (p_estado in (1, 4) and rp.motivo_alta_discapacidad = 5))));


return query
select rd.* , dd.diagnostico, dd.cie_diez from reporte_discapacidad_result rd left outer join 
detalle_discapacidad dd 
on rd.cuil_titular = dd.cuil_titular
and rd.inte = dd.inte
where 
(p_diagnositco is null or p_diagnositco = '' or (p_diagnositco is not null and upper(dd.diagnostico) like  '%' || upper(p_diagnositco) || '%'))
and 
(p_ciex is null or p_ciex = '' or (p_ciex is not null and upper(dd.cie_diez) like  '%' || upper(p_ciex) || '%')); 

END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;

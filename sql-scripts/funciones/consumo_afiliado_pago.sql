DROP FUNCTION consumo_afiliado_pago(timestamp without time zone, timestamp without time zone, character varying, character varying, integer, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying);

drop type consumo_afiliado_pago;

create type consumo_afiliado_pago as 

(tipo_consumo varchar, id_liquidacion integer, fecha_prestacion timestamp without time zone,
apellido varchar, nombre varchar, docu_numero varchar, secciona varchar, cuit varchar, razon_soc varchar, codigo varchar,
descripcion varchar, presentacion varchar, laboratorio varchar, pieza varchar, cara varchar, importe_total numeric, nro_cuota smallint,
porcentaje_cuota smallint, cantidad numeric, importe numeric, ospim numeric, amtima numeric, receta numeric, porcentaje numeric,
localidad_prestador varchar, prov_prestador varchar, debitado_omint numeric, id_orden_pago integer, discapacitado varchar,
cta integer, periodo timestamp without time zone);

create type consumo_afiliado_pago as 

(tipo_consumo varchar, id_liquidacion integer, fecha_prestacion timestamp without time zone,
apellido varchar, nombre varchar, docu_numero varchar, secciona varchar, cuit varchar, razon_soc varchar, codigo varchar,
descripcion varchar, presentacion varchar, laboratorio varchar, pieza varchar, cara varchar, importe_total numeric, nro_cuota smallint,
porcentaje_cuota smallint, cantidad numeric, importe numeric, ospim numeric, amtima numeric, receta numeric, porcentaje numeric,
localidad_prestador varchar, prov_prestador varchar, debitado_omint numeric, id_orden_pago integer, discapacitado varchar,
cta integer, periodo timestamp without time zone);

-- Function: consumo_afiliado_pago(timestamp without time zone, timestamp without time zone, character varying, character varying, integer, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying)

CREATE OR REPLACE FUNCTION consumo_afiliado_pago(p_fecha_desde timestamp without time zone, p_fecha_hasta timestamp without time zone, p_codigo character varying, p_cuil character varying, p_inte integer, p_cuit character varying, p_sucu character varying, p_prestac character varying, p_ortop character varying, p_protesis character varying, p_odontogeneral character varying, p_liquidaciones character varying, p_discapacidad character varying, p_farmacia character varying)
  RETURNS SETOF consumo_afiliado_pago AS
$BODY$

declare fecha_desde_ timestamp without time zone;
declare fecha_hasta_ timestamp without time zone;

BEGIN

drop table if exists consumo_afiliado_result;

RAISE INFO 'CUIL: %',p_cuil;
RAISE INFO 'CUIT: %',p_cuit;
RAISE INFO 'DESDE: %',p_fecha_desde;
RAISE INFO 'HASTA: %',p_fecha_hasta;

fecha_desde_ = case when (p_fecha_desde is null) then '19000101' else p_fecha_desde end;
fecha_hasta_ = case when (p_fecha_hasta is null) then '99990101' else p_fecha_hasta end;

create table consumo_afiliado_result as

--prestaciones medicas
select cast('LIQUIDACION PRESTACIONAL' as character varying) as tipo_consumo,
 l.id_liquidacion,
 lp.fecha_prestacion as fecha_prestacion, a.apellido, a.nombre, a.docu_numero, s.descripcion as secciona,  
p.cuit, 
p.descripcion as razon_soc,
n.codigo,
n.descripcion, 
cast(null as character varying) as presentacion, 
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
lp.periodo as periodo        
from afiliado a, liquidacion l, liquidacion_prestacion lp, nomenclador n, prestador p, seccional s,  orden_pago_ospim_liquidaciones op, orden_pago_ospim os
where  
a.cuil_titular=lp.cuil_titular
and a.inte=lp.inte
and a.id_seccional=s.id_seccional 
and lp.id_liquidacion=l.id_liquidacion
and lp.id_prestacion=n.id_prestacion
and l.id_prestador=p.id_prestador
and cast(os.alta_fecha as date)< fecha_desde_
and cast(os.baja_fecha as date)<= fecha_hasta_
and cast(os.baja_fecha as date)>= fecha_desde_
and l.baja_fecha is null 
and lp.baja_fecha is null
and l.id_liquidacion=op.id_liquidacion
and os.id_orden_pago=op.id_orden_pago_ospim
and lp.tercerizado <> '1'
and (p_cuil is null or p_cuil = '' or (p_cuil is not null and p_cuil = a.cuil_titular and p_inte = a.inte))
and (p_cuit is null or p_cuit = '' or (p_cuit is not null and p_cuit = p.cuit))
and (p_codigo is null or p_codigo = '' or (p_codigo is not null and p_codigo = n.codigo))
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
																	    or id_cta_bcria_debito_crio=2)) as cta, lp.periodo as periodo
from afiliado a, liquidacion l, liquidacion_prestacion lp, nomenclador n, prestador p, seccional s, orden_pago_ospim_liquidaciones op, orden_pago_ospim os
where 
a.cuil_titular=lp.cuil_titular
and a.inte=lp.inte
and a.id_seccional=s.id_seccional 
and lp.id_liquidacion=l.id_liquidacion
and lp.id_prestacion=n.id_prestacion
and l.id_prestador=p.id_prestador
and cast(os.alta_fecha as date)<= fecha_hasta_
and cast(os.alta_fecha as date)>= fecha_desde_
and (os.baja_fecha is null or (os.baja_fecha is not null and date_trunc('month',os.baja_fecha) > date_trunc('month',os.alta_fecha)))
and l.baja_fecha is null 
and lp.baja_fecha is null
and l.id_liquidacion=op.id_liquidacion
and os.id_orden_pago=op.id_orden_pago_ospim
and lp.tercerizado <> '1'
and (p_cuil is null or p_cuil = '' or (p_cuil is not null and p_cuil = a.cuil_titular and p_inte = a.inte))
and (p_cuit is null or p_cuit = '' or (p_cuit is not null and p_cuit = p.cuit))
and (p_codigo is null or p_codigo = '' or (p_codigo is not null and p_codigo = n.codigo))
and p_liquidaciones = '1'

union all
select 'REINTEGRO FARMACIA', r.id_reintegro, rp.fecha as fecha_prestacion, a.apellido, a.nombre, a.docu_numero,  s.descripcion as secciona, 
'' as cuit, 
'' as razon_social, 
cast(rp.troquel as varchar) as codigo, 
n.nombre as medicamento,  
n.presentacion as presentacion, 
n.laboratorio as laboratorio, 
cast (null as character varying) as pieza, 
cast (null as character varying) as cara, 
rp.monto_ospim+rp.monto_amtima as importe_total, 
cast(null as smallint) as nro_cuota, 
cast(0 as smallint)  as porc_cuota,  
rp.cantidad, 
(rp.precio_al_publico) as importe, 
rp.monto_ospim , 
rp.monto_amtima, 
rp.nro_receta, 
cast(rp.total_cobertura as smallint) as porc_cuota,
null as localidad_prestador, 
null as provincia_prestador,
0,
os.id_orden_pago, a.discapacitado,
  (select distinct 2 from orden_pago_ospim_pagos opp
    where opp.id_orden_pago=os.id_orden_pago
    and (id_cta_bcria_cheque=2
    or id_cta_bcria_retencion=2
    or id_cta_bcria_transf_bcria=2
    or id_cta_bcria_debito_crio=2)) as cta, r.periodo as periodo
from afiliado a, reintegro_farmacia r, medicamento_reintegro_farmacia rp, medicamentos n, seccional s, lista_reintegro_farmacia_pago lr, 
	lista_reintegro_farmacia_pago_detalle lrp, orden_pago_ospim_lista_reintegros_farmacia op, orden_pago_ospim os
where 
r.id_reintegro=rp.id_reintegro
and r.cuil_titular=a.cuil_titular
and r.inte=a.inte
and r.id_seccional=s.id_seccional
and cast(os.alta_fecha as date)<= fecha_hasta_
and cast(os.alta_fecha as date)>= fecha_desde_
and (os.baja_fecha is null or (os.baja_fecha is not null and date_trunc('month',os.alta_fecha) < date_trunc('month',os.baja_fecha) ))
and rp.id_medicamento=n.id_medicamento
and r.id_reintegro=lrp.id_reintegro
and lrp.id_lista_reintegro_pago=lr.id
and lrp.id_lista_reintegro_pago=op.id_lista_reintegro_pago
and op.id_orden_pago_ospim=os.id_orden_pago
and p_farmacia = '1'
and (p_cuil is null or p_cuil = '' or (p_cuil is not null and p_cuil = r.cuil_titular and p_inte = r.inte))
union all
select 'REINTEGRO FARMACIA', r.id_reintegro, rp.fecha as fecha_prestacion, a.apellido, a.nombre, a.docu_numero,  s.descripcion as secciona, 
'' as cuit, 
'' as razon_social, 
cast(rp.troquel as varchar) as codigo, 
n.nombre as medicamento,  
n.presentacion as presentacion, 
n.laboratorio as laboratorio, 
cast (null as character varying) as pieza, 
cast (null as character varying) as cara, 
rp.monto_ospim+rp.monto_amtima, 
cast(null as smallint) as nro_cuota, 
cast(0 as smallint) as porc_cuota,  
rp.cantidad, 
(rp.precio_al_publico) as importe_total, 
rp.monto_ospim , 
rp.monto_amtima, 
rp.nro_receta, 
cast(rp.total_cobertura as smallint) as porc_cuota,
null as localidad_prestador, 
null as provincia_prestador,
0,
os.id_orden_pago, a.discapacitado,
  (select distinct 2 from orden_pago_ospim_pagos opp
    where opp.id_orden_pago=os.id_orden_pago
    and (id_cta_bcria_cheque=2
    or id_cta_bcria_retencion=2
    or id_cta_bcria_transf_bcria=2
    or id_cta_bcria_debito_crio=2)) as cta, r.periodo as periodo
from afiliado a, reintegro_farmacia r, medicamento_reintegro_farmacia rp, medicamentos n, seccional s, lista_reintegro_farmacia_pago lr, 
	lista_reintegro_farmacia_pago_detalle lrp, orden_pago_ospim_lista_reintegros_farmacia op, orden_pago_ospim os
where 
r.id_reintegro=rp.id_reintegro
and r.cuil_titular=a.cuil_titular
and r.inte=a.inte
and r.id_seccional=s.id_seccional
and cast(os.alta_fecha as date)< fecha_desde_
and cast(os.baja_fecha as date)<= fecha_hasta_
and cast(os.baja_fecha as date)>= fecha_desde_
and rp.id_medicamento=n.id_medicamento
and r.id_reintegro=lrp.id_reintegro
and lrp.id_lista_reintegro_pago=lr.id
and lrp.id_lista_reintegro_pago=op.id_lista_reintegro_pago
and op.id_orden_pago_ospim=os.id_orden_pago
and p_farmacia = '1'
and (p_cuil is null or p_cuil = '' or (p_cuil is not null and p_cuil = r.cuil_titular and p_inte = r.inte))

union all

select 'REINTEGRO PRESTACIONAL', r.id_reintegro, rp.fecha_prestacion as fecha_prestacion, a.apellido, a.nombre, a.docu_numero,  s.descripcion as secciona, 
case when rp.cuit is null or rp.cuit = '' then rp.cuit_entidad else rp.cuit end, rp.descripcion, rp.codigo, n.descripcion,  cast (null as character varying), cast (null as character varying), cast (null as character varying), 
cast (null as character varying), (rp.importe * rp.cantidad) as importe_total, cast(null as smallint), cast(null as smallint),  rp.cantidad, rp.importe, 
cast(null as numeric), cast(null as numeric), cast(null as numeric), cast(null as numeric),null, null,
 case when rp.tercerizado='1' then rp.importe*rp.cantidad else 0 end as debitado_omint, os.id_orden_pago, a.discapacitado,
  (select distinct 2 from orden_pago_ospim_pagos opp
    where opp.id_orden_pago=os.id_orden_pago
    and (id_cta_bcria_cheque=2
    or id_cta_bcria_retencion=2
    or id_cta_bcria_transf_bcria=2
    or id_cta_bcria_debito_crio=2)) as cta, rp.periodo as periodo
from afiliado a, reintegro r, reintegro_prestacion rp, nomenclador n, seccional s, lista_reintegro_pago lr, 
	lista_reintegro_pago_detalle lrp, orden_pago_ospim_lista_reintegros op, orden_pago_ospim os
where 
r.id_reintegro=rp.id_reintegro
and r.tipo_reintegro='pre' 
and r.cuil_titular=a.cuil_titular
and r.inte=a.inte
and r.id_seccional=s.id_seccional
and cast(os.alta_fecha as date)<= fecha_hasta_
and cast(os.alta_fecha as date)>= fecha_desde_
and (os.baja_fecha is null or (os.baja_fecha is not null and date_trunc('month',os.alta_fecha) < date_trunc('month',os.baja_fecha) ))
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
    or id_cta_bcria_debito_crio=2))as cta, rp.periodo as periodo
from afiliado a, reintegro r, reintegro_prestacion rp, nomenclador n, seccional s, lista_reintegro_pago lr, 
	lista_reintegro_pago_detalle lrp, orden_pago_ospim_lista_reintegros op, orden_pago_ospim os
where 
r.id_reintegro=rp.id_reintegro
and r.tipo_reintegro='pre' 
and r.cuil_titular=a.cuil_titular
and r.inte=a.inte
and r.id_seccional=s.id_seccional
and cast(os.alta_fecha as date)< fecha_desde_
and cast(os.baja_fecha as date)<= fecha_hasta_
and cast(os.baja_fecha as date)>= fecha_desde_
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

union all

select 'ODONTOLOGIA GENERAL', r.id_reintegro_user, rp.fecha_prestacion as fecha_prestacion, a.apellido,a.nombre, a.docu_numero, s.descripcion as secciona, 
case when rp.cuit is null or rp.cuit = '' then rp.cuit_entidad else rp.cuit end, rp.descripcion, rp.codigo, n.descripcion, cast (null as character varying), cast (null as character varying), cast (null as character varying), 
cast (null as character varying), (rp.importe * rp.cantidad), cast(null as smallint), cast(null as smallint), rp.cantidad, rp.importe, 
cast(null as numeric), cast(null as numeric), cast(null as numeric), cast(null as numeric), null, null,
 case when rp.tercerizado='1' then rp.importe*rp.cantidad else 0 end as debitado_omint,  os.id_orden_pago,  a.discapacitado,
  (select distinct 2 from orden_pago_ospim_pagos opp
    where opp.id_orden_pago=os.id_orden_pago
    and (id_cta_bcria_cheque=2
    or id_cta_bcria_retencion=2
    or id_cta_bcria_transf_bcria=2
    or id_cta_bcria_debito_crio=2)), cast (null as timestamp without time zone)
from afiliado a, reintegro r, reintegro_prestacion rp, nomenclador n, seccional s,  lista_reintegro_pago lr, lista_reintegro_pago_detalle lrp, 
	orden_pago_ospim_lista_reintegros op, orden_pago_ospim os
where
r.id_reintegro=rp.id_reintegro
and r.tipo_reintegro='pre' 
and r.cuil_titular=a.cuil_titular
and r.inte=a.inte
and r.id_seccional=s.id_seccional
and cast(os.alta_fecha as date)<= fecha_hasta_
and cast(os.alta_fecha as date)>= fecha_desde_
and rp.id_prestacion=n.id_prestacion
and n.id_tipo_nomenclador=1
and r.id_reintegro=lrp.id_reintegro
and lrp.id_lista_reintegro_pago=lr.id
and lrp.id_lista_reintegro_pago=op.id_lista_reintegro_pago
and op.id_orden_pago_ospim=os.id_orden_pago
and (os.baja_fecha is null or (os.baja_fecha is not null and date_trunc('month',os.alta_fecha) < date_trunc('month',os.baja_fecha) ))

and (p_cuil is null or p_cuil = '' or (p_cuil is not null and p_cuil = r.cuil_titular and p_inte = r.inte))
and (p_cuit is null or p_cuit = '' or (p_cuit is not null and (p_cuit = rp.cuit or p_cuit = rp.cuit_entidad)))
and (p_codigo is null or p_codigo = '' or (p_codigo is not null and p_codigo = rp.codigo))
and p_odontogeneral = '1'

union all

select 'ODONTOLOGIA GENERAL', r.id_reintegro_user, rp.fecha_prestacion as fecha_prestacion, a.apellido,a.nombre, a.docu_numero, s.descripcion as secciona, 
case when rp.cuit is null or rp.cuit = '' then rp.cuit_entidad else rp.cuit end, rp.descripcion, rp.codigo, n.descripcion, cast (null as character varying), cast (null as character varying), cast (null as character varying), 
cast (null as character varying), -1 * (rp.importe * rp.cantidad), cast(null as smallint), cast(null as smallint), rp.cantidad, rp.importe, 
cast(null as numeric), cast(null as numeric), cast(null as numeric), cast(null as numeric), null, null,
 case when rp.tercerizado='1' then rp.importe*rp.cantidad else 0 end as debitado_omint,  os.id_orden_pago,  a.discapacitado,
  (select distinct 2 from orden_pago_ospim_pagos opp
    where opp.id_orden_pago=os.id_orden_pago
    and (id_cta_bcria_cheque=2
    or id_cta_bcria_retencion=2
    or id_cta_bcria_transf_bcria=2
    or id_cta_bcria_debito_crio=2)), cast (null as timestamp without time zone)
from afiliado a, reintegro r, reintegro_prestacion rp, nomenclador n, seccional s,  lista_reintegro_pago lr, 
lista_reintegro_pago_detalle lrp, orden_pago_ospim_lista_reintegros op, orden_pago_ospim os
where
r.id_reintegro=rp.id_reintegro
and r.tipo_reintegro='pre' 
and r.cuil_titular=a.cuil_titular
and r.inte=a.inte
and r.id_seccional=s.id_seccional
and cast(os.alta_fecha as date)< fecha_desde_
and cast(os.baja_fecha as date)<= fecha_hasta_
and cast(os.baja_fecha as date)>= fecha_desde_
and rp.id_prestacion=n.id_prestacion
and n.id_tipo_nomenclador=1
and r.id_reintegro=lrp.id_reintegro
and lrp.id_lista_reintegro_pago=lr.id
and lrp.id_lista_reintegro_pago=op.id_lista_reintegro_pago
and op.id_orden_pago_ospim=os.id_orden_pago

and (p_cuil is null or (p_cuil is not null and p_cuil = r.cuil_titular and p_inte = r.inte))
and (p_cuit is null or (p_cuit is not null and (p_cuit = rp.cuit or p_cuit = rp.cuit_entidad)))
and (p_codigo is null or (p_codigo is not null and p_codigo = rp.codigo))
and p_odontogeneral = '1'

union all --protesis

select 'REINTEGRO PROTESIS', r.id_reintegro_user, rop.fecha_prestacion as fecha_prestacion, a.apellido, a.nombre, a.docu_numero, s.descripcion as secciona, 
'', '',rop.codigo,  n.descripcion, cast (null as character varying), cast (null as character varying),  rop.pieza, rop.cara, 
(rop.importe * rop.cantidad), cast(null as smallint), cast(null as smallint),  rop.cantidad, rop.importe, cast(null as numeric),  cast(null as numeric), 
 cast(null as numeric), cast(null as numeric), null,null,
case when rop.tercerizado='1' then rop.importe*rop.cantidad else 0 end as debitado_omint, os.id_orden_pago, a.discapacitado,
 (select distinct 2 from orden_pago_ospim_pagos opp
    where opp.id_orden_pago=os.id_orden_pago
    and (id_cta_bcria_cheque=2
    or id_cta_bcria_retencion=2
    or id_cta_bcria_transf_bcria=2
    or id_cta_bcria_debito_crio=2)), cast (null as timestamp without time zone)
from  afiliado a, reintegro r, reintegro_prestacion_odo_protesis rop, nomenclador n, prestador_externo pe, seccional s, 
lista_reintegro_pago_detalle lrp,orden_pago_ospim_lista_reintegros op, orden_pago_ospim os
where
r.id_reintegro=rop.id_reintegro
and r.cuil_titular=a.cuil_titular
and r.inte=a.inte
and r.id_seccional=s.id_seccional
and cast(os.alta_fecha as date)<= fecha_hasta_
and cast(os.alta_fecha as date)>= fecha_desde_
and rop.id_prestacion=n.id_prestacion
and r.id_reintegro=lrp.id_reintegro
and lrp.id_lista_reintegro_pago=op.id_lista_reintegro_pago
and op.id_orden_pago_ospim=os.id_orden_pago
and op.baja_fecha is null
and (os.baja_fecha is null or (os.baja_fecha is not null and date_trunc('month',os.alta_fecha) < date_trunc('month',os.baja_fecha) ))

and (p_cuil is null or p_cuil = '' or (p_cuil is not null and p_cuil = r.cuil_titular and p_inte = r.inte))
and (p_cuit is null or p_cuit = '' or (p_cuit is not null and (p_cuit = rop.cuit or p_cuit = pe.cuit)))
and (p_codigo is null or p_codigo = '' or (p_codigo is not null and p_codigo = rop.codigo))
and p_protesis = '1'

group by r.tipo_reintegro, r.id_reintegro_user, rop.fecha_prestacion, a.apellido,a.nombre, a.docu_numero, s.descripcion, 
rop.codigo, n.descripcion, rop.pieza, rop.cara, rop.cantidad, rop.importe, rop.tercerizado, os.id_orden_pago,a.discapacitado

union all

select 'REINTEGRO PROTESIS', r.id_reintegro_user, rop.fecha_prestacion as fecha_prestacion, a.apellido, a.nombre, a.docu_numero, s.descripcion as secciona, 
'', '',rop.codigo,  n.descripcion, cast (null as character varying), cast (null as character varying),  rop.pieza, rop.cara, 
-1 * (rop.importe * rop.cantidad), cast(null as smallint), cast(null as smallint), rop.cantidad, rop.importe, cast(null as numeric),  cast(null as numeric), 
 cast(null as numeric), cast(null as numeric), null,null,
case when rop.tercerizado='1' then rop.importe*rop.cantidad else 0 end as debitado_omint, os.id_orden_pago, a.discapacitado,
 (select distinct 2 from orden_pago_ospim_pagos opp
    where opp.id_orden_pago=os.id_orden_pago
    and (id_cta_bcria_cheque=2
    or id_cta_bcria_retencion=2
    or id_cta_bcria_transf_bcria=2
    or id_cta_bcria_debito_crio=2)), cast (null as timestamp without time zone) 
from  afiliado a, reintegro r, reintegro_prestacion_odo_protesis rop, nomenclador n, prestador_externo pe, seccional s, 
lista_reintegro_pago_detalle lrp,orden_pago_ospim_lista_reintegros op, orden_pago_ospim os
where 
r.id_reintegro=rop.id_reintegro
and r.cuil_titular=a.cuil_titular
and r.inte=a.inte
and r.id_seccional=s.id_seccional
and cast(os.alta_fecha as date)< fecha_desde_
and cast(os.baja_fecha as date)<= fecha_hasta_
and cast(os.baja_fecha as date)>= fecha_desde_
and rop.id_prestacion=n.id_prestacion
and rop.id_prestador_externo=pe.id_prestador_externo
and r.id_reintegro=lrp.id_reintegro
and lrp.id_lista_reintegro_pago=op.id_lista_reintegro_pago
and op.id_orden_pago_ospim=os.id_orden_pago
and op.baja_fecha is null

and (p_cuil is null or p_cuil = '' or (p_cuil is not null and p_cuil = r.cuil_titular and p_inte = r.inte))
and (p_cuit is null or p_cuit = '' or (p_cuit is not null and (p_cuit = rop.cuit or p_cuit = pe.cuit)))
and (p_codigo is null or p_codigo = '' or (p_codigo is not null and p_codigo = rop.codigo))
and p_protesis = '1'

group by  r.tipo_reintegro, r.id_reintegro_user, rop.fecha_prestacion, a.apellido,a.nombre, a.docu_numero, s.descripcion, 
rop.codigo, n.descripcion, rop.pieza, rop.cara, rop.cantidad, rop.importe, rop.tercerizado, os.id_orden_pago,a.discapacitado

union all--ortopedia


select 'REINTEGRO ORTOPEDIA/ORTODONCIA', de.id_cuota,
poo.fecha_prestacion as fecha_prestacion, a.apellido, a.nombre, a.docu_numero, s.descripcion,  '', '',
poo.codigo, n.descripcion, null, null, poo.pieza, poo.cara, de.importe, de.nro_cuota , de.porcentaje as porc_cuota, '1', poo.importe, 
 cast(null as numeric),  cast(null as numeric),  cast(null as numeric), cast(null as numeric),null, null,
case when poo.tercerizado='1' then de.importe else 0 end as debitado_omint, os.id_orden_pago, a.discapacitado,
 (select distinct 2 from orden_pago_ospim_pagos opp
    where opp.id_orden_pago=os.id_orden_pago
    and (id_cta_bcria_cheque=2
    or id_cta_bcria_retencion=2
    or id_cta_bcria_transf_bcria=2
    or id_cta_bcria_debito_crio=2)), cast (null as timestamp without time zone)
from reintegro r, reintegro_prestacion_odo_ort poo, detalle_cuota de, afiliado a,nomenclador n, prestador_externo pe, seccional s, 
lista_reintegro_pago_detalle lrp, orden_pago_ospim_lista_reintegros op, orden_pago_ospim os 
where 
r.id_reintegro=poo.id_reintegro
and r.id_reintegro=de.id_reintegro
and r.cuil_titular=a.cuil_titular
and r.inte=a.inte
and r.id_seccional=s.id_seccional
and cast(os.alta_fecha as date)<= fecha_hasta_
and cast(os.alta_fecha as date)>= fecha_desde_
and poo.id_prestacion=n.id_prestacion
and poo.id_prestador_externo = pe.id_prestador_externo
and de.id_cuota=lrp.id_reintegro
and lrp.id_lista_reintegro_pago=op.id_lista_reintegro_pago
and op.id_orden_pago_ospim=os.id_orden_pago
and (os.baja_fecha is null or (os.baja_fecha is not null and date_trunc('month',os.alta_fecha) < date_trunc('month',os.baja_fecha) ))

and (p_cuil is null or p_cuil = '' or (p_cuil is not null and p_cuil = r.cuil_titular and p_inte = r.inte))
and (p_cuit is null or p_cuit = '' or (p_cuit is not null and (p_cuit = poo.cuit or p_cuit = pe.cuit)))
and (p_codigo is null or p_codigo = '' or (p_codigo is not null and p_codigo = poo.codigo))
and p_ortop = '1'

group by  de.id_cuota, poo.fecha_prestacion, a.apellido, a.nombre, a.docu_numero, s.descripcion, 
poo.codigo, n.descripcion, poo.pieza, poo.cara,poo.importe, de.nro_cuota, de.porcentaje, de.importe, poo.tercerizado, os.id_orden_pago,a.discapacitado

union all


select 'REINTEGRO ORTOPEDIA/ORTODONCIA', de.id_cuota,
poo.fecha_prestacion as fecha_prestacion, a.apellido, a.nombre, a.docu_numero, s.descripcion,  '','',
poo.codigo, n.descripcion, null, null, poo.pieza, poo.cara, de.importe, de.nro_cuota , de.porcentaje as porc_cuota, '1',-1* poo.importe, 
 cast(null as numeric),  cast(null as numeric),  cast(null as numeric), cast(null as numeric),null, null,
case when poo.tercerizado='1' then de.importe else 0 end as debitado_omint, os.id_orden_pago, a.discapacitado,
 (select distinct 2 from orden_pago_ospim_pagos opp
    where opp.id_orden_pago=os.id_orden_pago
    and (id_cta_bcria_cheque=2
    or id_cta_bcria_retencion=2
    or id_cta_bcria_transf_bcria=2
    or id_cta_bcria_debito_crio=2)), cast (null as timestamp without time zone)
from reintegro r, reintegro_prestacion_odo_ort poo, detalle_cuota de, afiliado a,nomenclador n, prestador_externo pe, seccional s, 
lista_reintegro_pago_detalle lrp, orden_pago_ospim_lista_reintegros op, orden_pago_ospim os 
where 
r.id_reintegro=poo.id_reintegro
and r.id_reintegro=de.id_reintegro
and r.cuil_titular=a.cuil_titular
and r.inte=a.inte
and r.id_seccional=s.id_seccional
and cast(os.alta_fecha as date)< fecha_desde_
and cast(os.baja_fecha as date)<= fecha_hasta_
and cast(os.baja_fecha as date)>= fecha_desde_
and poo.id_prestacion=n.id_prestacion
and poo.id_prestador_externo = pe.id_prestador_externo
and de.id_cuota=lrp.id_reintegro
and lrp.id_lista_reintegro_pago=op.id_lista_reintegro_pago
and op.id_orden_pago_ospim=os.id_orden_pago

and (p_cuil is null or p_cuil = '' or (p_cuil is not null and p_cuil = r.cuil_titular and p_inte = r.inte))
and (p_cuit is null or p_cuit = '' or (p_cuit is not null and (p_cuit = poo.cuit or p_cuit = pe.cuit)))
and (p_codigo is null or p_codigo = '' or (p_codigo is not null and p_codigo = poo.codigo))
and p_ortop = '1'

group by  de.id_cuota, poo.fecha_prestacion, a.apellido, a.nombre, a.docu_numero, s.descripcion, 
poo.codigo, n.descripcion, poo.pieza, poo.cara,poo.importe, de.nro_cuota, de.porcentaje, de.importe, poo.tercerizado, os.id_orden_pago,a.discapacitado;


--create temp table consumo_afiliado_result as 
return query
select * from consumo_afiliado_result
order by tipo_consumo, fecha_prestacion; 
END;
$BODY$
  LANGUAGE plpgsql VOLATILE


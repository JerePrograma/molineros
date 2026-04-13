CREATE OR REPLACE FUNCTION consumo_afiliado_pago(documento character varying, cuit_p character varying, vigen_desde_p date, vigen_hasta_p date)
  RETURNS SETOF consumo_afiliado_pago AS
$BODY$
BEGIN

drop table if exists consumo_afiliado_result;

RAISE INFO 'DOCUMENTO: %',documento;
RAISE INFO 'CUIT: %',cuit_p;
RAISE INFO 'DESDE: %',vigen_desde_p;
RAISE INFO 'HASTA: %',vigen_hasta_p;



create table consumo_afiliado_result as
--prestaciones medicas
select cast('LIQUIDACION PRESTACIONAL' as character varying) as tipo_consumo,
 l.id_liquidacion,
 lp.fecha_prestacion as fecha_prestacion, a.apellido, a.nombre, a.docu_numero, s.descripcion as seccional,  p.cuit, p.descripcion as razon_soc,
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
case when l.debitado is not null and l.debitado<>0 then -1* (lp.importe * lp.cantidad) else 0 end as debitado_omint, os.id_orden_pago, a.discapacitado, (select 2 from orden_pago_ospim_pagos opp
																	    where opp.id_orden_pago=os.id_orden_pago
																	    and (id_cta_bcria_cheque=2
																	    or id_cta_bcria_retencion=2
																	    or id_cta_bcria_transf_bcria=2)) as cta,cast(null as timestamp without time zone)
from afiliado a, liquidacion l, liquidacion_prestacion lp, nomenclador n, prestador p, seccional s,  orden_pago_ospim_liquidaciones op, orden_pago_ospim os
where a.cuil_titular=lp.cuil_titular
and a.inte=lp.inte
and a.id_seccional=s.id_seccional 
and lp.id_liquidacion=l.id_liquidacion
and lp.id_prestacion=n.id_prestacion
and l.id_prestador=p.id_prestador
and cast(os.alta_fecha as date)< cast($3 as date)
and cast(os.baja_fecha as date)<= cast($4 as date)
and cast(os.baja_fecha as date)>= cast($3 as date)
and l.baja_fecha is null 
and lp.baja_fecha is null
and l.id_liquidacion=op.id_liquidacion
and os.id_orden_pago=op.id_orden_pago_ospim
and lp.tercerizado <> '1'
union all
select 'LIQUIDACION PRESTACIONAL' as tipo_consumo,
 l.id_liquidacion,
 lp.fecha_prestacion as fecha_prestacion, a.apellido, a.nombre, a.docu_numero, s.descripcion as seccional, p.cuit, p.descripcion as razon_soc,
n.codigo,n.descripcion, null as presentacion, null as laboratorio ,null as pieza, null as cara, (lp.importe * lp.cantidad) as importe_total, cast (null as smallint) as nro_cuota, cast(null as smallint) as porcentaje_cuota, lp.cantidad, lp.importe, cast(null as numeric) as ospim,cast(null as numeric) as amtima,
cast (null as numeric) as receta, cast(null as numeric) as porcentaje,cast(null as varchar) as localidad_prestador, cast(null as varchar) as prov_prestador, 
case when l.debitado is not null and l.debitado<>0 then (lp.importe * lp.cantidad) else 0 end as debitado_omint, os.id_orden_pago, a.discapacitado,  (select 2 from orden_pago_ospim_pagos opp
																	    where opp.id_orden_pago=os.id_orden_pago
																	    and (id_cta_bcria_cheque=2
																	    or id_cta_bcria_retencion=2
																	    or id_cta_bcria_transf_bcria=2
																	    or id_cta_bcria_debito_crio=2)) as cta,cast(null as timestamp without time zone)
from afiliado a, liquidacion l, liquidacion_prestacion lp, nomenclador n, prestador p, seccional s, orden_pago_ospim_liquidaciones op, orden_pago_ospim os
where a.cuil_titular=lp.cuil_titular
and a.inte=lp.inte
and a.id_seccional=s.id_seccional 
and lp.id_liquidacion=l.id_liquidacion
and lp.id_prestacion=n.id_prestacion
and l.id_prestador=p.id_prestador
and cast(os.alta_fecha as date)<= cast($4 as date)
and cast(os.alta_fecha as date)>= cast($3 as date)
and (os.baja_fecha is null or (os.baja_fecha is not null and date_trunc('month',os.baja_fecha) > date_trunc('month',os.alta_fecha)))
and l.baja_fecha is null 
and lp.baja_fecha is null
and l.id_liquidacion=op.id_liquidacion
and os.id_orden_pago=op.id_orden_pago_ospim
and lp.tercerizado <> '1'
union all
select 'REINTEGRO PRESTACIONAL', r.id_reintegro_user, rp.fecha_prestacion as fecha_prestacion, a.apellido, a.nombre, a.docu_numero,  s.descripcion as seccional, 
rp.cuit,  rp.descripcion, rp.codigo, n.descripcion,  cast (null as character varying), cast (null as character varying), cast (null as character varying), 
cast (null as character varying), (rp.importe * rp.cantidad) as importe_total, cast(null as smallint), cast(null as smallint),  rp.cantidad, rp.importe, 
cast(null as numeric), cast(null as numeric), cast(null as numeric), cast(null as numeric),null, null,
 case when rp.tercerizado='1' then rp.importe*rp.cantidad else 0 end as debitado_omint, os.id_orden_pago, a.discapacitado,
  (select 2 from orden_pago_ospim_pagos opp
    where opp.id_orden_pago=os.id_orden_pago
    and (id_cta_bcria_cheque=2
    or id_cta_bcria_retencion=2
    or id_cta_bcria_transf_bcria=2
    or id_cta_bcria_debito_crio=2)) as cta,cast(null as timestamp without time zone)
from afiliado a, reintegro r, reintegro_prestacion rp, nomenclador n, seccional s, lista_reintegro_pago lr, 
	lista_reintegro_pago_detalle lrp, orden_pago_ospim_lista_reintegros op, orden_pago_ospim os
where r.id_reintegro=rp.id_reintegro
and r.tipo_reintegro='pre' 
and r.cuil_titular=a.cuil_titular
and r.inte=a.inte
and r.id_seccional=s.id_seccional
and cast(os.alta_fecha as date)<= cast($4 as date)
and cast(os.alta_fecha as date)>= cast($3 as date)
and (os.baja_fecha is null or (os.baja_fecha is not null and date_trunc('month',os.alta_fecha) < date_trunc('month',os.baja_fecha) ))
and rp.id_prestacion=n.id_prestacion
and n.id_tipo_nomenclador<>1
and r.id_reintegro=lrp.id_reintegro
and lrp.id_lista_reintegro_pago=lr.id
and lrp.id_lista_reintegro_pago=op.id_lista_reintegro_pago
and op.id_orden_pago_ospim=os.id_orden_pago
union all
select 'REINTEGRO PRESTACIONAL', r.id_reintegro_user, rp.fecha_prestacion as fecha_prestacion, a.apellido, a.nombre, a.docu_numero,  s.descripcion as seccional, 
rp.cuit,  rp.descripcion, rp.codigo, n.descripcion,  cast (null as character varying), cast (null as character varying), cast (null as character varying), 
cast (null as character varying), -1* (rp.importe * rp.cantidad), cast(null as smallint), cast(null as smallint),  rp.cantidad, rp.importe, 
cast(null as numeric), cast(null as numeric), cast(null as numeric), cast(null as numeric),null, null,
 case when rp.tercerizado='1' then rp.importe*rp.cantidad else 0 end as debitado_omint, os.id_orden_pago, a.discapacitado,
  (select 2 from orden_pago_ospim_pagos opp
    where opp.id_orden_pago=os.id_orden_pago
    and (id_cta_bcria_cheque=2
    or id_cta_bcria_retencion=2
    or id_cta_bcria_transf_bcria=2
    or id_cta_bcria_debito_crio=2))as cta,cast(null as timestamp without time zone)
from afiliado a, reintegro r, reintegro_prestacion rp, nomenclador n, seccional s, lista_reintegro_pago lr, 
	lista_reintegro_pago_detalle lrp, orden_pago_ospim_lista_reintegros op, orden_pago_ospim os
where r.id_reintegro=rp.id_reintegro
and r.tipo_reintegro='pre' 
and r.cuil_titular=a.cuil_titular
and r.inte=a.inte
and r.id_seccional=s.id_seccional
and cast(os.alta_fecha as date)< cast($3 as date)
and cast(os.baja_fecha as date)<= cast($4 as date)
and cast(os.baja_fecha as date)>= cast($3 as date)
and rp.id_prestacion=n.id_prestacion
and n.id_tipo_nomenclador<>1
and r.id_reintegro=lrp.id_reintegro
and lrp.id_lista_reintegro_pago=lr.id
and lrp.id_lista_reintegro_pago=op.id_lista_reintegro_pago
and op.id_orden_pago_ospim=os.id_orden_pago
union all
select 'ODONTOLOGIA GENERAL', r.id_reintegro_user, rp.fecha_prestacion as fecha_prestacion, a.apellido,a.nombre, a.docu_numero, s.descripcion as seccional, 
rp.cuit, rp.descripcion,rp.codigo, n.descripcion, cast (null as character varying), cast (null as character varying), cast (null as character varying), 
cast (null as character varying), (rp.importe * rp.cantidad), cast(null as smallint), cast(null as smallint), rp.cantidad, rp.importe, 
cast(null as numeric), cast(null as numeric), cast(null as numeric), cast(null as numeric), null, null,
 case when rp.tercerizado='1' then rp.importe*rp.cantidad else 0 end as debitado_omint,  os.id_orden_pago,  a.discapacitado,
  (select 2 from orden_pago_ospim_pagos opp
    where opp.id_orden_pago=os.id_orden_pago
    and (id_cta_bcria_cheque=2
    or id_cta_bcria_retencion=2
    or id_cta_bcria_transf_bcria=2
    or id_cta_bcria_debito_crio=2)),cast(null as timestamp without time zone)
from afiliado a, reintegro r, reintegro_prestacion rp, nomenclador n, seccional s,  lista_reintegro_pago lr, lista_reintegro_pago_detalle lrp, 
	orden_pago_ospim_lista_reintegros op, orden_pago_ospim os
where r.id_reintegro=rp.id_reintegro
and r.tipo_reintegro='pre' 
and r.cuil_titular=a.cuil_titular
and r.inte=a.inte
and r.id_seccional=s.id_seccional
and cast(os.alta_fecha as date)<= cast($4 as date)
and cast(os.alta_fecha as date)>= cast($3 as date)
and rp.id_prestacion=n.id_prestacion
and n.id_tipo_nomenclador=1
and r.id_reintegro=lrp.id_reintegro
and lrp.id_lista_reintegro_pago=lr.id
and lrp.id_lista_reintegro_pago=op.id_lista_reintegro_pago
and op.id_orden_pago_ospim=os.id_orden_pago
and (os.baja_fecha is null or (os.baja_fecha is not null and date_trunc('month',os.alta_fecha) < date_trunc('month',os.baja_fecha) ))
union all
select 'ODONTOLOGIA GENERAL', r.id_reintegro_user, rp.fecha_prestacion as fecha_prestacion, a.apellido,a.nombre, a.docu_numero, s.descripcion as seccional, 
rp.cuit, rp.descripcion,rp.codigo, n.descripcion, cast (null as character varying), cast (null as character varying), cast (null as character varying), 
cast (null as character varying), -1 * (rp.importe * rp.cantidad), cast(null as smallint), cast(null as smallint), rp.cantidad, rp.importe, 
cast(null as numeric), cast(null as numeric), cast(null as numeric), cast(null as numeric), null, null,
 case when rp.tercerizado='1' then rp.importe*rp.cantidad else 0 end as debitado_omint,  os.id_orden_pago,  a.discapacitado,
  (select 2 from orden_pago_ospim_pagos opp
    where opp.id_orden_pago=os.id_orden_pago
    and (id_cta_bcria_cheque=2
    or id_cta_bcria_retencion=2
    or id_cta_bcria_transf_bcria=2
    or id_cta_bcria_debito_crio=2)),cast(null as timestamp without time zone)
from afiliado a, reintegro r, reintegro_prestacion rp, nomenclador n, seccional s,  lista_reintegro_pago lr, 
lista_reintegro_pago_detalle lrp, orden_pago_ospim_lista_reintegros op, orden_pago_ospim os
where r.id_reintegro=rp.id_reintegro
and r.tipo_reintegro='pre' 
and r.cuil_titular=a.cuil_titular
and r.inte=a.inte
and r.id_seccional=s.id_seccional
and cast(os.alta_fecha as date)< cast($3 as date)
and cast(os.baja_fecha as date)<= cast($4 as date)
and cast(os.baja_fecha as date)>= cast($3 as date)
and rp.id_prestacion=n.id_prestacion
and n.id_tipo_nomenclador=1
and r.id_reintegro=lrp.id_reintegro
and lrp.id_lista_reintegro_pago=lr.id
and lrp.id_lista_reintegro_pago=op.id_lista_reintegro_pago
and op.id_orden_pago_ospim=os.id_orden_pago
union all --protesis
select 'REINTEGRO PROTESIS', r.id_reintegro_user, rop.fecha_prestacion as fecha_prestacion, a.apellido, a.nombre, a.docu_numero, s.descripcion as seccional, 
'', '',rop.codigo,  n.descripcion, cast (null as character varying), cast (null as character varying),  rop.pieza, rop.cara, 
(rop.importe * rop.cantidad), cast(null as smallint), cast(null as smallint),  rop.cantidad, rop.importe, cast(null as numeric),  cast(null as numeric), 
 cast(null as numeric), cast(null as numeric), null,null,
case when rop.tercerizado='1' then rop.importe*rop.cantidad else 0 end as debitado_omint, os.id_orden_pago, a.discapacitado,
 (select 2 from orden_pago_ospim_pagos opp
    where opp.id_orden_pago=os.id_orden_pago
    and (id_cta_bcria_cheque=2
    or id_cta_bcria_retencion=2
    or id_cta_bcria_transf_bcria=2
    or id_cta_bcria_debito_crio=2)),cast(null as timestamp without time zone)
from  afiliado a, reintegro r, reintegro_prestacion_odo_protesis rop,nomenclador n, seccional s, 
lista_reintegro_pago_detalle lrp,orden_pago_ospim_lista_reintegros op, orden_pago_ospim os
where r.id_reintegro=rop.id_reintegro
and r.cuil_titular=a.cuil_titular
and r.inte=a.inte
and r.id_seccional=s.id_seccional
and cast(os.alta_fecha as date)<= cast($4 as date)
and cast(os.alta_fecha as date)>= cast($3 as date)
and rop.id_prestacion=n.id_prestacion
and r.id_reintegro=lrp.id_reintegro
and lrp.id_lista_reintegro_pago=op.id_lista_reintegro_pago
and op.id_orden_pago_ospim=os.id_orden_pago
and op.baja_fecha is null
and (os.baja_fecha is null or (os.baja_fecha is not null and date_trunc('month',os.alta_fecha) < date_trunc('month',os.baja_fecha) ))
group by  r.tipo_reintegro, r.id_reintegro_user, rop.fecha_prestacion, a.apellido,a.nombre, a.docu_numero, s.descripcion, 
rop.codigo, n.descripcion, rop.pieza, rop.cara, rop.cantidad, rop.importe, rop.tercerizado, os.id_orden_pago,a.discapacitado
union all
select 'REINTEGRO PROTESIS', r.id_reintegro_user, rop.fecha_prestacion as fecha_prestacion, a.apellido, a.nombre, a.docu_numero, s.descripcion as seccional, 
'', '',rop.codigo,  n.descripcion, cast (null as character varying), cast (null as character varying),  rop.pieza, rop.cara, 
-1 * (rop.importe * rop.cantidad), cast(null as smallint), cast(null as smallint),  rop.cantidad, rop.importe, cast(null as numeric),  cast(null as numeric), 
 cast(null as numeric), cast(null as numeric), null,null,
case when rop.tercerizado='1' then rop.importe*rop.cantidad else 0 end as debitado_omint, os.id_orden_pago, a.discapacitado,
 (select 2 from orden_pago_ospim_pagos opp
    where opp.id_orden_pago=os.id_orden_pago
    and (id_cta_bcria_cheque=2
    or id_cta_bcria_retencion=2
    or id_cta_bcria_transf_bcria=2
    or id_cta_bcria_debito_crio=2)),cast(null as timestamp without time zone)
from  afiliado a, reintegro r, reintegro_prestacion_odo_protesis rop,nomenclador n, seccional s, 
lista_reintegro_pago_detalle lrp,orden_pago_ospim_lista_reintegros op, orden_pago_ospim os
where r.id_reintegro=rop.id_reintegro
and r.cuil_titular=a.cuil_titular
and r.inte=a.inte
and r.id_seccional=s.id_seccional
and cast(os.alta_fecha as date)< cast($3 as date)
and cast(os.baja_fecha as date)<= cast($4 as date)
and cast(os.baja_fecha as date)>= cast($3 as date)
and rop.id_prestacion=n.id_prestacion
and r.id_reintegro=lrp.id_reintegro
and lrp.id_lista_reintegro_pago=op.id_lista_reintegro_pago
and op.id_orden_pago_ospim=os.id_orden_pago
and op.baja_fecha is null
group by  r.tipo_reintegro, r.id_reintegro_user, rop.fecha_prestacion, a.apellido,a.nombre, a.docu_numero, s.descripcion, 
rop.codigo, n.descripcion, rop.pieza, rop.cara, rop.cantidad, rop.importe, rop.tercerizado, os.id_orden_pago,a.discapacitado
union all--ortopedia
select 'REINTEGRO ORTOPEDIA/ORTODONCIA', de.id_cuota,
poo.fecha_prestacion as fecha_prestacion, a.apellido, a.nombre, a.docu_numero, s.descripcion,  '', '',
poo.codigo, n.descripcion, null, null, poo.pieza, poo.cara, de.importe, de.nro_cuota , de.porcentaje as porc_cuota, '1', poo.importe, 
 cast(null as numeric),  cast(null as numeric),  cast(null as numeric), cast(null as numeric),null, null,
case when poo.tercerizado='1' then de.importe else 0 end as debitado_omint, os.id_orden_pago, a.discapacitado,
 (select 2 from orden_pago_ospim_pagos opp
    where opp.id_orden_pago=os.id_orden_pago
    and (id_cta_bcria_cheque=2
    or id_cta_bcria_retencion=2
    or id_cta_bcria_transf_bcria=2
    or id_cta_bcria_debito_crio=2)),cast(null as timestamp without time zone)
from reintegro r, reintegro_prestacion_odo_ort poo, detalle_cuota de, afiliado a,nomenclador n, seccional s, 
lista_reintegro_pago_detalle lrp, orden_pago_ospim_lista_reintegros op, orden_pago_ospim os 
where r.id_reintegro=poo.id_reintegro
and r.id_reintegro=de.id_reintegro
and r.cuil_titular=a.cuil_titular
and r.inte=a.inte
and r.id_seccional=s.id_seccional
and cast(os.alta_fecha as date)<= cast($4 as date)
and cast(os.alta_fecha as date)>= cast($3 as date)
and poo.id_prestacion=n.id_prestacion
and de.id_cuota=lrp.id_reintegro
and lrp.id_lista_reintegro_pago=op.id_lista_reintegro_pago
and op.id_orden_pago_ospim=os.id_orden_pago
and (os.baja_fecha is null or (os.baja_fecha is not null and date_trunc('month',os.alta_fecha) < date_trunc('month',os.baja_fecha) ))
group by  de.id_cuota, poo.fecha_prestacion, a.apellido, a.nombre, a.docu_numero, s.descripcion, 
poo.codigo, n.descripcion, poo.pieza, poo.cara,poo.importe, de.nro_cuota, de.porcentaje, de.importe, poo.tercerizado, os.id_orden_pago,a.discapacitado
union all
select 'REINTEGRO ORTOPEDIA/ORTODONCIA', de.id_cuota,
poo.fecha_prestacion as fecha_prestacion, a.apellido, a.nombre, a.docu_numero, s.descripcion,  '','',
poo.codigo, n.descripcion, null, null, poo.pieza, poo.cara, de.importe, de.nro_cuota , de.porcentaje as porc_cuota, '1',-1* poo.importe, 
 cast(null as numeric),  cast(null as numeric),  cast(null as numeric), cast(null as numeric),null, null,
case when poo.tercerizado='1' then de.importe else 0 end as debitado_omint, os.id_orden_pago, a.discapacitado,
 (select 2 from orden_pago_ospim_pagos opp
    where opp.id_orden_pago=os.id_orden_pago
    and (id_cta_bcria_cheque=2
    or id_cta_bcria_retencion=2
    or id_cta_bcria_transf_bcria=2
    or id_cta_bcria_debito_crio=2)),cast(null as timestamp without time zone)
from reintegro r, reintegro_prestacion_odo_ort poo, detalle_cuota de, afiliado a,nomenclador n, seccional s, 
lista_reintegro_pago_detalle lrp, orden_pago_ospim_lista_reintegros op, orden_pago_ospim os 
where r.id_reintegro=poo.id_reintegro
and r.id_reintegro=de.id_reintegro
and r.cuil_titular=a.cuil_titular
and r.inte=a.inte
and r.id_seccional=s.id_seccional
and cast(os.alta_fecha as date)< cast($3 as date)
and cast(os.baja_fecha as date)<= cast($4 as date)
and cast(os.baja_fecha as date)>= cast($3 as date)
and poo.id_prestacion=n.id_prestacion
and de.id_cuota=lrp.id_reintegro
and lrp.id_lista_reintegro_pago=op.id_lista_reintegro_pago
and op.id_orden_pago_ospim=os.id_orden_pago
group by  de.id_cuota, poo.fecha_prestacion, a.apellido, a.nombre, a.docu_numero, s.descripcion, 
poo.codigo, n.descripcion, poo.pieza, poo.cara,poo.importe, de.nro_cuota, de.porcentaje, de.importe, poo.tercerizado, os.id_orden_pago,a.discapacitado;

--create temp table consumo_afiliado_result as 
return query
select * from consumo_afiliado_result; 
END;
$BODY$
  LANGUAGE plpgsql VOLATILE

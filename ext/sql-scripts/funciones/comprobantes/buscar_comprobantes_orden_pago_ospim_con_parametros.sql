drop type reporte_op_result cascade;
create type reporte_op_result as (c__fecha_emision timestamp without time zone, c__fecha_recepcion timestamp without time zone,
c__importe_comprobante numeric, c__nro varchar, c__tipo varchar, c__id_punto_venta smallint, c__cuit character(11), c__alta_fecha timestamp without time zone, c__alta_usr varchar, c__modi_fecha timestamp without time zone, c__modi_usr character varying, c__baja_fecha timestamp without time zone, c__baja_usr varchar, c__compro_letra varchar, c__compro_sucu integer, c__cuit_acreedor varchar, c__sucu_acreedor varchar, c__seccional integer, c__observaciones varchar, c__vto timestamp without time zone, c__periodo_prestacion date, c__debito_para_egreso boolean, op__id_orden_pago integer, e__razon_soc character varying) ;

 CREATE OR REPLACE FUNCTION buscar_comprobantes_orden_pago_ospim_por_fecha(IN p_date_ini date, IN p_date_fin date, cuit_p varchar, sucur_p varchar, id_prestador_p integer, compro_tipo_v varchar, compro_letra_v varchar, compro_sucur_v integer, compro_nro_v varchar)
  RETURNS setof reporte_op_result AS
$BODY$
declare cuit_v varchar;
BEGIN
drop table if exists aux_op;


create temp table aux_op as
select  
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
  e.razon_soc
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
  e.razon_soc
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
where opop.id_punto_venta_antic is not null
and (cast(opo.alta_fecha  as date)>=$1 and cast(opo.alta_fecha as date)<=$2) 
or (cast(opo.baja_fecha  as date)>=$1 and cast(opo.baja_fecha as date)<=$2);

if cuit_p is null THEN 
   cuit_v=cuit from prestador where id_prestador=id_prestador_p;
else   
   cuit_v=cuit_p;
END IF;

RAISE INFO 'CUIT: %',cuit_v;

return query
select fecha_emision,
  fecha_recepcion,
  total,
  compro_nro,
  compro_tipo,
  id_punto_venta,
  cuit,
  alta_fecha,
  alta_usr,
  modi_fecha,
  modi_usr,
  baja_fecha,
  baja_usr,
  compro_letra,
  compro_sucu,
  cuit_acreedor ,
  sucu_acreedor,
  seccional, 
  observaciones,
  vto,
  periodo_prestacion,
  debito_para_egreso,
  id_orden_pago,
  razon_soc
from aux_op 
where (cuit_v is null or (cuit_v is not null and cuit_acreedor=cuit_v)) 
and (sucur_p is null or (sucur_p is not null and sucu_acreedor=sucur_p))
and (compro_tipo_v is null or (compro_tipo_v is not null and compro_tipo=compro_tipo_v))
and (compro_letra_v is null or (compro_letra_v is not null and compro_letra=compro_letra_v))
and (compro_sucur_v is null or (compro_sucur_v is not null and compro_sucu=compro_sucur_v))
and (compro_nro_v  is null or (compro_nro_v  is not null and compro_nro=compro_nro_v));

END; 
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
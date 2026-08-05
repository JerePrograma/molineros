DROP FUNCTION buscar_comprobantes_amtima_anticipos_a_rendir
(IN p_cuit_acreedor character varying, IN p_sucu_acreedor character varying, IN id_seccional integer);
CREATE OR REPLACE FUNCTION buscar_comprobantes_amtima_anticipos_a_rendir
(IN p_cuit_acreedor character varying, IN p_sucu_acreedor character varying, IN id_seccional integer)
  RETURNS TABLE(
  fecha_emision timestamp without time zone, 
  fecha_recepcion timestamp without time zone, 
  importe_comprobante numeric,
  nro character varying, 
  tipo character varying, 
  id_punto_venta smallint, 
  cuit character(11),
  alta_fecha timestamp without time zone, 
  alta_usr character varying, 
  modi_fecha timestamp without time zone,
  modi_usr character varying, 
  baja_fecha timestamp without time zone,
  baja_usr character varying, 
  compro_letra character varying, 
  compro_sucu integer,
     cuit_acreedor character(11),
  sucu_acreedor character(11),
  seccional integer,
  observaciones character varying(250),
  vto timestamp without time zone,
  periodo_prestacion date,
  debito_para_egreso boolean,
  anulado_fecha  timestamp without time zone,
  anulado_usr character varying,
  opOrigen integer,
  fechaOPOrigen date) AS
$BODY$



select     c.fecha_emision,
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
  c.anulado_fecha,
  c.anulado_usr,
  opo.id_orden_pago,
  cast(opo.alta_fecha as date)
from  comprobante_amtima c 
--que este asociado a una op
inner join comprobante_orden_pago_amtima copo
on c.id_punto_venta =  copo.id_punto_venta
and c.compro_tipo =copo.compro_tipo
and c.compro_letra  =copo.compro_letra
and c.compro_sucu =copo.compro_sucu
and c.compro_nro =copo.compro_nro
and c.cuit = copo.cuit
inner join orden_pago_amtima opo
on copo.id_orden_pago_amtima = opo.id_orden_pago
--pero que no este asociado a un pago
-- o que este asociado pero a una pago anulado
where c.compro_tipo = 'ANT'
   and c.id_punto_venta = 1
   and opo.baja_fecha is null
   and not exists (select 1 from orden_pago_amtima_pagos opop 
			inner join orden_pago_amtima opo_pago
			on opop.id_orden_pago = opo_pago.id_orden_pago
			where opop.id_punto_venta_antic =  c.id_punto_venta
			and opop.compro_tipo_antic =c.compro_tipo
			and opop.compro_letra_antic  =c.compro_letra
			and opop.compro_sucu_antic =c.compro_sucu
			and opop.compro_nro_antic =c.compro_nro
			and opop.cuit_antic = c.cuit
			and opo_pago.baja_fecha is null)
   and ($1 = c.cuit_acreedor)
   and ($2 = c.sucu_acreedor)
   and ($3 is null or $3 = c.seccional);
   
 
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;



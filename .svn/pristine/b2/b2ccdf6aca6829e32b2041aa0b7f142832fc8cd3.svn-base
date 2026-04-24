DROP FUNCTION buscar_comprobantes_impagos(IN p_pto_vta numeric, IN p_tipo character varying, IN p_nro character varying, 
IN p_cuit character varying, IN p_letra character varying, IN p_sucu integer, IN p_fecha_emision date, IN p_fecha_recepcion date,
IN p_periodo date, IN p_cuit_acreedor character varying, IN p_sucu_acreedor character varying, IN id_seccional integer);

CREATE OR REPLACE FUNCTION buscar_comprobantes_impagos(IN p_pto_vta numeric, IN p_tipo character varying, IN p_nro character varying, 
IN p_cuit character varying, IN p_letra character varying, IN p_sucu integer, IN p_fecha_emision date, IN p_fecha_recepcion date,
IN p_periodo date, IN p_cuit_acreedor character varying, IN p_sucu_acreedor character varying, IN id_seccional integer)
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
  pagado boolean,
  op_existente boolean,
  anulado_fecha  timestamp without time zone,
  anulado_usr character varying) AS
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
  false,
  false,
  c.anulado_fecha,
  c.anulado_usr
from  comprobante c 
where not exists (
	select 1 from comprobante_orden_pago_ospim copo
	inner join orden_pago_ospim opo
	on copo.id_orden_pago_ospim = opo.id_orden_pago
	where c.id_punto_venta =  copo.id_punto_venta
	and c.compro_tipo =copo.compro_tipo
	and c.compro_letra  =copo.compro_letra
	and c.compro_sucu =copo.compro_sucu
	and c.compro_nro =copo.compro_nro
	and c.cuit = copo.cuit
	and opo.baja_fecha is null )
   and ( $1 is null or $1 = c.id_punto_venta)
   and ( $2 is null or $2 = c.compro_tipo)
   and ( $3 is null or $3 = c.compro_nro)
   and ( $4 is null or $4 = c.cuit )
   and ( $5 is null or $5 = c.compro_letra)
   and ( $6 is null or $6 = c.compro_sucu)
   and ( $7 is null or $7 = cast(c.fecha_emision as date)) 
   and ( $8 is null or $8 = cast(c.fecha_recepcion as date)) 
   and ( $9 is null or $9 = c.periodo_prestacion)
   and ( $10 is null or $10 = c.cuit_acreedor)
   and ( $11 is null or $11 = c.sucu_acreedor)
   and ( $11 is null or (c.sucu_acreedor = $11 and (($12 is null and c.seccional is null) or c.seccional = $12)))
   and not exists (select 1 from comprobante_liquidacion cl
	inner join liquidacion l
	on cl.id_liquidacion = l.id_liquidacion
	where c.id_punto_venta =  cl.id_punto_venta
	and c.compro_tipo =cl.compro_tipo
	and c.compro_letra  =cl.compro_letra
	and c.compro_sucu =cl.compro_sucu
	and c.compro_nro =cl.compro_nro
	and c.cuit = cl.cuit
	and l.baja_fecha is null )
	and c.compro_tipo <> 'REI'
	and c.baja_fecha is null
	and c.anulado_fecha is null;
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;

drop FUNCTION buscar_comprobante_debito_liquidacion_por_id_liq(IN id_liquidacion integer);
CREATE OR REPLACE FUNCTION buscar_comprobante_debito_liquidacion_por_id_liq(IN id_liquidacion integer)
  RETURNS TABLE(
  fecha_emision timestamp without time zone, 
  fecha_recepcion timestamp without time zone, 
  importe_comprobante numeric, 
  nro character varying, 
  tipo character varying, 
  id_punto_venta smallint, 
  cuit character,
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
  anulado_usr character varying) AS
$BODY$

select c.fecha_emision,
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
  c.anulado_usr 
from comprobante_liquidacion cl, comprobante c 
where
	cl.id_liquidacion = $1
   and	c.id_punto_venta = cl.id_punto_venta
   and c.compro_tipo = cl.compro_tipo
   and c.compro_nro = cl.compro_nro
   and c.cuit = cl.cuit
   and c.compro_letra = cl.compro_letra
   and c.compro_sucu = cl.compro_sucu
   and c.compro_tipo = 'NDB';
   
 
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION buscar_comprobante_liquidacion_por_id_liq(integer) OWNER TO postgres;

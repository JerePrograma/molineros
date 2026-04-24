CREATE OR REPLACE FUNCTION buscar_ultimo_comprobante_ospim_automatico()
  RETURNS TABLE(fecha_emision timestamp without time zone, fecha_recepcion timestamp without time zone, importe_comprobante numeric, nro character varying, tipo character varying, id_punto_venta smallint, cuit character, alta_fecha timestamp without time zone, alta_usr character varying, modi_fecha timestamp without time zone, modi_usr character varying, baja_fecha timestamp without time zone, baja_usr character varying, compro_letra character varying, compro_sucu integer, cuit_acreedor character, sucu_acreedor character, seccional integer, observaciones character varying, vto timestamp without time zone, periodo_prestacion date, debito_para_egreso boolean) AS
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
  c.debito_para_egreso
from comprobante c
where compro_nro = (select max(compro_nro) from comprobante where compro_tipo = 'LIQ')
and compro_tipo = 'LIQ';

$BODY$
  LANGUAGE sql VOLATILE


CREATE OR REPLACE FUNCTION buscar_detalle_liquidaciones_pagas_periodo(IN param_periodo timestamp without time zone)
  RETURNS TABLE(op_id_orden_pago integer, op_importe numeric, op_id_seccional integer, op_prestador boolean, op_farmacia boolean, op_cuit_acreedor character varying, op_sucu_acreedor character varying, op_observaciones character varying, op_alta_fecha timestamp without time zone, op_alta_usr character varying, op_alta_ip character varying, op_modi_fecha timestamp without time zone, op_modi_usr character varying, op_modi_ip character varying, op_baja_fecha timestamp without time zone, op_baja_usr character varying, op_baja_ip character varying, c_id_punto_venta smallint, c_compro_tipo character varying, c_compro_nro character varying, c_cuit character, c_compro_letra character varying, c_compro_sucu integer, cc_alta_fecha timestamp without time zone, cc_alta_usr character varying, cc_modi_fecha timestamp without time zone, cc_modi_usr character varying, cc_baja_fecha timestamp without time zone, cc_baja_usr character varying, cc_concepto_id integer, cc_importe numeric, l_id_liquidacion integer, l_id_prestador integer, l_id_domicilio integer, l_fecha timestamp without time zone, l_periodo timestamp without time zone, l_estado integer, l_entidad character varying, l_compro_a_debitar_tipo character varying, l_compro_a_debitar_letra character varying, l_sucu integer, l_compro_a_debitar_numero character varying, l_fecha_emitido timestamp without time zone, l_fecha_recibido timestamp without time zone, l_fecha_vencimiento timestamp without time zone, l_baja_fecha timestamp without time zone, l_alta_fecha timestamp without time zone, l_alta_usr character varying, l_modi_fecha timestamp without time zone, l_modi_usr character varying, l_baja_usr character varying, l_tipo_liquidacion character varying, l_importe numeric, l_debitado numeric, l_observaciones character varying, l_tercerizado character varying, pla_id_domicilio integer, pla_vigen_desde timestamp without time zone, pla_baja_fecha timestamp without time zone, pd_cuit character varying, pd_id_tipo_prestador smallint, pd_tipo_matricula character, pd_nro_matricula integer, pd_id_mat_provincia integer, pd_id_mat_categoria character, pd_contacto character varying, pd_id_seccional integer, pd_observaciones character varying, pd_rein_liqui smallint, pd_id_condicion_de_iva smallint, pd_cheque_a_nombre_de character varying, pd_alta_fecha timestamp without time zone, pd_alta_usr character varying, pd_modi_fecha timestamp without time zone, pd_modi_usr character varying, pd_baja_fecha timestamp without time zone, pd_baja_usr character varying, pd_descripcion character varying) AS
$BODY$

select  
 opo.id_orden_pago,
 opo.importe,
 opo.id_seccional,
 opo.prestador,
 opo.farmacia,
 opo.cuit_acreedor,
 opo.sucu_acreedor,
 opo.observaciones,
 opo.alta_fecha,
 opo.alta_usr,
 opo.alta_ip,
 opo.modi_fecha,
 opo.modi_usr,
 opo.modi_ip ,
 opo.baja_fecha,
 opo.baja_usr,
 opo.baja_ip,
 
 c.id_punto_venta,
 c.compro_tipo,
 c.compro_nro,
 c.cuit,
 c.compro_letra,
 c.compro_sucu,
 
 cc.alta_fecha, 
 cc.alta_usr,
 cc.modi_fecha,
 cc.modi_usr,
 cc.baja_fecha,
 cc.baja_usr,
 cc.concepto_id,
 cc.importe,
 
l.id_liquidacion,
l.id_prestador,
l.id_domicilio,
l.fecha,
l.periodo,
l.estado,
l.entidad,
l.compro_a_debitar_tipo,
l.compro_a_debitar_letra,
l.sucu,
l.compro_a_debitar_numero,
l.fecha_emitido,
l.fecha_recibido,
l.fecha_vencimiento,
l.baja_fecha,
l.alta_fecha,
l.alta_usr,
l.modi_fecha,
l.modi_usr,
l.baja_usr,
l.tipo_liquidacion,
l.importe,
l.debitado,
l.observaciones,
l.tercerizado,
--pla.id_domicilio,
--pla.vigen_desde,
--pla.baja_fecha,
0,
LOCALTIMESTAMP,
LOCALTIMESTAMP,

pd.cuit,
pd.id_tipo_prestador,
pd.tipo_matricula,
pd.nro_matricula,
pd.id_mat_provincia,
pd.id_mat_categoria,
pd.contacto,
pd.id_seccional,
pd.observaciones,
pd.rein_liqui,
pd.id_condicion_de_iva,
pd.cheque_a_nombre_de,
pd.alta_fecha,
pd.alta_usr,
pd.modi_fecha,
pd.modi_usr,
pd.baja_fecha,
pd.baja_usr,
pd.descripcion
 
 from orden_pago_ospim opo, 
 comprobante_orden_pago_ospim copo, 
 comprobante c, 
 concepto_comprobante cc,
 comprobante_liquidacion cl,
 liquidacion l,
 prestador pd

 where 
--op
 opo.alta_fecha >= (cast ($1 as timestamp without time zone) - interval '1 months') and
 opo.alta_fecha < $1 and 
 
 opo.baja_fecha is null and 
 opo.id_orden_pago = copo.id_orden_pago_ospim
--comprobante
and copo.cuit = c.cuit
and copo.compro_nro = c.compro_nro
and copo.compro_tipo = c.compro_tipo
and copo.compro_sucu = c.compro_sucu
and copo.compro_letra = c.compro_letra
and copo.id_punto_venta = c.id_punto_venta
--concepto comprobante
and c.cuit = cc.cuit
and c.compro_nro = cc.compro_nro
and c.compro_tipo = cc.compro_tipo
and c.compro_sucu = cc.compro_sucu
and c.compro_letra = cc.compro_letra
and c.id_punto_venta = cc.id_punto_venta
and cc.concepto_id = 127
and c.id_punto_venta = cl.id_punto_venta
and c.compro_tipo = cl.compro_tipo
and c.compro_nro = cl.compro_nro
and c.cuit = cl.cuit
and c.compro_letra = cl.compro_letra
and c.compro_sucu = cl.compro_sucu
and c.compro_tipo != 'NDB'

and cl.id_liquidacion = l.id_liquidacion 
and l.id_prestador = pd.id_prestador
 
$BODY$
  LANGUAGE sql VOLATILE

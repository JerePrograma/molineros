CREATE OR REPLACE FUNCTION busca_liquidacion_header_por_id(IN id integer)
  RETURNS TABLE(l_id_liquidacion integer, l_id_prestador integer, l_id_domicilio integer, l_fecha timestamp without time zone, l_periodo timestamp without time zone, l_estado integer, l_entidad character varying, l_compro_a_debitar_tipo character varying, l_compro_a_debitar_letra character varying, l_sucu integer, l_compro_a_debitar_numero character varying, l_fecha_emitido timestamp without time zone, l_fecha_recibido timestamp without time zone, l_fecha_vencimiento timestamp without time zone, l_baja_fecha timestamp without time zone, l_alta_fecha timestamp without time zone, l_alta_usr character varying, l_modi_fecha timestamp without time zone, l_modi_usr character varying, l_baja_usr character varying, l_tipo_liquidacion character varying, l_importe numeric, l_debitado numeric, l_id_orden_compra integer, l_observaciones character varying, l_tercerizado character varying, pla_id_domicilio integer, pla_vigen_desde timestamp without time zone, pla_baja_fecha timestamp without time zone, pd_cuit character varying, pd_id_tipo_prestador smallint, pd_tipo_matricula character, pd_nro_matricula integer, pd_id_mat_provincia integer, pd_id_mat_categoria character, pd_contacto character varying, pd_id_seccional integer, pd_observaciones character varying, pd_rein_liqui smallint, pd_id_condicion_de_iva smallint, pd_cheque_a_nombre_de character varying, pd_alta_fecha timestamp without time zone, pd_alta_usr character varying, pd_modi_fecha timestamp without time zone, pd_modi_usr character varying, pd_baja_fecha timestamp without time zone, pd_baja_usr character varying, pd_descripcion character varying) AS
$BODY$

select
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
l.id_orden_compra,
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

from liquidacion l,
     --prestad_lugar_atencion pla,
     prestador pd     

where  l.id_liquidacion = $1 and
l.id_prestador = pd.id_prestador
--l.id_prestador = pla.id_prestador and
--pla.baja_fecha is null or pla.baja_fecha > current_timestamp and
--pla.id_prestador = pd.id_prestador


$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
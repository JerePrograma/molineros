drop function busca_prestaciones_reintegro_odo_protesis_por_id(IN id integer)

CREATE OR REPLACE FUNCTION busca_prestaciones_reintegro_odo_protesis_por_id(IN id integer)
  RETURNS TABLE(rp_id integer, rp_id_prestacion integer, rp_id_prestador integer, rp_id_plan integer, rp_fecha_prestacion timestamp without time zone, rp_cantidad numeric, rp_importe numeric, rp_compro_a_debitar_tipo character varying, rp_compro_a_debitar_numero character varying, rp_tercerizado character varying, rp_cuit character varying, rp_descripcion character varying, rp_alta_fecha timestamp without time zone, rp_alta_usr character varying, rp_modi_fecha timestamp without time zone, rp_modi_usr character varying, rp_pieza character varying, rp_cara character varying, rp_honorarios numeric, rp_gastos numeric, rp_id_prestador_externo integer, pd_cuit character varying, pd_id_tipo_prestador smallint, pd_tipo_matricula character, pd_nro_matricula integer, pd_id_mat_provincia integer, pd_id_mat_categoria character, pd_contacto character varying, pd_id_seccional integer, pd_observaciones character varying, pd_rein_liqui smallint, pd_id_condicion_de_iva smallint, pd_cheque_a_nombre_de character varying, pd_alta_fecha timestamp without time zone, pd_alta_usr character varying, pd_modi_fecha timestamp without time zone, pd_modi_usr character varying, pd_baja_fecha timestamp without time zone, pd_baja_usr character varying, pd_descripcion character varying, pp_tope_cantidad smallint, pp_tope_importe numeric, pp_tope_individ_cantidad smallint, pp_tope_individ_importe numeric, pp_id_prestacion integer, pp_id_plan integer, n_id_especialidad integer, n_descripcion character varying, n_marca_rein_liq smallint, n_observaciones character varying, n_alta_fecha timestamp without time zone, n_alta_usr character varying, n_modi_fecha timestamp without time zone, n_modi_usr character varying, n_baja_fecha timestamp without time zone, n_baja_usr character varying, n_codigo character varying) AS
$BODY$

select

rp.id,
rp.id_prestacion,
0,--rp.id_prestador,
rp.id_plan,
rp.fecha_prestacion,
rp.cantidad,
rp.importe,
rp.compro_a_debitar_tipo,
rp.compro_a_debitar_numero,
rp.tercerizado,
rp.cuit,
rp.descripcion,
rp.alta_fecha,
rp.alta_usr,
rp.modi_fecha,
rp.modi_usr,
rp.pieza,
rp.cara,
rp.honorarios,
rp.gastos,
rp.id_prestador_externo,

cast('' as character varying),--pd.cuit,
cast(0 as smallint),--pd.id_tipo_prestador,
cast('' as character varying),--pd.tipo_matricula,
0, --pd.nro_matricula,
0,--pd.id_mat_provincia,
cast('' as character varying),--pd.id_mat_categoria,
cast('' as character varying),--pd.contacto,
0,--pd.id_seccional,
cast('' as character varying),--pd.observaciones,
cast(0 as smallint), --pd.rein_liqui,
cast(0 as smallint), --pd.id_condicion_de_iva,
cast('' as character varying), --pd.cheque_a_nombre_de,
cast(null as timestamp without time zone),--pd.alta_fecha,
cast('' as character varying),--pd.alta_usr,
cast(null as timestamp without time zone),--pd.modi_fecha,
cast('' as character varying),--pd.modi_usr,
cast(null as timestamp without time zone),--pd.baja_fecha,
cast('' as character varying),--pd.baja_usr,
cast('' as character varying),--pd.descripcion,
pp.tope_cantidad,
pp.tope_importe,
pp.tope_individ_cantidad,
pp.tope_individ_importe,
pp.id_prestacion,
pp.id_plan,

n.id_especialidad,
n.descripcion,
n.marca_rein_liq,
n.observaciones,
n.alta_fecha,
n.alta_usr,
n.modi_fecha,
n.modi_usr,
n.baja_fecha,
n.baja_usr,
n.codigo

from reintegro r,
     reintegro_prestacion_odo_protesis rp,
     --prestador pd,
     plan_prestacion pp,
     nomenclador n,
     afiliado a,
     afi_domicilio af,
     seccional s

where  r.id_reintegro = $1 and

r.id_reintegro = rp.id_reintegro and
--rp.id_prestador = pd.id_prestador and
--(pd.rein_liqui = 2 or pd.rein_liqui = 3) and
rp.id_plan = pp.id_plan and
rp.id_prestacion = pp.id_prestacion and
pp.id_prestacion = n.id_prestacion and
r.cuil_titular = a.cuil_titular and
r.inte = a.inte and
a.cuil_titular = af.cuil_titular and
af.inte = 0 and
a.id_seccional = s.id_seccional and
af.modi_fecha = (select max(a1.modi_fecha) from afi_domicilio a1 where a1.cuil_titular = af.cuil_titular and a1.inte = af.inte)

$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;

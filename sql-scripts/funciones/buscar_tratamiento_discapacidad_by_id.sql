--DROP FUNCTION buscar_tratamiento_discapacidad_by_id(integer);

CREATE OR REPLACE FUNCTION buscar_tratamiento_discapacidad_by_id(IN id_tratamiento integer)
  RETURNS TABLE(a_cuil_titular character varying, a_inte integer, a_id_ospim integer, 
  a_id_ospim_baja_fecha timestamp without time zone, a_id_uoma integer, 
  a_id_uoma_baja_fecha timestamp without time zone, a_id_amtima integer, a_id_amtima_baja_fecha timestamp without time zone, 
  a_apellido character varying, a_nombre character varying, a_documento_tipo character varying, 
  a_sexo character varying, a_cuil character varying, a_naci_fecha date, a_id_estado_civil_sss integer, a_civil_esta character varying, 
  a_id_parentesco_sss integer, a_parentesco character varying, a_ingre_fecha date, a_anterior_os integer, a_vigen_fecha timestamp without time zone, 
  a_observaciones character varying, a_pres_ssalud_fecha date, a_alta_fecha timestamp without time zone, 
  a_alta_usr character varying, a_modi_fecha timestamp without time zone, a_modi_usr character varying, 
  a_baja_fecha timestamp without time zone, a_baja_usr character varying, a_discapacitado character varying, 
  a_docu_numero character varying, a_nacionalidad integer, a_aportante_titular integer, a_nro_afiliado integer, 
  s_id_seccional integer, s_descripcion character varying, td_id_tratamiento integer, td_id_prestacion integer, 
  td_cuil_titular character varying, td_inte integer, td_cantidad numeric, td_periodicidad character varying, 
  td_periodo_desde timestamp without time zone, td_periodo_hasta timestamp without time zone, td_importe_total numeric, 
  td_alta_fecha timestamp without time zone, td_alta_usr character varying, td_modi_fecha timestamp without time zone, 
  td_modi_usr character varying, td_baja_fecha timestamp without time zone, td_baja_usr character varying, 
  td_id_prestador integer, td_recupera_ape boolean, td_estado integer, td_observaciones character varying, 
  n_id_especialidad integer, n_descripcion character varying, n_marca_rein_liq smallint, 
  n_observaciones character varying, n_alta_fecha timestamp without time zone, n_alta_usr character varying, 
  n_modi_fecha timestamp without time zone, n_modi_usr character varying, n_baja_fecha timestamp without time zone, 
  n_baja_usr character varying, n_codigo character varying, prestador_string character varying, td_cuit character varying, 
  td_sucursal character varying, td_razon_soc character varying, td_id_seccional character varying, 
  td_cantidad_viajes_mes numeric, td_cantidad_kilometros_dia numeric, td_cantidad_kilometros_mes numeric, 
  td_importe_kilometro_unit numeric, td_hs_espera_dia numeric, td_hs_espera_mes numeric, td_importe_hs_espera_unit numeric, 
  td_importe_tercerizado numeric, td_id_tercerizadora character varying) AS
$BODY$


select  
a.cuil_titular,
a.inte,
a.id_ospim ,
a.id_ospim_baja_fecha,
a.id_uoma,
a.id_uoma_baja_fecha,
a.id_amtima,
a.id_amtima_baja_fecha,
a.apellido,
a.nombre,
a.documento_tipo,
a.sexo,
a.cuil, 
a.naci_fecha, 
a.id_estado_civil_sss, 
ec.descripcion as civil_esta, 
a.id_parentesco_sss,
p.descripcion as parentesco,
a.ingre_fecha,
a.anterior_os,
a.vigen_fecha,
a.observaciones,
a.pres_ssalud_fecha,
a.alta_fecha,
a.alta_usr,
a.modi_fecha,
a.modi_usr,
a.baja_fecha,
a.baja_usr,
a.discapacitado,
a.docu_numero,
a.nacionalidad, 
a.aportante_titular,
a.nro_afiliado,

s.id_seccional,
s.descripcion,

td.id_tratamiento,
td.id_prestacion,
td.cuil_titular,
td.inte,
td.cantidad,
td.periodicidad,
td.periodo_desde,
td.periodo_hasta,
td.importe_total,
td.alta_fecha,
td.alta_usr,
td.modi_fecha,
td.modi_usr,
td.baja_fecha,
td.baja_usr,
td.id_prestador,
td.recupera_ape,
td.estado,
td.observaciones,

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
n.codigo,

e.razon_soc || ', ' || e.cuit,

e.cuit,
e.sucursal,
e.razon_soc,
td.id_seccional, 

td.cantidad_viajes_mes,
td.cantidad_kilometros_dia,
td.cantidad_kilometros_mes,
td.importe_kilometro_unit, 
--total mes y total dia
td.hs_espera_dia,
td.hs_espera_mes,
td.importe_hs_espera_unit,

td.importe_tercerizado,
td.id_tercerizadora
 
 from
 tratamiento_discapacidad td,
 afiliado a,
 seccional s,
 nomenclador n,
 empresa e,
 parentesco_sss p,
 estado_civil_sss ec
 
 where
 td.id_tratamiento = $1 and
 td.cuil_titular = a.cuil_titular and
 td.inte = a.inte and 
 td.id_prestacion = n.id_prestacion and
 a.id_seccional = s.id_seccional and
 td.cuit = e.cuit and
 td.prestador = e.sucursal and
 a.id_parentesco_sss = p.codigo and 
 a.id_estado_civil_sss = ec.codigo;
 
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
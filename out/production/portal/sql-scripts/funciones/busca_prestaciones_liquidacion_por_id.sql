CREATE OR REPLACE FUNCTION busca_prestaciones_liquidacion_por_id(IN id integer)
  RETURNS TABLE(lp_id_liquidacion integer, lp_orden integer, lp_cuil_titular character varying, 
  lp_inte integer, lp_id_prestacion integer, lp_fecha_prestacion timestamp without time zone, 
  lp_cantidad numeric, lp_importe numeric, lp_servicio character varying, lp_solicitado numeric, 
  lp_debitado numeric, lp_resultado numeric, lp_tercerizado character varying, lp_periodo timestamp without time zone, 
  a_cuil_titular character varying, a_inte integer, a_id_ospim integer, a_id_ospim_baja_fecha timestamp without time zone, 
  a_id_uoma integer, a_id_uoma_baja_fecha timestamp without time zone, a_id_amtima integer, 
  a_id_amtima_baja_fecha timestamp without time zone, a_apellido character varying, a_nombre character varying, 
  a_documento_tipo character varying, a_sexo character varying, a_cuil character varying, 
  a_naci_fecha date, a_id_estado_civil_sss integer, a_civil_esta character varying, a_id_parentesco_sss integer, a_parentesco character varying, a_ingre_fecha date, 
  a_anterior_os integer, a_vigen_fecha timestamp without time zone, a_observaciones character varying, 
  a_pres_ssalud_fecha date, a_alta_fecha timestamp without time zone, a_alta_usr character varying, 
  a_modi_fecha timestamp without time zone, a_modi_usr character varying, a_baja_fecha timestamp without time zone, 
  a_baja_usr character varying, a_discapacitado character varying, a_docu_numero character varying, 
  a_nacionalidad integer, a_aportante_titular integer, a_nro_afiliado integer, s_id_seccional integer, 
  s_descripcion character varying, ad_vigen_desde timestamp without time zone, ad_domi_tipo character varying, 
  ad_calle character varying, ad_piso character varying, ad_depto character varying, ad_oficina character varying, 
  ad_postal_codi character varying, ad_barrio character varying, ad_cod_area_telefono character varying, 
  ad_telefono character varying, ad_cod_area_celular character varying, ad_celular character varying, 
  ad_observaciones character varying, ad_domi_val character varying, ad_alta_fecha timestamp without time zone, 
  ad_alta_usr character varying, ad_modi_fecha timestamp without time zone, ad_modi_usr character varying, 
  ad_baja_fecha timestamp without time zone, ad_baja_usr character varying, ad_provincia integer, ad_localidad integer, 
  ad_numero character varying, n_id_prestacion integer, n_id_especialidad integer, n_descripcion character varying, 
  n_marca_rein_liq smallint, n_observaciones character varying, n_alta_fecha timestamp without time zone, 
  n_alta_usr character varying, n_modi_fecha timestamp without time zone, n_modi_usr character varying, 
  n_baja_fecha timestamp without time zone, n_baja_usr character varying, n_codigo character varying) AS
$BODY$

select

  lp.id_liquidacion,
  lp.orden,
  lp.cuil_titular,
  lp.inte,
  lp.id_prestacion,
  lp.fecha_prestacion,
  lp.cantidad,
  lp.importe,
  lp.servicio,
  lp.solicitado,
  lp.debitado,
  lp.resultado,
  lp.tercerizado,
  lp.periodo,

a.cuil_titular,
a.inte,
a.id_ospim ,
a.id_ospim_baja_fecha  ,
a.id_uoma ,
a.id_uoma_baja_fecha ,
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

af.vigen_desde,
af.domi_tipo, 
af.calle, 
af.piso, 
af.depto, 
af.oficina, 
af.postal_codi, 
af.barrio,
af.cod_area_telefono, 
af.telefono,
af.cod_area_celular,
af.celular, 
af.observaciones, 
af.domi_val,
af.alta_fecha,
af.alta_usr, 
af.modi_fecha,
af.modi_usr,
af.baja_fecha,
af.baja_usr,
af.provincia,
af.localidad,
af.numero, 

n.id_prestacion,
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

from liquidacion_prestacion lp left outer join 
     afiliado a on
     lp.cuil_titular = a.cuil_titular and
     lp.inte = a.inte  
     left outer join afi_domicilio af on
     a.cuil_titular = af.cuil_titular and
     af.inte = 0 and
     (af.baja_fecha is null or af.baja_fecha > current_timestamp) and
     (a.baja_fecha is null or a.baja_fecha > current_timestamp)    
     left outer join
     seccional s on
     a.id_seccional = s.id_seccional
     left outer join
     nomenclador n on
     lp.id_prestacion = n.id_prestacion
     left outer join
     parentesco_sss p on
     a.id_parentesco_sss = p.codigo
     left outer join
     estado_civil_sss ec on
     a.id_estado_civil_sss = ec.codigo

     where lp.id_liquidacion = $1
 order by lp.servicio
  
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
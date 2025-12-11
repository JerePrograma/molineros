CREATE OR REPLACE FUNCTION busca_reintegro_header_por_id(IN id integer)
  RETURNS TABLE(r_id_reintegro integer, r_cuil_titular character varying, r_inte integer, 
  r_fecha timestamp without time zone, r_periodo timestamp without time zone, r_liquidado timestamp without time zone, 
  r_auditado timestamp without time zone, r_alta_fecha timestamp without time zone, 
  r_alta_usr character varying, r_modi_fecha timestamp without time zone, r_modi_usr character varying, 
  r_baja_fecha timestamp without time zone, r_baja_usr character varying, r_estado integer, 
  r_entidad character varying, r_tipo_reintegro character varying, r_observaciones character varying, 
  r_id_reintegro_user integer, a_cuil_titular character varying, a_inte integer, a_id_ospim integer, 
  a_id_ospim_baja_fecha timestamp without time zone, a_id_uoma integer, a_id_uoma_baja_fecha timestamp without time zone, 
  a_id_amtima integer, a_id_amtima_baja_fecha timestamp without time zone, a_apellido character varying, 
  a_nombre character varying, a_documento_tipo character varying, a_sexo character varying, 
  a_cuil character varying, a_naci_fecha date, a_id_estado_civil_sss integer, a_civil_esta character varying, 
  a_id_parentesco_sss integer, a_parentesco character varying, a_ingre_fecha date, a_anterior_os integer, a_vigen_fecha timestamp without time zone, 
  a_observaciones character varying, a_pres_ssalud_fecha date, a_alta_fecha timestamp without time zone, 
  a_alta_usr character varying, a_modi_fecha timestamp without time zone, a_modi_usr character varying, 
  a_baja_fecha timestamp without time zone, a_baja_usr character varying, a_discapacitado character varying, 
  a_docu_numero character varying, a_nacionalidad integer, a_aportante_titular integer, a_nro_afiliado integer, 
  s_id_seccional integer, s_descripcion character varying, ad_vigen_desde timestamp without time zone, 
  ad_domi_tipo character varying, ad_calle character varying, ad_piso character varying, ad_depto character varying, 
  ad_oficina character varying, ad_postal_codi character varying, ad_barrio character varying, ad_cod_area_telefono character varying, 
  ad_telefono character varying, ad_cod_area_celular character varying, ad_celular character varying, ad_observaciones character varying, 
  ad_domi_val character varying, ad_alta_fecha timestamp without time zone, ad_alta_usr character varying, 
  ad_modi_fecha timestamp without time zone, ad_modi_usr character varying, ad_baja_fecha timestamp without time zone, 
  ad_baja_usr character varying, ad_provincia integer, ad_localidad integer, ad_numero character varying) AS
$BODY$

select
r.id_reintegro,
r.cuil_titular,
r.inte,
r.fecha,
r.periodo,
r.liquidado,
r.auditado,
r.alta_fecha as alta_fecha_r,
r.alta_usr as alta_usr_r,
r.modi_fecha as modi_fecha_r,
r.modi_usr as modi_usr_r,
r.baja_fecha as baja_fecha_r,
r.baja_usr as baja_usr_r,
r.estado,
r.entidad,
r.tipo_reintegro,
r.observaciones,
r.id_reintegro_user,
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
af.numero

from reintegro r,
     afiliado a,
     afi_domicilio af,
     seccional s,
     parentesco_sss p,
     estado_civil_sss ec

where  r.id_reintegro = $1 and
r.cuil_titular = a.cuil_titular and
r.inte = a.inte and
a.cuil_titular = af.cuil_titular and
af.inte = 0 and
af.vigen_desde = (select max(vigen_desde) from afi_domicilio where cuil_titular = a.cuil_titular and inte = 0) and
r.id_seccional = s.id_seccional and
a.id_estado_civil_sss = ec.codigo and
a.id_parentesco_sss = p.codigo

$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
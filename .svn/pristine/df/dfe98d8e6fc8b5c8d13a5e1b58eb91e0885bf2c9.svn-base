CREATE OR REPLACE FUNCTION busca_reintegro_farmacia_header_por_id(IN reintegro_id_p integer)
  RETURNS TABLE(r_fecha timestamp without time zone, r_periodo date, r_id_seccional integer, 
  r_cuil_titular character varying, r_inte integer, r_alta_fecha timestamp without time zone, 
  r_alta_usr character varying, r_modi_fecha timestamp without time zone, r_modi_usr character varying, 
  r_baja_fecha timestamp without time zone, r_baja_usr character varying, r_id_reintegro integer, 
  r_observacion character varying, a_cuil_titular character varying, a_inte integer, a_id_ospim integer, 
  a_id_ospim_baja_fecha timestamp without time zone, a_id_uoma integer, a_id_uoma_baja_fecha timestamp without time zone, 
  a_id_amtima integer, a_id_amtima_baja_fecha timestamp without time zone, a_apellido character varying, 
  a_nombre character varying, a_documento_tipo character varying, a_sexo character varying, 
  a_cuil character varying, a_naci_fecha date, a_id_estado_civil_sss integer, a_civil_esta character varying, 
  a_id_parentesco_sss integer, a_parentesco character varying, 
  a_ingre_fecha date, a_anterior_os integer, a_vigen_fecha timestamp without time zone, a_observaciones character varying, 
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
  ad_baja_fecha timestamp without time zone, ad_baja_usr character varying, ad_provincia integer, 
  ad_localidad integer, ad_numero character varying) AS
$BODY$

select 
r.fecha as fecha_r,
r.periodo,
r.id_seccional as r_id_seccional_,
r.cuil_titular as r_cuil_titular_,
r.inte as r_inte_,
r.alta_fecha as r_alta_fecha_,
r.alta_usr as r_alta_usr_,
r.modi_fecha as r_modi_fecha_,
r.modi_usr as r_modi_usr_,
r.baja_fecha as r_baja_fecha_,
r.baja_usr as r_baja_usr_,
r.id_reintegro as r_id_reintegro_,
r.observacion as r_id_observacion_,

a.cuil_titular,
a.inte,
a.id_ospim ,
a.id_ospim_baja_fecha,
a.id_uoma,
a.id_uoma_baja_fecha,
a.id_amtima,
a.id_amtima_baja_fecha,
a.apellido,
a.nombre as nombre_a,
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
a.observaciones as a_observaciones_,
a.pres_ssalud_fecha,
a.alta_fecha as alta_fecha_a,
a.alta_usr as alta_usr_a,
a.modi_fecha as mori_fecha_a,
a.modi_usr as modi_usr_a,
a.baja_fecha as baja_fecha_a,
a.baja_usr as baja_usr_a,
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

from reintegro_farmacia r
inner join seccional s
on r.id_seccional = s.id_seccional
inner join afiliado a
on r.cuil_titular = a.cuil_titular
and r.inte = a.inte
inner join parentesco_sss p
on a.id_parentesco_sss = p.codigo
inner join estado_civil_sss ec
on a.id_estado_civil_sss = ec.codigo
inner join afi_domicilio af
on a.cuil_titular = af.cuil_titular and
af.inte = 0 and
af.vigen_desde = (select max(vigen_desde) from afi_domicilio where cuil_titular = a.cuil_titular and inte = 0)
and r.id_reintegro=$1;

$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
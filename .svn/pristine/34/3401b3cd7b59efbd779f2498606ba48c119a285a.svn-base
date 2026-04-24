CREATE OR REPLACE FUNCTION busca_afiliado_incluso_dado_baja_c_i(IN cuil character, IN inte integer)
  RETURNS TABLE(id_ospim integer, id_uoma integer, id_amtima integer, apellido character varying, 
  nombre character varying, documento_tipo character varying, sexo character varying, cuil character varying, 
  naci_fecha date, id_estado_civil_sss integer, civil_esta character varying, nacionalidad integer, id_parentesco_sss integer, parentesco character varying, 
  id_seccional integer, anterior_os integer, vigen_fecha timestamp without time zone, observaciones character varying, 
  pres_ssalud_fecha date, alta_usr character varying, modi_usr character varying, discapacitado character varying, 
  docu_numero character varying, censo2013 smallint, email character varying, domi_tipo character varying, 
  calle character varying, piso character varying, depto character varying, oficina character varying, 
  postal_codi character varying, barrio character varying, cod_area_telefono character varying, telefono character varying, 
  cod_area_tel_laboral character varying, tel_laboral character varying, cod_area_celular character varying, 
  celular character varying, observaciones_dom character varying, domi_val character varying, alta_usr_d character varying, 
  modi_usr_d character varying, provincia integer, localidad integer, numero character varying, 
  aportante_titular integer, baja_f timestamp without time zone, baja_u character varying, ingre_f date, 
  id_motivo_baja integer, id_amtima_baja_fecha timestamp without time zone, id_ospim_baja_fecha timestamp without time zone, 
  id_uoma_baja_fecha timestamp without time zone, descripcion character varying, id_afi_docum integer, 
  id_documento integer, fecha_vto date) AS
$BODY$

select
a.id_ospim,
a.id_uoma,
a.id_amtima,
a.apellido,
a.nombre,
a.documento_tipo,
a.sexo,
a.cuil,
a.naci_fecha,
a.id_estado_civil_sss,
ec.descripcion as civil_esta,
a.nacionalidad,
a.id_parentesco_sss,
p.descripcion as parentesco,
a.id_seccional,
a.anterior_os,
a.vigen_fecha,
a.observaciones,
a.pres_ssalud_fecha,
a.alta_usr,
a.modi_usr,
a.discapacitado,
a.docu_numero,
a.censo2013,
a.email,
af.domi_tipo,
af.calle,
af.piso,
af.depto,
af.oficina,
af.postal_codi,
af.barrio,
af.cod_area_telefono,
af.telefono,
af.cod_area_tel_laboral,
af.tel_laboral,
af.cod_area_celular,
af.celular,
af.observaciones,
af.domi_val,
af.alta_usr,
af.modi_usr,
af.provincia,
af.localidad,
af.numero,
a.aportante_titular,
a.baja_fecha,
a.baja_usr,
a.ingre_fecha,
a.id_motivo_baja,
a.id_amtima_baja_fecha,
a.id_ospim_baja_fecha,
a.id_uoma_baja_fecha,
s.descripcion,
ad.id,
ad.id_documento,
ad.fecha_vto
from afiliado a
inner join parentesco_sss p on a.id_parentesco_sss = p.codigo
inner join estado_civil_sss ec on a.id_estado_civil_sss = ec.codigo 
left outer join afi_documento ad 
on a.cuil_titular = ad.cuil_titular 
and a.inte=ad.inte
and ad.id_documento in (5,15)
and ad.baja_fecha is null
and ad.fecha_vto=(select max(_ad.fecha_vto) from afi_documento _ad where _ad.cuil_titular = ad.cuil_titular and _ad.inte=ad.inte and _ad.id_documento in (5,15) and _ad.baja_fecha is null)
		
		inner join afi_domicilio af
		on a.cuil_titular = af.cuil_titular
		and 0 = af.inte
		and af.modi_fecha in (select max(a1.modi_fecha) from afi_domicilio a1 where a1.cuil_titular = $1 and a1.inte = 0)
		inner join seccional s 
		on a.id_seccional = s.id_seccional

where
a.cuil_titular = $1
and a.inte=$2
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
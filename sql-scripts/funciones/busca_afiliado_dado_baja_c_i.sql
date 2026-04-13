CREATE OR REPLACE FUNCTION busca_afiliado_dado_baja_c_i(IN cuil character, IN inte integer)
  RETURNS TABLE(id_ospim integer, id_uoma integer, id_amtima integer, apellido character varying, nombre character varying, documento_tipo character varying, sexo character varying, cuil character varying, naci_fecha date, civil_esta character varying, nacionalidad integer, parentesco character varying, id_seccional integer, anterior_os integer, vigen_fecha timestamp without time zone, observaciones character varying, pres_ssalud_fecha date, alta_usr character varying, modi_usr character varying, discapacitado character varying, docu_numero character varying, domi_tipo character varying, calle character varying, piso character varying, depto character varying, oficina character varying, postal_codi character varying, barrio character varying, telefono character varying, observaciones_dom character varying, domi_val character varying, alta_usr_d character varying, modi_usr_d character varying, provincia integer, localidad integer, numero character varying, aportante_titular integer, baja_f timestamp without time zone, baja_u character varying, id_plan integer, id_parentesco_sss integer, id_estado_civil_sss integer) AS
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
ec.descripcion as estado_civil, 
a.nacionalidad, 
ps.descripcion as parentesco, 
a.id_seccional, 
a.anterior_os, 
a.vigen_fecha, 
a.observaciones, 
a.pres_ssalud_fecha, 
a.alta_usr, 
a.modi_usr, 
a.discapacitado, 
a.docu_numero, 
af.domi_tipo, 
af.calle, 
af.piso, 
af.depto, 
af.oficina, 
af.postal_codi, 
af.barrio, 
af.telefono, 
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
ap.id_plan,
a.id_parentesco_sss,
a.id_estado_civil_sss
from afiliado a, afi_domicilio af, afi_plan ap, estado_civil_sss ec, parentesco_sss ps
where a.cuil_titular = $1
and a.inte=$2
and a.cuil_titular = af.cuil_titular
and af.inte = 0
and af.modi_fecha in (select max(a1.modi_fecha) from afi_domicilio a1 where a1.cuil_titular = $1 and a1.inte = 0)
and ap.cuil_titular=a.cuil_titular
and ap.inte=0
and ec.codigo=a.id_estado_civil_sss
and ps.codigo=a.id_parentesco_sss
and ap.alta_fecha =
	(select max(appp.alta_fecha) from afi_plan appp where appp.cuil_titular=ap.cuil_titular and appp.inte=ap.inte);

$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
-- Function: busca_reintegro_por_id(integer)

-- DROP FUNCTION busca_reintegro_por_id(integer);

CREATE OR REPLACE FUNCTION busca_reintegro_por_id(IN id integer)
  RETURNS TABLE(id_reintegro integer, cuil_titular character varying, inte integer, fecha timestamp without time zone, periodo timestamp without time zone, liquidado timestamp without time zone, auditado timestamp without time zone, alta_fecha_r timestamp without time zone, alta_usr_r character varying, modi_fecha_r timestamp without time zone, modi_usr_r character varying, baja_fecha_r timestamp without time zone, baja_usr_r character varying, estado integer, entidad character varying, tipo_reintegro character varying, id_prestacion integer, id_prestador integer, id_plan integer, fecha_prestacion timestamp without time zone, cantidad numeric, importe numeric, compro_a_debitar_tipo character varying, compro_a_debitar_numero character varying, tercerizado character varying, cuit character varying, id_tipo_prestador smallint, tipo_matricula character, nro_matricula integer, id_mat_provincia integer, id_mat_categoria character, contacto character varying, id_seccional integer, observaciones_pd character varying, rein_liqui smallint, id_condicion_de_iva smallint, cheque_a_nombre_de character varying, alta_fecha_pd timestamp without time zone, alta_usr_pd character varying, modi_fecha_pd timestamp without time zone, modi_usr_pd character varying, baja_fecha_pd timestamp without time zone, baja_usr_pd character varying, descripcion_pd character varying, tope_cantidad smallint, tope_importe numeric, tope_individ_cantidad smallint, tope_individ_importe numeric, id_especialidad integer, descripcion_n character varying, marca_rein_liq smallint, observaciones_n character varying, alta_fecha_n timestamp without time zone, alta_usr_n character varying, modi_fecha_n timestamp without time zone, modi_usr_n character varying, baja_fecha_n timestamp without time zone, baja_usr_n character varying, id_ospim integer, id_uoma integer, id_amtima integer, apellido character varying, nombre character varying, documento_tipo character varying, sexo character varying, cuil character varying, naci_fecha date, civil_esta character varying, parentesco character varying, ingre_fecha date, anterior_os integer, vigen_fecha timestamp without time zone, observaciones_a character varying, pres_ssalud_fecha date, alta_fecha_a timestamp without time zone, alta_usr_a character varying, modi_fecha_a timestamp without time zone, modi_usr_a character varying, baja_fecha_a timestamp without time zone, baja_usr_a character varying, discapacitado character varying, docu_numero character varying, nacionalidad integer, aportante_titular integer, nro_afiliado integer, id_seccional integer, descripcion_s character varying, vigen_desde timestamp without time zone, domi_tipo character varying, calle character varying, piso character varying, depto character varying, oficina character varying, postal_codi character varying, barrio character varying, telefono character varying, observaciones_ad character varying, domi_val character varying, alta_fecha_ad timestamp without time zone, alta_usr_ad character varying, modi_fecha_ad timestamp without time zone, modi_usr_ad character varying, baja_fecha_ad timestamp without time zone, baja_usr_ad character varying, provincia_ad integer, localidad_ad integer, numero character varying) AS
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

rp.id_prestacion,
rp.id_prestador,
rp.id_plan,

rp.fecha_prestacion,
rp.cantidad,
rp.importe,
rp.compro_a_debitar_tipo,
rp.compro_a_debitar_numero,
rp.tercerizado,

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
pd.descripcion,

pp.tope_cantidad,
pp.tope_importe,
pp.tope_individ_cantidad,
pp.tope_individ_importe,

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

a.id_ospim,
a.id_uoma,
a.id_amtima,
a.apellido,
a.nombre,
a.documento_tipo,
a.sexo,
a.cuil, 
a.naci_fecha, 
a.civil_esta, 
a.parentesco,
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
af.telefono, 
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
     reintegro_prestacion rp,
     prestador pd,
     plan_prestacion pp,
     nomenclador n,
     afiliado a,
     afi_domicilio af,
     seccional s

where  r.id_reintegro = $1 and

r.id_reintegro = rp.id_reintegro and
rp.id_prestador = pd.id_prestador and
rp.id_plan = pp.id_plan and
rp.id_prestacion = pp.id_prestacion and
pp.id_prestacion = n.id_prestacion and
r.cuil_titular = a.cuil_titular and
r.inte = a.inte and
a.cuil_titular = af.cuil_titular and
af.inte = 0 and
(af.baja_fecha is null or af.baja_fecha > current_timestamp) and
(a.baja_fecha is null or a.baja_fecha > current_timestamp) and
a.id_seccional = s.id_seccional

$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION busca_reintegro_por_id(integer) OWNER TO postgres;

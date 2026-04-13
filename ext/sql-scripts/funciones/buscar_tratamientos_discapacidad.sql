CREATE OR REPLACE FUNCTION buscar_tratamientos_discapacidad(IN fecha_desde timestamp without time zone,
 IN fecha_hasta timestamp without time zone, IN cuil character varying, IN inte integer, IN entidad character varying, 
IN nro_afi integer, IN codprestad character varying, IN id_prestador character varying, IN estado integer, IN codprestaci character varying)
  RETURNS TABLE(a_cuil_titular character varying, a_inte integer, a_id_ospim integer, a_id_ospim_baja_fecha timestamp without time zone, a_id_uoma integer, a_id_uoma_baja_fecha timestamp without time zone, a_id_amtima integer, a_id_amtima_baja_fecha timestamp without time zone, a_apellido character varying, a_nombre character varying, a_documento_tipo character varying, a_sexo character varying, a_cuil character varying, a_naci_fecha date, a_id_estado_civil_sss integer, a_civil_esta character varying, a_id_parentesco_sss integer, a_parentesco character varying, a_ingre_fecha date, a_anterior_os integer, a_vigen_fecha timestamp without time zone, a_observaciones character varying, a_pres_ssalud_fecha date, a_alta_fecha timestamp without time zone, a_alta_usr character varying, a_modi_fecha timestamp without time zone, a_modi_usr character varying, a_baja_fecha timestamp without time zone, a_baja_usr character varying, a_discapacitado character varying, a_docu_numero character varying, a_nacionalidad integer, a_aportante_titular integer, a_nro_afiliado integer, td_id_tratamiento integer, td_id_prestacion integer, td_cuil_titular character varying, td_inte integer, td_cantidad numeric, td_periodicidad character varying, td_periodo_desde timestamp without time zone, td_periodo_hasta timestamp without time zone, td_importe_total numeric, td_alta_fecha timestamp without time zone, td_alta_usr character varying, td_modi_fecha timestamp without time zone, td_modi_usr character varying, td_baja_fecha timestamp without time zone, td_baja_usr character varying, td_id_prestador integer, td_recupera_ape boolean, td_estado integer, td_observaciones character varying, n_id_especialidad integer, n_descripcion character varying, n_marca_rein_liq smallint, n_observaciones character varying, n_alta_fecha timestamp without time zone, n_alta_usr character varying, n_modi_fecha timestamp without time zone, n_modi_usr character varying, n_baja_fecha timestamp without time zone, n_baja_usr character varying, n_codigo character varying, td_cuit character varying, td_sucursal character varying, td_razon_soc character varying, td_id_seccional character varying) AS
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

td.cuit,
td.prestador,
e.razon_soc,
td.id_seccional
 
 from 
 tratamiento_discapacidad td,
 afiliado a,
 nomenclador n,
 empresa e,
 parentesco_sss p,
 estado_civil_sss ec
 
 where 
 ($4 is null or ($4 is not null and td.inte=$4)) and
 ($3 is null or $3 = '' or ($3 is not null and td.cuil_titular=$3)) and
 ($1 is null or ($1 is not null and td.periodo_desde>=$1)) and
 ($2 is null or ($2 is not null and td.periodo_hasta<=$2)) and
 ($7 is null or $7 = '' or ($7 is not null and td.cuit = $7)) and
  a.id_parentesco_sss=p.codigo
 and a.id_estado_civil_sss=ec.codigo and 
 td.cuit = e.cuit and
 td.prestador = e.sucursal and

 ($8 is null or $8 = '' or ($8 is not null and UPPER(e.razon_soc) like UPPER('%' || $8 || '%'))) and
 ($9 = 0 or ($9 != 0 and td.estado = $9)) and
 
 td.cuil_titular = a.cuil_titular and
 td.inte = a.inte and
 
 ($6 is null or ($6 is not null and ((a.id_ospim = $6 and $5 = 'O.S.P.I.M.') or (a.id_uoma = $6 and $5 = 'U.O.M.A.') or (a.id_amtima = $6 and $5 = 'A.M.T.I.M.A.')))) and
 
 td.id_prestacion = n.id_prestacion and
 ($10 is null or $10 = '' or ($10 is not null and $10 = n.codigo));
  
 --td.baja_fecha is null

$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
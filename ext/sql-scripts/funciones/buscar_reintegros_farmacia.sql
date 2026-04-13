CREATE TYPE result_reintegros_farmacia AS
   (r_fecha timestamp without time zone,
    r_periodo date,
    r_id_seccional integer,
    r_cuil_titular character varying,
    r_inte integer,
    r_alta_fecha timestamp without time zone,
    r_alta_usr character varying,
    r_modi_fecha timestamp without time zone,
    r_modi_usr character varying,
    r_baja_fecha timestamp without time zone,
    r_baja_usr character varying,
    r_id_reintegro integer,
    r_observacion character varying,
    a_cuil_titular character varying,
    a_inte integer,
    a_id_ospim integer,
    a_id_ospim_baja_fecha timestamp without time zone,
    a_id_uoma integer,
    a_id_uoma_baja_fecha timestamp without time zone,
    a_id_amtima integer,
    a_id_amtima_baja_fecha timestamp without time zone,
    a_apellido character varying,
    a_nombre character varying,
    a_documento_tipo character varying,
    a_sexo character varying,
    a_cuil character varying,
    a_naci_fecha date,
    a_id_estado_civil_sss integer,
    a_civil_esta character varying,
    a_id_parentesco_sss integer,
    a_parentesco character varying,
    a_ingre_fecha date,
    a_anterior_os integer,
    a_vigen_fecha timestamp without time zone,
    a_observaciones character varying,
    a_pres_ssalud_fecha date,
    a_alta_fecha timestamp without time zone,
    a_alta_usr character varying,
    a_modi_fecha timestamp without time zone,
    a_modi_usr character varying,
    a_baja_fecha timestamp without time zone,
    a_baja_usr character varying,
    a_discapacitado character varying,
    a_docu_numero character varying,
    a_nacionalidad integer,
    a_aportante_titular integer,
    a_nro_afiliado integer,
    s_id_seccional integer,
    s_descripcion character varying,
    ad_vigen_desde timestamp without time zone,
    ad_domi_tipo character varying,
    ad_calle character varying,
    ad_piso character varying,
    ad_depto character varying,
    ad_oficina character varying,
    ad_postal_codi character varying,
    ad_barrio character varying,
    ad_cod_area_telefono character varying,
    ad_telefono character varying,
    ad_cod_area_celular character varying,
    ad_celular character varying,
    ad_observaciones character varying,
    ad_domi_val character varying,
    ad_alta_fecha timestamp without time zone,
    ad_alta_usr character varying,
    ad_modi_fecha timestamp without time zone,
    ad_modi_usr character varying,
    ad_baja_fecha timestamp without time zone,
    ad_baja_usr character varying,
    ad_provincia integer,
    ad_localidad integer,
    ad_numero character varying,
    rp_id_reintegro integer,
    rp_fecha timestamp without time zone,
    rp_nro_receta integer,
    rp_fecha_receta date,
    rp_profesional character varying,
    rp_cantidad integer,
    rp_troquel integer,
    rp_cober_sss numeric,
    rp_cober_amtima numeric,
    rp_cober_ospim numeric,
    rp_monto_ospim numeric,
    rp_monto_amtima numeric,
    rp_precio_al_publico numeric,
    rp_precio_ospim numeric,
    rp_total_med numeric,
    rp_total_cobertura numeric,
    rp_total numeric,
    rp_alta_fecha timestamp without time zone,
    rp_alta_usr character varying,
    rp_modi_fecha timestamp without time zone,
    rp_modi_usr character varying,
    rp_baja_fecha timestamp without time zone,
    rp_baja_usr character varying,
    rp_id_medicamento integer,
    rp_id integer,
    m_troquel numeric,
    m_nombre character varying,
    m_presentacion character varying,
    m_monto_ioma numeric,
    m_norma_ioma character(1),
    m_cober_ioma character(1),
    m_laboratorio character varying,
    m_precio numeric,
    m_fecha timestamp without time zone,
    m_controlado character(1),
    m_importado character(1),
    m_tipo_venta character(1),
    m_iva character(1),
    m_cod_dto_pami character(1),
    m_cod_lab integer,
    m_nro_registro numeric,
    m_baja character(1),
    m_cod_barra character varying,
    m_unidades integer,
    m_tamanio character(1),
    m_heladera character(1),
    m_sifar character(1),
    m_baja_especial character(1),
    m_accion character varying,
    m_droga character varying,
    m_id_medicamento integer,
    r__id_orden_pago integer,
    r__nro_cheque numeric,
    r__fecha_op timestamp without time zone,
    opor_id_lista_reintegro_pago integer,
    m_porc_ospim numeric,
    m_porc_amtima numeric,
    m_porc_sssalud numeric,
    m_pmoe_n numeric);
    
    
CREATE OR REPLACE FUNCTION buscar_reintegros_farmacia(reintegro_p integer, fecha_desde_p timestamp without time zone, fecha_hasta_p timestamp without time zone, pediodo_desde_p timestamp without time zone, periodo_hasta_p timestamp without time zone, seccional_p integer, prestacion_p character varying, entidad_p character varying, nro_afi_p integer, inte_p integer, cuil_p character varying, alta_usr_p character varying, estado_p integer, id_medicamento_p integer, receta_p integer)
  RETURNS SETOF result_reintegros_farmacia AS
$BODY$
BEGIN

drop table if exists REINTE_AUX;

if (estado_p = 0) then

CREATE TEMP TABLE REINTE_AUX AS
--todos los reintegros,
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
af.celular, 
af.cod_area_celular,  
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

--medicamento detalle

  rp.id_reintegro,
  rp.fecha,
  rp.nro_receta,
  rp.fecha_receta,
  rp.profesional,
  rp.cantidad,
  rp.troquel,
  rp.cober_sss,
  rp.cober_amtima,
  rp.cober_ospim,
  rp.monto_ospim,
  rp.monto_amtima,
  rp.precio_al_publico,
  rp.precio_ospim,  
  rp.total_med,
  rp.total_cobertura,
  rp.total,
  rp.alta_fecha as alta_fecha_rp,
  rp.alta_usr as alta_usr_rp,
  rp.mod_fecha as modi_fecha_rp,
  rp.modi_usr as modi_usr_rp,
  rp.baja_fecha as baja_fecha_rp,
  rp.baja_usr as baja_usr_rp,
  rp.id_medicamento as id_medicamento_rp,
  rp.id,  
--medicamento
  
  m.troquel as troquel_m,
  m.nombre,
  m.presentacion,
  m.monto_ioma,
  m.norma_ioma,
  m.cober_ioma,
  m.laboratorio,
  m.precio,
  m.fecha as fecha_m,
  m.controlado,
  m.importado ,
  m.tipo_venta ,
  m.iva ,
  m.cod_dto_pami ,
  
  m.cod_lab,
  m.nro_registro,
  m.baja ,
  m.cod_barra,
  m.unidades,
  m.tamanio,
  m.heladera,
  m.sifar,
  m.baja_especial,	  
  m.accion,  
  m.droga,  
  m.id_medicamento,

  opo.id_orden_pago,
  opos.nro_cheque,  
  opo.alta_fecha as fecha_op,
  opor.id_lista_reintegro_pago,

 trunc(v.porc_ospim,2) as cinco, 
 trunc(v.porc_amtima,2) as seis, 
 trunc(v.porc_sssalud,2) as siete,  
 v.pmoe_n as ocho

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
on a.cuil_titular = af.cuil_titular 
and af.inte = 0 
and (af.baja_fecha is null or af.baja_fecha>r.fecha)

left outer join lista_reintegro_farmacia_pago_detalle opor
on r.id_reintegro = opor.id_reintegro

left outer join orden_pago_ospim_lista_reintegros_farmacia opol
on opor.id_lista_reintegro_pago = opol.id_lista_reintegro_pago
and opol.baja_fecha is null
left outer join orden_pago_ospim opo
on opol.id_orden_pago_ospim = opo.id_orden_pago
and opo.baja_fecha is null
left outer join orden_pago_ospim_pagos opos
on opo.id_orden_pago = opos.id_orden_pago
----medicam
inner join medicamento_reintegro_farmacia rp
on r.id_reintegro = rp.id_reintegro 
left outer join medicamentos m
on rp.id_medicamento = m.id_medicamento
left outer join vademecum v
on v.registro=m.nro_registro
where ($1 = 0 or $1 is null or ($1 != 0 and r.id_reintegro=$1)) 
and ($2 is null or ($2 is not null and r.fecha>=$2)) 
and ($3 is null or ($3 is not null and r.fecha<=$3)) 
and ($4 is null or ($4 is not null and r.periodo>=$4)) 
and ($5 is null or ($5 is not null and r.periodo<=$5)) 
and ($6 = 0 or $6 is null or ($6 != 0 and r.id_seccional = $6))
and ($10 is null or ($10 is not null and a.inte=$10))
and ($11 is null or $11 = '' or ($11 is not null and a.cuil_titular=$11)) 
and ($12 is null or $12 = '' or ($12 != '' and upper(r.alta_usr)=upper($12))) 
and ($9 is null or ($9 is not null and (
	(a.id_ospim = $9 and $8 = 'O.S.P.I.M.') or 
	(a.id_uoma = $9 and $8 = 'U.O.M.A.') or 
	(a.id_amtima = $9 and $8 = 'A.M.T.I.M.A.')))) 
and ($14 = 0 or $14 is null or ($14 != 0 and rp.id_medicamento=$14))
and m.fecha=(select max(fecha) from medicamentos m2 where m2.nro_registro=m.nro_registro)
and af.vigen_desde = (select max(vigen_desde) from afi_domicilio where cuil_titular = a.cuil_titular and inte = 0 and (af.baja_fecha is null or af.baja_fecha>r.fecha))
and rp.baja_fecha is null
--and ($15 = 0 or $15 is null or (15 is not null and $15 != 0 and rp.nro_receta=$15))


order by r.fecha
limit 1000;

end if;

--no pagados
if (estado_p = 1) then

CREATE TEMP TABLE REINTE_AUX AS
--todos los reintegros,
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
af.celular, 
af.cod_area_celular,  
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

--medicamento detalle

  rp.id_reintegro,
  rp.fecha,
  rp.nro_receta,
  rp.fecha_receta,
  rp.profesional,
  rp.cantidad,
  rp.troquel,
  rp.cober_sss,
  rp.cober_amtima,
  rp.cober_ospim,
  rp.monto_ospim,
  rp.monto_amtima,
  rp.precio_al_publico,
  rp.precio_ospim,  
  rp.total_med,
  rp.total_cobertura,
  rp.total,
  rp.alta_fecha as alta_fecha_rp,
  rp.alta_usr as alta_usr_rp,
  rp.mod_fecha as modi_fecha_rp,
  rp.modi_usr as modi_usr_rp,
  rp.baja_fecha as baja_fecha_rp,
  rp.baja_usr as baja_usr_rp,
  rp.id_medicamento as id_medicamento_rp,
  rp.id,  
--medicamento
  
  m.troquel as troquel_m,
  m.nombre,
  m.presentacion,
  m.monto_ioma,
  m.norma_ioma,
  m.cober_ioma,
  m.laboratorio,
  m.precio,
  m.fecha as fecha_m,
  m.controlado,
  m.importado ,
  m.tipo_venta ,
  m.iva ,
  m.cod_dto_pami ,
  
  m.cod_lab ,
  m.nro_registro ,
  m.baja ,
  m.cod_barra  ,
  m.unidades ,
  m.tamanio ,
  m.heladera ,
  m.sifar ,
  m.baja_especial ,	  
  m.accion  ,  
  m.droga  ,  
  m.id_medicamento ,

  opo.id_orden_pago,
  opos.nro_cheque,  
  opo.alta_fecha as fecha_op,
  opor.id_lista_reintegro_pago,

 trunc(v.porc_ospim,2) as cinco, 
 trunc(v.porc_amtima,2) as seis, 
 trunc(v.porc_sssalud,2) as siete,  
 v.pmoe_n as ocho
 
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
on a.cuil_titular = af.cuil_titular
and af.inte = 0 
--and (entidad_p = r.entidad) 
left outer join lista_reintegro_farmacia_pago_detalle opor
on r.id_reintegro = opor.id_reintegro
left outer join orden_pago_ospim_lista_reintegros_farmacia opol
on opor.id_lista_reintegro_pago = opol.id_lista_reintegro_pago
and opol.baja_fecha is null
left outer join orden_pago_ospim opo
on opol.id_orden_pago_ospim = opo.id_orden_pago
and opo.baja_fecha is null
left outer join orden_pago_ospim_pagos opos
on opo.id_orden_pago = opos.id_orden_pago
inner join medicamento_reintegro_farmacia rp
on r.id_reintegro = rp.id_reintegro 
left outer join medicamentos m
on rp.id_medicamento = m.id_medicamento
left outer join vademecum v
on v.registro=m.nro_registro
and m.fecha=(select max(fecha) from medicamentos m2 where m2.nro_registro=m.nro_registro)
where ($1 = 0 or $1 is null or ($1 != 0 and r.id_reintegro=$1))
and ($2 is null or ($2 is not null and r.fecha>=$2)) 
and ($3 is null or ($3 is not null and r.fecha<=$3)) 
and ($4 is null or ($4 is not null and r.periodo>=$4)) 
and ($5 is null or ($5 is not null and r.periodo<=$5))
and ($6 = 0 or $6 is null or ($6 != 0 and r.id_seccional = $6))
and ($12 = '' or $12 is null or  ($12 != '' and upper(r.alta_usr)=upper($12))) 
and ($9 is null or ($9 is not null and (
	(a.id_ospim = $9 and $8 = 'O.S.P.I.M.') or 
	(a.id_uoma = $9 and $8 = 'U.O.M.A.') or 
	(a.id_amtima = $9 and $8 = 'A.M.T.I.M.A.')))) 
and ($10 is null or ($10 is not null and a.inte=$10)) 
and ($11 is null or $11 = '' or ($11 is not null and a.cuil_titular=$11))
and ($14 = 0 or $14 is null or ($14 != 0 and rp.id_medicamento=$14))
--and ($15 = 0 or $14 is null or ($15 != 0 and rp.nro_receta=$15))
and rp.baja_fecha is null
and af.vigen_desde = (select max(vigen_desde) from afi_domicilio where cuil_titular = a.cuil_titular and inte = 0 and (af.baja_fecha is null or af.baja_fecha>r.fecha))
and (af.baja_fecha is null or af.baja_fecha>r.fecha)
and r.id_reintegro not in (select id_reintegro from lista_reintegro_farmacia_pago_detalle opor 
								inner join orden_pago_ospim_lista_reintegros_farmacia opolr
								on opor.id_lista_reintegro_pago = opolr.id_lista_reintegro_pago
								where opolr.baja_fecha is null)
and r.id_reintegro not in (select id_reintegro from lista_reintegro_farmacia_pago_detalle opor 
								inner join orden_pago_amtima_lista_reintegros opalr
								on opor.id_lista_reintegro_pago = opalr.id_lista_reintegro_pago
								where opalr.baja_fecha is null)
order by r.fecha
limit 1000;

end if;
--los pagados
if (estado_p = 2) then

CREATE TEMP TABLE REINTE_AUX AS
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
af.celular, 
af.cod_area_celular,  
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

--medicamento detalle

  rp.id_reintegro,
  rp.fecha,
  rp.nro_receta,
  rp.fecha_receta,
  rp.profesional,
  rp.cantidad,
  rp.troquel,
  rp.cober_sss,
  rp.cober_amtima,
  rp.cober_ospim,
  rp.monto_ospim,
  rp.monto_amtima,
  rp.precio_al_publico,
  rp.precio_ospim,  
  rp.total_med,
  rp.total_cobertura,
  rp.total,
  rp.alta_fecha as alta_fecha_rp,
  rp.alta_usr as alta_usr_rp,
  rp.mod_fecha as modi_fecha_rp,
  rp.modi_usr as modi_usr_rp,
  rp.baja_fecha as baja_fecha_rp,
  rp.baja_usr as baja_usr_rp,
  rp.id_medicamento as id_medicamento_rp,
  rp.id,  
--medicamento
  
  m.troquel as troquel_m,
  m.nombre,
  m.presentacion,
  m.monto_ioma,
  m.norma_ioma,
  m.cober_ioma,
  m.laboratorio,
  m.precio,
  m.fecha as fecha_m,
  m.controlado,
  m.importado ,
  m.tipo_venta ,
  m.iva ,
  m.cod_dto_pami ,
  
  m.cod_lab ,
  m.nro_registro ,
  m.baja ,
  m.cod_barra  ,
  m.unidades ,
  m.tamanio ,
  m.heladera ,
  m.sifar ,
  m.baja_especial ,	  
  m.accion  ,  
  m.droga  ,  
  m.id_medicamento ,

  opo.id_orden_pago,
  opos.nro_cheque,  
  opo.alta_fecha as fecha_op,
  opor.id_lista_reintegro_pago,

 trunc(v.porc_ospim,2) as cinco, 
 trunc(v.porc_amtima,2) as seis, 
 trunc(v.porc_sssalud,2) as siete,  
 v.pmoe_n as ocho
 
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
on a.cuil_titular = af.cuil_titular 
and af.inte = 0 
--and (entidad_p = r.entidad)  
inner join lista_reintegro_farmacia_pago_detalle opor
on r.id_reintegro = opor.id_reintegro
inner join orden_pago_ospim_lista_reintegros_farmacia opol
on opor.id_lista_reintegro_pago = opol.id_lista_reintegro_pago
inner join orden_pago_ospim opo
on opol.id_orden_pago_ospim = opo.id_orden_pago
inner join orden_pago_ospim_pagos opos
on opo.id_orden_pago = opos.id_orden_pago
inner join medicamento_reintegro_farmacia rp
on r.id_reintegro = rp.id_reintegro 
left outer join medicamentos m
on rp.id_medicamento = m.id_medicamento
left outer join vademecum v
on v.registro=m.nro_registro
where rp.baja_fecha is null
and m.fecha=(select max(fecha) from medicamentos m2 where m2.nro_registro=m.nro_registro)
and af.vigen_desde = (select max(vigen_desde) from afi_domicilio where cuil_titular = a.cuil_titular and inte = 0)
and opol.baja_fecha is null
and opo.baja_fecha is null
and ($1 = 0 or $1 is null or ($1 != 0 and r.id_reintegro=$1)) 
and ($2 is null or ($2 is not null and r.fecha>=$2)) 
and ($3 is null or ($3 is not null and r.fecha<=$3)) 
and ($4 is null or ($4 is not null and r.periodo>=$4)) 
and ($5 is null or ($5 is not null and r.periodo<=$5)) 
and ($6 = 0 or $6 is null or ($6 != 0 and r.id_seccional = $6))
and ($9 is null or ($9 is not null and (
	(a.id_ospim = $9 and $8 = 'O.S.P.I.M.') or 
	(a.id_uoma = $9 and $8 = 'U.O.M.A.') or 
	(a.id_amtima = $9 and $8 = 'A.M.T.I.M.A.')))) 
and ($10 is null or ($10 is not null and a.inte=$10)) 
and ($11 is null or $11 = '' or ($11 is not null and a.cuil_titular=$11))
and ($12 = '' or $12 is null or ($12 != '' and upper(r.alta_usr)=upper($12)))
and ($14 = 0 or $14 is null or ($14 != 0 and rp.id_medicamento=$14))
--and ($15 = 0 or ($15 != 0 and rp.nro_receta=$15))
union all 
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
af.celular, 
af.cod_area_celular,  
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

--medicamento detalle

  rp.id_reintegro,
  rp.fecha,
  rp.nro_receta,
  rp.fecha_receta,
  rp.profesional,
  rp.cantidad,
  rp.troquel,
  rp.cober_sss,
  rp.cober_amtima,
  rp.cober_ospim,
  rp.monto_ospim,
  rp.monto_amtima,
  rp.precio_al_publico,
  rp.precio_ospim,  
  rp.total_med,
  rp.total_cobertura,
  rp.total,
  rp.alta_fecha as alta_fecha_rp,
  rp.alta_usr as alta_usr_rp,
  rp.mod_fecha as modi_fecha_rp,
  rp.modi_usr as modi_usr_rp,
  rp.baja_fecha as baja_fecha_rp,
  rp.baja_usr as baja_usr_rp,
  rp.id_medicamento as id_medicamento_rp,
  rp.id,  
--medicamento
  
  m.troquel as troquel_m,
  m.nombre,
  m.presentacion,
  m.monto_ioma,
  m.norma_ioma,
  m.cober_ioma,
  m.laboratorio,
  m.precio,
  m.fecha as fecha_m,
  m.controlado,
  m.importado ,
  m.tipo_venta ,
  m.iva ,
  m.cod_dto_pami ,
  
  m.cod_lab ,
  m.nro_registro ,
  m.baja ,
  m.cod_barra  ,
  m.unidades ,
  m.tamanio ,
  m.heladera ,
  m.sifar ,
  m.baja_especial ,	  
  m.accion  ,  
  m.droga  ,  
  m.id_medicamento ,

  opo.id_orden_pago,
  opos.nro_cheque,  
  opo.alta_fecha as fecha_op,
  opor.id_lista_reintegro_pago,

 trunc(v.porc_ospim,2) as cinco, 
 trunc(v.porc_amtima,2) as seis, 
 trunc(v.porc_sssalud,2) as siete,  
 v.pmoe_n as ocho
 
from reintegro_farmacia r
inner join seccional s
on r.id_seccional = s.id_seccional
inner join afiliado a
on r.cuil_titular = a.cuil_titular
and r.inte = a.inte
inner join afi_domicilio af
on a.cuil_titular = af.cuil_titular 
and af.inte = 0 
and (af.baja_fecha is null or af.baja_fecha>r.fecha)
--and (entidad_p = r.entidad)  
inner join lista_reintegro_farmacia_pago_detalle opor
on r.id_reintegro = opor.id_reintegro
inner join orden_pago_amtima_lista_reintegros opol
on opor.id_lista_reintegro_pago = opol.id_lista_reintegro_pago
inner join orden_pago_amtima opo
on opol.id_orden_pago_amtima = opo.id_orden_pago
inner join orden_pago_amtima_pagos opos
on opo.id_orden_pago = opos.id_orden_pago
inner join medicamento_reintegro_farmacia rp
on r.id_reintegro = rp.id_reintegro 
left outer join medicamentos m
on rp.id_medicamento = m.id_medicamento
left outer join vademecum v
on v.registro=m.nro_registro
where rp.baja_fecha is null
and m.fecha=(select max(fecha) from medicamentos m2 where m2.nro_registro=m.nro_registro)
and af.vigen_desde = (select max(vigen_desde) from afi_domicilio where cuil_titular = a.cuil_titular and inte = 0)
and opol.baja_fecha is null
and opo.baja_fecha is null
and ($1 = 0 or $1 is null or ($1 != 0 and r.id_reintegro=$1)) 
and ($2 is null or ($2 is not null and r.fecha>=$2)) 
and ($3 is null or ($3 is not null and r.fecha<=$3)) 
and ($4 is null or ($4 is not null and r.periodo>=$4)) 
and ($5 is null or ($5 is not null and r.periodo<=$5)) 
and ($6 = 0 or $6 is null or ($6 != 0 and r.id_seccional = $6))
and ($9 is null or ($9 is not null and (
	(a.id_ospim = $9 and $8 = 'O.S.P.I.M.') or 
	(a.id_uoma = $9 and $8 = 'U.O.M.A.') or 
	(a.id_amtima = $9 and $8 = 'A.M.T.I.M.A.')))) 
and ($10 is null or ($10 is not null and a.inte=$10)) 
and ($11 is null or $11 = '' or ($11 is not null and a.cuil_titular=$11))
and ($12 = '' or $12 is null or ($12 != '' and upper(r.alta_usr)=upper($12)))
and ($14 = 0 or $14 is null or ($14 != 0 and rp.id_medicamento=$14))

order by 6
limit 1000;

end if;

 update REINTE_AUX ra
set id_orden_pago=opo.id_orden_pago,
     nro_cheque=opos.nro_cheque,  
     fecha_op=opo.alta_fecha
from orden_pago_amtima_lista_reintegros opol,orden_pago_amtima opo, orden_pago_amtima_pagos opos
where ra.id_lista_reintegro_pago = opol.id_lista_reintegro_pago
and opol.baja_fecha is null
and opol.id_orden_pago_amtima = opo.id_orden_pago
and opo.baja_fecha is null
and opo.id_orden_pago = opos.id_orden_pago
and ra.id_orden_pago is null;

return query
select * from REINTE_AUX order by 2;

END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
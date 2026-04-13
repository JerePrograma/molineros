-- DROP FUNCTION buscar_reporte_reintegros_odo_orto(integer);

CREATE OR REPLACE FUNCTION buscar_reporte_reintegros_odo_orto(IN id_lista integer)
  RETURNS TABLE(r_id_reintegro integer, r_cuil_titular character varying, r_inte integer, 
  r_fecha timestamp without time zone, r_periodo timestamp without time zone, r_liquidado timestamp without time zone, 
  r_auditado timestamp without time zone, r_alta_fecha timestamp without time zone, r_alta_usr character varying, 
  r_modi_fecha timestamp without time zone, r_modi_usr character varying, r_baja_fecha timestamp without time zone, 
  r_baja_usr character varying, r_estado integer, r_entidad character varying, r_tipo_reintegro character varying, 
  r_id_reintegro_user integer, rp_id_prestacion integer, rp_id_prestador integer, rp_id_plan integer, 
  rp_fecha_prestacion timestamp without time zone, rp_cantidad smallint, rp_importe numeric, 
  rp_compro_a_debitar_tipo character varying, rp_compro_a_debitar_numero character varying, 
  rp_tercerizado character varying, rp_cuit character varying, rp_descripcion character varying, 
  rp_alta_fecha timestamp without time zone, rp_alta_usr character varying, rp_modi_fecha timestamp without time zone, 
  rp_modi_usr character varying, rp_pieza character varying, rp_cara character varying, rp_honorarios numeric, 
  rp_gastos numeric, rp_id_prestador_externo integer, rp_codigo character varying, n_id_especialidad integer, 
  n_descripcion character varying, n_marca_rein_liq smallint, n_observaciones character varying, 
  n_alta_fecha timestamp without time zone, n_alta_usr character varying, n_modi_fecha timestamp without time zone, 
  n_modi_usr character varying, n_baja_fecha timestamp without time zone, n_baja_usr character varying, 
  n_codigo character varying, a_cuil_titular character varying, a_inte integer, a_id_ospim integer, 
  a_id_ospim_baja_fecha timestamp without time zone, a_id_uoma integer, a_id_uoma_baja_fecha timestamp without time zone, 
  a_id_amtima integer, a_id_amtima_baja_fecha timestamp without time zone, a_apellido character varying, 
  a_nombre character varying, a_documento_tipo character varying, a_sexo character varying, 
  a_cuil character varying, a_naci_fecha date, a_id_estado_civil_sss integer, a_civil_esta character varying, a_id_parentesco_sss integer, a_parentesco character varying, 
  a_ingre_fecha date, a_anterior_os integer, a_vigen_fecha timestamp without time zone, 
  a_observaciones character varying, a_pres_ssalud_fecha date, a_alta_fecha timestamp without time zone, 
  a_alta_usr character varying, a_modi_fecha timestamp without time zone, a_modi_usr character varying, 
  a_baja_fecha timestamp without time zone, a_baja_usr character varying, a_discapacitado character varying, 
  a_docu_numero character varying, a_nacionalidad integer, a_aportante_titular integer, a_nro_afiliado integer, 
  s_id_seccional integer, s_descripcion character varying, dc_id_reintegro_user integer, dc_id_reintegro integer, 
  dc_nro_cuota smallint, dc_fecha timestamp without time zone, dc_periodo timestamp without time zone, 
  dc_porcentaje smallint, dc_importe numeric, dc_diagnostico character varying, dc_plan_tratamiento character varying, 
  dc_tiempo_estimado character varying, dc_pronostico character varying, dc_informe character varying, 
  dc_compro_a_debitar_tipo character varying, dc_compro_a_debitar_numero character varying, 
  dc_estado integer, suma_por_afiliado numeric, suma_total numeric) AS
$BODY$

declare importe_total numeric(10,2);

begin

drop table if exists reintegro_afiliado_total;
drop table if exists total_importe_por_afiliado;

create temp table reintegro_afiliado_total as
select oppr.id_lista_reintegro_reporte, dc.id_cuota, cuil_titular, inte, rp.id_prestacion, sum(dc.importe) as importe, sum(1) as cantidad, sum(dc.importe)*sum(1) as total
from  lista_reintegro_reporte_detalle oppr, detalle_cuota dc, reintegro r, reintegro_prestacion_odo_ort rp
where 
oppr.id_lista_reintegro_reporte = id_lista
and oppr.id_reintegro = dc.id_cuota
and dc.id_reintegro = r.id_reintegro
and r.id_reintegro = rp.id_reintegro
group by oppr.id_lista_reintegro_reporte, dc.id_cuota, r.cuil_titular, r.inte, rp.id_prestacion;

create temp table total_importe_por_afiliado as
select cuil_titular, inte, sum(total) as total_afiliado
from reintegro_afiliado_total
group by cuil_titular, inte;

importe_total = sum(total)
from reintegro_afiliado_total;

return query
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
	r.id_reintegro_user,
	
	rp.id_prestacion,
	0,--rp.id_prestador,
	rp.id_plan,
	rp.fecha_prestacion,
	cast(1 as smallint), --rp.cantidad,	
	dc.importe, --imoprte de la cuota
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
	rp.codigo,
	
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

	a.cuil_titular,
	a.inte,
	a.id_ospim,
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
	
	--s.id_seccional,
	--s.descripcion, 
	cast(0 as integer),
	cast('' as character varying),
	
	dc.id_cuota,
	dc.id_reintegro,
	dc.nro_cuota,
	dc.fecha,
	dc.periodo,
	dc.porcentaje,
	dc.importe,
	dc.diagnostico,
	dc.plan_tratamiento,
	dc.tiempo_estimado,
	dc.pronostico,
	dc.informe,
	dc.compro_a_debitar_tipo,
	dc.compro_a_debitar_numero,
	dc.estado,
	
	tia.total_afiliado,
	importe_total	
	
	from lista_reintegro_reporte_detalle opor,
		detalle_cuota dc,
		reintegro r,
		reintegro_prestacion_odo_ort rp,
		nomenclador n,
	    afiliado a,
	    --seccional s,
	    total_importe_por_afiliado tia,
	    lista_reintegro_reporte lrp,
	    parentesco_sss,
	    estado_civil_sss ec
	    
	where opor.id_lista_reintegro_reporte = id_lista
	and opor.id_reintegro = dc.id_cuota
	and dc.id_reintegro = r.id_reintegro
	and r.id_reintegro = rp.id_reintegro 
	and rp.id_prestacion = n.id_prestacion
	and r.cuil_titular = a.cuil_titular 
	and r.inte = a.inte 
	and opor.id_lista_reintegro_reporte = lrp.id
	--and lrp.id_seccional = s.id_seccional
	and a.cuil_titular = tia.cuil_titular
	and a.inte = tia.inte
	and a.id_parentesco_sss = p.codigo
	and a.id_estado_civil_sss = ec.codigo;

END;  
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
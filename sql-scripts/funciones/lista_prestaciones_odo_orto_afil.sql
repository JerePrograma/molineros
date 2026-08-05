 CREATE OR REPLACE FUNCTION lista_prestaciones_odo_orto_afil(IN cuil character varying, IN inte integer)
	  RETURNS TABLE(
	  
	  r__fecha timestamp without time zone, 
	  r__periodo timestamp without time zone, 
	  rp__prestacion integer, 
	  rp__codigo character varying, 
	  r__id_seccional integer, 
	  r__cuil_titular character varying, 
	  r__inte integer, 
	  r__descripcion character varying, 
	  
	  r__reintegro integer, 
	  r__tipo_reintegro character varying, 
	  r__b_fecha timestamp without time zone, 
	  r__b_usr character varying,
	  
	  
	  rp__cuit character varying,
	  rp__descripcion character varying, 
	  rp__prestador integer, 
	  rp__importe numeric,
	  
	  r__id_plan integer, 
	  r__nombre_plan character varying, 
	  r__fecha_baja timestamp without time zone, 
	  r__id_orden_pago integer, 
	  r__nro_cheque numeric, 
	  r__fecha_op timestamp without time zone, 
	  opor_id_lista_reintegro_pago integer, 
	  r__estado integer, 
	  r__id_reintegro_user integer,
	  
		dc_id_reintegro_user integer,
		dc_id_reintegro integer,
		dc_nro_cuota smallint,
		dc_fecha timestamp without time zone,
		dc_periodo timestamp without time zone,
		dc_porcentaje smallint,
		dc_importe numeric,
		dc_diagnostico character varying,
		dc_plan_tratamiento character varying,
		dc_tiempo_estimado character varying,
		dc_pronostico character varying,
		dc_informe character varying,
		dc_compro_a_debitar_tipo character varying,
		dc_compro_a_debitar_numero character varying,
		dc_estado integer
	 ) AS
	  
	$BODY$
	select
	r.fecha,
	r.periodo,
	rp.id_prestacion,
	rp.codigo,
	s.id_seccional,
	a.cuil_titular,
	a.inte,
	s.descripcion,
	r.id_reintegro,
	r.tipo_reintegro,
	r.baja_fecha,
	r.baja_usr,
	
	
	rp.cuit,
	rp.descripcion,
	0,
	--rp.id_prestador,
	rp.importe,
	----
	
	p.id_plan,
	p.descripcion as nombre_plan,
	a.baja_fecha,
	opo.id_orden_pago,
	opos.nro_cheque,
	opo.alta_fecha as fecha_op,
	opor.id_lista_reintegro_pago,
	r.estado,
	r.id_reintegro_user,
	
	---------------
	
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
	dc.estado	
	----------------
		
	from reintegro r
	inner join seccional s
	on r.id_seccional = s.id_seccional
	and r.tipo_reintegro = 'ort'
	inner join detalle_cuota dc
	on r.id_reintegro = dc.id_reintegro
	inner join afiliado a
	on r.cuil_titular = a.cuil_titular
	and r.inte = a.inte	
	and
	($2 is null or ($2 is not null and a.inte=$2)) and
	($1 is null or $1 = '' or ($1 is not null and a.cuil_titular=$1))
		
	inner join lista_reintegro_pago_detalle opor
	on dc.id_cuota = opor.id_reintegro --en este caso el join se hace por detalle_cuenta	
	
	inner join orden_pago_ospim_lista_reintegros opol
	on opor.id_lista_reintegro_pago = opol.id_lista_reintegro_pago
	and opol.baja_fecha is null
	
	inner join orden_pago_ospim opo
	on opol.id_orden_pago_ospim = opo.id_orden_pago
	and opo.baja_fecha is null
	
	left outer join orden_pago_ospim_pagos opos
	on opo.id_orden_pago = opos.id_orden_pago		
	
	left outer join
	afi_plan ap on a.cuil_titular = ap.cuil_titular and ap.inte=0
	and ap.alta_fecha = (select max(ap2.alta_fecha) from afi_plan ap2 where ap2.cuil_titular = ap.cuil_titular and ap2.inte = ap.inte)
	
	left outer join plan p on ap.id_plan = p.id_plan
	
	left outer join reintegro_prestacion_odo_ort rp
	on r.id_reintegro = rp.id_reintegro
	
	left outer join nomenclador n
	on rp.id_prestacion = n.id_prestacion
	
	order by r.fecha
	limit 200;
	$BODY$
	  LANGUAGE 'sql' VOLATILE
	  COST 100
	  ROWS 1000;

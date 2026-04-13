CREATE OR REPLACE FUNCTION buscar_reintegros_odo_orto_impagos(IN reintegro integer, IN fecha_desde timestamp without time zone, IN fecha_hasta timestamp without time zone, IN pediodo_desde timestamp without time zone, IN periodo_hasta timestamp without time zone, IN seccional integer, IN prestacion character varying, IN entidad character varying, IN nro_afi integer, IN inte integer, IN cuil character varying, IN alta_usr character varying, IN estado integer)
  RETURNS TABLE(r__fecha timestamp without time zone, r__periodo timestamp without time zone, rp__prestacion integer, rp__codigo character varying, r__id_seccional integer, r__cuil_titular character varying, r__inte integer, r__id_ospim integer, r__descripcion character varying, r__reintegro integer, r__tipo_reintegro character varying, r__b_fecha timestamp without time zone, r__b_usr character varying, rp__cuit character varying, rp__descripcion character varying, rp__prestador integer, rp__importe numeric, r__id_plan integer, r__nombre_plan character varying, r__fecha_baja timestamp without time zone, r__id_orden_pago integer, r__nro_cheque numeric, r__fecha_op timestamp without time zone, opor_id_lista_reintegro_pago integer, r__estado integer, r__id_reintegro_user integer, dc_id_reintegro_user integer, dc_id_reintegro integer, dc_nro_cuota smallint, dc_fecha timestamp without time zone, dc_periodo timestamp without time zone, dc_porcentaje smallint, dc_importe numeric, dc_diagnostico character varying, dc_plan_tratamiento character varying, dc_tiempo_estimado character varying, dc_pronostico character varying, dc_informe character varying, dc_compro_a_debitar_tipo character varying, dc_compro_a_debitar_numero character varying, dc_estado integer) AS
$BODY$
	select

	r.fecha,
		r.periodo,
		rp.id_prestacion,
		rp.codigo,
		s.id_seccional,
		a.cuil_titular,
		a.inte,
		a.id_ospim,
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
		p.id_plan,
		p.descripcion as nombre_plan,
		a.baja_fecha,		
		0,
		
		cast (null as numeric),
		cast (null as timestamp without time zone),

		opor.id_lista_reintegro_pago,
		r.estado,
		r.id_reintegro_user,
		
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
		
	from	reintegro r	
	inner join seccional s
	on r.id_seccional = s.id_seccional

	inner join detalle_cuota dc
	on r.id_reintegro = dc.id_reintegro 

	and (dc.id_cuota = 0 or dc.id_cuota not in (select id_reintegro from lista_reintegro_pago_detalle opor 
								inner join orden_pago_ospim_lista_reintegros opolr 
								on opor.id_lista_reintegro_pago = opolr.id_lista_reintegro_pago
								where opor.id_reintegro = dc.id_cuota and
								opolr.baja_usr is null
							  ))	
	inner join afiliado a
	on r.cuil_titular = a.cuil_titular
	and r.inte = a.inte
	and
	--	($8 = r.entidad) and
		r.tipo_reintegro = 'ort' and	
		($1 = 0 or ($1 != 0 and dc.id_cuota=$1)) and
		($2 is null or ($2 is not null and r.fecha>=$2)) and
		($3 is null or ($3 is not null and r.fecha<=$3)) and
		($4 is null or ($4 is not null and r.periodo>=$4)) and
		($5 is null or ($5 is not null and r.periodo<=$5)) and
		($6 = 0 or ($6 != 0 and r.id_seccional = $6)) and
		($12 = '' or ($12 != '' and r.alta_usr=$12)) and
		($13 = 0 or ($13 != 0 and dc.estado=$13)) and
		($9 is null or 
			($9 is not null and 
				(
				(a.id_ospim = $9 and $8 = 'O.S.P.I.M.') or 
				(a.id_uoma = $9 and $8 = 'U.O.M.A.') or 
				(a.id_amtima = $9 and $8 = 'A.M.T.I.M.A.')
				)
			)
		) and
		($10 is null or ($10 is not null and a.inte=$10)) and
		($11 is null or $11 = '' or ($11 is not null and a.cuil_titular=$11))
	left outer join
	afi_plan ap on a.cuil_titular = ap.cuil_titular and ap.inte=0
	and ap.alta_fecha = (select max(ap2.alta_fecha) from afi_plan ap2 where ap2.cuil_titular = ap.cuil_titular and ap2.inte = ap.inte)		
	left outer join plan p on ap.id_plan = p.id_plan 
	left outer join reintegro_prestacion_odo_ort rp
	
	on r.id_reintegro = rp.id_reintegro
	and ($7 is null or $7 = '' or ($7 is not null and rp.codigo = $7))
	
	left outer join lista_reintegro_pago_detalle opor	
	on dc.id_cuota = opor.id_reintegro
	and opor.tipo_reintegro = 'ort'
	
	left outer join orden_pago_ospim_lista_reintegros opolr on 
	opor.id_lista_reintegro_pago = opolr.id_lista_reintegro_pago
	and opolr.baja_usr is null
	
	order by a.id_ospim, a.cuil_titular, a.inte, r.id_reintegro, dc.nro_cuota
	limit 200;
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
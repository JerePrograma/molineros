DROP FUNCTION buscar_reintegroslist(p_idseccional integer, p_date_ini timestamp without time zone, p_date_fin timestamp without time zone);

CREATE OR REPLACE FUNCTION buscar_reintegroslist(p_idseccional integer, p_date_ini timestamp without time zone, p_date_fin timestamp without time zone) 
RETURNS TABLE(id_lista_reintegro_pago integer, 
	id_reintegro integer,
	importe numeric(10,2),
	id_seccional integer,
	cuil_titular character varying)
    LANGUAGE sql
    AS $BODY$

    select
	d.id_lista_reintegro_pago,
	d.id_reintegro ,
	d.importe ,
	r.id_seccional,
	r.cuil_titular
	 from lista_reintegro_pago_detalle d inner join reintegro r
	 on d.id_reintegro = r.id_reintegro
	where 
	(d.tipo_reintegro is null or d.tipo_reintegro = '') and 
	id_lista_reintegro_pago  in (select id from lista_reintegro_pago lrp
										where lrp.id_seccional = $1
										and lrp.alta_fecha >= $2
										and lrp.alta_fecha < $3 + interval '1 day'  
										and id not in (select id_lista_reintegro_pago from orden_pago_ospim_lista_reintegros where baja_fecha is null))
	 
	union 
	
	select 	
	d.id_lista_reintegro_pago,
	dc.id_cuota ,
	d.importe ,
	r.id_seccional,
	r.cuil_titular
	from lista_reintegro_pago_detalle d inner join 
	 detalle_cuota dc on d.id_reintegro = dc.id_cuota
	 inner join reintegro r
	 on dc.id_reintegro = r.id_reintegro
	where 
	d.tipo_reintegro = 'ort' and 
	id_lista_reintegro_pago  in (select id from lista_reintegro_pago lrp
										where lrp.id_seccional = $1
										and lrp.alta_fecha >= $2
										and lrp.alta_fecha < $3 + interval '1 day'  
										and id not in (select id_lista_reintegro_pago from orden_pago_ospim_lista_reintegros where baja_fecha is null));
										
$BODY$;


ALTER FUNCTION public.buscar_reintegroslist(p_idseccional integer, p_date_ini timestamp without time zone, p_date_fin timestamp without time zone)  OWNER TO postgres;
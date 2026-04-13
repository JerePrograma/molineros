CREATE OR REPLACE FUNCTION buscar_reintegros_farmacia_list(IN p_idseccional integer, IN p_date_ini timestamp without time zone, IN p_date_fin timestamp without time zone)
  RETURNS TABLE(id_lista_reintegro_pago integer, id_reintegro integer, importe numeric, id_seccional integer, cuil_titular character varying) AS
$BODY$

    select
	d.id_lista_reintegro_pago,
	d.id_reintegro ,
	d.importe ,
	r.id_seccional,
	r.cuil_titular
	 from lista_reintegro_farmacia_pago_detalle d inner join reintegro_farmacia r
	 on d.id_reintegro = r.id_reintegro
	where 
	(d.tipo_reintegro is null or d.tipo_reintegro = '') and 
	id_lista_reintegro_pago  in (select id from lista_reintegro_farmacia_pago lrp
										where lrp.id_seccional = $1
										and lrp.alta_fecha >= $2
										and lrp.alta_fecha < $3 + interval '1 day'  
										and (id not in (select id_lista_reintegro_pago from orden_pago_ospim_lista_reintegros where baja_fecha is null)) 
										and (id not in (select id_lista_reintegro_pago from orden_pago_ospim_lista_reintegros_farmacia where baja_fecha is null)))
										
$BODY$
  LANGUAGE sql VOLATILE


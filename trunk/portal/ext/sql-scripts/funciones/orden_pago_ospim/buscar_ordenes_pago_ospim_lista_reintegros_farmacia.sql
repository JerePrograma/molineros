CREATE OR REPLACE FUNCTION buscar_ordenes_pago_ospim_lista_reintegros_farmacia(IN p_op_ospim_id integer)
  RETURNS TABLE(id_lista_reintegro_pago integer, id_reintegro integer, importe numeric, id_seccional integer) AS
$BODY$
		select 	
		d.id_lista_reintegro_pago,
		d.id_reintegro ,
		d.importe ,
		lrp.id_seccional
		from orden_pago_ospim_lista_reintegros_farmacia opr
		inner join lista_reintegro_pago lrp
		on opr.id_lista_reintegro_pago = lrp.id
		inner join lista_reintegro_pago_detalle d
		on lrp.id = d.id_lista_reintegro_pago 
		where id_orden_pago_ospim = $1;
$BODY$
  LANGUAGE sql VOLATILE

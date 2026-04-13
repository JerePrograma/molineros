create or replace function insertar_canje_cheque_propio(p_op_id integer, p_op_id_nueva integer, p_id_movimiento integer, p_user character varying)
  RETURNS integer AS
$BODY$
BEGIN
	insert into canje_cheques_propios (id_orden_pago_ospim, alta_fecha, alta_usr, modi_fecha, modi_usr, id_orden_pago_ospim_nueva, id_movimiento)
	values (p_op_id, localtimestamp, p_user, localtimestamp, p_user, p_op_id_nueva, p_id_movimiento);

	return currval('canje_cheques_propios_id_seq');
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;


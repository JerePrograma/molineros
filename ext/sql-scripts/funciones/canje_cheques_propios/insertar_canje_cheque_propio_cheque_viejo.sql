create or replace function insertar_canje_cheque_propio_cheque_viejo(p_canje_id integer, p_nro_cheque numeric, p_id_banco integer)
  RETURNS integer AS
$BODY$
BEGIN
	insert into canje_cheques_propios_viejos (canje_id, nro_cheque, id_banco)
	values (p_canje_id, p_nro_cheque, p_id_banco);

	return 0;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;


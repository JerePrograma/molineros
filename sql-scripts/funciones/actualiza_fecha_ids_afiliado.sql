CREATE OR REPLACE FUNCTION actualiza_fecha_ids_afiliado(p_modificouoma boolean, p_fechabajauoma date, p_modificoamtima boolean, p_fechabajaamtima date, p_modificoospim boolean, p_fechabajaospim date, p_cuil character varying)
  RETURNS integer AS
$BODY$
BEGIN

	if p_modificoOspim = true then
		UPDATE afiliado SET id_ospim_baja_fecha = p_fechaBajaOspim where cuil_titular = p_cuil;
	end if;
	if p_modificoUoma = true then
		UPDATE afiliado set id_uoma_baja_fecha = p_fechaBajaUoma where cuil_titular = p_cuil;
	end if;
	if p_modificoAmtima = true then
		UPDATE afiliado set id_amtima_baja_fecha = p_fechaBajaAmtima where cuil_titular = p_cuil;
	end if;

return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE


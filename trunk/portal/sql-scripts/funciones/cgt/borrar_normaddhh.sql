CREATE OR REPLACE FUNCTION borrar_normaddhh(id_normadh_p integer, p_user character varying)
  RETURNS integer AS
$BODY$
BEGIN
update norma_ddhh set baja_fecha=current_date, baja_usr = p_user  where id=$1;
return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION borrar_normaddhh(integer, character varying)
  OWNER TO postgres;
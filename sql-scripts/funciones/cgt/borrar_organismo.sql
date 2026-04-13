CREATE OR REPLACE FUNCTION borrar_organismo(id_organismo_p integer)
  RETURNS integer AS
$BODY$
BEGIN
update organismo set baja_fecha=current_date where id_organismo=$1;
update organismo_contacto set baja_fecha=current_date where id_organismo=$1;
update organismo_linea set baja_fecha=current_date where id_organismo=$1;
update organismo_comentario set baja_fecha=current_date where id_organismo=$1;
return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE

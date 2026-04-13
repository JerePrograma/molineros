CREATE OR REPLACE FUNCTION borrar_area(id_area_p integer)
  RETURNS integer AS
$BODY$
BEGIN
update area set baja_fecha=current_date where id_area=$1;
update area_contacto set baja_fecha=current_date where id_area=$1;
update area_linea set baja_fecha=current_date where id_area=$1;
update area_comentario set baja_fecha=current_date where id_area=$1;
return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE

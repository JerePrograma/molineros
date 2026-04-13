CREATE OR REPLACE FUNCTION borrar_listas_boletin(id_boletin_p integer, username_p character varying)
  RETURNS void AS
$BODY$
update boletin_listas
set baja_fecha=current_date,
    baja_user=$2
where id_boletin=$1
    
$BODY$
  LANGUAGE sql VOLATILE

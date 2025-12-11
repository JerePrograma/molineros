CREATE OR REPLACE FUNCTION borrar_listas_subscriber(id_subscriber_p integer, username_p character varying)
  RETURNS void AS
$BODY$
update subscriber_mailing
set baja_fecha=current_date,
    baja_user=$2
where id_subscriber=$1
    
$BODY$
  LANGUAGE sql VOLATILE

CREATE OR REPLACE FUNCTION borrar_lista_destinatarios(id_lista_p integer, username_p character varying)
  RETURNS void AS
$BODY$
update subscriber_mailing
set baja_fecha=current_date,
    baja_user=$2
where id_mailing_list=$1


    
$BODY$
  LANGUAGE sql VOLATILE

CREATE OR REPLACE FUNCTION borrar_destinatario(id_destinatario_p integer, username_p character varying)
  RETURNS integer AS
$BODY$
update subscriber_mailing
set baja_fecha=current_date,
    baja_user=$2
where id_subscriber=$1;


update mail_subscriber  
set baja_fecha=current_date,
    baja_user=$2
where id=$1;

select 0

    
$BODY$
  LANGUAGE sql VOLATILE

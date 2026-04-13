CREATE OR REPLACE FUNCTION actualizar_destinatario(id_dest_p integer, nombre_p character varying, apellido_p character varying, title_p character varying, email_p character varying, is_prueba_p boolean, username_p character varying)
  RETURNS void AS
$BODY$

update mail_subscriber
set nombre=$2,
    apellido=$3,
    tratamiento=$4,
    email=$5,
    casilla_prueba=$6,
    modi_user=$7
where id=$1
    
$BODY$
  LANGUAGE sql VOLATILE
  COST 100;
ALTER FUNCTION actualizar_destinatario(integer, character varying, character varying, character varying, character varying, boolean, character varying)
  OWNER TO postgres;


CREATE OR REPLACE FUNCTION inserta_subscriber(id_subscriberp integer, nombre_p character varying, apellido_p character varying, tratamiento_p character varying, email_p character varying, is_prueba_p boolean, alta_user_p character varying)
  RETURNS integer AS
$BODY$
BEGIN
INSERT INTO mail_subscriber(id_subscriber, nombre, apellido, tratamiento, email, casilla_prueba, casilla_prueba, alta_user, alta_fecha)
VALUES ($1, $2, $3, $4, $5, $6, $7, current_date );
return currval('mail_subscriber_id_seq');
END;
$BODY$
  LANGUAGE plpgsql VOLATILE

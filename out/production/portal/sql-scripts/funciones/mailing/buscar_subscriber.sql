CREATE OR REPLACE FUNCTION buscar_subscriber(IN id_subscriber_p integer)
  RETURNS TABLE(id_destinatario integer, nombre character varying, apellido character varying, tratamiento character varying, email character varying, listas character varying, casilla_prueba boolean) AS
$BODY$
select s.id, nombre, apellido, tratamiento, email, array_to_string(array(SELECT sm.id_mailing_list 
									 FROM subscriber_mailing sm
									 where sm.id_subscriber=$1
									 and sm.baja_fecha is null), ','), casilla_prueba
from mail_subscriber s
where s.id=$1
$BODY$
  LANGUAGE sql VOLATILE

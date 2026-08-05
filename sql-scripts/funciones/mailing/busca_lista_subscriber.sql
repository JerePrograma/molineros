CREATE OR REPLACE FUNCTION buscar_lista_subscriber(IN id_lista_p integer)
  RETURNS TABLE(id_destinatario integer, nombre character varying, apellido character varying, tratamiento character varying, email character varying) AS
$BODY$
select s.id, nombre, apellido, tratamiento, email
from subscriber_mailing sm, mail_subscriber s
where sm.id_subscriber=s.id
and id_mailing_list=$1
and sm.baja_fecha is null
and s.baja_fecha is null
$BODY$
  LANGUAGE sql VOLATILE

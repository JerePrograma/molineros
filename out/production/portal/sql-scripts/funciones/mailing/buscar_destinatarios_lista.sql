CREATE OR REPLACE FUNCTION buscar_destinatarios_lista(IN id_lista_p integer)
  RETURNS TABLE(id_destinatario integer, nombre character varying, apellido character varying, tratamiento character varying, email character varying, casilla_prueba boolean) AS
$BODY$
select ml.id, ml.nombre, ml.apellido, ml.tratamiento, ml.email, ml.casilla_prueba
from mail_subscriber ml, subscriber_mailing ms
where ms.id_mailing_list=$1
and ms.id_subscriber=ml.id
and ml.baja_fecha is null
and ms.baja_fecha is null
$BODY$
  LANGUAGE sql VOLATILE

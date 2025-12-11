CREATE OR REPLACE FUNCTION buscar_lista_mailing(IN id_lista_p integer)
  RETURNS TABLE(id_lista integer, nombre character varying, observaciones character varying, alta_user character varying) AS
$BODY$
select id, descripcion, observaciones, alta_user
from mailing_list o
where o.baja_fecha is null
and id=$1
$BODY$
  LANGUAGE sql VOLATILE

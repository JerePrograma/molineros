CREATE OR REPLACE FUNCTION buscar_listas_mailing(IN nombre character varying)
  RETURNS TABLE(id_lista integer, nombre character varying, observaciones character varying, alta_user character varying) AS
$BODY$
select id, descripcion, observaciones, alta_user
from mailing_list o
where o.baja_fecha is null
$BODY$
  LANGUAGE sql VOLATILE
  COST 100

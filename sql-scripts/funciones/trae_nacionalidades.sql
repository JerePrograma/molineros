CREATE OR REPLACE FUNCTION trae_nacionalidades()
  RETURNS TABLE(id integer, detalle character varying, id_sssuper integer) AS
$BODY$
select id, 
       detalle,
       id_sssuper 
from nacionalidad 
order by detalle
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
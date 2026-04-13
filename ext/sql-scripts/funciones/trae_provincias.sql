CREATE OR REPLACE FUNCTION trae_provincias() 
RETURNS TABLE(id_provincia integer,
 detalle character varying, id_sssalud integer)
    LANGUAGE sql
    AS $BODY$
select id_provincia, 
       detalle,
       id_sssalud
from provincia 
order by detalle
$BODY$;

CREATE OR REPLACE FUNCTION trae_usuarios_alta_reintegros() 
RETURNS TABLE(username character varying)
    LANGUAGE sql
    AS $BODY$
select distinct lower(alta_usr)
from reintegro_farmacia 
where alta_usr != '';
 $BODY$;
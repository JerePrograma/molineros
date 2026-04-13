CREATE OR REPLACE FUNCTION trae_chequeras() 
RETURNS TABLE(id_chequera integer,
 id_cuenta integer,
 descripcion character varying)
    LANGUAGE sql
    AS $BODY$
select id_chequera, id_cuenta, descripcion
from chequera
$BODY$;


ALTER FUNCTION public.trae_chequeras() OWNER TO postgres;

--

CREATE OR REPLACE FUNCTION trae_situaciones_revista() 
RETURNS TABLE(id integer,
 detalle character varying)
    LANGUAGE sql
    AS $BODY$
select id_situ_revista, 
       detalle
from situacion_revista
order by detalle
$BODY$;


ALTER FUNCTION public.trae_situaciones_revista() OWNER TO postgres;

--

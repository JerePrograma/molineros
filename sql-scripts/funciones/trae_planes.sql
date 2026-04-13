CREATE OR REPLACE FUNCTION trae_planes() 
RETURNS TABLE(id_plan integer,
 descripcion character varying)
    LANGUAGE sql
    AS $BODY$
select id_plan, 
       descripcion 
from plan 
where baja_fecha is null
order by descripcion
$BODY$;


ALTER FUNCTION public.trae_planes() OWNER TO postgres;

--

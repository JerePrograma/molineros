CREATE OR REPLACE FUNCTION trae_planes_omint() 
RETURNS TABLE(id_plan integer,
 id_plan_omint integer, descripcion character varying)
    LANGUAGE sql
    AS $BODY$
select id_plan, 
       id_plan_omint,
       descripcion
from plan_omint 
order by id_plan_omint
$BODY$;


ALTER FUNCTION public.trae_planes_omint() OWNER TO postgres;

--

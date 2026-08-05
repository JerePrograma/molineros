CREATE OR REPLACE FUNCTION trae_aportes_plan(id_plan_v integer) 
RETURNS TABLE(id_plan integer,
 descripcion character varying)
    LANGUAGE sql
    AS $BODY$
select a.id_aporte, 
       a.descripcion 
from plan_aporte pa,plan p, aporte a  
where pa.id_plan=$1
and p.id_plan=pa.id_plan
and a.id_aporte=pa.id_aporte
order by descripcion
$BODY$;


ALTER FUNCTION public.trae_aportes_plan(id_plan_v integer) OWNER TO postgres;

--

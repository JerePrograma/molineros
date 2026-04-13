CREATE OR REPLACE FUNCTION trae_plan_afiliado_fecha(cuilv character varying,
 intev integer,
 p_periodo timestamp without time zone) 
RETURNS TABLE(id_plan integer,
 descripcion character varying)
    LANGUAGE sql
    AS $BODY$
select a.id_plan,p.descripcion
from afi_plan a, plan p
where p.id_plan=a.id_plan
--and a.vigen_desde <= current_timestamp 
and (a.baja_fecha is null  or a.baja_fecha > $3)
and a.cuil_titular=$1
and a.inte=0
$BODY$;


ALTER FUNCTION public.trae_plan_afiliado_fecha(cuilv character varying, intev integer, p_periodo timestamp without time zone) OWNER TO postgres;

--

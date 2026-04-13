CREATE OR REPLACE FUNCTION trae_inspectores() 
RETURNS TABLE(id integer,
 nombre character varying,
 alta_fecha timestamp without time zone,
 alta_usr character varying,
 modi_fecha timestamp without time zone,
 modi_usr character varying,
 baja_fecha timestamp without time zone,
 baja_usr character varying)
    LANGUAGE sql
    AS $BODY$
select id,
  nombre,
  alta_fecha,
  alta_usr,
  modi_fecha,
  modi_usr,
  baja_fecha,
  baja_usr
from inspector 
order by nombre
$BODY$;


ALTER FUNCTION public.trae_inspectores() OWNER TO postgres;

--

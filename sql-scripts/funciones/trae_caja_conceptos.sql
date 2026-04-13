CREATE OR REPLACE FUNCTION trae_caja_conceptos() 
RETURNS TABLE(
id integer,
descripcion character varying,
codigo character varying,
tipo char,
ex_id_cuenta character varying,
alta_fecha timestamp without time zone,
alta_usr character varying,
modi_fecha timestamp without time zone,
modi_usr character varying,
baja_fecha timestamp without time zone,
baja_usr character varying)
    LANGUAGE sql
    AS $BODY$

select 
id,
descripcion,
codigo,
tipo,
ex_id_cuenta,
alta_fecha,
alta_usr,
modi_fecha,
modi_usr,
baja_fecha,
baja_usr
from caja_conceptos
order by descripcion asc;
$BODY$;


ALTER FUNCTION public.trae_caja_conceptos() OWNER TO postgres;

--

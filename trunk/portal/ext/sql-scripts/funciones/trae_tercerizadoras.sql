CREATE OR REPLACE FUNCTION trae_tercerizadoras() 
RETURNS TABLE(id_tercerizadora character varying,
 descripcion character varying)
    LANGUAGE sql
    AS $BODY$
select id_tercerizadora, 
       descripcion
from tercerizadora_servicio
where baja_fecha is null
order by descripcion
$BODY$;


ALTER FUNCTION public.trae_tercerizadoras() OWNER TO postgres;

--

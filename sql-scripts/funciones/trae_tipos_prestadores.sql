CREATE OR REPLACE FUNCTION trae_tipos_prestadores() 
RETURNS TABLE(id_tipo_prestador smallint,
 descripcion character varying)
    LANGUAGE sql
    AS $BODY$
select id_tipo_prestador, 
       descripcion
from tipo_prestador
order by descripcion
$BODY$;


ALTER FUNCTION public.trae_tipos_prestadores() OWNER TO postgres;

--

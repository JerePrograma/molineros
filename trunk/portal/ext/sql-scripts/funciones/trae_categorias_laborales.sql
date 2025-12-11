CREATE OR REPLACE FUNCTION trae_categorias_laborales() 
RETURNS TABLE(id integer,
 detalle character varying)
    LANGUAGE sql
    AS $BODY$
select id_categoria, 
       categoria
from categoria_laboral
where baja_fecha is null
order by categoria
$BODY$;


ALTER FUNCTION public.trae_categorias_laborales() OWNER TO postgres;

--

CREATE OR REPLACE FUNCTION trae_posicion_iva() 
RETURNS TABLE(id_posicion integer,
 detalle character varying)
    LANGUAGE sql
    AS $BODY$
select id_posicion, 
       detalle
from posicion_iva
order by detalle
$BODY$;


ALTER FUNCTION public.trae_posicion_iva() OWNER TO postgres;

--

CREATE OR REPLACE FUNCTION trae_prestadores() 
RETURNS TABLE(id_prestador integer,
 cuit character varying,
 descripcion character varying)
    LANGUAGE sql
    AS $BODY$
select id_prestador,cuit,descripcion from prestador order by descripcion
$BODY$;


ALTER FUNCTION public.trae_prestadores() OWNER TO postgres;

--

CREATE OR REPLACE FUNCTION trae_obras_sociales() 
RETURNS TABLE(codigo integer,
 razon character varying)
    LANGUAGE sql
    AS $BODY$
select codigo,razon from obra_social order by razon
$BODY$;


ALTER FUNCTION public.trae_obras_sociales() OWNER TO postgres;

--

CREATE OR REPLACE FUNCTION cerrar_acta(p_acta_id integer,
 p_usr character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
BEGIN
update acta set acta_cerrada = true, cierre_usr = p_usr where id = p_acta_id;
    
return 1;
END;
$BODY$;


ALTER FUNCTION public.cerrar_acta(p_acta_id integer, p_usr character varying) OWNER TO postgres;

--

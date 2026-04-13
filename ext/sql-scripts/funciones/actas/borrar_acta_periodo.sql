CREATE OR REPLACE FUNCTION borrar_acta_periodo(p_acta_periodo_id integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
BEGIN
delete from acta_periodos where id = p_acta_periodo_id;
    
return 1;
END;
$BODY$;


ALTER FUNCTION public.borrar_acta_periodo(p_acta_periodo_id integer) OWNER TO postgres;

--

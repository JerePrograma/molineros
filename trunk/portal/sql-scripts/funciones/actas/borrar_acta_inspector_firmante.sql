CREATE OR REPLACE FUNCTION borrar_acta_inspector_firmante(p_acta_id integer,
 p_inspector_id integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
BEGIN
delete from acta_inspector where id_acta = p_acta_id and  id_inspector =  p_inspector_id;
    
return 1;
END;
$BODY$;


ALTER FUNCTION public.borrar_acta_inspector_firmante(p_acta_id integer, p_inspector_id integer) OWNER TO postgres;

--

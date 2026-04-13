CREATE OR REPLACE FUNCTION borrar_detalle_acta(p_detalle_insp_id integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
BEGIN
delete from acta_detalle_inspectores where id = p_detalle_insp_id;
    
return 1;
END;
$BODY$;


ALTER FUNCTION public.borrar_detalle_acta(p_detalle_insp_id integer) OWNER TO postgres;

--

CREATE OR REPLACE FUNCTION inserta_acta_inspector_firmante(p_acta_id integer,
 p_inspector_id integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
BEGIN
INSERT INTO acta_inspector(
            id_acta, id_inspector)
    VALUES (p_acta_id, p_inspector_id);

return 1;
END;
$BODY$;


ALTER FUNCTION public.inserta_acta_inspector_firmante(p_acta_id integer, p_inspector_id integer) OWNER TO postgres;

--

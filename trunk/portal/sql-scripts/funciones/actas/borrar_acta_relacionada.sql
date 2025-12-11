CREATE OR REPLACE FUNCTION borrar_acta_relacionada(p_acta_rel_id integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
BEGIN
delete from acta_pagos where acta_relacion_id = p_acta_rel_id;
delete from acta_relacion where id =  p_acta_rel_id;
    
return 1;
END;
$BODY$;


ALTER FUNCTION public.borrar_acta_relacionada(p_acta_rel_id integer) OWNER TO postgres;

--

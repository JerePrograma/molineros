CREATE OR REPLACE FUNCTION borra_cuota_ortopedia(id_reintegro integer,
 username character varying) 
RETURNS integer
    LANGUAGE sql
    AS $BODY$
    delete from detalle_cuota
    where id_reintegro=$1;
    select 1;
$BODY$;

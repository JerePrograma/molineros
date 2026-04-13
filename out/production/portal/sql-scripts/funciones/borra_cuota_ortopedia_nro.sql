CREATE OR REPLACE FUNCTION borra_cuota_ortopedia_nro(id_reintegro integer, id_cuota integer) 
RETURNS integer
    LANGUAGE sql
    AS $BODY$
    delete from detalle_cuota
    where id_reintegro=$1
    and nro_cuota=$2;
    select 1;
$BODY$;

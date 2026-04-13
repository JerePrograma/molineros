CREATE OR REPLACE FUNCTION borra_reintegro_farmacia(id_reintegro integer,
 username character varying) 
RETURNS integer
    LANGUAGE sql
    AS $BODY$
    update reintegro_farmacia
    set baja_usr=$2,
    baja_fecha=current_timestamp
    where id_reintegro=$1;
    delete from lista_reintegro_farmacia_pago_detalle l where l.id_reintegro = $1;
    select 1;
$BODY$;
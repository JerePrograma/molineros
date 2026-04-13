CREATE OR REPLACE FUNCTION trae_id_primer_reintegro_lista(id_lista integer) 
RETURNS TABLE(id_reintegro integer)
    LANGUAGE sql
    AS $BODY$

select min(id_reintegro) as id_reintegro from lista_reintegro_pago_detalle  where id_lista_reintegro_pago = $1  

$BODY$;
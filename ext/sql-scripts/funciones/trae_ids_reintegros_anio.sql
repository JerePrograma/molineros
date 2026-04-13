CREATE OR REPLACE FUNCTION trae_ids_reintegros_anio(cuil_v character,
 inte_v integer,
 prestacion integer) 
RETURNS TABLE(id_reintegro_user int)
    LANGUAGE sql
    AS $BODY$

select r.id_reintegro_user  from reintegro r, reintegro_prestacion rp
where 
r.id_reintegro = rp.id_reintegro
and r.cuil_titular = $1
and r.inte = $2
and rp.id_prestacion = $3
and rp.fecha_prestacion >= (select date_trunc('year', localtimestamp));

$BODY$;



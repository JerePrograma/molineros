CREATE OR REPLACE FUNCTION trae_cantidad_prestaciones(cuil_v character,
 inte_v integer,
 prestacion integer) 
RETURNS SETOF bigint
    LANGUAGE sql
    AS $BODY$

select count(*) from reintegro r, reintegro_prestacion rp
where 
r.id_reintegro = rp.id_reintegro
and r.cuil_titular = $1
and r.inte = $2
and rp.id_prestacion = $3
and rp.fecha_prestacion > (select date_trunc('year', localtimestamp));

$BODY$;


ALTER FUNCTION public.trae_cantidad_prestaciones(cuil_v character, inte_v integer, prestacion integer) OWNER TO postgres;

--

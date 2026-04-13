CREATE OR REPLACE FUNCTION reporte_escolaridad_amtima(fecha_desde date,
 fecha_hasta date,
 fecha_baja date) 
RETURNS SETOF reporte_escolaridad_amtima_result
    LANGUAGE plpgsql
    AS $BODY$
BEGIN
drop table if exists amtima_escolaridad;

create temp table amtima_escolaridad as 
select id_amtima, a.id_seccional ||' - '|| s.descripcion as seccional, cast('' as varchar) as ape_nombre_titular, apellido||', '||nombre as ape_nombre_benef, inte, naci_fecha
from afiliado a, seccional s
where naci_fecha between fecha_desde and fecha_hasta
and id_amtima>0 
and (a.baja_fecha is null or a.baja_fecha>fecha_baja)
and a.id_seccional=s.id_seccional
order by naci_fecha;

update amtima_escolaridad e
set ape_nombre_titular=a.apellido||', '||a.nombre
from afiliado a
where a.id_amtima=e.id_amtima
and a.inte=0;

return query
select * from amtima_escolaridad;
END;
$BODY$;


ALTER FUNCTION public.reporte_escolaridad_amtima(fecha_desde date, fecha_hasta date, fecha_baja date) OWNER TO postgres;

--

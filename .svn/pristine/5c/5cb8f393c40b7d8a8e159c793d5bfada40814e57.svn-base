CREATE OR REPLACE FUNCTION listado_baja_total_periodo(id_tercerizadora_v character varying,
 baja_fecha_desde date,
 baja_fecha_hasta date) 
RETURNS SETOF lista_baja_total_periodo
    LANGUAGE plpgsql
    AS $BODY$
BEGIN
--BAJAS TOTALES
return query
select a.cuil_titular,docu_numero,parentesco,apellido,nombre,ingre_fecha,a.baja_fecha,descripcion as plan
from afiliado a, afi_tercerizadora_servicio at,afi_plan p, plan pl
where a.baja_fecha is not null 
and a.baja_fecha< baja_fecha_hasta
and a.baja_fecha> baja_fecha_desde
and a.cuil_titular=at.cuil_titular
and at.inte=0
and at.id_tercerizadora=id_tercerizadora_v
and p.cuil_titular=a.cuil_titular
and p.vigen_desde= (select max(vigen_desde) from afi_plan afp where afp.cuil_titular=p.cuil_titular and afp.inte=0)
and pl.id_plan=p.id_plan
and exists (select 1 
	    from afiliado 
	    where a.baja_fecha is not null 
	    and a.baja_fecha< baja_fecha_hasta
	    and a.baja_fecha> baja_fecha_desde
	    and a.cuil_titular=at.cuil_titular
	    and a.inte=0)
order by baja_fecha;
END;
$BODY$;


ALTER FUNCTION public.listado_baja_total_periodo(id_tercerizadora_v character varying, baja_fecha_desde date, baja_fecha_hasta date) OWNER TO postgres;

--

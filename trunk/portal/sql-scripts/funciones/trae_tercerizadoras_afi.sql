CREATE OR REPLACE FUNCTION trae_tercerizadoras_afi(cuil character varying,
 inte integer) 
RETURNS TABLE(id_tercerizadora character varying,
 descripcion character varying,
 fecha_ingreso date,
 fecha_egreso date)
    LANGUAGE sql
    AS $BODY$
select a.id_tercerizadora, 
       t.descripcion,
       a.fecha_inicio_pres,
       a.fecha_fin_pres       
from afi_tercerizadora_servicio a
INNER JOIN tercerizadora_servicio t on (a.id_tercerizadora=t.id_tercerizadora)
where a.cuil_titular=$1
and (a.baja_fecha is null or a.baja_fecha > current_timestamp)
--and a.inte=$2
order by a.fecha_inicio_pres
$BODY$;


ALTER FUNCTION public.trae_tercerizadoras_afi(cuil character varying, inte integer) OWNER TO postgres;

--

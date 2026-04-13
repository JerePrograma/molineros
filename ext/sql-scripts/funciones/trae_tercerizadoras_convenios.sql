drop function trae_tercerizadoras_convenios() 

CREATE OR REPLACE FUNCTION trae_tercerizadoras_convenios() 
RETURNS TABLE(

 id_tercerizadora character varying,
 descripcion character varying,
 observaciones character varying,
 alta_fecha timestamp without time zone, 
 alta_usr character varying, 
 modi_fecha timestamp without time zone, 
 modi_usr character varying, baja_fecha timestamp without time zone, 
 baja_usr character varying,
 convenios boolean
 )
    LANGUAGE sql
    AS $BODY$
select 

id_tercerizadora,
descripcion,
observaciones,
alta_fecha,
alta_usr,
modi_fecha,
modi_usr,
baja_fecha,
baja_usr,
convenios
       
from tercerizadora_servicio a
where convenios = true
order by a.descripcion;

$BODY$;


ALTER FUNCTION public.trae_tercerizadoras_afi(cuil character varying, inte integer) OWNER TO postgres;

--

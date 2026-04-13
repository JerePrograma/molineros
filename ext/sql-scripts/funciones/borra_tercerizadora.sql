CREATE OR REPLACE FUNCTION borra_tercerizadora(cuil_p character varying,
 inte_p integer,
 id_tercerizadora character varying,
 fecha_ingreso_p date,
 username character varying) 
RETURNS integer
    LANGUAGE sql
    AS $BODY$
    update afi_tercerizadora_servicio
    set baja_usr=$5,
    baja_fecha=current_timestamp
    where cuil_titular=$1
    and inte=$2
    and id_tercerizadora=$3    
    and fecha_inicio_pres=$4;
    select 1;
$BODY$;


ALTER FUNCTION public.borra_tercerizadora(cuil_p character varying, inte_p integer, id_tercerizadora character varying, fecha_ingreso_p date, username character varying) OWNER TO postgres;

--

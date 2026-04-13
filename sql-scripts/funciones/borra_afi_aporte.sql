CREATE OR REPLACE FUNCTION borra_afi_aporte(cuil_p character varying,
 inte_p integer,
 id_aporte_p integer,
 fecha_ingreso_p date,
 username character varying,
 OUT plan character varying) 
RETURNS character varying
    LANGUAGE plpgsql
    AS $BODY$
BEGIN
    update afi_aportes
    set baja_fecha=current_timestamp,
    baja_usr=$5    
    where cuil_titular=$1
    and inte=$2
    and id_aporte=$3    
    and fecha_ingre=$4;

    select descripcion from trae_plan_afiliado($1,$2) into $6;
END;
$BODY$;


ALTER FUNCTION public.borra_afi_aporte(cuil_p character varying, inte_p integer, id_aporte_p integer, fecha_ingreso_p date, username character varying, OUT plan character varying) OWNER TO postgres;

--

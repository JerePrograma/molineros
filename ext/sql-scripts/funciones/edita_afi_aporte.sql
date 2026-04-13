CREATE OR REPLACE FUNCTION edita_afi_aporte(cuil_p character varying,
 inte_p integer,
 id_aporte_p integer,
 fecha_ingreso_p date,
 fecha_egreso_p date,
 username character varying,
 OUT plan character varying) 
RETURNS character varying
    LANGUAGE plpgsql
    AS $BODY$
BEGIN
    UPDATE afi_aportes
    set fecha_egre=$5,
    modi_usr=$6,
    modi_fecha=current_timestamp
    where cuil_titular=$1
    and inte=$2
    and id_aporte=$3
    and fecha_ingre=$4;    
    
    select descripcion from trae_plan_afiliado($1,$2) into $7;
END;
$BODY$;


ALTER FUNCTION public.edita_afi_aporte(cuil_p character varying, inte_p integer, id_aporte_p integer, fecha_ingreso_p date, fecha_egreso_p date, username character varying, OUT plan character varying) OWNER TO postgres;

--

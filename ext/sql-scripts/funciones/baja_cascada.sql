CREATE OR REPLACE FUNCTION baja_cascada(cuil_p character varying,
 inte_p integer,
 fecha_egreso_p date,
 motivo_baja integer,
 username character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
BEGIN

--BAJA SITU LABORAL
update afi_situ_laboral
set fecha_egre=fecha_egreso_p,
    modi_usr=username,
    modi_fecha=current_timestamp,
    id_motivo_baja=motivo_baja
where cuil_titular=cuil_p
and inte=inte_p
and (fecha_egre is null or fecha_egre>current_timestamp);

perform baja_cascada_sin_situ_laboral_tachito(cuil_p , inte_p , fecha_egreso_p , motivo_baja, username);

RETURN 1;	

END;
$BODY$;


ALTER FUNCTION public.baja_cascada(cuil_p character varying, inte_p integer, fecha_egreso_p date, motivo_baja integer, username character varying) OWNER TO postgres;

--

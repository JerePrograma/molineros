CREATE OR REPLACE FUNCTION fu_obtener_edad(pd_fecha_ini date,
 pd_fecha_fin date,
 OUT pn_edad integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
BEGIN
pn_edad := FLOOR(((DATE_PART('YEAR',pd_fecha_fin)-DATE_PART('YEAR',pd_fecha_ini))* 372 + (DATE_PART('MONTH',pd_fecha_fin) - DATE_PART('MONTH',pd_fecha_ini))*31 + (DATE_PART('DAY',pd_fecha_fin)-DATE_PART('DAY',pd_fecha_ini)))/372);
END;
$BODY$;


ALTER FUNCTION public.fu_obtener_edad(pd_fecha_ini date, pd_fecha_fin date, OUT pn_edad integer) OWNER TO postgres;

--

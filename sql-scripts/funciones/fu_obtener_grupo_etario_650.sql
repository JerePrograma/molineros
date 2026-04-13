CREATE OR REPLACE FUNCTION fu_obtener_grupo_etario_650(pd_fecha_ini date,
 pd_fecha_fin date,
 OUT pn_edad integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare grupo_etario int;
declare edad int;
BEGIN
edad = FLOOR(((DATE_PART('YEAR',pd_fecha_fin)-DATE_PART('YEAR',pd_fecha_ini))* 372 + (DATE_PART('MONTH',pd_fecha_fin) - DATE_PART('MONTH',pd_fecha_ini))*31 + (DATE_PART('DAY',pd_fecha_fin)-DATE_PART('DAY',pd_fecha_ini)))/372);
pn_edad=id from grupo_etario_650 where min<=edad and max>=edad;
END;
$BODY$;


ALTER FUNCTION public.fu_obtener_grupo_etario_650(pd_fecha_ini date, pd_fecha_fin date, OUT pn_edad integer) OWNER TO postgres;

--

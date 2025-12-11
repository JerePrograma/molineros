CREATE OR REPLACE FUNCTION nomina_ddjj_empresa(cuit_p character varying,
 periodo_desde date,
 periodo_hasta date) 
RETURNS SETOF reporte_aportes_contrib_empresa_periodo
    LANGUAGE plpgsql
    AS $BODY$
BEGIN
return query select * 
from detalle_declaracion_jurada 
where cuit='30691064626'
and periodo>=periodo_desde
and periodo<=periodo_hasta;

END;
$BODY$;


ALTER FUNCTION public.nomina_ddjj_empresa(cuit_p character varying, periodo_desde date, periodo_hasta date) OWNER TO postgres;

--

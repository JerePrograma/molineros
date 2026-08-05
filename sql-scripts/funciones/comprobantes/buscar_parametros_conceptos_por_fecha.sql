drop FUNCTION buscar_parametros_conceptos_por_fecha(desde date, hasta date);
CREATE OR REPLACE FUNCTION buscar_parametros_conceptos_por_fecha(desde date, hasta date) 
RETURNS TABLE(parametro character varying,
	id_concepto integer,
	valido_desde date,
	valido_hasta date,
	observaciones character varying)
    LANGUAGE sql
    AS $BODY$


select parametro,
	id_concepto,
	valido_desde,
	valido_hasta,
	observaciones
from  parametros_conceptos
 where ((cast(valido_desde as date) <= cast($2 as date)  and cast(valido_hasta as date)>=cast($2 as date)) 
    or (cast(valido_desde as date) <= cast($1 as date)  and cast(valido_hasta as date)>=cast($1 as date))
    or (cast(valido_desde as date) >= cast($1 as date)  and cast(valido_hasta as date)<=cast($2 as date)))
 and parametro <> 'reintegros_amtima'
$BODY$;

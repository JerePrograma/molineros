drop FUNCTION buscar_parametros_cuenta_por_fecha(desde date, hasta date);
CREATE OR REPLACE FUNCTION buscar_parametros_cuenta_por_fecha(desde date, hasta date) 
RETURNS TABLE(parametro character varying,
	id_cuenta integer,
	valido_desde date,
	valido_hasta date,
	observaciones character varying,
	cuenta character varying,
	numero character varying)
    LANGUAGE sql
    AS $BODY$


select parametro,
	id_cuenta_maestro,
	pc.valido_desde,
	pc.valido_hasta,
	observaciones,
	pcuentas.cuenta,
	pcuentas.numero
from  parametros_contabilidad pc,
	plan_cuentas pcuentas
    where ((cast(pc.valido_desde as date) <= cast($2 as date)  and cast(pc.valido_hasta as date)>=cast($2 as date)) 
    or (cast(pc.valido_desde as date) <= cast($1 as date)  and cast(pc.valido_hasta as date)>=cast($1 as date))
    or (cast(pc.valido_desde as date) >= cast($1 as date)  and cast(pc.valido_hasta as date)<=cast($2 as date)))
	and pc.id_plan_cuenta = pcuentas.id_cuenta_maestro
    and cast(pcuentas.valido_desde as date) <= cast($1 as date) 
    and cast(pcuentas.valido_hasta as date) >= cast($1 as date) 
$BODY$;
  
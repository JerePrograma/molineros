drop FUNCTION buscar_parametros_cuenta();
CREATE OR REPLACE FUNCTION buscar_parametros_cuenta() 
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
	id_plan_cuenta,
	pc.valido_desde,
	pc.valido_hasta,
	observaciones,
	pcuentas.cuenta,
	pcuentas.numero
from  parametros_contabilidad pc,
	plan_cuentas pcuentas
where  pc.id_plan_cuenta = pcuentas.id_cuenta_maestro
     and cast(pcuentas.valido_desde as date) <= cast(pc.valido_desde as date) 
    and cast(pcuentas.valido_hasta as date) >= cast(pc.valido_desde as date) 
$BODY$;
  
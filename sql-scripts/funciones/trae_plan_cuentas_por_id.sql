drop FUNCTION trae_plan_cuentas_por_id(id int);
drop FUNCTION trae_plan_cuentas_por_id(id int, p_fecha date)

CREATE OR REPLACE FUNCTION trae_plan_cuentas_por_id(id int, p_fecha date)
RETURNS TABLE(
		numero character varying,
        cuenta character varying,
        imputable boolean,
        id int,
        valido_desde date,
        valido_hasta date,
        tipo character varying
  )
LANGUAGE sql
AS $BODY$
	select numero, cuenta, imputable, id_cuenta_maestro, valido_Desde, valido_hasta, tipo
	from plan_cuentas
	where id_cuenta_maestro = $1
	and  cast(valido_desde as date) <= cast($2 as date)
	and  cast(valido_hasta as date) >= cast($2 as date);
	
	
$BODY$;

--

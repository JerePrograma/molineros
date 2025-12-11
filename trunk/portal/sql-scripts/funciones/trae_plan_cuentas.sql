Drop FUNCTION trae_plan_cuentas() ;
CREATE OR REPLACE FUNCTION trae_plan_cuentas(p_fecha date) 
RETURNS TABLE(
		numero character varying,
        cuenta character varying,
        imputable boolean,
        tipo character varying,
        id int
  )
LANGUAGE sql
AS $BODY$
	select numero, cuenta, imputable, tipo, id_cuenta_maestro
	from plan_cuentas
	where cast(valido_desde as date) <= cast($1 as date)
	and  cast(valido_hasta as date) >= cast($1 as date)
	order by numero;
	
	
$BODY$;

--

drop function trae_concepto_liquidacion();
CREATE OR REPLACE FUNCTION trae_concepto_liquidacion(fecha date)

RETURNS TABLE(id integer,
 descripcion character varying,
 numero character varying,
 cuenta character varying)
    LANGUAGE sql
    AS $BODY$
  
	select c.id_concepto_maestro,
    c.descripcion,
    pc.numero,
    pc.cuenta
    from concepto_maestro  cm
    inner join conceptos c
    on cm.id = c.id_concepto_maestro
    inner join plan_cuentas pc
    on c.id_plan_cuenta = pc.id_cuenta_maestro
    and pc.valido_desde <= cast($1 as date) and pc.valido_hasta >= cast($1 as date)
    where c.liquidaciones = true
    and cast(cm.valido_desde as date) <= cast($1 as date) and cast(cm.valido_hasta as date)>=cast($1 as date)
    and cast(c.valido_desde as date) <= cast($1  as date) and cast(c.valido_hasta as date)>=cast($1 as date)
    order by descripcion;

$BODY$;


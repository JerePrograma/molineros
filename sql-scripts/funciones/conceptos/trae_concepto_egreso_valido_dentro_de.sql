CREATE OR REPLACE FUNCTION trae_concepto_egreso_valido_dentro_de(desde date, hasta date) 
RETURNS TABLE(
  id integer,
  descripcion character varying(100),
  liquidaciones boolean ,
  egreso boolean ,
  ingreso boolean ,
  sub_egreso boolean,
  sub_ingreso boolean ,
  valido_desde date,
  valido_hasta date,
  id_plan_cuenta integer,
  id_plan_cuenta_pasivo integer,
  numero character varying,
  cuenta character varying,
  numero_pasivo character varying,
  cuenta_pasivo character varying)
    LANGUAGE sql
    AS $BODY$
  
    select c.id_concepto_maestro,
	  c.descripcion,
	  c.liquidaciones,
	  c.egreso ,
	  c.ingreso,
	  c.sub_egreso ,
	  c.sub_ingreso,
	  c.valido_desde,
	  c.valido_hasta,
	  c.id_plan_cuenta,
	  c.id_plan_cuenta_pasivo,
	  pc.numero,
	  pc.cuenta,
	  pcp.numero,
	  pcp.cuenta
    from conceptos  c
    left outer join plan_cuentas pc
    on c.id_plan_cuenta = pc.id_cuenta_maestro
	and cast(pc.valido_desde as date)  <= cast(c.valido_desde as date)
	and cast(pc.valido_hasta as date)  >= cast(c.valido_desde as date)
    left outer join plan_cuentas pcp
	on c.id_plan_cuenta_pasivo = pcp.id_cuenta_maestro
	and cast(pcp.valido_desde as date)  <= cast(c.valido_desde as date)
	and cast(pcp.valido_hasta as date)  >= cast(c.valido_desde as date)     
    where ((cast(c.valido_desde as date) <= cast($2 as date)  and cast(c.valido_hasta as date)>=cast($2 as date)) 
    or (cast(c.valido_desde as date) <= cast($1 as date)  and cast(c.valido_hasta as date)>=cast($1 as date))
    or (cast(c.valido_desde as date) >= cast($1 as date)  and cast(c.valido_hasta as date)<=cast($2 as date)))
    and ingreso = false
    and c.id_concepto_maestro <> (select id from concepto_maestro where descripcion_original = 'AJUSTE')
    order by descripcion, valido_desde;

$BODY$;


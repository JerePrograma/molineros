drop FUNCTION trae_conceptos();
CREATE OR REPLACE FUNCTION trae_conceptos(fecha date) 
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
    from conceptos  c, plan_cuentas pc, plan_cuentas pcp
    where c.id_plan_cuenta = pc.id_cuenta_maestro
    and c.id_plan_cuenta_pasivo = pcp.id_cuenta_maestro
    and c.valido_desde <= cast($1 as date)  and c.valido_hasta>=cast($1 as date)
    and c.id_concepto_maestro <> (select id from concepto_maestro where descripcion_original = 'AJUSTE')
	and cast(pc.valido_desde as date)  <= cast(c.valido_desde as date)
	and cast(pc.valido_hasta as date)  >= cast(c.valido_desde as date) 
	and cast(pcp.valido_desde as date)  <= cast(c.valido_desde as date)
	and cast(pcp.valido_hasta as date)  >= cast(c.valido_desde as date) 
    order by descripcion;

$BODY$;


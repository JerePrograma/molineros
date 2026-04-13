

CREATE OR REPLACE FUNCTION trae_tipos_mov_bcrios_por_fechas(desde date, hasta date) 
RETURNS TABLE(
  descripcion character varying,
  id_tipo_mov integer, 
  valido_desde date, 
  valido_hasta date,
  c__id integer,
  c__descripcion character varying(100),
  c__liquidaciones boolean ,
  c__egreso boolean ,
  c__ingreso boolean ,
  c__sub_egreso boolean,
  c__sub_ingreso boolean ,
  c__valido_desde date,
  c__valido_hasta date,
  c__id_plan_cuenta integer,
  c__id_plan_cuenta_pasivo integer,
  c__numero character varying,
  c__cuenta character varying,
  c__numero_pasivo character varying,
  c__cuenta_pasivo character varying)
    LANGUAGE sql
    AS $BODY$
  
    select t.descripcion, t.id_tipo_mov_maestro, t.valido_desde, t.valido_hasta,  c.id_concepto_maestro,
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
	from tipo_mov_bcrio t
	left outer join conceptos c
	on t.concepto_id = c.id_concepto_maestro
	and cast(c.valido_desde as date) <= cast(t.valido_desde as date)  and cast(c.valido_hasta as date)>=cast(t.valido_desde as date)
	left outer join plan_cuentas pc
	on c.id_plan_cuenta = pc.id_cuenta_maestro
	and pc.valido_desde <= cast(t.valido_desde  as date) and pc.valido_hasta >= cast(t.valido_desde as date)
	left outer join plan_cuentas pcp
	on c.id_plan_cuenta_pasivo = pcp.id_cuenta_maestro
	and pcp.valido_desde <= cast(t.valido_desde  as date) and pcp.valido_hasta >= cast(t.valido_desde as date)
	where  ((cast(t.valido_desde as date) <= cast($2 as date)  and cast(t.valido_hasta as date)>=cast($2 as date)) 
    or (cast(t.valido_desde as date) <= cast($1 as date)  and cast(t.valido_hasta as date)>=cast($1 as date))
    or (cast(t.valido_desde as date) >= cast($1 as date)  and cast(t.valido_hasta as date)<=cast($2 as date)))
	and baja_fecha is null
	order by t.descripcion asc;

	 
$BODY$;


drop FUNCTION buscar_nomenclador_conceptos_por_id_prestacion(p_id_prestacion integer, desde date, hasta date) ;
CREATE OR REPLACE FUNCTION buscar_nomenclador_conceptos_por_id_prestacion(p_id_prestacion integer, desde date, hasta date) 
RETURNS TABLE(
   	codigo character varying,
	id_prestacion integer,
	descripcion character varying,
	id_tipo_nomenclador integer,
	tipo_nomenclador character varying,
	coef_gastos numeric,
	coef_honorarios numeric,
	importe numeric,
	marca_rein_liq integer,
	desc_honorarios_ambulatorio character varying,
	nomenclador_conceptos_ha_id integer,
	id_honorarios_ambulatorio integer,
	ha_valido_desde date,
	ha_valido_hasta date,
	desc_honorarios_internacion character varying,
	nomenclador_conceptos_hi_id integer,
	id_honorarios_internacion integer,
	hi_valido_desde date,
	hi_valido_hasta date,
	desc_gastos_ambulatorio character varying,
	nomenclador_conceptos_ga_id integer,
	id_gastos_ambulatorio integer,
	ga_valido_desde date,
	ga_valido_hasta date,
	desc_gastos_internacion character varying,
	nomenclador_conceptos_gi_id integer,
	id_gastos_internacion integer,
	gi_valido_desde date,
	gi_valido_hasta date)
    LANGUAGE sql
    AS $BODY$
  
    

select n.codigo,  
	n.id_prestacion,
	n.descripcion,
	n.id_tipo_nomenclador,
	tn.descripcion,
	n.coef_gastos,
	n.coef_honorarios,
	n.importe,
	cast(n.marca_rein_liq as integer),
	ha.descripcion, 
	ha.id,
	ha.concepto_id,
	ha.valido_desde,
	ha.valido_hasta,
	hi.descripcion, 
	hi.id,
	hi.concepto_id,
	hi.valido_desde,
	hi.valido_hasta,
	ga.descripcion, 
	ga.id,
	ga.concepto_id,
	ga.valido_desde,
	ga.valido_hasta,
	gi.descripcion, 
	gi.id,
	gi.concepto_id,
	gi.valido_desde,
	gi.valido_hasta
from nomenclador n
left outer join tipo_nomenclador tn
on n.id_tipo_nomenclador = tn.id_tipo_nomenclador
left outer join
	( select nc.id, nc.id_prestacion, c.descripcion, c.id_concepto_maestro as concepto_id, nc.valido_desde, nc.valido_hasta from nomenclador_conceptos  nc,
	 conceptos c
	 where nc.concepto_id = c.id_concepto_maestro
	and tipo_id = 1
	and nc.valido_desde = $2
	and c.valido_desde <= nc.valido_desde
	and c.valido_hasta >= nc.valido_desde) ha
on n.id_prestacion = ha.id_prestacion
left outer join
	  (select nc.id, nc.id_prestacion,c.descripcion, c.id_concepto_maestro as concepto_id, nc.valido_desde, nc.valido_hasta from nomenclador_conceptos  nc,
	 conceptos c
	 where nc.concepto_id = c.id_concepto_maestro
	 and tipo_id = 2
	 and nc.valido_desde = $2
	and c.valido_desde <= nc.valido_desde
	and c.valido_hasta >= nc.valido_desde) hi
on n.id_prestacion = hi.id_prestacion
left outer join 
	  (select nc.id, nc.id_prestacion,c.descripcion, c.id_concepto_maestro as concepto_id, nc.valido_desde, nc.valido_hasta from nomenclador_conceptos  nc,
	 conceptos c
	 where  nc.concepto_id = c.id_concepto_maestro
	and tipo_id = 3
	and nc.valido_desde = $2
	and c.valido_desde <= nc.valido_desde
	and c.valido_hasta >= nc.valido_desde) ga
on n.id_prestacion = ga.id_prestacion
left outer join
	(select nc.id, nc.id_prestacion,c.descripcion, c.id_concepto_maestro as concepto_id, nc.valido_desde, nc.valido_hasta from nomenclador_conceptos  nc,
	conceptos c
	where  nc.concepto_id = c.id_concepto_maestro
	and tipo_id = 4
	and nc.valido_desde = $2
	and c.valido_desde <= nc.valido_desde
	and c.valido_hasta >= nc.valido_desde) gi
on n.id_prestacion = gi.id_prestacion
where n.id_prestacion = $1
and n.baja_Fecha is null;

$BODY$;
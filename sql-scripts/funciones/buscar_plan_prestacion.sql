-- Function: buscar_plan_prestacion(integer, character varying, integer)

-- DROP FUNCTION buscar_plan_prestacion(integer, character varying, integer);

CREATE OR REPLACE FUNCTION buscar_plan_prestacion(IN p_prestacion_id integer, IN p_prestacion_nombre character varying, IN p_plan_id integer)
  RETURNS TABLE(prest__id_prestacion integer, prest__id_especialidad integer, prest__descripcion character varying, prest__marca_rein_liq smallint, prest__observaciones character varying, prest__alta_fecha timestamp without time zone, prest__alta_usr character varying, prest__modi_fecha timestamp without time zone, prest__modi_usr character varying, prest__baja_fecha timestamp without time zone, prest__baja_usr character varying, prest__id_tipo_nomenclador integer, prest__codigo character varying, pprest__id_prestacion integer, pprest__id_plan integer, pprest__tope_cantidad smallint, pprest__tope_importe numeric, pprest__tope_individ_cantidad smallint, pprest__tope_individ_importe numeric) AS
$BODY$
 
 select 
  p.id_prestacion  ,
  p.id_especialidad ,
  p.descripcion,
  p.marca_rein_liq , 
  p.observaciones,
  p.alta_fecha   ,
  p.alta_usr ,
  p.modi_fecha   ,
  p.modi_usr ,
  p.baja_fecha  ,
  p.baja_usr,
  p.id_tipo_nomenclador ,
  p.codigo ,
  pp.id_prestacion  ,
  pp.id_plan  ,
  pp.tope_cantidad  ,
  pp.tope_importe  ,
  pp.tope_individ_cantidad  ,
  pp.tope_individ_importe  
from nomenclador p
inner join plan_prestacion pp
on p.id_prestacion = pp.id_prestacion
where ($1 is null or ($1 is not null  and p.id_prestacion=$1))
	and ($2 is null or ($2 is not null and upper(p.descripcion) like '%'||upper($2)||'%'))	
	and ($3 is null or ($3 is not null and pp.id_plan=$3))

$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION buscar_plan_prestacion(integer, character varying, integer) OWNER TO postgres;

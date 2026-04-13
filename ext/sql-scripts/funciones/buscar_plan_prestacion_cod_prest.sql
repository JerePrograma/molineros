-- Function: buscar_plan_prestacion_cod_prest(character varying, character varying, integer, character varying)

-- DROP FUNCTION buscar_plan_prestacion_cod_prest(character varying, character varying, integer, character varying);

CREATE OR REPLACE FUNCTION buscar_plan_prestacion_cod_prest(IN p_codigo character varying, IN p_prestacion_nombre character varying, IN p_plan_id integer, IN p_protesis character varying)
  RETURNS TABLE(prest__id_prestacion integer, prest__id_especialidad integer, prest__descripcion character varying, prest__marca_rein_liq smallint, prest__observaciones character varying, prest__alta_fecha timestamp without time zone, prest__alta_usr character varying, prest__modi_fecha timestamp without time zone, prest__modi_usr character varying, prest__baja_fecha timestamp without time zone, prest__baja_usr character varying, prest__id_tipo_nomenclador integer, prest__codigo character varying, pprest__id_prestacion integer, pprest__id_plan integer, pprest__tope_cantidad smallint, pprest__tope_importe numeric, pprest__tope_individ_cantidad smallint, pprest__tope_individ_importe numeric, prest__importe numeric) AS
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
  pp.tope_individ_cantidad,
  pp.tope_individ_importe,
  p.importe
from nomenclador p
inner join plan_prestacion pp
on p.id_prestacion = pp.id_prestacion
where 
	p.baja_fecha is null
	and ($1 is null or $1 = '' or ($1 is not null  and p.codigo like $1||'%'))
	and ($2 is null or ($2 is not null and upper(p.descripcion) like '%'||upper($2)||'%'))
	and ($3 is null or ($3 is not null and pp.id_plan=$3))
	and (($4 = '0' or ($4 = '1' and p.marca_rein_liq = 4)) --PROTESUS 
	or ($4 = '0' or ($4 = '2' and p.marca_rein_liq = 5)) --ORTOPEDIA_ORTODONCIA
	or ($4 = '0' or ($4 = '3'))); --DISCAPACIDAD
	
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION buscar_plan_prestacion_cod_prest(character varying, character varying, integer, character varying) OWNER TO postgres;
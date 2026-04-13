CREATE OR REPLACE FUNCTION buscar_aportes_por_plan(IN id_plan_p integer)
  RETURNS TABLE(
  id_aporte integer,
  tipo_aporte character varying(3),
  plan character varying(3),
  descripcion character varying(100),
  observaciones character varying(250),
  alta_fecha timestamp without time zone,
  alta_usr character varying(15),
  modi_fecha timestamp without time zone,
  modi_usr character varying(15),
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  genera_id_socio character(1),
  es_os boolean) AS
$BODY$
BEGIN

return query
select a.id_aporte,
  a.tipo_aporte,
  a.plan,
  a.descripcion,
  a.observaciones,
  a.alta_fecha,
  a.alta_usr,
  a.modi_fecha,
  a.modi_usr,
  a.baja_fecha,
  a.baja_usr,
  a.genera_id_socio,
  a.es_os 
from aporte a, plan_aporte pla    
where a.id_aporte = pla.id_aporte 
and pla.id_plan = id_plan_p
order by a.descripcion;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 100;
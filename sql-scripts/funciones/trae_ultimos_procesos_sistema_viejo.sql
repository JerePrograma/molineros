-- Function: trae_ultimos_procesos_sistema_viejo(date)

-- DROP FUNCTION trae_ultimos_procesos_sistema_viejo(date);

CREATE OR REPLACE FUNCTION trae_ultimos_procesos_sistema_viejo(IN fecha_p date)
  RETURNS SETOF date AS
$BODY$	

select distinct fecha_proceso from uoma_aportes order by fecha_proceso desc limit 5;

$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION trae_ultimos_procesos_sistema_viejo(date)
  OWNER TO postgres;
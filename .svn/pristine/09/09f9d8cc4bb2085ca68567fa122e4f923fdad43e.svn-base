-- Function: trae_ultimos_archivos_bco_uoma(date)

-- DROP FUNCTION trae_ultimos_archivos_bco_uoma(date);

CREATE OR REPLACE FUNCTION trae_ultimos_archivos_bco_uoma(IN fecha_p date)
  RETURNS TABLE(descripcion character varying, fecha_rendicion date, sum numeric) AS
$BODY$	

select descripcion, fecha_rendicion, sum(importe)
from uoma_aportes a , convenio_nacion c
where ($1 is null or $1 is not null and fecha_rendicion<=$1)
and (a.ente = c.id)

group by descripcion, fecha_rendicion
order by fecha_rendicion DESC , descripcion 
limit 13

$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION trae_ultimos_archivos_bco_uoma(date)
  OWNER TO postgres;
-- Function: trae_ultimos_archivos_bco_amtima(date)

-- DROP FUNCTION trae_ultimos_archivos_bco_amtima(date);

CREATE OR REPLACE FUNCTION trae_ultimos_archivos_bco_amtima(IN fecha_p date)
  RETURNS TABLE(descripcion character varying, fecha_rendicion date, sum numeric) AS
$BODY$	

select descripcion, fecha_rendicion, sum(importe)
from amtima_aportes a , convenio_nacion c
where ($1 is null or $1 is not null and fecha_rendicion<=$1)
and (a.ente = c.id)

group by descripcion, fecha_rendicion
order by fecha_rendicion DESC , descripcion 
limit 13

$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION trae_ultimos_archivos_bco_amtima(date)
  OWNER TO postgres;

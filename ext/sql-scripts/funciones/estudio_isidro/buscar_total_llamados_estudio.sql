CREATE OR REPLACE FUNCTION buscar_total_llamados_estudio(cuit_p character varying)
  RETURNS integer AS
$BODY$
declare total integer;
BEGIN

total= count(*)
from estudio_llamadas_empresas
where cuit=cuit_p;

return total;

END;
$BODY$
Language 'plpgsql'

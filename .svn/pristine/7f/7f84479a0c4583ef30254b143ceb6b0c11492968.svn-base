CREATE OR REPLACE FUNCTION autorizaciones.valida_periodo_no_consecutivo(cuil_titular_p character varying, inte_p integer)
  RETURNS date AS
$BODY$
BEGIN

return max(fecha)
from autorizaciones.autorizaciones_pmi
where cuil_titular=$1
and inte=$2
and baja_fecha is null;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION autorizaciones.valida_periodo_no_consecutivo(character varying, integer)
  OWNER TO postgres;

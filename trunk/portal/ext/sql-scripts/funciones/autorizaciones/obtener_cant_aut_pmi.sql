CREATE OR REPLACE FUNCTION autorizaciones.obtener_cant_aut_pmi(cuil_titular_p character varying, inte_p integer)
  RETURNS integer AS
$BODY$
declare cantidad integer;
BEGIN

cantidad=count(distinct id_autorizacion_pmi) 
from autorizaciones.autorizaciones_pmi
where cuil_titular=$1
and inte=$2
and baja_fecha is null;

return cantidad;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION autorizaciones.obtener_cant_aut_pmi(character varying, integer)
  OWNER TO postgres;
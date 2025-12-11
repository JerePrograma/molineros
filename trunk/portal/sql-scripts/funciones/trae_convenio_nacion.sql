CREATE OR REPLACE FUNCTION trae_convenio_nacion()
  RETURNS TABLE(descripcion character varying, id integer, cuenta_suc integer, tipo_boleta integer,ospim boolean,uoma boolean,amtima boolean) AS
$BODY$	
select descripcion,
       id,
       cuenta_suc,
       tipo_boleta,
       ospim,
       uoma,
       amtima
from convenio_nacion
order by tipo_boleta
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
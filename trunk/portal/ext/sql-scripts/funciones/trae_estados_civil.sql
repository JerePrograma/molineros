CREATE OR REPLACE FUNCTION trae_estados_civil()
  RETURNS TABLE(eciv_codigo integer, eciv_descripcion character varying) AS
$BODY$

select
codigo,
descripcion
from estado_civil_sss eciv_
order by codigo;

$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 100;
CREATE OR REPLACE FUNCTION trae_parentescos()
  RETURNS TABLE(par_codigo integer, par_descripcion character varying) AS
$BODY$

select
codigo,
descripcion
from parentesco_sss par_
order by codigo;

$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 100;
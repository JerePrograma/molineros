CREATE OR REPLACE FUNCTION novedades_sss.trae_tipos_novedad()
  RETURNS SETOF novedades_sss.cod_tipo_novedad AS
$BODY$

select
codigo,
grupo,CREATE OR REPLACE FUNCTION novedades_sss.trae_tipos_novedad()
  RETURNS SETOF novedades_sss.cod_tipo_novedad AS
$BODY$

select
codigo,
grupo,
descripcion
from novedades_sss.tipo_novedad
order by grupo;

$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 100;
descripcion
from novedades_sss.tipo_novedad
order by grupo;

$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 100;
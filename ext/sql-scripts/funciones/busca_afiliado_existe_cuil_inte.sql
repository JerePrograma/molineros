CREATE OR REPLACE FUNCTION busca_afiliado_existe_cuil_inte(cuil character)
  RETURNS SETOF character varying AS
$BODY$

select
a.cuil_titular
from afiliado a
where a.cuil=$1
and (a.baja_fecha is null or a.baja_fecha > current_timestamp);

$BODY$
  LANGUAGE 'sql' VOLATILE

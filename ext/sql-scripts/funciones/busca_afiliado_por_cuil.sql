-- Function: busca_afiliado_por_cuil(character)

-- DROP FUNCTION busca_afiliado_por_cuil(character);

CREATE OR REPLACE FUNCTION busca_afiliado_por_cuil(cuil character)
  RETURNS SETOF character varying AS
$BODY$

select
a.cuil_titular

from afiliado a

where a.cuil = $1
and (a.baja_fecha is null or a.baja_fecha > current_timestamp);

$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION busca_afiliado_por_cuil(character) OWNER TO postgres;

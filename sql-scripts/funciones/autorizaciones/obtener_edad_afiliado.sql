CREATE OR REPLACE FUNCTION autorizaciones.obtener_edad_afiliado(cuil_titular character varying, inte integer)
  RETURNS SETOF date AS
$BODY$

select naci_fecha from public.afiliado where 
($1 is null or ($1 is not null and cuil_titular=$1))
and ($2 is null or ($2 is not null and inte=$2));

$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION autorizaciones.obtener_edad_afiliado(character varying, integer)
  OWNER TO postgres;
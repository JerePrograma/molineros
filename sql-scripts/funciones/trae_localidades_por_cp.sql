CREATE OR REPLACE FUNCTION trae_localidades_por_cp(codpos integer)
  RETURNS TABLE(id_localidad integer, detalle character varying, 
  id_provincia integer, cod_postal integer, cod_area_telefono character varying,
  id_provinciasss integer, id_localidadesss integer)
  AS
$BODY$
select id_localidad, 
       detalle,
       id_provincia,
       cod_postal,
       cod_area_telefono,
       id_provinciasss,
       id_localidadesss
from localidad 
where cod_postal = $1 
order by detalle
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
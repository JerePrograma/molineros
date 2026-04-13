CREATE OR REPLACE FUNCTION buscar_areas(IN id_organismo integer)
  RETURNS TABLE(id_area integer, id_organismo integer, denominacion character varying, telefono character varying, web character varying, lineas text) AS
$BODY$
select o.id_area, o.id_organismo, o.denominacion, o.telefono, o.web, array_to_string(array(SELECT ol.linea 
										          FROM area_linea ol
										          where ol.id_area=o.id_area
										          and ol.baja_fecha is null), ' - ')       
from area o
where o.id_organismo=$1
and o.baja_fecha is null
$BODY$
  LANGUAGE sql VOLATILE
  COST 100


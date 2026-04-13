CREATE OR REPLACE FUNCTION buscar_area(IN id_area integer)
  RETURNS TABLE(id_area integer, id_organismo integer, denominacion character varying, telefono character varying, web character varying, observaciones character varying) AS
$BODY$
select o.id_area, o.id_organismo, o.denominacion, o.telefono, o.web, o.observaciones
from area o
where id_area=$1
and baja_fecha is null
$BODY$
  LANGUAGE sql VOLATILE

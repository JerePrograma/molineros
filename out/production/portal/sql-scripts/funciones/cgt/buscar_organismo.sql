CREATE OR REPLACE FUNCTION buscar_organismo(IN id_organismo integer)
  RETURNS TABLE(id_organismo integer, denominacion character varying, ambito character varying, telefono character varying, web character varying, observaciones character varying, sigla character varying, orbita character varying) AS
$BODY$
select o.id_organismo, o.denominacion, o.ambito, o.telefono, o.web, o.observaciones, o.sigla, o.orbita
from organismo o
where id_organismo=$1
and baja_fecha is null
$BODY$
  LANGUAGE sql VOLATILE

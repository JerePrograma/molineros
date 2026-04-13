CREATE OR REPLACE FUNCTION borra_liquidacion_prestacion(id_liquidacion integer, orden integer, username character varying)
  RETURNS integer AS
$BODY$
    delete from liquidacion_prestacion
    where id_liquidacion=$1
    and orden=$2;
    select 1;
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100;
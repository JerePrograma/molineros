CREATE OR REPLACE FUNCTION cambio_estado_tratamiento(id_tratamiento integer, estado integer, username character varying)
  RETURNS integer AS
$BODY$
    update tratamiento_discapacidad
    set estado = $2,
    modi_fecha = localtimestamp,
    modi_usr = $3
    where id_tratamiento=$1;
    select 1;
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100;
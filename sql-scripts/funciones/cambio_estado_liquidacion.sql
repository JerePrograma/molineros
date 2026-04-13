CREATE OR REPLACE FUNCTION cambio_estado_liquidacion(id_liquidacion integer, estado integer, username character varying)
  RETURNS integer AS
$BODY$
    update liquidacion
    set estado = $2,
    modi_fecha = localtimestamp,
    modi_usr = $3    
    where id_liquidacion=$1;
    select 1;
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100;
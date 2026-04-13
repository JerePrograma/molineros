CREATE OR REPLACE FUNCTION borra_liquidacion(id_liquidacion integer, username character varying)
  RETURNS integer AS
$BODY$
    update liquidacion
    set baja_usr=$2,
    baja_fecha=current_timestamp
    where id_liquidacion=$1;
    select 1;
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100;
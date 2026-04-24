drop function finalizar_tratamientos_discapacidad ()

CREATE OR REPLACE FUNCTION finalizar_tratamientos_discapacidad(IN estado_fin integer )
  RETURNS integer AS
$BODY$
    update tratamiento_discapacidad
    set estado = $1,
    modi_fecha = localtimestamp,
    modi_usr = 'repor_auto'
    where localtimestamp > periodo_hasta and estado = 1;
    select 1;
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100;
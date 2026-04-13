CREATE OR REPLACE FUNCTION correo.update_estado_items(ids_item character varying, separador character varying, estado character varying, username character varying)
  RETURNS integer AS
$BODY$
    update correo.item_correspondencia
    set estado = $3,
    modi_fecha = localtimestamp,
    modi_usr = $4
    where id in (select * from split_cadena($1,$2) );
    select 1;
$BODY$
  LANGUAGE sql VOLATILE
  COST 100;
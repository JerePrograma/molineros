CREATE OR REPLACE FUNCTION correo.marcar_recibido(id_pa integer, username character varying)
  RETURNS integer AS
$BODY$       
begin
    update correo.item_correspondencia ic
    set modi_fecha = localtimestamp,
    modi_usr = username,
    estado = 'RECIBIDO'
    where ic.id=id_pa;
    return 1;

   end;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
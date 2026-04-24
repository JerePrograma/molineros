CREATE OR REPLACE FUNCTION correo.borrar_item_del_paquete(id_item integer, username character varying)
  RETURNS integer AS
$BODY$       
begin
    delete from correo.lista_paquete lp
    where lp.id_item_correspondencia=id_item;
    return 1;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
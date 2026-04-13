CREATE OR REPLACE FUNCTION correo.borrar_lista_items_paquete(id_paquete_p integer, username character varying)
  RETURNS integer AS
$BODY$       
begin
    delete from correo.lista_paquete c
    where c.id_paquete=id_paquete_p;
    return 1;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

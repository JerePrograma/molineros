CREATE OR REPLACE FUNCTION correo.insertar_lista_items_paquete(p_id_paquete integer, p_id_item integer, p_usr character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
  begin
  insert into correo.lista_paquete(id_paquete,
	id_item_correspondencia, alta_fecha, alta_usr, modi_fecha, modi_usr)
  
  values (p_id_paquete, p_id_item, localtimestamp, p_usr, localtimestamp, p_usr); 
   
  return 1;
  end;
$BODY$;


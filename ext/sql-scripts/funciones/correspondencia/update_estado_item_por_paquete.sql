CREATE OR REPLACE FUNCTION correo.update_estado_item_por_paquete(p_id_paquete integer, p_username character varying)
  RETURNS integer AS
$BODY$       
begin
    
	update correo.item_correspondencia c
	set estado = 'INGRESADO' 
	where exists(
		select 1 
		from correo.lista_paquete lp 
		where lp.id_paquete = p_id_paquete 
		and c.id=lp.id_item_correspondencia
	);	
    return 1;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;


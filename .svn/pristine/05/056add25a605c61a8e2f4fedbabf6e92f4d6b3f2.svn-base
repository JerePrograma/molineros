CREATE OR REPLACE FUNCTION insertar_comprobante_item(
 c_pto_venta integer,
 c_compro_tipo character varying,
 c_compro_nro character varying, 
 c_compro_letra character varying,
 c_compro_sucu integer,
 c_cuit character,
 c_item integer,
 c_importe_comp numeric,
 c_porcentaje numeric,
 c_valor numeric,
 c_ivains numeric,
 c_ivanins numeric,
 c_ivaexen numeric,
 c_observaciones character varying,
 c_usuario character varying,
 c_motivo integer)
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
    declare ci_item integer;
    declare current_item integer;
  begin
	if (c_item = 0) then   
	  	current_item = (select max(item) from compro_items where compro_tipo = c_compro_tipo and compro_nro = c_compro_nro and compro_letra = c_compro_letra and compro_sucu = c_compro_sucu and id_punto_venta = c_pto_venta and cuit = c_cuit);
		if (current_item is null) then
  			ci_item = 1;
		else 	
			ci_item=(select max(item) from compro_items where compro_tipo = c_compro_tipo and compro_nro = c_compro_nro and compro_letra = c_compro_letra and compro_sucu = c_compro_sucu and id_punto_venta = c_pto_venta and cuit = c_cuit) + 1; 
    	end if;
    else 
    	ci_item = c_item;
    end if;	
  	
  insert into compro_items (
    id_punto_venta,
    compro_tipo,
    compro_nro,
    compro_letra,
    compro_sucu,
    cuit,
    item,    
	saldo,
    porcentaje,
    valor,
    ivains,
    ivanins,
    ivaexen,    
    observaciones,
    alta_fecha,
    alta_usr,
    modi_fecha,
    modi_usr,
    motivo)    

    values (
	 c_pto_venta,
	 c_compro_tipo,
	 c_compro_nro, 
	 c_compro_letra,
	 c_compro_sucu,
	 c_cuit,
	 ci_item,
	 c_importe_comp,
	 c_porcentaje,
	 c_valor,
	 c_ivains,
	 c_ivanins,
	 c_ivaexen,
	 c_observaciones,
	 localtimestamp,
	 c_usuario,
	 localtimestamp,
	 c_usuario,
	 c_motivo);
	 RETURN 0;
  end;  
$BODY$;

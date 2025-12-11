CREATE OR REPLACE FUNCTION borrar_comprobante_item (
 c_pto_venta integer,
 c_compro_tipo character varying,
 c_compro_nro character varying, 
 c_compro_letra character varying,
 c_compro_sucu integer,
 c_cuit character,
 c_item integer,
 c_usuario character varying)

RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$  
  begin		  
	  delete from compro_items cl where 		 	
		cl.id_punto_venta = c_pto_venta
		and cl.compro_tipo = c_compro_tipo
		and cl.compro_nro = c_compro_nro 
		and cl.cuit = c_cuit
		and cl.compro_letra = c_compro_letra 
		and cl.compro_sucu = c_compro_sucu
		and cl.item = c_item;	  
  return  0;
  end;
$BODY$;
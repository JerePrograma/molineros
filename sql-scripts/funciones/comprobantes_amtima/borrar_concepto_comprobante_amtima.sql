
 CREATE OR REPLACE FUNCTION borrar_concepto_comprobante_amtima(
 c_pto_venta integer,
 c_compro_tipo character varying,
 c_compro_nro character varying, 
 c_compro_letra character varying,
 c_compro_sucu integer,
 c_cuit character varying,
 c_concepto_id integer,
 c_usuario character varying)
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
    declare ban integer;
  begin
	  
	  delete from concepto_comprobante_amtima
	  where  compro_tipo  = c_compro_tipo  
	  	and compro_nro  = c_compro_nro
	  	and compro_letra  = c_compro_letra
	  	and compro_sucu = c_compro_sucu 
	  	and id_punto_venta  = c_pto_venta
	    and cuit = c_cuit
	    and concepto_id = c_concepto_id;
	    
  return  0;
  end;  
$BODY$;

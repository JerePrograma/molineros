
 CREATE OR REPLACE FUNCTION insertar_concepto_comprobante_amtima(
 c_pto_venta integer,
 c_compro_tipo character varying,
 c_compro_nro character varying, 
 c_compro_letra character varying,
 c_compro_sucu integer,
 c_cuit character varying,
 c_concepto_id integer,
 c_importe numeric,
 c_usuario character varying)
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
    declare ban integer;
  begin	
	  insert into concepto_comprobante_amtima (
	    compro_tipo,  compro_nro , compro_letra, compro_sucu, id_punto_venta,
	    cuit, concepto_id, alta_fecha,    alta_usr,    modi_fecha,    modi_usr, importe )
	    values (c_compro_tipo, c_compro_nro, c_compro_letra, c_compro_sucu, c_pto_venta, c_cuit, c_concepto_id, 
	    LOCALTIMESTAMP, c_usuario , LOCALTIMESTAMP, c_usuario, c_importe);
  return  0;
  end;  
$BODY$;

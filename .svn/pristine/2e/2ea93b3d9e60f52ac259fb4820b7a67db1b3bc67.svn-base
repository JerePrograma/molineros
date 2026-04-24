CREATE OR REPLACE FUNCTION actualizar_comprobante_item(
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
 c_motivo integer
 )
 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$     
  begin	
  update compro_items set  
    saldo = c_importe_comp,
    porcentaje = c_porcentaje,
    valor = c_valor,
    ivains = c_ivains,
    ivanins = c_ivanins,
    ivaexen = c_ivaexen,    
    observaciones = c_observaciones,
    modi_fecha = localtimestamp,
    modi_usr = c_usuario,
    motivo = c_motivo
    
	where id_punto_venta = c_pto_venta
	and compro_tipo = c_compro_tipo
	and compro_nro = c_compro_nro
	and compro_letra = c_compro_letra
	and compro_sucu = c_compro_sucu
	and cuit = c_cuit
	and item = c_item;
	return 0;
  end;  
$BODY$;
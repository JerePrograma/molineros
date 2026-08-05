CREATE OR REPLACE FUNCTION actualizar_comprobantes_liquidacion_item (
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
 c_id integer)
 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$     
  begin	  
	  insert into compro_items
	  select c_pto_venta, c_compro_tipo, c_compro_nro, ci.item, 0, 0, 0, 0, 0, ci.saldo, ci.observaciones, 
	  		 ci.alta_fecha, ci.alta_usr, localtimestamp, c_usuario, null, null, c_cuit, c_compro_letra, c_compro_sucu
	  from compro_items ci, comprobante_liquidacion cl
	  where cl.id_liquidacion = c_id
	  and ci.id_punto_venta = cl.id_punto_venta
	  and ci.compro_tipo = cl.compro_tipo
	  and ci.compro_letra = cl.compro_letra
	  and ci.compro_sucu = cl.compro_sucu
	  and ci.compro_nro = cl.compro_nro
	  and ci.cuit = cl.cuit;
	  	  
	  delete from compro_items ci where exists (
		select 1 from comprobante_liquidacion cl
		where cl.id_liquidacion = c_id
		and cl.id_punto_venta = ci.id_punto_venta 
		and cl.compro_tipo = ci.compro_tipo
		and cl.compro_nro = ci.compro_nro 
		and cl.cuit = ci.cuit 
		and cl.compro_letra = ci.compro_letra 
		and cl.compro_sucu = ci.compro_sucu 
		and cl.alta_fecha = (select min (alta_fecha) from comprobante_liquidacion cl2 
			where cl2.id_liquidacion = c_id)
	  );
  return  0;
  end;
$BODY$;
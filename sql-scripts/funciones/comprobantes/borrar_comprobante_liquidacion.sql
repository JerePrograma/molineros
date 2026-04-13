CREATE OR REPLACE FUNCTION borrar_comprobante_liquidacion (
 c_id integer,
 c_usuario character varying)

RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
    declare ban integer;
    declare fecha_alt timestamp without time zone;
    declare c_id_punto_venta smallint;
    declare c_compro_tipo character varying;
    declare c_compro_nro character varying;
    declare c_cuit character varying; 
	declare c_compro_letra character varying; 
	declare	c_compro_sucu integer;
  begin
	  
	  delete from concepto_comprobante cc where exists (
	  
		select 1 from comprobante_liquidacion cl 
		where cl.id_liquidacion = c_id
		and cl.id_punto_venta = cc.id_punto_venta 
		and cl.compro_tipo = cc.compro_tipo
		and cl.compro_nro = cc.compro_nro 
		and cl.cuit = cc.cuit 
		and cl.compro_letra = cc.compro_letra 
		and cl.compro_sucu = cc.compro_sucu 
		and cl.alta_fecha = (select min (cl2.alta_fecha) from comprobante_liquidacion cl2 
			where cl2.id_liquidacion = c_id)			  	 
	  );	  
	  
	  delete from compro_items ci where exists (
	  
		select 1 from comprobante_liquidacion cl 
		where cl.id_liquidacion = c_id
		and cl.id_punto_venta = ci.id_punto_venta 
		and cl.compro_tipo = ci.compro_tipo
		and cl.compro_nro = ci.compro_nro 
		and cl.cuit = ci.cuit 
		and cl.compro_letra = ci.compro_letra 
		and cl.compro_sucu = ci.compro_sucu 
		and cl.alta_fecha = (select min (cl2.alta_fecha) from comprobante_liquidacion cl2 
			where cl2.id_liquidacion = c_id)			  	 
	  );	      
	  
	  fecha_alt = cl.alta_fecha from comprobante_liquidacion cl where id_liquidacion = c_id and cl.alta_fecha = (select min (cl2.alta_fecha) from comprobante_liquidacion cl2 	
		where cl2.id_liquidacion = c_id);
	  c_id_punto_venta = cl.id_punto_venta from comprobante_liquidacion cl where id_liquidacion = c_id and cl.alta_fecha = (select min (cl2.alta_fecha) from comprobante_liquidacion cl2 	
		where cl2.id_liquidacion = c_id);
	  c_compro_tipo = cl.compro_tipo from comprobante_liquidacion cl where id_liquidacion = c_id and cl.alta_fecha = (select min (cl2.alta_fecha) from comprobante_liquidacion cl2 	
		where cl2.id_liquidacion = c_id);
	  c_compro_nro = cl.compro_nro from comprobante_liquidacion cl where id_liquidacion = c_id and cl.alta_fecha = (select min (cl2.alta_fecha) from comprobante_liquidacion cl2 	
		where cl2.id_liquidacion = c_id);
	  c_cuit = cl.cuit from comprobante_liquidacion cl where id_liquidacion = c_id and cl.alta_fecha = (select min (cl2.alta_fecha) from comprobante_liquidacion cl2 	
		where cl2.id_liquidacion = c_id);
	  c_compro_letra = cl.compro_letra from comprobante_liquidacion cl where id_liquidacion = c_id and cl.alta_fecha = (select min (cl2.alta_fecha) from comprobante_liquidacion cl2 	
		where cl2.id_liquidacion = c_id);
	  c_compro_sucu = cl.compro_sucu from comprobante_liquidacion cl where id_liquidacion = c_id and cl.alta_fecha = (select min (cl2.alta_fecha) from comprobante_liquidacion cl2 	
		where cl2.id_liquidacion = c_id);
		
  	  delete from comprobante_liquidacion cl where id_liquidacion = c_id and cl.alta_fecha = (select min (cl2.alta_fecha) from comprobante_liquidacion cl2 
		where cl2.id_liquidacion = c_id);	 
	  		
	  delete from comprobante cl where 
		cl.id_punto_venta = c_id_punto_venta
		and cl.compro_tipo = c_compro_tipo
		and cl.compro_nro = c_compro_nro
		and cl.cuit = c_cuit 
		and cl.compro_letra = c_compro_letra 
		and cl.compro_sucu = c_compro_sucu;
	  
  return  0;
  end;
$BODY$;
DROP FUNCTION actualizar_comprobante(integer, character varying, character varying, character varying, integer, character, numeric, timestamp without time zone, timestamp without time zone, timestamp without time zone, character varying);
 
 CREATE OR REPLACE FUNCTION actualizar_comprobante(
 c_pto_venta integer,
 c_compro_tipo character varying,
 c_compro_nro character varying, 
 c_compro_letra character varying,
 c_compro_sucu integer,
 c_cuit character,
 c_cuit_acreedor character varying,
 c_sucu_acreedor  character varying,
 c_seccional integer,
 c_importe_comp numeric,
 c_fecha_emision_comp timestamp without time zone,
 c_fecha_recepcion_comp timestamp without time zone,
 c_fecha_vencimiento_comp timestamp without time zone,
 c_obs character varying,
  p_periodo_prestacion date,
 c_usuario character varying)
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$     
  begin
	  update comprobante
	    set total = c_importe_comp, modi_fecha = LOCALTIMESTAMP, modi_usr = c_usuario,
	    	fecha_emision = c_fecha_emision_comp,
	    	fecha_recepcion = c_fecha_recepcion_comp,
	    	vto = c_fecha_vencimiento_comp, observaciones = c_obs,
	    	 cuit_acreedor = c_cuit_acreedor ,
 			sucu_acreedor = c_sucu_acreedor  ,
 			seccional =c_seccional ,
 			periodo_prestacion= p_periodo_prestacion
	    where compro_tipo = c_compro_tipo and compro_nro = c_compro_nro and 
	    	  compro_letra = c_compro_letra and compro_sucu = c_compro_sucu and id_punto_venta = c_pto_venta and cuit = c_cuit;
  return  0;
  end;  
$BODY$;

DROP FUNCTION insertar_comprobante(
 c_pto_venta integer,
 c_compro_tipo character varying,
 c_compro_nro character varying, 
 c_compro_letra character varying,
 c_compro_sucu integer,
 c_cuit character,
 c_importe_comp numeric,
 c_fecha_emision_comp timestamp without time zone,
 c_fecha_recepcion_comp timestamp without time zone,
 c_fecha_vencimiento_comp timestamp without time zone,
 c_id integer,
 c_tipo_entidad character varying, --'liq' para liquidaciones, 'opo' para orden pago ospim
 c_usuario character varying);
 
CREATE OR REPLACE FUNCTION insertar_comprobante(c_pto_venta integer, c_compro_tipo character varying, c_compro_nro character varying,
c_compro_letra character varying, c_compro_sucu integer, c_cuit character varying, 
c_cuit_acreedor character varying, c_sucu_acreedor character varying, c_seccional integer, c_importe_comp numeric, 
c_fecha_emision_comp timestamp without time zone, c_fecha_recepcion_comp timestamp without time zone, 
c_fecha_vencimiento_comp timestamp without time zone, p_obs character varying, p_periodo_prestacion date, c_usuario character varying, p_debito_para_egreso boolean)
  RETURNS integer AS
$BODY$
    declare ban integer;
  begin	
	  insert into comprobante (
	    compro_tipo,  compro_nro , compro_letra, compro_sucu, id_punto_venta,
	    fecha,    total,    alta_fecha,    alta_usr,    modi_fecha,    modi_usr ,  exen,
	    grava ,
	    iva_total ,
	    ivan_total,
	    fecha_emision,
	    fecha_recepcion,
	    vto,	    
	    cuit, observaciones, cuit_acreedor,
 		 sucu_acreedor,  seccional, periodo_prestacion, debito_para_egreso)
	    values (c_compro_tipo, c_compro_nro, c_compro_letra, c_compro_sucu, c_pto_venta, LOCALTIMESTAMP, c_importe_comp, LOCALTIMESTAMP,
	     c_usuario , LOCALTIMESTAMP, c_usuario,0,0,0,0, c_fecha_emision_comp, c_fecha_recepcion_comp, c_fecha_vencimiento_comp , c_cuit, p_obs,  c_cuit_acreedor ,
 		 c_sucu_acreedor, c_seccional, p_periodo_prestacion, p_debito_para_egreso);
  return  0;
  end;  
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;

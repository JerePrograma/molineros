DROP FUNCTION insertar_orden_pago(numeric, character varying, timestamp without time zone, numeric, numeric, timestamp without time zone, timestamp without time zone, numeric, character varying, character varying, character varying, character varying, integer, timestamp without time zone, timestamp without time zone, character varying, character varying, numeric, integer, character varying, character varying, integer);
CREATE OR REPLACE FUNCTION insertar_orden_pago(
p_importe numeric,
  p_cuit_acreedor character varying, 
  p_sucu_acreedor character varying, 
  p_seccional integer,
  p_obs character varying,
  p_usuario character varying,
 p_descuento numeric,
 p_descuentodrogueria numeric,
 p_fechadesde timestamp without time zone,
 p_fechahasta timestamp without time zone) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
        declare v_id_orden_pago integer;
  begin
	  
	   v_id_orden_pago=max(id_orden_pago) from orden_pago_amtima;
	  v_id_orden_pago=v_id_orden_pago+1;
	  
	  
	  insert into orden_pago_amtima (  id_orden_pago,  importe,  fecha_desde,  fecha_hasta,  descuento,  descuento_por_drogueria,
		  alta_fecha,  alta_usr,  modi_fecha,  modi_usr,  id_seccional,  cuit_acreedor,  sucu_acreedor,  observaciones)
	  
    VALUES (v_id_orden_pago, p_importe, p_fechaDesde, p_fechaHasta, 
            p_descuento, p_descuentoDrogueria, LOCALTIMESTAMP, p_usuario, LOCALTIMESTAMP, p_usuario, 
  			p_seccional , p_cuit_acreedor,  p_sucu_acreedor , p_obs );

 
  return v_id_orden_pago;
  end;  
$BODY$;


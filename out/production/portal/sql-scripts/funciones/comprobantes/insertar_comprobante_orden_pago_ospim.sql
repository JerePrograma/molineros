 CREATE OR REPLACE FUNCTION insertar_comprobante_orden_pago_ospim(p_timpo_comp character varying,
 p_nro_comp character varying,
 p_pto_venta integer,
 p_cuit character,
 p_importe_comp numeric,
 p_fecha_emision_comp timestamp without time zone,
 p_fecha_recepcion_comp timestamp without time zone,
 p_id integer,
 p_usuario character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
    declare ban integer;  
  begin
	
	ban=(select 1 from comprobante where compro_tipo = p_timpo_comp and compro_nro = p_nro_comp and id_punto_venta = p_pto_venta and cuit = p_cuit);
	
	if (ban is null or ban = 0) then
	  insert into comprobante (
	    compro_tipo,   compro_nro ,
	    fecha,    total,    alta_fecha,    alta_usr,    modi_fecha,    modi_usr ,  exen,
	    grava ,
	    iva_total ,
	    ivan_total,
	    fecha_emision,
	    fecha_recepcion,
	    id_punto_venta,
	    cuit)
	    values (p_timpo_comp, p_nro_comp, LOCALTIMESTAMP, p_importe_comp, LOCALTIMESTAMP,
	     p_usuario , LOCALTIMESTAMP, p_usuario,0,0,0,0, p_fecha_emision_comp, p_fecha_recepcion_comp , p_pto_venta, p_cuit);
	  end if;
	  
     insert into comprobante_orden_pago_ospim (id_orden_pago_ospim,  compro_tipo,   compro_nro , id_punto_venta, cuit)
     values (p_id, p_timpo_comp, p_nro_comp, p_pto_venta, p_cuit);
 
  return  0;
  end;  
$BODY$;


ALTER FUNCTION public.insertar_comprobante_orden_pago_ospim(p_timpo_comp character varying, p_nro_comp character varying, p_pto_venta integer, p_cuit character, p_importe_comp numeric, p_fecha_emision_comp timestamp without time zone, p_fecha_recepcion_comp timestamp without time zone, p_id integer, p_usuario character varying) OWNER TO postgres;

--

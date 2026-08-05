DROP FUNCTION insertar_comprobante_op(
 c_pto_venta integer,
 c_compro_tipo character varying,
 c_compro_nro character varying, 
 c_compro_letra character varying,
 c_compro_sucu integer,
 c_cuit character varying,
 c_id integer,
 c_usuario character varying);
 
 CREATE OR REPLACE FUNCTION insertar_comprobante_op(
  c_pto_venta integer,
 c_compro_tipo character varying,
 c_compro_nro character varying, 
 c_compro_letra character varying,
 c_compro_sucu integer,
 c_cuit character varying,
 c_id integer,
 c_usuario character varying)
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
    declare ban integer;
  begin	
     	insert into comprobante_orden_pago_ospim (id_orden_pago_ospim, compro_tipo, compro_nro, id_punto_venta, cuit, compro_letra, compro_sucu)
        values (c_id, c_compro_tipo, c_compro_nro, c_pto_venta, c_cuit, c_compro_letra, c_compro_sucu);
        return 0;
  end;  
$BODY$;

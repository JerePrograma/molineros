CREATE OR REPLACE FUNCTION actualiza_acta(p_id integer, p_numero character varying, p_cuit character varying, p_sucu character varying, p_fecha_inicio timestamp without time zone, p_fecha_pago timestamp without time zone, p_otros numeric, p_interes numeric, p_capital numeric, p_deuda_actas_asociadas numeric, p_cierre_fecha date, p_user character varying)
  RETURNS integer AS
$BODY$
declare resultDom integer;
BEGIN
UPDATE acta
   SET cuit=p_cuit, numero = p_numero, fecha_inicio=p_fecha_inicio, fecha_pago=p_fecha_pago, 
       modi_fecha=LOCALTIMESTAMP, modi_usr=p_user,  otros=p_otros, sucursal= p_sucu, interes=p_interes, capital = p_capital, deuda_actas_asociadas = p_deuda_actas_asociadas,
       cierre_fecha = p_cierre_fecha
 WHERE id=p_id;

return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE


-- Function: actualiza_acta_no_os(integer, character varying, character varying, character varying, timestamp without time zone, timestamp without time zone, numeric, numeric, numeric, numeric, date, character varying, boolean, character varying, character varying, date, date)

-- DROP FUNCTION actualiza_acta_no_os(integer, character varying, character varying, character varying, timestamp without time zone, timestamp without time zone, numeric, numeric, numeric, numeric, date, character varying, boolean, character varying, character varying, date, date);

CREATE OR REPLACE FUNCTION actualiza_acta_no_os(p_id integer, p_numero character varying, p_cuit character varying, p_sucu character varying, p_fecha_inicio timestamp without time zone, p_fecha_pago timestamp without time zone, p_otros numeric, p_interes numeric, p_capital numeric, p_deuda_actas_asociadas numeric, p_cierre_fecha date, p_user character varying, p_molinera boolean, p_estado character varying, p_entidad character varying, p_periodo_ini date, p_periodo_fin date, p_capital_sindicato numeric, p_interes_sindicato numeric, p_capital_solidario numeric, p_interes_solidario numeric, p_capital_usufructo numeric, p_interes_usufructo numeric, p_capital_art46 numeric, p_interes_art46 numeric)
  RETURNS integer AS
$BODY$
declare resultDom integer;
BEGIN
UPDATE acta_no_os
   SET cuit=p_cuit, numero = p_numero, fecha_inicio=p_fecha_inicio, fecha_pago=p_fecha_pago, 
       modi_fecha=LOCALTIMESTAMP, modi_usr=p_user,  otros=p_otros, sucursal= p_sucu, interes=p_interes, capital = p_capital, deuda_actas_asociadas = p_deuda_actas_asociadas,
       cierre_fecha = p_cierre_fecha,       
       estado=p_estado,
       entidad=p_entidad,
       periodo_ini=p_periodo_ini,
       periodo_fin=p_periodo_fin,
       capital_sindicato=p_capital_sindicato,
       interes_sindicato=p_interes_sindicato,
       capital_solidario=p_capital_solidario,
       interes_solidario=p_interes_solidario,
       capital_usufructo=p_capital_usufructo,
       interes_usufructo=p_interes_usufructo,
       capital_art46=p_capital_art46,
       interes_art46=p_interes_art46
 WHERE id=p_id;

return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE


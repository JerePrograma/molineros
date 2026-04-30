-- Function: inserta_acta_no_os(character varying, character varying, character varying, timestamp without time zone, timestamp without time zone, numeric, numeric, numeric, numeric, date, character varying, boolean, character varying, character varying, date, date)

-- DROP FUNCTION inserta_acta_no_os(character varying, character varying, character varying, timestamp without time zone, timestamp without time zone, numeric, numeric, numeric, numeric, date, character varying, boolean, character varying, character varying, date, date);

CREATE OR REPLACE FUNCTION inserta_acta_no_os(p_numero character varying, p_cuit character varying, p_sucu character varying, p_fecha_inicio timestamp without time zone, p_fecha_pago timestamp without time zone, p_otros numeric, p_interes numeric, p_capital numeric, p_deuda_actas_asociadas numeric, p_cierre_fecha date, p_user character varying, p_molinera boolean, p_estado character varying, p_entidad character varying, p_periodo_ini date, p_periodo_fin date, p_capital_sindicato numeric, p_interes_sindicato numeric, p_capital_solidario numeric, p_interes_solidario numeric, p_capital_usufructo numeric, p_interes_usufructo numeric, p_capital_art46 numeric, p_interes_art46 numeric)
  RETURNS integer AS
$BODY$
declare resultDom integer;
BEGIN
INSERT INTO acta_no_os(
            numero, cuit, fecha_inicio, fecha_pago, alta_fecha, alta_usr, 
            modi_fecha, modi_usr, otros, sucursal, interes, capital,deuda_actas_asociadas, cierre_fecha, molinera, estado, entidad,
            periodo_ini, periodo_fin, acta_cerrada, capital_sindicato, interes_sindicato, capital_solidario, interes_solidario,
            capital_usufructo, interes_usufructo, capital_art46, interes_art46)
select p_numero, p_cuit, p_fecha_inicio, p_fecha_pago, LOCALTIMESTAMP, p_user, LOCALTIMESTAMP, 
            p_user, p_otros,p_sucu, p_interes, p_capital,p_deuda_actas_asociadas, p_cierre_fecha, r.molinera, p_estado, p_entidad,
            p_periodo_ini, p_periodo_fin, true, p_capital_sindicato, p_interes_sindicato, p_capital_solidario, p_interes_solidario,
	    p_capital_usufructo, p_interes_usufructo, p_capital_art46, p_interes_art46
from empresa e
left outer join ramo_empresa r
on r.id_ramo_empresa=e.id_ramo_empresa
where e.cuit=p_cuit
and e.sucursal='000';


return currval('acta_no_os_id_seq');
END;
$BODY$
  LANGUAGE plpgsql VOLATILE

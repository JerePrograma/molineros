-- Function: inserta_convenio_no_os(character varying, character varying, character varying, timestamp without time zone, timestamp without time zone, numeric, numeric, numeric, numeric, numeric, character varying, character varying)

-- DROP FUNCTION inserta_convenio_no_os(character varying, character varying, character varying, timestamp without time zone, timestamp without time zone, numeric, numeric, numeric, numeric, numeric, character varying, character varying);

CREATE OR REPLACE FUNCTION inserta_convenio_no_os(p_numero character varying, p_cuit character varying, p_sucu character varying, p_fecha_inicio timestamp without time zone, p_fecha_pago timestamp without time zone, p_interes numeric, p_ajuste_capital numeric, p_ajuste_interes numeric, p_deuda_actas_asociadas numeric, p_deuda_convenios_asociados numeric, p_user character varying, p_entidad character varying)
  RETURNS integer AS
$BODY$
declare resultDom integer;
BEGIN
INSERT INTO convenio_no_os(
            numero, cuit, fecha_inicio, fecha_pago, alta_fecha, alta_usr, 
            modi_fecha, modi_usr, ajuste_capital, ajuste_interes, sucursal, interes, deuda_actas_asociadas, deuda_convenios_asociados, entidad)
    VALUES (p_numero, p_cuit, p_fecha_inicio, p_fecha_pago, LOCALTIMESTAMP, p_user, LOCALTIMESTAMP, 
            p_user, p_ajuste_capital, p_ajuste_interes, p_sucu, p_interes,   p_deuda_actas_asociadas ,  p_deuda_convenios_asociados, p_entidad );


return currval('convenio_no_os_id_seq');
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION inserta_convenio_no_os(character varying, character varying, character varying, timestamp without time zone, timestamp without time zone, numeric, numeric, numeric, numeric, numeric, character varying, character varying)
  OWNER TO postgres;


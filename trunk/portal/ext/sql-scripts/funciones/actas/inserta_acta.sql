-- Function: inserta_acta(character varying, character varying, character varying, timestamp without time zone, timestamp without time zone, numeric, numeric, numeric, numeric, date, character varying)

-- DROP FUNCTION inserta_acta(character varying, character varying, character varying, timestamp without time zone, timestamp without time zone, numeric, numeric, numeric, numeric, date, character varying);

CREATE OR REPLACE FUNCTION inserta_acta(p_numero character varying, p_cuit character varying, p_sucu character varying, p_fecha_inicio timestamp without time zone, p_fecha_pago timestamp without time zone, p_otros numeric, p_interes numeric, p_capital numeric, p_deuda_actas_asociadas numeric, p_cierre_fecha date, p_user character varying)
  RETURNS integer AS
$BODY$
declare resultDom integer;
BEGIN
INSERT INTO acta(
            numero, cuit, fecha_inicio, fecha_pago, alta_fecha, alta_usr, 
            modi_fecha, modi_usr, otros, sucursal, interes, capital,deuda_actas_asociadas, cierre_fecha, molinera)
select p_numero, p_cuit, p_fecha_inicio, p_fecha_pago, LOCALTIMESTAMP, p_user, LOCALTIMESTAMP, 
            p_user, p_otros,p_sucu, p_interes, p_capital,p_deuda_actas_asociadas, p_cierre_fecha, r.molinera
from empresa e
left outer join ramo_empresa r
on e.id_ramo_empresa=r.id_ramo_empresa
where e.cuit=p_cuit
and e.sucursal='000';


return currval('acta_id_seq');
END;
$BODY$
  LANGUAGE plpgsql VOLATILE


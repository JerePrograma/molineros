CREATE OR REPLACE FUNCTION insertar_detalle_asiento(
	p_asiento_id integer,
	p_pase integer,
	p_id_plan_cuentas integer,
	p_comprobante character varying,
	p_debe numeric(12,2),
	p_haber numeric(12,2),
	p_observaciones character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN
insert into detalle_asiento(asiento_id, pase, id_plan_cuentas, comprobante, debe, haber, observaciones)
values (p_asiento_id, p_pase,  p_id_plan_cuentas, p_comprobante, p_debe, p_haber, p_observaciones);
 
return currval('detalle_asiento_id_seq');
END;
$BODY$;

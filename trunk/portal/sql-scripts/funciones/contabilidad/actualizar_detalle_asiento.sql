CREATE OR REPLACE FUNCTION actualizar_detalle_asiento(
	p_asiento_id integer,
	p_pase integer,
	p_id_plan_cuentas integer,
	p_comprobante character varying,
	p_debe numeric(12,2),
	p_haber numeric(12,2),
	p_observaciones character varying,
	p_id integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN
update detalle_asiento
set  pase = p_pase, id_plan_cuentas = p_id_plan_cuentas, comprobante = p_comprobante, debe = p_debe, haber = p_haber, observaciones = p_observaciones
where id = p_id;
 
return 1;
END;
$BODY$;

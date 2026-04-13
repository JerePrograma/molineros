DROP FUNCTION inserta_recibo_ingreso(
 p_recibo_id integer,
 p_nro_cheque numeric,
 p_id_banco integer,
 p_numero_deposito character varying (30),
 p_importe numeric(12,2),
 p_fecha date,
 p_id_estado_efectivo integer,
 p_user character varying,
 p_id_cuenta_bcria_destino_deposito integer,
 p_id_anticipo_recibo_concepto integer) ;
 
 CREATE OR REPLACE FUNCTION inserta_recibo_ingreso(
 p_recibo_id integer,
 p_nro_cheque numeric,
 p_id_banco integer,
 p_numero_deposito character varying (30),
 p_importe numeric(12,2),
 p_fecha date,
 p_id_estado_efectivo integer,
 p_user character varying,
 p_id_cuenta_bcria_destino_deposito integer,
 p_id_recibo_ingreso_tipo_deposito integer,
 p_id_anticipo_recibo_concepto integer) 
	
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN
	
INSERT INTO recibo_ingresos(
	recibo_id, 	nro_cheque,	id_banco,	numero_deposito,	importe, fecha, id_estado_efectivo, alta_fecha,    alta_usr,    modi_fecha,    modi_usr, id_cuenta_bcria_destino_deposito, id_anticipo_recibo_concepto, id_recibo_ingreso_tipo_deposito)
    VALUES (p_recibo_id, 	p_nro_cheque,	p_id_banco, p_numero_deposito, p_importe, p_fecha, p_id_estado_efectivo, localtimestamp, p_user, localtimestamp, p_user, p_id_cuenta_bcria_destino_deposito, p_id_anticipo_recibo_concepto, p_id_recibo_ingreso_tipo_deposito);



return currval('recibo_ingresos_id_seq');
END;
$BODY$;


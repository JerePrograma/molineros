CREATE OR REPLACE FUNCTION actualiza_cheque(p_nro_cheque numeric,
 p_cuit character varying,
 p_a_nombre_de character varying,
 p_fecha timestamp without time zone,
 p_importe numeric,
 p_alta_usr character varying,
 p_prestador boolean,
 concepto_p character varying,
 p_id_cta_bcria integer,
 p_debito_credito char(1),
 p_id_banco integer,
 p_id_estado integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN
	
	
update cheque set cuit = p_cuit, a_nombre_de = p_a_nombre_de, fecha = p_fecha, importe = p_importe, alta_fecha = LOCALTIMESTAMP, alta_usr = p_alta_usr,
modi_fecha = LOCALTIMESTAMP, modi_usr =  p_alta_usr, prestador = p_prestador, concepto = concepto_p, id_cta_bcria = p_id_cta_bcria, debito_credito = p_debito_credito,
id_estado = p_id_estado, baja_fecha = null, baja_usr  =null
where nro_cheque = p_nro_cheque
and id_banco = p_id_banco;

return 1;
END;
$BODY$;


ALTER FUNCTION public.actualiza_cheque(p_nro_cheque numeric, p_cuit character varying, p_a_nombre_de character varying, p_fecha timestamp without time zone, p_importe numeric, p_alta_usr character varying, p_prestador boolean, concepto_p character varying, p_id_cta_bcria integer, p_debito_credito char(1),  p_id_banco integer, p_id_estado integer) OWNER TO postgres;

--

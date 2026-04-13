
CREATE OR REPLACE FUNCTION insertar_cheques_amtima(p_nro_cheque numeric,
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
	
	
INSERT INTO cheque_amtima(
            nro_cheque, cuit, a_nombre_de, fecha, importe, alta_fecha, alta_usr, modi_fecha, modi_usr, prestador, concepto, id_cta_bcria, debito_credito, id_banco, id_estado)
    VALUES ( p_nro_cheque, p_cuit, p_a_nombre_de, p_fecha, p_importe, LOCALTIMESTAMP, p_alta_usr, LOCALTIMESTAMP, p_alta_usr, p_prestador, concepto_p, p_id_cta_bcria, p_debito_credito, p_id_banco, p_id_estado );


return 1;
END;
$BODY$;


ALTER FUNCTION public.insertar_cheques_amtima(p_nro_cheque numeric, p_cuit character varying, p_a_nombre_de character varying, p_fecha timestamp without time zone, p_importe numeric, p_alta_usr character varying, p_prestador boolean, concepto_p character varying, p_id_cta_bcria integer, p_debito_credito char(1),  p_id_banco integer, p_id_estado integer) OWNER TO postgres;

--

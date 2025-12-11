
CREATE OR REPLACE FUNCTION anular_cheque(p_cheque_nro numeric, p_id_banco integer, p_date date,
 p_usr character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
BEGIN
update cheque set baja_fecha = p_date, baja_usr = p_usr, concepto = concepto || ' - ANULADO' where nro_cheque = p_cheque_nro and id_banco = p_id_banco;
    
return 1;
END;
$BODY$;


ALTER FUNCTION public.anular_cheque(p_cheque_nro numeric, p_id_banco integer, p_date date, p_usr character varying) OWNER TO postgres;

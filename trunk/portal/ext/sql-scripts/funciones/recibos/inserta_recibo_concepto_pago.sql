 CREATE OR REPLACE FUNCTION inserta_recibo_concepto_pago(
 p_recibo_concepto_id integer,
 p_recibo_ingreso_id integer,
 p_importe numeric(12,2), 
 p_user character varying) 
	
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN
	
INSERT INTO recibo_conceptos_pagos( recibo_concepto_id ,	recibo_ingreso_id,
			importe, alta_fecha,  alta_usr, modi_fecha, modi_usr, pendiente_derivar)
    VALUES (p_recibo_concepto_id, p_recibo_ingreso_id, 	p_importe,	localtimestamp, p_user, localtimestamp, p_user, p_importe);


return 1;
END;
$BODY$;


ALTER FUNCTION public.inserta_recibo_concepto_pago(
 p_recibo_concepto_id integer,
 p_recibo_ingreso_id integer,
 p_importe numeric(12,2), 
 p_user character varying)    OWNER TO postgres;

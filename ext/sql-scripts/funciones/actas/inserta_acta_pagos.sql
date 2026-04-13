DROP FUNCTION inserta_acta_pagos(p_acta_id integer,
 p_tipo character(3),
 p_fecha_pago timestamp without time zone,
 p_importe numeric,
 p_user character varying,
 p_nro_cheque numeric,
 	p_banco_cheque integer) ;
 
 
 CREATE OR REPLACE FUNCTION inserta_acta_pagos(p_acta_id integer,
 p_tipo character(3),
 p_fecha_pago timestamp without time zone,
 p_importe numeric,
 p_user character varying,
 p_nro_cheque numeric,
 	p_banco_cheque integer,
 	p_forma char(1)) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN
insert into acta_pagos (acta_id, tipo, fecha_pago, importe, interes, alta_fecha ,
  alta_usr,  modi_fecha, modi_usr, nro_cheque ,
 	banco_cheque, forma )
values ( p_acta_id, p_tipo, p_fecha_pago, p_importe, 0, 
localtimestamp, p_user,localtimestamp, p_user,p_nro_cheque,p_banco_cheque, p_forma);


return 1;
END;
$BODY$;


ALTER FUNCTION public.inserta_acta_pagos(p_acta_id integer,
 p_tipo character(3),
 p_fecha_pago timestamp without time zone,
 p_importe numeric,
 p_user character varying,
 p_nro_cheque numeric,
 	p_banco_cheque integer,
 	p_forma char(1)) OWNER TO postgres;

--
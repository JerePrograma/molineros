CREATE OR REPLACE FUNCTION borrar_acta_pagos(p_acta_pago_id integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
    declare p_nro_cheque numeric;
    declare p_banco_cheque integer;
BEGIN
	p_nro_cheque = nro_cheque from acta_pagos where id = p_acta_pago_id;
	p_banco_cheque = banco_cheque from acta_pagos where id = p_acta_pago_id;
delete from acta_pagos where id = p_acta_pago_id;
delete from cheque c where nro_cheque =p_nro_cheque and id_banco = p_banco_cheque and id_estado = 2;
return 1;
END;
$BODY$;


ALTER FUNCTION public.borrar_acta_pagos(p_acta_pago_id integer) OWNER TO postgres;

--

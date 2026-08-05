-- Function: borrar_acta_pagos(integer)

-- DROP FUNCTION borrar_acta_pagos(integer);

CREATE OR REPLACE FUNCTION borrar_acta_no_os_pagos(p_acta_pago_id integer)
  RETURNS integer AS
$BODY$
    declare p_nro_cheque numeric;
    declare p_banco_cheque integer;
BEGIN
	p_nro_cheque = nro_cheque from acta_no_os_pagos where id = p_acta_pago_id;
	p_banco_cheque = banco_cheque from acta_no_os_pagos where id = p_acta_pago_id;
delete from acta_no_os_pagos where id = p_acta_pago_id;
delete from cheque c where nro_cheque =p_nro_cheque and id_banco = p_banco_cheque and id_estado = 2;
return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION borrar_acta_pagos(integer)
  OWNER TO postgres;


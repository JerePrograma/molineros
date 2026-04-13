-- Function: inserta_acta_no_os_pagos(integer, character, timestamp without time zone, numeric, character varying, numeric, integer, character)

-- DROP FUNCTION inserta_acta_no_os_pagos(integer, character, timestamp without time zone, numeric, character varying, numeric, integer, character);

CREATE OR REPLACE FUNCTION inserta_acta_no_os_pagos(p_acta_id integer, p_tipo character, p_fecha_pago timestamp without time zone, p_importe numeric, p_user character varying, p_nro_cheque numeric, p_banco_cheque integer, p_forma character)
  RETURNS integer AS
$BODY$
declare resultDom integer;
BEGIN
insert into acta_no_os_pagos (acta_id, tipo, fecha_pago, importe, interes, alta_fecha ,
  alta_usr,  modi_fecha, modi_usr, nro_cheque ,
 	banco_cheque, forma )
values ( p_acta_id, p_tipo, p_fecha_pago, p_importe, 0, 
localtimestamp, p_user,localtimestamp, p_user,p_nro_cheque,p_banco_cheque, p_forma);


return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION inserta_acta_no_os_pagos(integer, character, timestamp without time zone, numeric, character varying, numeric, integer, character)
  OWNER TO postgres;


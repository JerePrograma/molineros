-- Function: inserta_convenio_no_os_pagos(integer, character, timestamp without time zone, numeric, numeric, character varying, numeric, integer, integer)

-- DROP FUNCTION inserta_convenio_no_os_pagos(integer, character, timestamp without time zone, numeric, numeric, character varying, numeric, integer, integer);

CREATE OR REPLACE FUNCTION inserta_convenio_no_os_pagos(p_convenio_id integer, p_tipo character, p_fecha_pago timestamp without time zone, p_importe numeric, p_interes numeric, p_user character varying, p_nro_cheque numeric, p_banco_cheque integer, p_nrocuota integer)
  RETURNS integer AS
$BODY$
declare resultDom integer;
BEGIN
insert into convenio_no_os_pagos (convenio_id, tipo, fecha_pago, importe, alta_fecha ,
  alta_usr,  modi_fecha, modi_usr, nro_cheque ,
 	banco_cheque,cuota_id, interes )
values ( p_convenio_id, p_tipo, p_fecha_pago, p_importe, 
localtimestamp, p_user,localtimestamp, p_user,p_nro_cheque,p_banco_cheque, p_nrocuota, p_interes);


return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION inserta_convenio_no_os_pagos(integer, character, timestamp without time zone, numeric, numeric, character varying, numeric, integer, integer)
  OWNER TO postgres;


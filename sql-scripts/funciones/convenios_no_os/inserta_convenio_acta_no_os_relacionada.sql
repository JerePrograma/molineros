-- Function: inserta_convenio_acta_no_os_relacionada(integer, integer, numeric, numeric, character varying)

-- DROP FUNCTION inserta_convenio_acta_no_os_relacionada(integer, integer, numeric, numeric, character varying);

CREATE OR REPLACE FUNCTION inserta_convenio_acta_no_os_relacionada(p_convenio_id integer, p_acta_relacionada_id integer, p_importe numeric, p_saldo numeric, p_usr character varying)
  RETURNS integer AS
$BODY$
declare v_periodo date;
BEGIN
	
insert into convenio_actas_no_os (convenio_id, acta_id, importe, saldo, alta_fecha,alta_usr, modi_fecha, modi_usr) 
values (p_convenio_id, p_acta_relacionada_id, p_importe, p_saldo, localtimestamp, p_usr, localtimestamp, p_usr);

insert into acta_no_os_pagos (acta_id, tipo, fecha_pago, importe, interes, alta_fecha ,
  alta_usr,  modi_fecha, modi_usr, convenio_acta_id, forma)
values (p_acta_relacionada_id, 'PGO', localtimestamp, p_importe, 0, 
localtimestamp, p_usr,localtimestamp, p_usr, currval('convenio_actas_no_os_id_seq'), 'E');

return currval('convenio_actas_no_os_id_seq');
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION inserta_convenio_acta_no_os_relacionada(integer, integer, numeric, numeric, character varying)
  OWNER TO postgres;


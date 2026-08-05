-- Function: anular_recibo_no_os(integer, character varying)

-- DROP FUNCTION anular_recibo_no_os(integer, character varying);

CREATE OR REPLACE FUNCTION anular_recibo_no_os(p_id integer, p_user character varying)
  RETURNS integer AS
$BODY$
    declare p_compro_nro character varying(50);
    declare p_cuit character(11);
BEGIN
update recibo_no_os set baja_fecha = localtimestamp, baja_usr = p_user where id = p_id;

p_compro_nro=numero from recibo_no_os where id = p_id;
p_cuit=cuit from recibo_no_os where id = p_id;
update comprobante set baja_fecha = localtimestamp, baja_usr = p_user
where id_punto_venta = 0 and compro_tipo = 'REC' and compro_letra = '' and compro_sucu= 0 and compro_nro = p_compro_nro and cuit = p_cuit ;

update recibo_no_os_ingresos set baja_fecha = localtimestamp, baja_usr = p_user where recibo_id = p_id;
update recibo_no_os_conceptos    set baja_fecha = localtimestamp, baja_usr = p_user where recibo_id = p_id;


update cheque  set  id_estado = (select id from cheque_estado where descripcion = 'Cargado')
from acta_no_os_pagos ap
where cheque.nro_cheque = ap.nro_cheque
and cheque.id_banco = ap.banco_cheque
and ap.recibo_id = p_id; 

update cheque  set  id_estado = (select id from cheque_estado where descripcion = 'Cargado')
from convenio_no_os_pagos ap
where cheque.nro_cheque = ap.nro_cheque
and cheque.id_banco = ap.banco_cheque
and ap.recibo_id = p_id; 

update acta_no_os_pagos set recibo_id = null where recibo_id = p_id;
update convenio_no_os_pagos set recibo_id = null where recibo_id = p_id;

update cheque  set  baja_fecha = localtimestamp, baja_usr = p_user, concepto = cheque.concepto || ' - RECIBO ANULADO'
from recibo_no_os_ingresos rc 
where cheque.nro_cheque = rc.nro_cheque
and cheque.id_banco = rc.id_banco
and rc.recibo_id = p_id
and cheque.id_estado = (select id from cheque_estado where descripcion = 'Recibido');

update cheque  set  id_estado = (select id from cheque_estado where descripcion = 'Recibido')
from recibo_no_os_conceptos rc 
where cheque.nro_cheque = rc.nro_cheque_no_depositado
and cheque.id_banco = rc.id_banco_no_depositado
and rc.recibo_id = p_id 
and cheque.id_estado = (select id from cheque_estado where descripcion = 'Sustituido');


update cheque  set  id_estado = (select id from cheque_estado where descripcion = 'Rechazado')
from recibo_no_os_conceptos rc 
where cheque.nro_cheque = rc.nro_cheque_rechazado
and cheque.id_banco = rc.id_banco_rechazado
and rc.recibo_id = p_id
and cheque.id_estado = (select id from cheque_estado where descripcion = 'Sustituido');

update recibo_conceptos_pagos rcp set  baja_fecha = localtimestamp, baja_usr = p_user
from recibo_no_os_ingresos ri
where rcp.recibo_ingreso_id = ri.id
and ri.recibo_id = p_id;

return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION anular_recibo_no_os(integer, character varying)
  OWNER TO postgres;


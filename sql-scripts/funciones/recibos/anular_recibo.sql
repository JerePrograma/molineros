CREATE OR REPLACE FUNCTION anular_recibo(p_id integer,  p_user character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
    declare p_compro_nro character varying(50);
    declare p_cuit character(11);
BEGIN
update recibo set baja_fecha = localtimestamp, baja_usr = p_user where id = p_id;

p_compro_nro=numero from recibo where id = p_id;
p_cuit=cuit from recibo where id = p_id;
update comprobante set baja_fecha = localtimestamp, baja_usr = p_user
where id_punto_venta = 0 and compro_tipo = 'REC' and compro_letra = '' and compro_sucu= 0 and compro_nro = p_compro_nro and cuit = p_cuit ;

update recibo_ingresos set baja_fecha = localtimestamp, baja_usr = p_user where recibo_id = p_id;
update recibo_conceptos    set baja_fecha = localtimestamp, baja_usr = p_user where recibo_id = p_id;


update cheque  set  id_estado = (select id from cheque_estado where descripcion = 'Cargado')
from acta_pagos ap
where cheque.nro_cheque = ap.nro_cheque
and cheque.id_banco = ap.banco_cheque
and ap.recibo_id = p_id; 

update cheque  set  id_estado = (select id from cheque_estado where descripcion = 'Cargado')
from convenio_pagos ap
where cheque.nro_cheque = ap.nro_cheque
and cheque.id_banco = ap.banco_cheque
and ap.recibo_id = p_id; 

update acta_pagos set recibo_id = null where recibo_id = p_id;
update convenio_pagos set recibo_id = null where recibo_id = p_id;

update cheque  set  baja_fecha = localtimestamp, baja_usr = p_user, concepto = cheque.concepto || ' - RECIBO ANULADO'
from recibo_ingresos rc 
where cheque.nro_cheque = rc.nro_cheque
and cheque.id_banco = rc.id_banco
and rc.recibo_id = p_id
and cheque.id_estado = (select id from cheque_estado where descripcion = 'Recibido');

update cheque  set  id_estado = (select id from cheque_estado where descripcion = 'Recibido')
from recibo_conceptos rc 
where cheque.nro_cheque = rc.nro_cheque_no_depositado
and cheque.id_banco = rc.id_banco_no_depositado
and rc.recibo_id = p_id 
and cheque.id_estado = (select id from cheque_estado where descripcion = 'Sustituido');


update cheque  set  id_estado = (select id from cheque_estado where descripcion = 'Rechazado')
from recibo_conceptos rc 
where cheque.nro_cheque = rc.nro_cheque_rechazado
and cheque.id_banco = rc.id_banco_rechazado
and rc.recibo_id = p_id
and cheque.id_estado = (select id from cheque_estado where descripcion = 'Sustituido');

update recibo_conceptos_pagos rcp set  baja_fecha = localtimestamp, baja_usr = p_user
from recibo_ingresos ri
where rcp.recibo_ingreso_id = ri.id
and ri.recibo_id = p_id;

return 1;
END;
$BODY$;


ALTER FUNCTION public.anular_recibo(p_id integer,  p_user character varying)  OWNER TO postgres;

--

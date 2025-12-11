create or replace function buscar_ultimo_cheque_amtima_de_op(p_id_cta_bcria integer)
returns numeric 
LANGUAGE sql
AS $BODY$

select max(nro_cheque) from orden_pago_amtima_pagos
 where id_orden_pago = (select max( id_orden_pago) from orden_pago_amtima_pagos where id_cta_bcria_cheque = $1 and nro_cheque is not null);

$BODY$;
ALTER FUNCTION public.buscar_ultimo_cheque_amtima_de_op(p_id_cta_bcria integer) OWNER TO postgres;



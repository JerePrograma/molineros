
create or replace function buscar_ultima_razon_social_cheque_op (
 p_cuit character varying, p_sucu character varying, p_seccional integer
)
returns character varying 
LANGUAGE sql
AS $BODY$

	select a_nombre_de
	from cheque c
	where  nro_cheque = (select max(opop.nro_cheque) from orden_pago_ospim_pagos  opop
				inner join orden_pago_ospim op
				on opop.id_orden_pago = op.id_orden_pago
				where op.cuit_acreedor = $1
				and (($3 is null and op.sucu_acreedor = $2) or ($3 is not null and op.sucu_acreedor = '000'))
				and ($3 is null or $3 = op.id_seccional))
	limit 1;


$BODY$;

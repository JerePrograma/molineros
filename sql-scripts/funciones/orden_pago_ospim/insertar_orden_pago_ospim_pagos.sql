drop function insertar_orden_pago_ospim_pagos(
p_id_orden_pago integer,
p_id_punto_venta_antic numeric ,
p_compro_tipo_antic character varying(3) ,
p_compro_nro_antic character varying(50) ,
p_cuit_antic character(11) ,
p_compro_letra_antic character varying(1) ,
p_compro_sucu_antic integer ,
p_nro_cheque numeric(15,0),
p_id_banco_cheque integer,
p_id_cta_bcria_cheque integer,
p_id_cta_bcria_retencion integer,
p_importe_retencion numeric(12,2),
p_id_cta_bcria_transf_bcria integer,
p_importe_transf_bcria numeric(12,2),
p_nro_transf_bcria character varying(50),
p_id_cta_bcria_debito_crio integer,
p_importe_debito_bcrio numeric(12,2),
p_nro_debito_bcrio character varying(50)
);

create or replace function insertar_orden_pago_ospim_pagos(
p_id_orden_pago integer,
p_id_punto_venta_antic numeric ,
p_compro_tipo_antic character varying(3) ,
p_compro_nro_antic character varying(50) ,
p_cuit_antic character(11) ,
p_compro_letra_antic character varying(1) ,
p_compro_sucu_antic integer ,
p_nro_cheque numeric(15,0),
p_id_banco_cheque integer,
p_id_cta_bcria_cheque integer,
p_id_cta_bcria_retencion integer,
p_importe_retencion numeric(12,2),
p_id_cta_bcria_debito_crio integer,
p_importe_debito_bcrio numeric(12,2),
p_nro_debito_bcrio character varying(50),
p_tipo_pago integer
)
RETURNS integer
 LANGUAGE plpgsql
 AS $BODY$
 declare ban integer;  
begin


insert into orden_pago_ospim_pagos (id_orden_pago, id_punto_venta_antic, compro_tipo_antic, compro_nro_antic, cuit_antic, compro_letra_antic,
 compro_sucu_antic, nro_cheque, id_banco_cheque, id_cta_bcria_cheque, id_cta_bcria_retencion, importe_retencion,  id_cta_bcria_debito_crio, importe_debito_bcrio, nro_debito_bcrio, tipo_pago )
values (p_id_orden_pago, p_id_punto_venta_antic, p_compro_tipo_antic, p_compro_nro_antic, p_cuit_antic, p_compro_letra_antic,
 p_compro_sucu_antic, p_nro_cheque, p_id_banco_cheque, p_id_cta_bcria_cheque, p_id_cta_bcria_retencion, p_importe_retencion, 
  p_id_cta_bcria_debito_crio, p_importe_debito_bcrio, p_nro_debito_bcrio, p_tipo_pago);

 return  0;
end;  
$BODY$;

drop function reporte_egresos_por_concepto_cta_46(p_fecha_ini date, p_fecha_fin date);

create or replace function reporte_egresos_por_concepto_cta_46(p_fecha_ini date, p_fecha_fin date) 
RETURNS table (descripcion character varying, importe numeric)
    LANGUAGE sql
    AS $BODY$



select descripcion_original, sum(importe)  as importe
from (
select cc.concepto_id,  sum(case when comp.debito_para_egreso = true then -1 * cc.importe else cc.importe end) as importe
from orden_pago_ospim op,
     comprobante_orden_pago_ospim copo,
     concepto_comprobante cc,
     comprobante comp
where op.id_orden_pago=copo.id_orden_pago_ospim
and copo.cuit = cc.cuit
and copo.compro_sucu = cc.compro_sucu
and copo.compro_letra = cc.compro_letra
and copo.compro_tipo = cc.compro_tipo
and copo.compro_nro = cc.compro_nro
and copo.id_punto_venta = cc.id_punto_venta
and comp.cuit = cc.cuit
and comp.compro_sucu = cc.compro_sucu
and comp.compro_letra = cc.compro_letra
and comp.compro_tipo = cc.compro_tipo
and comp.compro_nro = cc.compro_nro
and comp.id_punto_venta = cc.id_punto_venta
and CAST(op.alta_fecha AS DATE) >= $1 and CAST(op.alta_fecha AS DATE) <=$2
and (op.baja_fecha is null or (op.baja_fecha is not null and date_trunc('month',op.baja_fecha) > date_trunc('month',op.alta_fecha)))
and exists (select 1 from orden_pago_ospim_pagos opp
	    where opp.id_orden_pago=op.id_orden_pago
	    and (id_cta_bcria_cheque=2
	    or id_cta_bcria_retencion=2
	    or id_cta_bcria_transf_bcria=2
	    or id_cta_bcria_debito_crio=2))
group by cc.concepto_id
union all
select cc.concepto_id, -1* sum(case when comp.debito_para_egreso = true then -1 * cc.importe else cc.importe end) as importe
from orden_pago_ospim op,
     comprobante_orden_pago_ospim copo,
     concepto_comprobante cc,
     comprobante comp
where op.id_orden_pago=copo.id_orden_pago_ospim
and copo.cuit = cc.cuit
and copo.compro_sucu = cc.compro_sucu
and copo.compro_letra = cc.compro_letra
and copo.compro_tipo = cc.compro_tipo
and copo.compro_nro = cc.compro_nro
and copo.id_punto_venta = cc.id_punto_venta
and comp.cuit = cc.cuit
and comp.compro_sucu = cc.compro_sucu
and comp.compro_letra = cc.compro_letra
and comp.compro_tipo = cc.compro_tipo
and comp.compro_nro = cc.compro_nro
and comp.id_punto_venta = cc.id_punto_venta
and cast(op.baja_fecha as date)>= $1 and cast(op.baja_fecha as date)<=$2
and cast(op.alta_fecha as date) < $1
and exists (select 1 from orden_pago_ospim_pagos opp
	    where opp.id_orden_pago=op.id_orden_pago
	    and (id_cta_bcria_cheque=2
	    or id_cta_bcria_retencion=2
	    or id_cta_bcria_transf_bcria=2
	    or id_cta_bcria_debito_crio=2))
group by cc.concepto_id
union all
--ANTIC
select c.concepto_id,  -1* sum(c.importe) as importe from 
orden_pago_ospim opo,
orden_pago_ospim_pagos opop,
concepto_comprobante c
where opo.id_orden_pago = opop.id_orden_pago
and opop.compro_tipo_antic = c.compro_tipo
and opop.compro_sucu_antic = c.compro_sucu
and opop.compro_letra_antic = c.compro_letra
and opop.compro_nro_antic = c.compro_nro
and opop.cuit_antic = c.cuit
and opop.id_punto_venta_antic = c.id_punto_venta
and cast(opo.alta_fecha as date) >= $1 and cast(opo.alta_fecha as date)<=$2
and (opo.baja_fecha is null or (opo.baja_fecha is not null and date_trunc('month',opo.baja_fecha) > date_trunc('month',opo.alta_fecha)))
and ((exists  (select 1 from orden_pago_ospim_pagos opp
	    where opp.id_orden_pago=opo.id_orden_pago
	    and (id_cta_bcria_cheque=2
	    or id_cta_bcria_retencion=2
	    or id_cta_bcria_transf_bcria=2
	    or id_cta_bcria_debito_crio=2)) 
	and not exists (select 1 from orden_pago_ospim_pagos opp
	    where opp.id_orden_pago=opo.id_orden_pago
	    and (id_cta_bcria_cheque<>2
	    or id_cta_bcria_retencion<>2
	    or id_cta_bcria_transf_bcria<>2
	    or id_cta_bcria_debito_crio<>2))) )
    and exists (select 1 from comprobante_orden_pago_ospim copo2,
				orden_pago_ospim_pagos opop2
				where   opop.compro_tipo_antic = copo2.compro_tipo
					and opop.compro_sucu_antic = copo2.compro_sucu
					and opop.compro_letra_antic = copo2.compro_letra
					and opop.compro_nro_antic = copo2.compro_nro
					and opop.cuit_antic = copo2.cuit
					and opop.id_punto_venta_antic = copo2.id_punto_venta
					and copo2.id_orden_pago_ospim  = opop2.id_orden_pago
					and (opop2.id_cta_bcria_cheque=2
					    or opop2.id_cta_bcria_retencion=2
					    or opop2.id_cta_bcria_transf_bcria=2
					    or opop2.id_cta_bcria_debito_crio=2))
group by c.concepto_id
union all
select c.concepto_id,  sum(c.importe) as importe  from 
orden_pago_ospim opo,
orden_pago_ospim_pagos opop,
concepto_comprobante c
where opo.id_orden_pago = opop.id_orden_pago
and opop.compro_tipo_antic = c.compro_tipo
and opop.compro_sucu_antic = c.compro_sucu
and opop.compro_letra_antic = c.compro_letra
and opop.compro_nro_antic = c.compro_nro
and opop.cuit_antic = c.cuit
and opop.id_punto_venta_antic = c.id_punto_venta
and cast(opo.baja_fecha as date)>= $1 and cast(opo.baja_fecha as date)<=$2
and cast(opo.alta_fecha as date)< $1
group by c.concepto_id
) asd
left outer join concepto_maestro cm
on asd.concepto_id = cm.id
group by descripcion_original
order by descripcion_original;

$BODY$;
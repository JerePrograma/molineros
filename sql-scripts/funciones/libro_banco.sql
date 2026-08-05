-- Function: libro_banco(date, date, integer)

-- DROP FUNCTION libro_banco(date, date, integer);

CREATE OR REPLACE FUNCTION libro_banco(IN p_fecha_ini date, IN p_fecha_fin date, IN p_id_cta_bcria integer)
  RETURNS TABLE(fecha date, debito_credito character, comprobante character varying, descripcion character varying, importe numeric, id_cuenta_bcria integer) AS
$BODY$

select  libro_banco.fecha,
  libro_banco.debito_credito,
  libro_banco.comprobante,
  libro_banco.descripcion,
  libro_banco.importe,
  libro_banco.id_cuenta_bcria
from (
	--extraccion bcria
	select fecha,  
	case when (sum(importe) ) < 0 then 'D' else 'C' end as debito_credito,
	comprobante, 
	descripcion, 
	case when (sum(importe) ) < 0 then -1* sum(importe) else  sum(importe) end  as importe,
	id_cuenta_bcria
	from (
		select cast(fecha as date),  debito_credito, 'ACR AFIP - '||cast(fecha as date) as comprobante, tmb.descripcion, 
		importe , CAST(2 AS INTEGER) as id_cuenta_bcria
		from detalle_extraccion_bancaria    deb
		inner join codigo_ext_bcrias_afip cod 
		on cast (deb.codigo_movimiento as integer) =  cod.codigo
		inner join tipo_mov_bcrio tmb
		on cod.id_tipo_mov = tmb.id_tipo_mov_maestro
		and cast(tmb.valido_desde as date) <=  cast(fecha as date)
		and cast(tmb.valido_hasta as date) >=  cast(fecha as date)
		where tipo = 'PRO'
		and cast(fecha as date) > (cast($1 as date) - interval '1 day') and cast(fecha as date) < (cast($2 as date)+ interval '1 day')
		and ($3 = 0 or $3 = 2) --BANCO NACION/46
	) sdf
	group by fecha , comprobante, descripcion, id_cuenta_bcria
	/*UNION ALL
	select fecha_transf as fecha,
	 'C',  
	 'ACR AFIP - '||cast(fecha_transf as date) as comprobante, 
	 'Pago Anticipos' as descripcion,
	  sum(importe) as importe, 
	  CAST(2 AS INTEGER) as id_cuenta_bcria from os_aportes_detalle 
	where fecha_transf > (cast($1 as date) - interval '1 day') and fecha_transf <= (cast($2  as date) + interval '1 day')
	and ($3 = 0 or $3 = 2) --BANCO NACION/46
	and sucur like 'AN_%'
	group by fecha_transf*/
	union all
	--subsidio
	select cast(fecha_proceso as date) , case when sum(subsidio ) > 0 then 'C' else 'D'  end as debitocredito, 'Subsidio - ' || cast(fecha_proceso as date)  as comprobante, 'Acreditación mensual subsidio', 
	sum(subsidio ) as importe, 2 as id_cuenta_bcria
	from detalle_subsidio_os 
	where ($3 = 0 or $3 = 2) --BANCO NACION/46
	group by fecha_proceso
	having cast(fecha_proceso as date) > ($1 - interval '1 day') and cast(fecha_proceso as date) < ($2 + interval '1 day')
	---union all
	--os_aportes
	/*select cast(fecha_proceso as date) , deb_cred3, 'ACR AFIP - Anticipo - ' || cast(fecha_proceso as date)  as comprobante, 'AFIP Anticipo', 
	case when deb_cred3 = 'D' then -1*importe_ant else importe_ant end as importe, 2 as id_cuenta_bcria
	from os_aportes_footer_fn 
	where importe_ant <> 0  
	and cast(fecha_proceso as date) > ($1 - interval '1 day') and cast(fecha_proceso as date) < ($2 + interval '1 day')
	and ($3 = 0 or $3 = 2) --BANCO NACION/46*/
	
	
	
	
	union all
	--MOVIMIENTO BANCARIO
	select cast(fecha_movimiento as date) , 
		case when deb_cred then 'D' else 'C' end, 
		'Mov bancario - ' || mb.id_movimiento  as comprobante, tmb.descripcion , 
		cast (importe_movimiento as numeric) as importe, id_cuenta_bcria as id_cuenta_bcria
	from  movimiento_banco mb
	inner join tipo_mov_bcrio tmb
	on mb.id_tipo_mov= tmb.id_tipo_mov_maestro
	and cast(tmb.valido_desde as date) <=  cast(fecha_movimiento as date)
	and cast(tmb.valido_hasta as date) >=  cast(fecha_movimiento as date)
	where cast(fecha_movimiento as date) > ($1 - interval '1 day') and cast(fecha_movimiento as date) < ($2 + interval '1 day')
	and ($3 = 0 or id_cuenta_bcria = $3)
	union all 
	select cast(mb.baja_fecha as date) , 
		case when deb_cred then 'C' else 'D' end, 
		'Mov bancario - ANULADO - ' || mb.id_movimiento  as comprobante, tmb.descripcion, 
		cast (importe_movimiento as numeric) as importe, id_cuenta_bcria as id_cuenta_bcria
	from  movimiento_banco mb
	inner join tipo_mov_bcrio tmb
	on mb.id_tipo_mov= tmb.id_tipo_mov_maestro
	and cast(tmb.valido_desde as date) <=  cast(mb.fecha_movimiento as date)
	and cast(tmb.valido_hasta as date) >=  cast(mb.fecha_movimiento as date)
	where mb.baja_fecha is not null
	and cast(mb.baja_fecha as date) > ($1 - interval '1 day') and cast(mb.baja_fecha as date) < ($2 + interval '1 day')
	and ($3 = 0 or id_cuenta_bcria = $3)
	union all
	--DEPOSITO BANCARIO
	select r.fecha,'C', 'RCB - ' || r.numero || ' - ' || ritd.descripcion  as comprobante , 
	'Cuit: ' || r.cuit || ' - ' || coalesce(e.razon_soc, '') ,  sum(ri.importe) as importe, id_cuenta_bcria_destino_deposito as id_cuenta_bcria
	from recibo r 
	inner join recibo_ingresos ri
	on r.id = ri.recibo_id
	and ri.numero_deposito is not null
	inner join recibo_ingreso_tipo_deposito ritd
	on ri.id_recibo_ingreso_tipo_deposito = ritd.id
	left outer join empresa e
	on e.cuit = r.cuit
	and e.sucursal = r.sucursal
	where ($3 = 0 or id_cuenta_bcria_destino_deposito = $3)
	group by r.numero, r.fecha, r.cuit, r.sucursal, e.razon_soc, id_cuenta_bcria_destino_deposito, ritd.descripcion 
	having  r.fecha > ($1 - interval '1 day') and  r.fecha < ($2 + interval '1 day')
	union all
	select cast (r.baja_fecha as date),'D', 'RCB - ANULADO - ' || r.numero || ' - '  || ritd.descripcion   as comprobante , 
	'Cuit: ' || r.cuit || ' - ' || coalesce(e.razon_soc, '') ,  sum(ri.importe) as importe, id_cuenta_bcria_destino_deposito as id_cuenta_bcria
	from recibo r 
	inner join recibo_ingresos ri
	on r.id = ri.recibo_id
	and ri.numero_deposito is not null 
	inner join recibo_ingreso_tipo_deposito ritd
	on ri.id_recibo_ingreso_tipo_deposito = ritd.id
	left outer join empresa e
	on e.cuit = r.cuit
	and e.sucursal = r.sucursal
	where ri.numero_deposito is not null 
	and r.baja_fecha is not null
	and ($3 = 0 or id_cuenta_bcria_destino_deposito = $3) 
	group by r.numero, r.baja_fecha, r.cuit, r.sucursal, e.razon_soc, id_cuenta_bcria_destino_deposito, ritd.descripcion 
	having  r.baja_fecha > ($1 - interval '1 day') and  r.baja_fecha < ($2 + interval '1 day')
	union all
	--CHEQUE	
	select cast(c.fecha as date), 'D', 'Cheque - ' || c.nro_cheque  as comprobante, 
	'OP - '  || op.id_orden_pago || ' - Cuit:' || coalesce(op.cuit_acreedor,'') || ' - ' || coalesce(e.razon_soc,'') as descripcion,
	 c.importe, c.id_cta_bcria
	from cheque c
	inner join orden_pago_ospim_pagos opop
	on c.nro_cheque = opop.nro_cheque
	and c.id_banco = opop.id_banco_cheque
	inner join orden_pago_ospim op
	on opop.id_orden_pago = op.id_orden_pago
	left outer join empresa e
	on op.cuit_acreedor = e.cuit
	and e.sucursal = '000'
	where cast(c.fecha as date) > ($1 - interval '1 day') and  cast(c.fecha as date) < ($2 + interval '1 day')
	and ($3 = 0 or id_cta_bcria = $3)
	union all
	select  cast(op.baja_fecha as date), 'C', 
	'Cheque - ' || (case when c.baja_fecha is not null then 'ANULADO - ' else '' end) || c.nro_cheque  as comprobante, 
	(case when op.baja_fecha is not null then 'OP - ANULADA - ' else 'OP - ' end) || op.id_orden_pago || ' - Cuit:' || coalesce( op.cuit_acreedor,'') || ' - ' || coalesce(e.razon_soc, '') as descripcion,
	 c.importe, c.id_cta_bcria
	from cheque c
	inner join orden_pago_ospim_pagos opop
	on c.nro_cheque = opop.nro_cheque
	and c.id_banco = opop.id_banco_cheque
	inner join orden_pago_ospim op
	on opop.id_orden_pago = op.id_orden_pago
	left outer join empresa e
	on op.cuit_acreedor = e.cuit
	and e.sucursal = '000'
	where op.baja_fecha is not null 
	and cast(op.baja_fecha as date) > ($1 - interval '1 day') and  cast(op.baja_fecha as date) < ($2 + interval '1 day')
	and ($3 = 0 or id_cta_bcria = $3)
	union all
	--DEBITO BANC
	select cast(op.alta_fecha as date), 'D',  tp.descripcion ||' - ' || (case when opop.nro_debito_bcrio is not null then  opop.nro_debito_bcrio else '' end) as comprobante, 
	'OP - '  || op.id_orden_pago || ' - Cuit:' || coalesce(op.cuit_acreedor,'') || ' - ' || coalesce(e.razon_soc,'') as descripcion,
	 opop.importe_debito_bcrio, id_cta_bcria_debito_crio
	from  orden_pago_ospim_pagos opop
	inner join tipo_pago tp
	on opop.tipo_pago = tp.id_tipo_pago
	inner join orden_pago_ospim op
	on opop.id_orden_pago = op.id_orden_pago
	left outer join empresa e
	on op.cuit_acreedor = e.cuit
	and e.sucursal = '000'
	where cast(op.alta_fecha as date) > ($1 - interval '1 day') and  cast(op.alta_fecha as date) < ($2 + interval '1 day')
	and ($3 = 0 or id_cta_bcria_debito_crio = $3)
	and importe_debito_bcrio is not null
	and opop.tipo_pago != 2
	union all
	select  cast(op.baja_fecha as date), 'C', 
	 tp.descripcion ||' - ANULADO - ' || (case when opop.nro_debito_bcrio is not null then  opop.nro_debito_bcrio else '' end) as comprobante, 
	'OP ANULADA - '  || op.id_orden_pago || ' - Cuit:' || coalesce(op.cuit_acreedor,'') || ' - ' || coalesce(e.razon_soc,'') as descripcion,
	 opop.importe_debito_bcrio,  id_cta_bcria_debito_crio
	from orden_pago_ospim_pagos opop
	inner join tipo_pago tp
	on opop.tipo_pago = tp.id_tipo_pago
	inner join orden_pago_ospim op
	on opop.id_orden_pago = op.id_orden_pago
	left outer join empresa e
	on op.cuit_acreedor = e.cuit
	and e.sucursal = '000'
	where op.baja_fecha is not null
	and cast(op.baja_fecha as date) > ($1 - interval '1 day') and  cast(op.baja_fecha as date) < ($2 + interval '1 day')
	and ($3 = 0 or id_cta_bcria_debito_crio = $3)
	and importe_debito_bcrio is not null
	and opop.tipo_pago != 2
) libro_banco  
order by fecha, debito_credito asc,descripcion



$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION libro_banco(date, date, integer) OWNER TO postgres;

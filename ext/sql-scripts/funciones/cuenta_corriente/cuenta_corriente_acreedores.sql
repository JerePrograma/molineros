------------ CUIDADO!!!!! ----------------------
/*
 * El estado de las liquidaciones esta harcodeado en java. Por lo tanto
 * si se crea un nuevo estado que represente una liquidacion cerrada,
 * y que por ese motivo deba aparecer en este listado, hay que agregarlo 
 * en los lugares correspondientes en esta query.
 * 
 * 
 * 
 * Si se modifica este sp, se debe modificar tambien cuenta_corriente_acreedores_sin_cuit.sql
 */
------------ CUIDADO!!!! -------------------------

/*create type return_cuenta_corriente_acreedores as
(
cuit character varying, 
sucu character varying, 
razon_social character varying, 
id_seccional integer, 
seccional character varying, 
fecha timestamp without time zone,
descripcion character varying, 
importe numeric, 
debito_credito character, 
periodo date, 
es_deuda boolean, 
fecha_pagado date, 
id_orden_pago_pagado integer
);*/

		
drop FUNCTION cuenta_corriente_acreedores(IN p_cuit character varying, IN p_sucu character varying, IN p_seccional integer, IN p_fecha_ini date, 
		IN p_fecha_fin date, IN p_fecha_pago date,  p_incluirProveedores boolean , p_incluirLiquidaciones boolean , p_incluirReintegros boolean);


CREATE OR REPLACE FUNCTION cuenta_corriente_acreedores(IN p_fecha_ini date, IN p_fecha_fin date, IN p_fecha_pago date,  p_incluirProveedores boolean , p_incluirLiquidaciones boolean , p_incluirReintegros boolean,
																IN p_cuit character varying, IN p_sucu character varying, IN p_seccional integer)
RETURNS SETOF return_cuenta_corriente_acreedores
LANGUAGE plpgsql
AS $BODY$
begin

	drop table if exists tmp_compro_pagos;
	drop table if exists tmp_saldos_iniciales;
	
	--inserto los que tienen comprobante, con saldo inicial o no
	create temp table tmp_saldos_iniciales as 
	select distinct c.cuit_acreedor, c.sucu_acreedor, c.seccional , coalesce(sini.fecha_inicio_ejercicio,'18000101') as fecha_inic  , coalesce(sini.saldo, 0)
	from comprobante c left outer join (
		select * from acreedores_saldo_inicial asi
		where fecha_inicio_ejercicio = (select max(fecha_inicio_ejercicio) 
						from acreedores_saldo_inicial 
						where cast(fecha_inicio_ejercicio as date) <= cast($1 as date)
						and cuit_acreedor = asi.cuit_acreedor
						and sucu_acreedor = asi.sucu_acreedor
						and ((seccional is null and asi.seccional is null) or (seccional is not null and seccional = asi.seccional)))
		) sini
	on c.cuit_acreedor = sini.cuit_acreedor
	and c.sucu_acreedor = sini.sucu_acreedor
	and ((c.seccional is null and sini.seccional is null) or (c.seccional is not null and c.seccional = sini.seccional))
	and (c.cuit_acreedor = $7)
	and (c.sucu_acreedor = $8 and (($9 is null and c.seccional is null) or c.seccional = $9));
	
	--inserto los que tienen saldo inicial y no comprobante
	insert into tmp_saldos_iniciales
	select distinct asi.cuit_acreedor, asi.sucu_acreedor, asi.seccional, asi.fecha_inicio_ejercicio, asi.saldo 
	from acreedores_saldo_inicial  asi
	where cast(fecha_inicio_ejercicio as date) <= cast($1 as date)
	and not exists (select 1 from tmp_saldos_iniciales where asi.cuit_acreedor = cuit_acreedor
								and asi.sucu_acreedor = sucu_acreedor
								and ((asi.seccional is null and seccional is null) or (asi.seccional is not null and asi.seccional = seccional)))
	and fecha_inicio_ejercicio = (select max(fecha_inicio_ejercicio) 
							from acreedores_saldo_inicial  
							where cast(fecha_inicio_ejercicio as date) <= cast($1 as date)
							and asi.cuit_acreedor = cuit_acreedor
							and asi.sucu_acreedor = sucu_acreedor
							and ((asi.seccional is null and seccional is null) or (asi.seccional is not null and asi.seccional = seccional)))
	and (asi.cuit_acreedor = $7)
	and (asi.sucu_acreedor = $8 and (($9 is null and asi.seccional is null) or asi.seccional = $9));
							

	create temp table tmp_compro_pagos as
	select comp.cuit, comp.compro_nro, comp.compro_sucu, comp.compro_tipo, comp.compro_letra, comp.id_punto_venta,
	cast(max(opo.alta_fecha) as date) as fecha_pagado , max(opo.id_orden_pago) as id_orden_pago_pagado 
	from orden_pago_ospim opo, comprobante_orden_pago_ospim copo, comprobante comp
	where $3 is not null 
	and  opo.id_orden_pago = copo.id_orden_pago_ospim 
	and copo.cuit = comp.cuit 
	and copo.id_punto_venta = comp.id_punto_venta 
	and  copo.compro_tipo = comp.compro_tipo 
	and copo.compro_sucu = comp.compro_sucu 
	and copo.compro_letra = comp.compro_letra 
	and copo.compro_nro = comp.compro_nro
	and comp.baja_fecha is null
	and (opo.baja_fecha is null or cast(opo.baja_fecha as date) >= $3)
	and ((cast(comp.fecha_recepcion as date) < ($2 + interval '1 day') and cast(comp.fecha_recepcion as date) > (cast('18000101' as date)))
		or (cast(comp.anulado_Fecha as date) < ($2 + interval '1 day') and cast(comp.anulado_Fecha as date) > (cast('18000101' as date))))
	group by comp.cuit, comp.compro_nro, comp.compro_sucu, comp.compro_tipo, comp.compro_letra, comp.id_punto_venta
	having $3 is not null;


	return query
	select asd.cuit_acreedor, asd.sucu_acreedor, e.razon_soc, s.id_seccional, s.descripcion, asd.fecha, 
			asd.descripcion, asd.total, cast(asd.debito_credito as character(1)) , asd.periodo, asd.es_deuda, asd.fecha_pagado, id_orden_pago_pagado from (
	--COMPROBANTES
	select cuit_acreedor, sucu_acreedor, seccional, fecha, descripcion, total, debito_credito, periodo, es_deuda,
	fecha_pagado, id_orden_pago_pagado,
	orden, liquidacion, reintegro
	from (
		select c.cuit_acreedor, c.sucu_acreedor, c.seccional, c.fecha_recepcion as fecha, 
			cast(c.compro_tipo || '-' || c.id_punto_venta || '-' || c.compro_nro as character varying) as descripcion, c.total, 
			cast(case when c.debito_para_egreso = true then 'C' else 'D' end as character) as debito_credito,
			c.periodo_prestacion as periodo,
			true as es_deuda,
			tcp.fecha_pagado,
			tcp.id_orden_pago_pagado,
			1 as orden,
			case when cl.id_liquidacion is not null	then true else false end as liquidacion,
		    case when exists (select 1 from orden_pago_ospim_lista_reintegros opolr, comprobante_orden_pago_ospim copo2 
		    							where opolr.id_orden_pago_ospim = copo2.id_orden_pago_ospim  
										and copo2.id_punto_venta =  c.id_punto_venta
										and copo2.compro_tipo =c.compro_tipo
										and copo2.compro_letra  =c.compro_letra
										and copo2.compro_sucu =c.compro_sucu
										and copo2.compro_nro =c.compro_nro
										and copo2.cuit = c.cuit ) then true else false end as reintegro
		from comprobante c 
		left outer join comprobante_liquidacion cl
		on  cl.cuit = c.cuit
		and cl.compro_nro = c.compro_nro
		and cl.compro_tipo = c.compro_tipo
		and cl.compro_sucu = c.compro_sucu
		and cl.compro_letra = c.compro_letra
		and cl.id_punto_venta = c.id_punto_venta
		left outer join liquidacion l 
		on cl.id_liquidacion = l.id_liquidacion
		left outer join tmp_compro_pagos tcp
		on  tcp.cuit = c.cuit
		and tcp.compro_nro = c.compro_nro
		and tcp.compro_tipo = c.compro_tipo
		and tcp.compro_sucu = c.compro_sucu
		and tcp.compro_letra = c.compro_letra
		and tcp.id_punto_venta = c.id_punto_venta
		where cast(c.fecha_recepcion as date) < ($2 + interval '1 day')
		and cast(c.fecha_recepcion as date) > (cast('18000101' as date))
		and (c.compro_tipo <> 'ANT' or (c.compro_tipo = 'ANT' and  c.id_punto_venta <> 1))
		and c.baja_Fecha is null
		and (cl.id_liquidacion is null or l.estado in (2,10,11))
	   )comprobantes_aux	
	union all
	select cuit_acreedor, sucu_acreedor, seccional, fecha, descripcion, total, debito_credito, periodo, es_deuda,
	fecha_pagado, id_orden_pago_pagado,
	orden, liquidacion, reintegro
	from (
		select c.cuit_acreedor, c.sucu_acreedor, c.seccional, c.anulado_fecha as fecha, 
			cast(c.compro_tipo || '-' || c.id_punto_venta || '-' || c.compro_nro as character varying) as descripcion, c.total, 
			cast(case when c.debito_para_egreso = true then 'D' else 'C' end as character varying) as debito_credito, c.periodo_prestacion as periodo,
			true as es_deuda,
			tcp.fecha_pagado,
			tcp.id_orden_pago_pagado,
			1 as orden,
			case when cl.id_liquidacion is not null	then true else false end as liquidacion,
		    case when exists (select 1 from orden_pago_ospim_lista_reintegros opolr, comprobante_orden_pago_ospim copo2 
		    							where opolr.id_orden_pago_ospim = copo2.id_orden_pago_ospim  
										and copo2.id_punto_venta =  c.id_punto_venta
										and copo2.compro_tipo =c.compro_tipo
										and copo2.compro_letra  =c.compro_letra
										and copo2.compro_sucu =c.compro_sucu
										and copo2.compro_nro =c.compro_nro
										and copo2.cuit = c.cuit ) then true else false end as reintegro
		from comprobante c 
		left outer join comprobante_liquidacion cl
		on  cl.cuit = c.cuit
		and cl.compro_nro = c.compro_nro
		and cl.compro_tipo = c.compro_tipo
		and cl.compro_sucu = c.compro_sucu
		and cl.compro_letra = c.compro_letra
		and cl.id_punto_venta = c.id_punto_venta
		left outer join liquidacion l 
		on cl.id_liquidacion = l.id_liquidacion
		left outer join tmp_compro_pagos tcp
		on  tcp.cuit = c.cuit
		and tcp.compro_nro = c.compro_nro
		and tcp.compro_tipo = c.compro_tipo
		and tcp.compro_sucu = c.compro_sucu
		and tcp.compro_letra = c.compro_letra
		and tcp.id_punto_venta = c.id_punto_venta
		where cast(c.anulado_Fecha as date) < ($2 + interval '1 day')
		and cast(c.anulado_Fecha as date) > (cast('18000101' as date))
		and (c.compro_tipo <> 'ANT' or (c.compro_tipo = 'ANT' and  c.id_punto_venta <> 1))
		and c.baja_Fecha is null
		and (cl.id_liquidacion is null or l.estado in (2,10,11))
	   )comprobantes_aux2
	union all
	--OPS
	select opo.cuit_acreedor, opo.sucu_acreedor,opo.id_seccional as seccional, opo.alta_fecha as fecha, 
	( 'OP '||opo.id_orden_pago || ' - Cheque' || ' - Nro: ' || opop.nro_cheque || ' - ' || cb.descripcion || ' ' || cb.nro_cuenta || '/' || cb.sucursal
	|| (case when exists (select 1 from comprobante_orden_pago_ospim where id_orden_pago_ospim = opo.id_orden_pago and compro_tipo = 'ANT') then ' - Pago de ANT' else '' end)) as descripcion, c.importe as total, 'C' as debito_credito, null as periodo,
	  false as es_deuda, null as fecha_pagado, opo.id_orden_pago as id_orden_pago_pagado, 2 as orden,
	  case when exists (select 1 from orden_pago_ospim_liquidaciones where id_orden_pago_ospim = opo.id_orden_pago) then true else false end as liquidacion,
	  case when exists (select 1 from orden_pago_ospim_lista_reintegros where  id_orden_pago_ospim = opo.id_orden_pago ) then true else false end as reintegro
	from orden_pago_ospim opo
	inner join orden_pago_ospim_pagos opop
	 on opo.id_orden_pago = opop.id_orden_pago
	inner join cheque c
	on opop.nro_cheque = c.nro_cheque
	and opop.id_banco_cheque = c.id_banco
	inner join cuenta_bcria cb
	on c.id_cta_bcria = cb.id_cuenta_bcria	
	inner join banco b
	on cb.id_banco = b.id_banco
	where opop.nro_cheque is not null
	and    cast(opo.alta_fecha as date) < ($2 + interval '1 day')
	and   cast(opo.alta_fecha as date) > (cast('18000101' as date))
	union all
	select opo.cuit_acreedor, opo.sucu_acreedor, opo.id_seccional as seccional, opo.alta_fecha as fecha, 
	( 'OP '||opo.id_orden_pago || ' - Retencion'|| ' - ' || cb2.descripcion || ' ' || cb2.nro_cuenta || '/' || cb2.sucursal 
	|| (case when exists (select 1 from comprobante_orden_pago_ospim where id_orden_pago_ospim = opo.id_orden_pago and compro_tipo = 'ANT') then ' - Pago de ANT' else '' end)) as descripcion , opop.importe_retencion as total, 'C' as debito_credito, null as periodo, false as es_deuda, null as fecha_pagado , opo.id_orden_pago as id_orden_pago_pagado,2 as orden,
	case when exists (select 1 from orden_pago_ospim_liquidaciones where id_orden_pago_ospim = opo.id_orden_pago) then true else false end as liquidacion,
	  case when exists (select 1 from orden_pago_ospim_lista_reintegros where  id_orden_pago_ospim = opo.id_orden_pago ) then true else false end as reintegro
	from orden_pago_ospim opo
	inner join orden_pago_ospim_pagos opop
	 on opo.id_orden_pago = opop.id_orden_pago
	inner join cuenta_bcria cb2
	on opop.id_cta_bcria_retencion = cb2.id_cuenta_bcria	
	inner join banco b2
	on cb2.id_banco = b2.id_banco
	where opop.id_cta_bcria_retencion is not null 
	and  cast(opo.alta_fecha as date) < ($2 + interval '1 day')
	and  cast(opo.alta_fecha as date) > (cast('18000101' as date))
	union all
	select opo.cuit_acreedor, opo.sucu_acreedor, opo.id_seccional as seccional, opo.alta_fecha as fecha, 
	( 'OP '||opo.id_orden_pago || ' - ' || tp.descripcion || ( case when opop.nro_debito_bcrio is not null then ' - Nro:' || opop.nro_debito_bcrio else '' end )|| ' - ' ||  cb4.descripcion || ' ' || cb4.nro_cuenta || '/' || cb4.sucursal
	|| (case when exists (select 1 from comprobante_orden_pago_ospim where id_orden_pago_ospim = opo.id_orden_pago and compro_tipo = 'ANT') then ' - Pago de ANT' else '' end)) as descripcion,opop.importe_debito_bcrio as total, 'C' as debito_credito, null as periodo, false as es_deuda, null as fecha_pagado,opo.id_orden_pago as id_orden_pago_pagado, 2 as orden,
	case when exists (select 1 from orden_pago_ospim_liquidaciones where id_orden_pago_ospim = opo.id_orden_pago) then true else false end as liquidacion,
	  case when exists (select 1 from orden_pago_ospim_lista_reintegros where  id_orden_pago_ospim = opo.id_orden_pago ) then true else false end as reintegro
	from orden_pago_ospim opo
	inner join orden_pago_ospim_pagos opop
	 on opo.id_orden_pago = opop.id_orden_pago
	inner join cuenta_bcria cb4
	on opop.id_cta_bcria_debito_crio = cb4.id_cuenta_bcria	
	inner join banco b4
	on cb4.id_banco = b4.id_banco
	inner join tipo_pago tp
	on opop.tipo_pago = tp.id_tipo_pago
	where opop.id_cta_bcria_debito_crio is not null 
	and   cast(opo.alta_fecha as date) < ($2 + interval '1 day')
	and   cast(opo.alta_fecha as date) > (cast('18000101' as date))
	union all
	--- OPS DADAS DE BAJA
	select opo.cuit_acreedor, opo.sucu_acreedor, opo.id_seccional as seccional, opo.baja_fecha as fecha, 
	( 'ANULACIÓN OP '||opo.id_orden_pago || ' - Cheque' || ' - Nro: ' || opop.nro_cheque || ' - ' || cb.descripcion || ' ' || cb.nro_cuenta || '/' || cb.sucursal
	|| (case when exists (select 1 from comprobante_orden_pago_ospim where id_orden_pago_ospim = opo.id_orden_pago and compro_tipo = 'ANT') then ' - Pago de ANT' else '' end)) as descripcion, c.importe as total, 'D' as debito_credito, null as periodo, false as es_deuda, null as fecha_pagado, opo.id_orden_pago as id_orden_pago_pagado,4 as orden,
	case when exists (select 1 from orden_pago_ospim_liquidaciones where id_orden_pago_ospim = opo.id_orden_pago) then true else false end as liquidacion,
	  case when exists (select 1 from orden_pago_ospim_lista_reintegros where  id_orden_pago_ospim = opo.id_orden_pago ) then true else false end as reintegro
	from orden_pago_ospim opo
	inner join orden_pago_ospim_pagos opop
	 on opo.id_orden_pago = opop.id_orden_pago
	inner join cheque c
	on opop.nro_cheque = c.nro_cheque
	and opop.id_banco_cheque = c.id_banco
	inner join cuenta_bcria cb
	on c.id_cta_bcria = cb.id_cuenta_bcria	
	inner join banco b
	on cb.id_banco = b.id_banco
	where opop.nro_cheque is not null 
	and opo.baja_fecha is not null
	and  cast(opo.baja_fecha as date) < ($2 + interval '1 day')
	and  cast(opo.baja_fecha as date) > (cast('18000101' as date))
	union all
	select opo.cuit_acreedor, opo.sucu_acreedor, opo.id_seccional as seccional, opo.baja_fecha as fecha, 
	( 'ANULACIÓN OP '||opo.id_orden_pago || ' - Retencion'|| ' - ' || cb2.descripcion || ' ' || cb2.nro_cuenta || '/' || cb2.sucursal 
	|| (case when exists (select 1 from comprobante_orden_pago_ospim where id_orden_pago_ospim = opo.id_orden_pago and compro_tipo = 'ANT') then ' - Pago de ANT' else '' end)) as descripcion , opop.importe_retencion as total, 'D' as debito_credito, null as periodo, false as es_deuda, null as fecha_pagado,opo.id_orden_pago as id_orden_pago_pagado, 4 as orden,
	case when exists (select 1 from orden_pago_ospim_liquidaciones where id_orden_pago_ospim = opo.id_orden_pago) then true else false end as liquidacion,
	  case when exists (select 1 from orden_pago_ospim_lista_reintegros where  id_orden_pago_ospim = opo.id_orden_pago ) then true else false end as reintegro
	from orden_pago_ospim opo
	inner join orden_pago_ospim_pagos opop
	 on opo.id_orden_pago = opop.id_orden_pago
	inner join cuenta_bcria cb2
	on opop.id_cta_bcria_retencion = cb2.id_cuenta_bcria	
	inner join banco b2
	on cb2.id_banco = b2.id_banco
	where opop.id_cta_bcria_retencion is not null 
	and opo.baja_fecha is not null
	and    cast(opo.baja_fecha as date) < ($2 + interval '1 day')
	and    cast(opo.baja_fecha as date) > (cast('18000101' as date))
	union all
	select opo.cuit_acreedor, opo.sucu_acreedor, opo.id_seccional as seccional, opo.baja_fecha as fecha,
	( 'ANULACIÓN OP '||opo.id_orden_pago || ' - '|| tp.descripcion  || ( case when opop.nro_debito_bcrio is not null then ' - Nro:' || opop.nro_debito_bcrio else '' end )|| ' - ' ||  cb4.descripcion || ' ' || cb4.nro_cuenta || '/' || cb4.sucursal
	|| (case when exists (select 1 from comprobante_orden_pago_ospim where id_orden_pago_ospim = opo.id_orden_pago and compro_tipo = 'ANT') then ' - Pago de ANT' else '' end)) as descripcion,opop.importe_debito_bcrio as total, 'D' as debito_credito, null as periodo, false as es_deuda, null as fecha_pagado,opo.id_orden_pago as id_orden_pago_pagado, 4 as orden,
	case when exists (select 1 from orden_pago_ospim_liquidaciones where id_orden_pago_ospim = opo.id_orden_pago) then true else false end as liquidacion,
	  case when exists (select 1 from orden_pago_ospim_lista_reintegros where  id_orden_pago_ospim = opo.id_orden_pago ) then true else false end as reintegro	
	from orden_pago_ospim opo
	inner join orden_pago_ospim_pagos opop
	 on opo.id_orden_pago = opop.id_orden_pago
	inner join cuenta_bcria cb4
	on opop.id_cta_bcria_debito_crio = cb4.id_cuenta_bcria	
	inner join banco b4
	on cb4.id_banco = b4.id_banco
	inner join tipo_pago tp
	on opop.tipo_pago = tp.id_tipo_pago
	where opop.id_cta_bcria_debito_crio is not null 
	and opo.baja_fecha is not null
	and  cast(opo.baja_fecha as date) < ($2 + interval '1 day')
	and  cast(opo.baja_fecha as date) > (cast('18000101' as date))
	
	union all

	select cuit_acreedor, sucu_Acreedor, seccional, null, null,null,null,null,null,null,null,-1 , true, true
	from acreedores_saldo_inicial a  
	left outer join prestador p
	on a.cuit_acreedor = p.cuit
	where ($4 = true and p.id_prestador is null) or  ($5 = true and p.id_prestador is not null) or $6 = true
	group by cuit_acreedor, sucu_Acreedor, seccional
) asd 
inner join tmp_saldos_iniciales si
on asd.cuit_acreedor = si.cuit_acreedor
and asd.sucu_acreedor = si.sucu_acreedor 
and ((asd.seccional is null and si.seccional is null) or (asd.seccional is not null and asd.seccional = si.seccional))
left outer join empresa e
on asd.cuit_acreedor = e.cuit
and asd.sucu_acreedor = e.sucursal
left outer join seccional s
on asd.seccional = s.id_seccional
WHERE (cast(asd.fecha as date)>= cast(si.fecha_inic as date) or asd.fecha is null)  
and (((($5 = true and liquidacion = true)  
or ($6 = true and reintegro = true))
or ($4 = true and liquidacion = false and reintegro = false))
or orden = -1)
and  ($7 is null or asd.cuit_acreedor = $7)
and ($8 is null or (asd.sucu_acreedor = $8 and (($9 is null and asd.seccional is null) or asd.seccional = $9)))
order by cuit_acreedor, sucu_acreedor, cast(fecha as date) asc, orden asc, asd.descripcion asc;




end;
$BODY$;

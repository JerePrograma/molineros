 /*  select * from cuenta_corriente_actas_convenios_apo_cont(null,null,'20120801','20120830')
CREATE OR REPLACE FUNCTION cuenta_corriente_actas_convenios(p_cuit character varying, p_sucu character varying, p_fecha_ini date, p_fecha_fin date)
  RETURNS SETOF return_cuenta_corriente_actas_convenios AS
$BODY$
begin

	drop table if exists tmp_saldos_iniciales_actas_c;
	
	--inserto todos los que tengan acta, tuvieran saldo inicial o no
	create temp table tmp_saldos_iniciales_actas_c as 
	select distinct c.cuit, c.sucursal, coalesce(sini.fecha_inicio_ejercicio,'18000101') as fecha_inic  , coalesce(sini.saldo, 0)
	from acta c left outer join (
		select * from actas_convenios_saldo_inicial asi
		where fecha_inicio_ejercicio = (select max(fecha_inicio_ejercicio) 
						from actas_convenios_saldo_inicial  
						where cast(fecha_inicio_ejercicio as date) <= cast($3 as date)
						and cuit = asi.cuit
						and sucu = asi.sucu)
		) sini
	on c.cuit = sini.cuit
	and c.sucursal = sini.sucu
	where ($1 is null or c.cuit = $1)
	and ($2 is null or c.sucursal = $2);
	
	--inserto los que tienen saldo inicial pero no tenian acta
	insert into tmp_saldos_iniciales_actas_c
	select distinct acsi.cuit, acsi.sucu, acsi.fecha_inicio_ejercicio, acsi.saldo 
	from actas_convenios_saldo_inicial  acsi
	where cast(fecha_inicio_ejercicio as date) <= cast($3 as date)
	and not exists (select 1 from tmp_saldos_iniciales_actas_c where acsi.cuit =cuit  and acsi.sucu = sucu )
	and fecha_inicio_ejercicio = (select max(fecha_inicio_ejercicio) 
							from actas_convenios_saldo_inicial  
							where cast(fecha_inicio_ejercicio as date) <= cast($3 as date)
							and cuit = acsi.cuit
							and sucu = acsi.sucu)
	and ($1 is null or acsi.cuit = $1)
	and ($2 is null or acsi.sucu = $2);
		
	--inserto los que tengan saldo inicial en alguna fecha mayor a $3
	insert into tmp_saldos_iniciales_actas_c
	select distinct acsi.cuit, acsi.sucu, cast('18000101' as date), 0 
	from actas_convenios_saldo_inicial  acsi
	where not exists (select 1 from tmp_saldos_iniciales_actas_c where acsi.cuit =cuit  and acsi.sucu = sucu)
	and ($1 is null or acsi.cuit = $1)
	and ($2 is null or acsi.sucu = $2);

	return query
	select e.cuit, e.sucursal, e.razon_soc, fecha_recepcion,cast(descripcion as character varying), total, cast(debito_credito as character) from (
	
	--ACTAS
	select cast (a.cierre_fecha as date) as fecha_recepcion, 'AC: ' || a.numero as descripcion, 
	  cuit, sucursal, (a.capital +  a.interes +  a.otros + a.deuda_actas_asociadas) as total,  
	  'C' as debito_credito
	 from acta a
	 where a.acta_cerrada = true
	 and ($1 is null or a.cuit = $1)
	 and ($2 is null or a.sucursal = $2)
	 and cast(a.cierre_fecha as date) > cast('18000101' as date) and cast(a.cierre_fecha as date) < ($4 + interval '1 day')
	 union all
	 --ACTAS ANULADAS
	select cast (a.baja_fecha as date) as fecha_recepcion, 'ANULACION - AC: ' || a.numero as descripcion, 
	  cuit, sucursal, (a.capital +  a.interes +  a.otros + a.deuda_actas_asociadas) as total,  
	  'D' as debito_credito
	 from acta a
	 where a.acta_cerrada = true
	 and ($1 is null or a.cuit = $1)
	 and ($2 is null or a.sucursal = $2)
	 and cierre_fecha is not null
	 and cast(a.baja_fecha as date) > cast('18000101' as date) and cast(a.baja_fecha as date) < ($4 + interval '1 day')
	 --anticipos
	 union all
	 select cast(r.fecha as datE), 'Ant. ' || r.numero, r.cuit, r.sucursal, rc.concepto_importe_adicional , 'D'
	from recibo_conceptos rc, recibo r
	where rc.recibo_id = r.id
	and caja_concepto_id  in (select id from concepto_maestro  where descripcion_original = 'ANTICIPO ACTAS/CONVENIOS')
	and ($1 is null or r.cuit = $1)
	 and ($2 is null or r.sucursal = $2)
	 and cast(r.fecha as date) > cast('18000101' as date) and cast(r.fecha as date) < ($4 + interval '1 day')
	 --anticipos anulados
	union all
	 select cast(r.baja_fecha as datE), 'Ant. ' || r.numero, r.cuit, r.sucursal, rc.concepto_importe_adicional , 'D'
	from recibo_conceptos rc, recibo r
	where rc.recibo_id = r.id
	and caja_concepto_id  in (select id from concepto_maestro  where descripcion_original = 'ANTICIPO ACTAS/CONVENIOS')
	and ($1 is null or r.cuit = $1)
	 and ($2 is null or r.sucursal = $2)
	 and cast(r.baja_fecha as date) > cast('18000101' as date) and cast(r.baja_fecha as date) < ($4 + interval '1 day')
	 union all
	 --pago por recibo
	select cast (r.fecha as date) as fecha_recepcion, 'AC: ' || a.numero || ' - R' || r.numero as descripcion , 
	  a.cuit, a.sucursal,   rcp.importe as total,     
	'D' as debito_credito
	 from acta a
	 left outer join empresa e
	 on a.cuit = e.cuit
	 and a.sucursal = e.sucursal
	 inner join recibo_conceptos rc
	 on a.id = rc.acta_id
	  inner  join recibo r
	 on rc.recibo_id = r.id
	 left outer join recibo_conceptos_pagos   rcp
	 on rcp.recibo_concepto_id = rc.id
	 left outer join recibo_ingresos ri
	 on rcp.recibo_ingreso_id = ri.id
	 where a.acta_cerrada = true
	 and ($1 is null or a.cuit = $1)
	 and ($2 is null or a.sucursal = $2)
	 and cast(r.fecha  as date) > cast('18000101' as date) and cast(r.fecha  as date) < ($4 + interval '1 day')
	 and ri.id_anticipo_recibo_concepto is null
	and r.id not in (select id from recibo r
		where baja_fecha is not null
		and exists (Select 1 from recibo where numero = r.numero and id <> r.id and alta_fecha > r.alta_fecha))
	--AGREGADO PARA CONTEMPLAR ANTICIPOS... MATO ACTA
	union all	 
	select cast (r.fecha as date) as fecha_recepcion, 'AC: ' || a.numero || ' - R' || r.numero as descripcion , 
	  a.cuit, a.sucursal,   rcp.importe as total,     
	'D' as debito_credito
	 from acta a
	 left outer join empresa e
	 on a.cuit = e.cuit
	 and a.sucursal = e.sucursal
	 inner join recibo_conceptos rc
	 on a.id = rc.acta_id
	  inner  join recibo r
	 on rc.recibo_id = r.id
	 left outer join recibo_conceptos_pagos   rcp
	 on rcp.recibo_concepto_id = rc.id
	 left outer join recibo_ingresos ri
	 on rcp.recibo_ingreso_id = ri.id
	 where a.acta_cerrada = true
	 and ($1 is null or a.cuit = $1)
	 and ($2 is null or a.sucursal = $2)
	 and cast(r.fecha  as date) > cast('18000101' as date) and cast(r.fecha  as date) < ($4 + interval '1 day')
	 and ri.id_anticipo_recibo_concepto is not null
	and r.id not in (select id from recibo r
		where baja_fecha is not null
		and exists (Select 1 from recibo where numero = r.numero and id <> r.id and alta_fecha > r.alta_fecha))		
	union all	 
	select distinct cast (r.fecha as date) as fecha_recepcion, 'USO ANTICIPO AC: ' || a.numero || ' - R' || r.numero as descripcion , 
	  a.cuit, a.sucursal,   ri.importe as total,     
	'C' as debito_credito
	 from acta a
	 left outer join empresa e
	 on a.cuit = e.cuit
	 and a.sucursal = e.sucursal
	 inner join recibo_conceptos rc
	 on a.id = rc.acta_id
	  inner  join recibo r
	 on rc.recibo_id = r.id
	 left outer join recibo_conceptos_pagos   rcp
	 on rcp.recibo_concepto_id = rc.id
	 left outer join recibo_ingresos ri
	 on r.id= ri.recibo_id
	 where a.acta_cerrada = true
	 and ($1 is null or a.cuit = $1)
	 and ($2 is null or a.sucursal = $2)
	 and cast(r.fecha  as date) > cast('18000101' as date) and cast(r.fecha  as date) < ($4 + interval '1 day')
	 and ri.id_anticipo_recibo_concepto is not null
	and r.id not in (select id from recibo r
		where baja_fecha is not null
		and exists (Select 1 from recibo where numero = r.numero and id <> r.id and alta_fecha > r.alta_fecha))		
	--HASTA ACA
	 union all
	--recibo anulado
	 select cast (r.baja_fecha as date) as fecha_recepcion, 'ANULACION - AC: ' || a.numero || ' - R' || r.numero as descripcion , 
	  a.cuit, a.sucursal,   rcp.importe as total,     
	'C' as debito_credito
	 from acta a
	 left outer join empresa e
	 on a.cuit = e.cuit
	 and a.sucursal = e.sucursal
	 inner join recibo_conceptos rc
	 on a.id = rc.acta_id
	  inner  join recibo r
	 on rc.recibo_id = r.id
	 left outer join recibo_conceptos_pagos   rcp
	 on rcp.recibo_concepto_id = rc.id
	 left outer join recibo_ingresos ri
	 on rcp.recibo_ingreso_id = ri.id
	 where a.acta_cerrada = true
	 and ($1 is null or a.cuit = $1)
	 and ($2 is null or a.sucursal = $2)
	 and cast(r.baja_fecha  as date) > cast('18000101' as date) and cast(r.baja_fecha  as date) < ($4 + interval '1 day')
	 and ri.id_anticipo_recibo_concepto is null
	 	and r.id not in (select id from recibo r
		where baja_fecha is not null
		and exists (Select 1 from recibo where numero = r.numero and id <> r.id and alta_fecha > r.alta_fecha))
	 union all
	 --pago con acta
	 select cast (a2.cierre_fecha as date) as fecha_recepcion, 'AC: ' || a.numero || ' - AC: ' ||  a2.numero as descripcion, 
	 a.cuit, a.sucursal,  ar.importe, 'D'
	 from acta a
	 inner join acta_relacion ar
	 on a.id = ar.acta_relacionada_id
	 and ar.baja_fecha is null
	 inner join acta a2
	 on ar.acta_id = a2.id
	 and a2.baja_fecha is null
	 and a2.cierre_fecha is not null
	 inner join acta_pagos ap
	 on ar.id = ap.acta_relacion_id
	 and ap.baja_fecha is null
	 where a.acta_cerrada = true
	 and ($1 is null or a.cuit = $1)
	 and ($2 is null or a.sucursal = $2)
	 and cast(a2.cierre_fecha as date) > cast('18000101' as date) and cast(a2.cierre_fecha  as date) < ($4 + interval '1 day')
	 union all
	--pago con acta anulada
	  select cast (a2.baja_fecha as date) as fecha_recepcion, 'ANULACION - AC: ' || a.numero || ' - AC: ' ||  a2.numero as descripcion, 
	 a.cuit, a.sucursal,  ar.importe, 'C'
	 from acta a
	 inner join acta_relacion ar
	 on a.id = ar.acta_relacionada_id
	 and ar.baja_fecha is null
	 inner join acta a2
	 on ar.acta_id = a2.id
	 and a2.baja_fecha is null
	 and a2.cierre_fecha is not null
	 inner join acta_pagos ap
	 on ar.id = ap.acta_relacion_id
	 and ap.baja_fecha is null
	 where a.acta_cerrada = true
	 and ($1 is null or a.cuit = $1)
	 and ($2 is null or a.sucursal = $2)
	 and cast(a2.baja_fecha as date) > cast('18000101' as date) and cast(a2.baja_fecha  as date) < ($4 + interval '1 day')
	 union all
	 --pago con convenio
	 select cast (c2.fecha_inicio as date) as fecha_recepcion, 'AC: ' || a.numero || ' - CO: ' ||  c2.numero as descripcion, 
	a.cuit, a.sucursal,  ca.importe, 'D'
	 from acta a
	 inner join convenio_actas ca
	 on a.id = ca.acta_id
	 and ca.baja_fecha is null
	 inner join convenio c2
	 on ca.convenio_id = c2.id
	 and c2.baja_fecha is null
	 inner join acta_pagos ap
	 on ca.id = ap.convenio_acta_id
	 and ap.baja_fecha is null
	 where a.acta_cerrada = true
	 and ($1 is null or a.cuit = $1)
	 and ($2 is null or a.sucursal = $2)
	 and cast(c2.fecha_inicio as date) > cast('18000101' as date) and cast(c2.fecha_inicio  as date) < ($4 + interval '1 day')
	 union all
	  --pago con convenio anulado
	 select cast (c2.baja_fecha as date) as fecha_recepcion, 'ANULACION - AC: ' || a.numero || ' - CO: ' ||  c2.numero as descripcion, 
	a.cuit, a.sucursal,  ca.importe, 'C'
	 from acta a
	 inner join convenio_actas ca
	 on a.id = ca.acta_id
	 and ca.baja_fecha is null
	 inner join convenio c2
	 on ca.convenio_id = c2.id
	 and c2.baja_fecha is null
	 inner join acta_pagos ap
	 on ca.id = ap.convenio_acta_id
	 and ap.baja_fecha is null
	 where a.acta_cerrada = true
	 and ($1 is null or a.cuit = $1)
	 and ($2 is null or a.sucursal = $2)
	 and cast(c2.baja_fecha as date) > cast('18000101' as date) and cast(c2.baja_fecha  as date) < ($4 + interval '1 day')
	--CONVENIOS 
	union all
	select cast (a.fecha_inicio as date) as fecha_recepcion, 'CO: ' || a.numero as descripcion, 
	 a.cuit, a.sucursal,   (a.interes + a.ajuste_capital + a.ajuste_interes + 
	 	(case when a.deuda_actas_asociadas is null then 0 else a.deuda_actas_asociadas end) + 
	 	(case when a.deuda_convenios_asociados is null then 0 else a.deuda_convenios_asociados end)) as total,  
	  'C' as debito_credito
	 from convenio a
	 where ($1 is null or a.cuit = $1)
	 and ($2 is null or a.sucursal = $2)
	 and cast(a.fecha_inicio as date) > cast('18000101' as date) and cast(a.fecha_inicio  as date) < ($4 + interval '1 day')
	 union all
	--convenios anulados
	select cast (a.baja_fecha as date) as fecha_recepcion, 'ANULACION - CO: ' || a.numero as descripcion, 
	 a.cuit, a.sucursal,   (a.interes + a.ajuste_capital + a.ajuste_interes + 
	 	(case when a.deuda_actas_asociadas is null then 0 else a.deuda_actas_asociadas end) + 
	 	(case when a.deuda_convenios_asociados is null then 0 else a.deuda_convenios_asociados end)) as total,  
	  'D' as debito_credito
	 from convenio a
	 where  ($1 is null or a.cuit = $1)
	 and ($2 is null or a.sucursal = $2)
	 and cast(a.baja_fecha as date) > cast('18000101' as date) and cast(a.baja_fecha  as date) < ($4 + interval '1 day')
	 union all	 
	 --pago por recibo
	select cast (r.fecha as date) as fecha_recepcion, 'CO: ' || a.numero || ' - R' || r.numero as descripcion , 
	 a.cuit, a.sucursal,   rcp.importe as total,     
	'D' as debito_credito
	 from convenio a
	 inner join recibo_conceptos rc
	 on a.id = rc.convenio_id
	  inner  join recibo r
	 on rc.recibo_id = r.id
	 left outer join recibo_conceptos_pagos   rcp
	 on rcp.recibo_concepto_id = rc.id
	 left outer join recibo_ingresos ri
	 on rcp.recibo_ingreso_id = ri.id
	 where ($1 is null or a.cuit = $1)
	 and ($2 is null or a.sucursal = $2)
	 and cast(r.fecha as date) > cast('18000101' as date) and cast(r.fecha  as date) < ($4 + interval '1 day')
	 and ri.id_anticipo_recibo_concepto is null
	 and r.id not in (select id from recibo r
		where baja_fecha is not null
		and exists (Select 1 from recibo where numero = r.numero and id <> r.id and alta_fecha > r.alta_fecha))
	 union all
	 --pago por recibo anulado
	select cast (r.baja_fecha as date) as fecha_recepcion, 'ANULACION - CO: ' || a.numero || ' - R' || r.numero as descripcion , 
	 a.cuit, a.sucursal,   rcp.importe as total,     
	'C' as debito_credito
	 from convenio a
	 inner join recibo_conceptos rc
	 on a.id = rc.convenio_id
	  inner  join recibo r
	 on rc.recibo_id = r.id
	 left outer join recibo_conceptos_pagos   rcp
	 on rcp.recibo_concepto_id = rc.id
	 left outer join recibo_ingresos ri
	 on rcp.recibo_ingreso_id = ri.id
	 where  ($1 is null or a.cuit = $1)
	 and ($2 is null or a.sucursal = $2)
	 and cast(r.baja_fecha as date) > cast('18000101' as date) and cast(r.baja_fecha  as date) < ($4 + interval '1 day')
	 and ri.id_anticipo_recibo_concepto is null
	 and r.id not in (select id from recibo r
		where baja_fecha is not null
		and exists (Select 1 from recibo where numero = r.numero and id <> r.id and alta_fecha > r.alta_fecha))
	 union all
	 --pago con convenio
	 select cast (a.fecha_inicio as date) as fecha_recepcion, 'CO: ' || a.numero || ' - CO: ' ||  c2.numero as descripcion, 
	a.cuit, a.sucursal,  ca.saldo, 'D'
	 from convenio a
	 inner join convenio_relacion ca
	 on a.id = ca.convenio_relacionado_id
	 and ca.baja_fecha is null
	 inner join convenio c2
	 on ca.convenio_id = c2.id
	 and c2.baja_fecha is null
	 inner join convenio_pagos ap
	 on ca.id = ap.convenio_relacion_id
	 and ap.baja_fecha is null
	 where   ($1 is null or a.cuit = $1)
	 and ($2 is null or a.sucursal = $2)
	 and cast(a.fecha_inicio as date) > cast('18000101' as date) and cast(a.fecha_inicio as date) < ($4 + interval '1 day')
	 union all
	  --pago con convenio anulado
	 select cast (a.baja_fecha as date) as fecha_recepcion, 'ANULACION - CO: ' || a.numero || ' - CO: ' ||  c2.numero as descripcion, 
	a.cuit, a.sucursal,  ca.saldo, 'D'
	 from convenio a
	 inner join convenio_relacion ca
	 on a.id = ca.convenio_relacionado_id
	 and ca.baja_fecha is null
	 inner join convenio c2
	 on ca.convenio_id = c2.id
	 and c2.baja_fecha is null
	 inner join convenio_pagos ap
	 on ca.id = ap.convenio_relacion_id
	 and ap.baja_fecha is null
	 where  ($1 is null or a.cuit = $1)
	 and ($2 is null or a.sucursal = $2)
	 and cast(a.baja_fecha as date) > cast('18000101' as date) and cast(a.baja_fecha as date) < ($4 + interval '1 day')
	 
	 union all 
	 (select null,null, cuit, sucursal, null,null from tmp_saldos_iniciales_actas_c  group by cuit, sucursal having ($1 is null or ($1 is not null and cuit = $1)))
) aux
inner join tmp_saldos_iniciales_actas_c si
on aux.cuit = si.cuit
and aux.sucursal = si.sucursal
left outer join empresa e
on aux.cuit = e.cuit
and aux.sucursal = e.sucursal 
where  cast(aux.fecha_recepcion as date)>= cast(si.fecha_inic as date)   or aux.fecha_recepcion is null
order by 1,2,fecha_recepcion asc, debito_credito ASC;


end;
$BODY$
  LANGUAGE plpgsql VOLATILE


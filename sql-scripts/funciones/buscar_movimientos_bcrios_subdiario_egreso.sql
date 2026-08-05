DROP FUNCTION buscar_movimientos_bcrios_subdiario_egreso(fecha_desde date,
 fecha_hasta date) ;
-- Function: buscar_movimientos_bcrios_subdiario_egreso(date, date)

-- DROP FUNCTION buscar_movimientos_bcrios_subdiario_egreso(date, date);

CREATE OR REPLACE FUNCTION buscar_movimientos_bcrios_subdiario_egreso(IN fecha_desde date, IN fecha_hasta date)
  RETURNS TABLE(id_movimiento integer, fecha_movimiento date, tipo_mov character varying, cuenta_bcria character varying, fecha_comprobante date, nro_compro character varying, mov_descripcion character varying, importe double precision, baja_fecha date, cuit character varying, nro_cheque_rechazado numeric, cta__nro_cuenta integer, cta__sucursal integer, cta__descripcion character varying, cta__numero_plan_cuenta_asociada character varying, cta__cuenta_asociada character varying, cta__cuenta_asociada_id integer, tipo__numero_plan_cuenta_asociada character varying, tipo__cuenta_asociada character varying, tipo__cuenta_asociada_id integer, tipo__id_tipo_mov integer) AS
$BODY$
    select * from (
	select 	m.id_movimiento,
	cast (m.fecha_movimiento as date),
	tmb.descripcion tipo_mov,
	cb.descripcion cuenta_bcria,
	m.fecha_comprobante,
	m.nro_compro,
	m.descripcion,
	case when ch.importe is null then m.importe_movimiento else ch.importe end as importe,
	cast(m.baja_fecha as date),
	ch.cuit,
	ch.nro_cheque,
	cb.nro_cuenta,
	cb.sucursal,
	cb.descripcion,
	pc.numero,
	pc.cuenta,
	pc.id_cuenta_maestro,
	pc2.numero,
	pc2.cuenta,
	pc2.id_cuenta_maestro,
	m.id_tipo_mov
	from movimiento_banco m
	left outer join  tipo_mov_bcrio tmb
	on m.id_tipo_mov = tmb.id_tipo_mov_maestro
	and cast(m.fecha_movimiento as date) <= tmb.valido_hasta and cast(m.fecha_movimiento as date) >= tmb.valido_desde
	left outer join  cuenta_bcria cb
	on m.id_cuenta_bcria = cb.id_cuenta_bcria
	left outer join plan_cuentas pc
	on cb.id_plan_cuenta = pc.id_cuenta_maestro
	and pc.valido_desde <= cast(m.fecha_movimiento as date) and pc.valido_hasta >= cast(m.fecha_movimiento as date)
	left outer join conceptos c
	on tmb.concepto_id = c.id_concepto_maestro
	and cast(c.valido_desde as date) <= cast(m.fecha_movimiento as date) 
	and cast(c.valido_hasta as date) >= cast(m.fecha_movimiento as date) 
	left outer join plan_cuentas pc2
	on c.id_plan_cuenta = pc2.id_cuenta_maestro
	and pc2.valido_desde <= cast(m.fecha_movimiento as date) and pc2.valido_hasta >= cast(m.fecha_movimiento as date)
	left outer join movimiento_banco_items mbi
	on m.id_movimiento = mbi.id_movimiento
	left outer join cheque ch
	on mbi.nro_cheque = ch.nro_cheque
	and mbi.id_banco = ch.id_banco
	and mbi.id_estado_cheque_nuevo = 5
	where ((fecha_movimiento>=$1 and fecha_movimiento<=$2) or (cast(m.baja_fecha as date) >= $1 and cast(m.baja_fecha as date) <= $2))
		and m.id_tipo_mov  in  (select id_tipo_mov_maestro from tipo_mov_bcrio   where concepto_id is not null )
	and c.sub_egreso=true
	and m.id_tipo_mov!=26 --CANJE CHEQUE LO MANEJAMOS DE OTRA FORMA
	-------------------
	union all -- UNION!
	-------------------
	select 	m.id_movimiento,
	cast (m.fecha_movimiento as date),
	tmb.descripcion tipo_mov,
	cb.descripcion cuenta_bcria,
	m.fecha_comprobante,
	m.nro_compro,
	m.descripcion,
	case when ch.importe is null then m.importe_movimiento else ch.importe end as importe,
	cast(m.baja_fecha as date),
	ch.cuit,
	ch.nro_cheque,
	cb.nro_cuenta,
	cb.sucursal,
	cb.descripcion,
	pc2.numero,
	pc2.cuenta,
	pc2.id_cuenta_maestro,
	pc.numero,
	pc.cuenta,
	pc.id_cuenta_maestro,
	m.id_tipo_mov
	from movimiento_banco m
	left outer join  tipo_mov_bcrio tmb
	on m.id_tipo_mov = tmb.id_tipo_mov_maestro
	and cast(m.fecha_movimiento as date) <= tmb.valido_hasta and cast(m.fecha_movimiento as date) >= tmb.valido_desde
	left outer join  cuenta_bcria cb
	on m.id_cuenta_bcria = cb.id_cuenta_bcria
	left outer join plan_cuentas pc
	on cb.id_plan_cuenta = pc.id_cuenta_maestro
	and pc.valido_desde <= cast(m.fecha_movimiento as date) and pc.valido_hasta >= cast(m.fecha_movimiento as date)
	left outer join conceptos c
	on tmb.concepto_id = c.id_concepto_maestro
	and cast(c.valido_desde as date) <= cast(m.fecha_movimiento as date) 
	and cast(c.valido_hasta as date) >= cast(m.fecha_movimiento as date) 
	left outer join plan_cuentas pc2
	on c.id_plan_cuenta = pc2.id_cuenta_maestro
	and pc2.valido_desde <= cast(m.fecha_movimiento as date) and pc2.valido_hasta >= cast(m.fecha_movimiento as date)
	left outer join movimiento_banco_items mbi
	on m.id_movimiento = mbi.id_movimiento
	left outer join cheque ch
	on mbi.nro_cheque = ch.nro_cheque
	and mbi.id_banco = ch.id_banco
	and mbi.id_estado_cheque_nuevo = 5
	where ((fecha_movimiento>=$1 and fecha_movimiento<=$2) or (cast(m.baja_fecha as date) >= $1 and cast(m.baja_fecha as date) <= $2))
	and m.id_tipo_mov  in  (select id_tipo_mov_maestro from tipo_mov_bcrio   where concepto_id is not null )
	and c.sub_egreso=true
	and m.id_tipo_mov=26 --CANJE CHEQUE LO MANEJAMOS DE OTRA FORMA
	-------------------
	union all -- UNION!
	-------------------
	select movs.id_movimiento,
	movs.fecha_movimiento,
	tmb.descripcion,
	cb.descripcion cuenta_bcria,
	movs.fecha_movimiento,
	'Comision bcria AFIP',
	'COMISIONES',
	-1 * movs.importe_movimiento,
	null,
	null,
	null,
	cb.nro_cuenta,
	cb.sucursal,
	cb.descripcion,
	pc.numero,
	pc.cuenta,
	pc.id_cuenta_maestro,
	pc2.numero,
	pc2.cuenta,
	pc2.id_cuenta_maestro,
	movs.id_tipo_mov
	 from 
		(select 0 as id_movimiento, cast(fecha as date) as fecha_movimiento , 
		cod.id_tipo_mov ,
		sum(importe) as importe_movimiento
		from detalle_extraccion_bancaria    deb
		inner join codigo_ext_bcrias_afip cod
		on cast (deb.codigo_movimiento as integer) =  cod.codigo
		where deb.tipo = 'PRO'
		and codigo in (18,6)
		group by fecha, cod.codigo, cod.descripcion, cod.id_tipo_mov
		having  fecha > ($1 - interval '1 day') and fecha < ($2 + interval '1 day')
		) movs
	left outer join tipo_mov_bcrio tmb
	on movs.id_tipo_mov = tmb.id_tipo_mov_maestro
	and cast(movs.fecha_movimiento as date) <= tmb.valido_hasta and cast(movs.fecha_movimiento as date) >= tmb.valido_desde
	left outer join  cuenta_bcria cb
	on 2 = cb.id_cuenta_bcria
	left outer join plan_cuentas pc
	on cb.id_plan_cuenta = pc.id_cuenta_maestro
		and pc.valido_desde <= cast(movs.fecha_movimiento as date) and pc.valido_hasta >= cast(movs.fecha_movimiento as date)
	left outer join conceptos c
	--on (select id_plan_cuenta from conceptos   where id = (select concepto_id from tipo_mov_bcrio  where id_tipo_mov in (select id_tipo_mov from codigo_ext_bcrias_afip  where codigo =18  ))) = pc2.id
	on c.id_concepto_maestro = tmb.concepto_id
	and cast(c.valido_Desde as date) <= cast(movs.fecha_movimiento as date)
	and cast(c.valido_hasta as date) >= cast(movs.fecha_movimiento as date)
	left outer join plan_cuentas pc2
	on c.id_plan_cuenta = pc2.id_cuenta_maestro
	and pc2.valido_desde <= cast(movs.fecha_movimiento as date) and pc2.valido_hasta >= cast(movs.fecha_movimiento as date)
 ) aux
 order by fecha_movimiento;

$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION buscar_movimientos_bcrios_subdiario_egreso(date, date)
  OWNER TO postgres;


CREATE OR REPLACE FUNCTION libro_caja_efectivo(IN p_fecha_ini date, IN p_fecha_fin date)
  RETURNS TABLE(fecha timestamp without time zone, debito_credito character, comprobante character varying, descripcion character varying, importe numeric) AS
$BODY$

select fecha , debito_credito, comprobante, descripcion || (case when asd.cuit is not null then ' - Cuit: ' ||asd.cuit  else '' end) || (case when e.razon_soc is not null then ' Razon: ' || e.razon_soc else '' end), sum(importe) from (
--RECIBOS
select  cast (r.fecha as timestamp without time zone),  
	'C' as debito_credito, 
	'Recibo: ' || r.numero as comprobante, 
	'Recibo Efectivo'  as descripcion,
	ri.importe, 
	r.cuit, 
	r.sucursal
from recibo  r
inner join  recibo_ingresos ri
on r.id = ri.recibo_id
where (ri.id_estado_efectivo  = 2 or ri.id_estado_efectivo  = 3)
and cast(r.fecha as date) > ($1 - interval '1 day') and  cast(r.fecha as date) < ($2 + interval '1 day')
and r.id not in (select id from recibo r2
	where baja_fecha is not null
	and exists (Select 1 from recibo where numero = r2.numero and id <> r2.id and alta_fecha > r2.alta_fecha))
union all 
select  r.baja_fecha, 
	'D' as debito_credito, 
	'Recibo: ' || r.numero || ' - ANULADO' as comprobante, 
	'Recibo Efectivo'  as descripcion,
	ri.importe, r.cuit, r.sucursal
from recibo  r
inner join  recibo_ingresos ri
on r.id = ri.recibo_id
where r.baja_fecha is not null
and  (ri.id_estado_efectivo  = 2 or ri.id_estado_efectivo  = 3)
and cast(r.baja_fecha as date) > ($1 - interval '1 day') and  cast(r.baja_fecha as date) < ($2 + interval '1 day')
and r.id not in (select id from recibo r2
	where baja_fecha is not null
	and exists (Select 1 from recibo where numero = r2.numero and id <> r2.id and alta_fecha > r2.alta_fecha))
union all
--MOVIMIENTOS BANCARIOS
select cast (mb.fecha_movimiento as timestamp without time zone) as fecha, 'D' as debito_credito, 
	'Mov Bcrio: ' || mb.id_movimiento as comprobante,
	'Deposito Efectivo' as descripcion, 
	ri.importe as importe, r.cuit, null as sucursal
from movimiento_banco_items  mbi
inner join movimiento_banco  mb
on mb.id_movimiento = mbi.id_movimiento
inner join recibo_ingresos ri
on mbi.recibo_ingreso_id = ri.id
inner join recibo r
on ri.recibo_id = r.id
where (recibo_ingreso_id is not null)
and cast(mb.fecha_movimiento as date) > ($1 - interval '1 day') and  cast(mb.fecha_movimiento as date) < ($2 + interval '1 day')
union all
select mb.baja_fecha as fecha, 'C' as debito_credito, 
	'Mov Bcrio: ' || mb.id_movimiento  || ' - ANULADO'  as comprobante,
	'Deposito Efectivo' as descripcion, 
	ri.importe as importe, r.cuit, null as sucursal
from movimiento_banco_items  mbi
inner join movimiento_banco  mb
on mb.id_movimiento = mbi.id_movimiento
left outer join recibo_ingresos ri
on mbi.recibo_ingreso_id = ri.id
inner join recibo r
on ri.recibo_id = r.id
where (recibo_ingreso_id is not null)
and mb.baja_fecha is not null
and cast(mb.baja_fecha as date) > ($1 - interval '1 day') and  cast(mb.baja_fecha as date) < ($2 + interval '1 day')
) asd 
left outer join empresa e
on asd.cuit = e.cuit
and (case when asd.sucursal is not null then asd.sucursal else '000' end)= e.sucursal
group by  fecha , 
		debito_credito, 
		comprobante, 
		descripcion || (case when asd.cuit is not null then ' - Cuit: ' ||asd.cuit  else '' end) || (case when e.razon_soc is not null then ' Razon: ' || e.razon_soc else '' end)
order by fecha asc, debito_Credito asc, comprobante asc, 4 asc ;



$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION libro_caja_efectivo(date, date) OWNER TO postgres;





--DEJO LA FUNCION LIBRO_CAJA ORIGINAL POR LAS DUDAS (EN ESTA SE TOMA EL EFECTIVO Y LOS CHEQUES EN EL MISMO REPORTE)
-- Function: libro_caja(date, date)

-- DROP FUNCTION libro_caja(date, date);
/*
CREATE OR REPLACE FUNCTION libro_caja(IN p_fecha_ini date, IN p_fecha_fin date)
  RETURNS TABLE(fecha timestamp without time zone, debito_credito character, comprobante character varying, descripcion character varying, importe numeric) AS
$BODY$

select fecha , debito_credito, comprobante, descripcion || (case when asd.cuit is not null then ' - Cuit: ' ||asd.cuit  else '' end) || (case when e.razon_soc is not null then ' Razon: ' || e.razon_soc else '' end), importe from (
--RECIBOS
select  
cast (r.fecha as timestamp without time zone),  
'C' as debito_credito, 
'Recibo: ' || r.numero as comprobante, 
case when ri.nro_cheque is not null then 'Recibo Cheque: ' || ri.nro_cheque else 
	case when ri.nro_cheque is null and  ri.numero_deposito is null then 'Recibo Efectivo'  end end as descripcion,
	ri.importe, r.cuit, r.sucursal
from recibo  r
inner join  recibo_ingresos ri
on r.id = ri.recibo_id
where ri.nro_cheque is not null or (ri.nro_cheque is null and  ri.numero_deposito is null)
and cast(r.fecha as date) > ($1 - interval '1 day') and  cast(r.fecha as date) < ($2 + interval '1 day')
and r.id not in (select id from recibo r2
	where baja_fecha is not null
	and exists (Select 1 from recibo where numero = r2.numero and id <> r2.id and alta_fecha > r2.alta_fecha))
union all 
select  
r.baja_fecha, 
'D' as debito_credito, 
'Recibo: ' || r.numero || ' - ANULADO' as comprobante, 
case when ri.nro_cheque is not null then 'Recibo Cheque: ' || ri.nro_cheque else 
	case when ri.nro_cheque is null and  ri.numero_deposito is null then 'Recibo Efectivo'  end end as descripcion,
	ri.importe, r.cuit, r.sucursal
from recibo  r
inner join  recibo_ingresos ri
on r.id = ri.recibo_id
where r.baja_fecha is not null
and( ri.nro_cheque is not null or (ri.nro_cheque is null and  ri.numero_deposito is null))
and cast(r.baja_fecha as date) > ($1 - interval '1 day') and  cast(r.baja_fecha as date) < ($2 + interval '1 day')
and r.id not in (select id from recibo r2
	where baja_fecha is not null
	and exists (Select 1 from recibo where numero = r2.numero and id <> r2.id and alta_fecha > r2.alta_fecha))
union all
--MOVIMIENTOS BANCARIOS
select cast (mb.fecha_movimiento as timestamp without time zone) as fecha, 'D' as debito_credito, 
	'Mov Bcrio: ' || mb.id_movimiento as comprobante,
	case when recibo_ingreso_id is not null then  'Deposito Efectivo' else
		 'Deposito cheque: '||c.nro_cheque end as descripcion, 
	case when recibo_ingreso_id is not null then  ri.importe else
		  c.importe   end as importe, c.cuit, null as sucursal
from movimiento_banco_items  mbi
inner join movimiento_banco  mb
on mb.id_movimiento = mbi.id_movimiento
left outer join cheque c
on mbi.nro_cheque = c.nro_cheque
and mbi.id_banco = c.id_banco
left outer join recibo_ingresos ri
on mbi.recibo_ingreso_id = ri.id
where ((id_estado_cheque_viejo = 3 and id_estado_cheque_nuevo = 4) or (recibo_ingreso_id is not null))
and cast(mb.fecha_movimiento as date) > ($1 - interval '1 day') and  cast(mb.fecha_movimiento as date) < ($2 + interval '1 day')
union all
select mb.baja_fecha as fecha, 'C' as debito_credito, 
	'Mov Bcrio: ' || mb.id_movimiento  || ' - ANULADO'  as comprobante,
	case when recibo_ingreso_id is not null then  'Deposito Efectivo' else
		'Deposito cheque: '||c.nro_cheque end as descripcion, 
	case when recibo_ingreso_id is not null then  ri.importe else
		  c.importe   end as importe, c.cuit, null as sucursal
from movimiento_banco_items  mbi
inner join movimiento_banco  mb
on mb.id_movimiento = mbi.id_movimiento
left outer join cheque c
on mbi.nro_cheque = c.nro_cheque
and mbi.id_banco = c.id_banco
left outer join recibo_ingresos ri
on mbi.recibo_ingreso_id = ri.id
where ((id_estado_cheque_viejo = 3 and id_estado_cheque_nuevo = 4) or (recibo_ingreso_id is not null))
and mb.baja_fecha is not null
and cast(mb.baja_fecha as date) > ($1 - interval '1 day') and  cast(mb.baja_fecha as date) < ($2 + interval '1 day')
) asd 
left outer join empresa e
on asd.cuit = e.cuit
and (case when asd.sucursal is not null then asd.sucursal else '000' end)= e.sucursal
order by fecha asc, comprobante asc, debito_Credito asc,4 asc ;



$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION libro_caja(date, date) OWNER TO postgres;
*/
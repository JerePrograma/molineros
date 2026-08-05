drop function reporte_anticipos(p_fecha_ini date, p_fecha_fin date, p_cuit character varying, p_sucu character varying);
create or replace function reporte_anticipos(p_fecha_ini date, p_fecha_fin date, p_cuit character varying, p_sucu character varying)
RETURNS TABLE(
  recibo character varying,
  fecha date,
  cuit character varying,
  sucursal character varying,
  importe numeric,
  debito_credito character(1),
  razon_soc character varying)
LANGUAGE sql
AS $BODY$


select recibo, fecha, aux.cuit, aux.sucursal, importe, debito_credito, e.razon_soc from (
	select 'Recibo ' || r.numero as recibo, 
		r.fecha, 
		r.cuit, 
		r.sucursal,
		(case when rc.concepto_importe_por_cheques is null then 0 else rc.concepto_importe_por_cheques  end ) +
		(case when rc.concepto_importe_adicional is null then 0 else rc.concepto_importe_adicional end ) as importe,
		'C' as debito_Credito
	from recibo r 
	inner join recibo_conceptos rc
	on r.id = rc.recibo_id
	where rc.caja_concepto_id = 122
	and r.baja_fecha is null
	and cast(r.fecha as date) >= $1 and cast(r.fecha as date) <= $2
	and ($3 is null or r.cuit = $3)
	and ($4 is null or r.sucursal = $4)
	union all
	select 'Recibo ' || r.numero ||  ' - Anticipo ' || r_orig.numero as recibo, 
		r.fecha, 
		r.cuit, 
		r.sucursal,
		(case when rc.concepto_importe_por_cheques is null then 0 else rc.concepto_importe_por_cheques  end ) +
		(case when rc.concepto_importe_adicional is null then 0 else rc.concepto_importe_adicional end ) ,
		'D' as debito_Credito
	from recibo_conceptos rc
	inner join recibo_ingresos ri
	on rc.id = ri.id_anticipo_recibo_concepto
	inner join recibo r 
	on ri.recibo_id  = r.id
	inner join recibo r_orig
	on rc.recibo_id = r_orig.id
	where r.baja_fecha is null
	and r_orig.baja_fecha is null
	and cast(r.fecha as date) >= $1 and cast(r.fecha as date) <= $2
	and ($3 is null or r.cuit = $3)
	and ($4 is null or r.sucursal = $4)
) aux
left outer join empresa e
on aux.cuit = e.cuit
and aux.sucursal = e.sucursal
order by  cuit,fecha asc ,debito_credito asc, 1;


$BODY$;


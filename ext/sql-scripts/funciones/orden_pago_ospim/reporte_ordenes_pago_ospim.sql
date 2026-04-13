DROP function reporte_ordenes_pago_ospim(p_fechaini date, p_fechafin date);

create or replace function reporte_ordenes_pago_ospim(p_fechaini date, p_fechafin date)
returns table(
fecha date, 
id_orden_pago integer, 
importe_op numeric, 
op_baja_fecha date,
cuit_acreedor character varying,
sucu_acreedor character varying,
id_seccional integer,
razon_soc character varying) 
LANGUAGE sql
as $BODY$

	select   cast (opo.alta_fecha as date), opo.id_orden_pago, 
	opo.importe,
	cast(opo.baja_fecha as date),
	opo.cuit_acreedor,
	opo.sucu_acreedor,
	opo.id_seccional,
	e.razon_soc
	from orden_pago_ospim  opo 
	left outer join empresa e
	on opo.cuit_acreedor = e.cuit
	and opo.sucu_acreedor = e.sucursal 
	where cast(opo.alta_fecha as date) > ($1 - interval '1 day')
	and cast(opo.alta_fecha  as date) < ($2 + interval '1 day')	
	and id_orden_pago <> 60
	order by opo.id_orden_pago asc;


$BODY$;

create or replace function buscar_saldo_inicial_caja(p_fecha date)
RETURNS TABLE(
	fecha_inicio_ejercicio date,
	saldo numeric )
LANGUAGE sql
AS $BODY$
select cbsi.fecha_inicio_ejercicio ,
	cbsi.saldo 
from caja_saldo_inicial   cbsi
where fecha_inicio_ejercicio = (select max(fecha_inicio_ejercicio) from caja_saldo_inicial  where fecha_inicio_ejercicio <= cast($1 as date))




$BODY$;
create or replace function buscar_saldo_inicial(p_cuenta_bcria integer, p_fecha date)
RETURNS TABLE(
  	id_cuenta_bcria integer,
	fecha_inicio_ejercicio date,
	saldo numeric,
	desripcion character varying,
	nro_cuenta integer ,
    sucursal integer )
LANGUAGE sql
AS $BODY$
select cbsi.id_cuenta_bcria ,
	cbsi.fecha_inicio_ejercicio ,
	cbsi.saldo ,
	cb.descripcion,
	cb.nro_cuenta ,
    cb.sucursal 
from cuenta_bcria_saldo_inicial   cbsi
inner join cuenta_bcria cb
on cbsi.id_cuenta_bcria = cb.id_cuenta_bcria 
where cbsi.id_cuenta_bcria = $1
and fecha_inicio_ejercicio = (select max(fecha_inicio_ejercicio) from cuenta_bcria_saldo_inicial  where fecha_inicio_ejercicio <= cast($2 as date))




$BODY$;
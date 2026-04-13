
create or replace function buscar_saldo_inicial_actas_convenios(p_cuit character varying, p_sucu character varying, p_seccinoal integer, p_fecha date)
RETURNS TABLE(
  	cuit character varying(13),
	sucu character varying(4),
	seccional integer,
	fecha_inicio_ejercicio date,
	saldo numeric(15,2))
LANGUAGE sql
AS $BODY$
select  	cuit,
		sucu,
		cast(null as integer),
		fecha_inicio_ejercicio,
		saldo 
from actas_convenios_saldo_inicial   si
where  ($1 is null or cuit = $1)
and ($2 is null or sucu = $2 )
and fecha_inicio_ejercicio >= (select max(fecha_inicio_ejercicio) from actas_convenios_saldo_inicial  
									where fecha_inicio_ejercicio <= cast($4 as date)
									and cuit = si.cuit
									and sucu = si.sucu)




$BODY$;
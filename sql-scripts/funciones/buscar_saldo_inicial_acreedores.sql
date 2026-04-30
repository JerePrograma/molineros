
create or replace function buscar_saldo_inicial_acreedores(p_cuit character varying, p_sucu character varying, p_seccinoal integer, p_fecha date)
RETURNS TABLE(
  	cuit_acreedor  character varying(13),
	sucu_acreedor character varying(4),
	seccional integer,
	fecha_inicio_ejercicio date,
	saldo numeric(15,2))
LANGUAGE sql
AS $BODY$
select  cuit_acreedor,
		sucu_acreedor,
		seccional,
		fecha_inicio_ejercicio,
		saldo 
from acreedores_saldo_inicial   si
where  ($1 is null or cuit_acreedor = $1)
	and ($2 is null or (sucu_acreedor = $2 and (($3 is null and seccional is null) or seccional = $3)))
and fecha_inicio_ejercicio >= (select max(fecha_inicio_ejercicio) from acreedores_saldo_inicial  
									where fecha_inicio_ejercicio <= cast($4 as date)
									and cuit_acreedor = si.cuit_acreedor
									and sucu_Acreedor = si.sucu_acreedor
									and (seccional = si.seccional or (seccional is null and si.seccional is null)))




$BODY$;
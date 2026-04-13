
drop function verificar_asientos_ordenados(p_ejercicio_ini date, p_ejercicio_fin date);
create or replace function verificar_asientos_ordenados(p_ejercicio_ini date, p_ejercicio_fin date)
RETURNS boolean
    LANGUAGE plpgsql
    AS $BODY$
declare v_ret  boolean;
BEGIN

	drop table if exists nros_asientos_temp;

	create temp table nros_asientos_temp as
	select  (row_number() OVER (ORDER BY fecha asc, id asc)) + 1 AS nuevo_nro, id, numero
	from asiento a
	where cast(ejercicio_desde as date) >= cast(p_ejercicio_ini as date)
	and cast(ejercicio_hasta as date) <= cast(p_ejercicio_fin as date)
	and numero > 1
	order by fecha asc, id asc;


	v_ret = false where exists(select 1 from nros_asientos_temp where nuevo_nro <> numero);

	if v_ret is null then
		v_ret = true;
	end if;
return v_ret;
END;
$BODY$;

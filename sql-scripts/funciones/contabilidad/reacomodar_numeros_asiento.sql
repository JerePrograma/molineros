create or replace function reacomodar_numeros_asiento(p_ejercicio_ini date, p_ejercicio_fin date)
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare v_nro  integer;
BEGIN

	drop table if exists modificaciones_nros_temp;

	create temp table modificaciones_nros_temp as
	select  (row_number() OVER (ORDER BY fecha asc, id asc)) +1 AS nuevo_nro, id, numero
	from asiento a
	where cast(ejercicio_desde as date) >= cast(p_ejercicio_ini as date)
	and cast(ejercicio_hasta as date) <= cast(p_ejercicio_fin as date)
	and numero > 1
	order by fecha asc, id asc;


	update asiento a  set numero = nuevo_nro
	from  modificaciones_nros_temp modif
	where a.id = modif.id;

return 1;
END;
$BODY$;

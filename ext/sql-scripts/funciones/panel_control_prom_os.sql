CREATE OR REPLACE FUNCTION panel_control_prom_os(fecha_desde date, fecha_hasta date)
  RETURNS SETOF panel_control_prom_os AS
$BODY$
declare periodo_viejo date;
declare periodo_nuevo date;
BEGIN


drop table if exists result;
create temp table result (periodo date, titulares bigint, promedio numeric);

periodo_nuevo=fecha_desde;

WHILE periodo_nuevo<=fecha_hasta LOOP
	RAISE INFO 'PERIODO: %', periodo_nuevo;
	insert into result(periodo, titulares, promedio)
	select periodo, count(*), sum(importebasecontribucionos)/count(*) 
	from detalle_declaracion_jurada ddj 
	where periodo=periodo_nuevo
	and importebasecontribucionos>0
	and ddj.secobligacion=(select max(secobligacion) from detalle_declaracion_jurada ddj2 
			       where ddj2.cuit=ddj.cuit
			       and ddj2.cuil=ddj.cuil
			       and ddj2.periodo=ddj.periodo)
	group by periodo;
	periodo_nuevo=periodo_nuevo+Interval '1 month';
END LOOP;

return query
select periodo, titulares, trunc(promedio,2)
from result;


END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
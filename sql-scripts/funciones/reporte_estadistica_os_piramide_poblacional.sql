create type reporte_estadistica_os_piramide_poblacional_result as (grupo varchar, femenino integer, masculino integer, total  integer);

CREATE OR REPLACE FUNCTION reporte_estadistica_os_piramide_poblacional()
  RETURNS SETOF reporte_estadistica_os_piramide_poblacional_result AS
$BODY$
begin
	drop table if exists reporte_estadistica_os_piramide_poblacional;
	CREATE temp table reporte_estadistica_os_piramide_poblacional AS
	SELECT mthreport.*	
	FROM 
		crosstab('select g.id as id, upper(a.sexo), count(*)::integer as cantidad
			  from grupo_etario_650 g, afiliado a
			  where fu_obtener_edad(a.naci_fecha, current_date) >= min
			  and fu_obtener_edad(a.naci_fecha, current_date)<=max
			  and (a.baja_fecha is null or baja_fecha>current_date)
			  and exists (select 1 from afi_aportes p 
					where p.cuil_titular=a.cuil_titular 
					and id_aporte in (1,2,9,8) 
					and (fecha_egre is null or fecha_egre>current_date) 
					and (p.baja_fecha is null or p.baja_fecha>current_date))
			  group by g.id,upper(a.sexo)
			  order by g.id--129985' 
			 )
			As mthreport(id integer,femenino integer , masculino integer)
	order by id;

alter table reporte_estadistica_os_piramide_poblacional add grupo varchar;

update reporte_estadistica_os_piramide_poblacional r
set grupo=cast(g.min as text)||'-'||cast(g.max as text)
from grupo_etario_650 g
where r.id=g.id;

return query
select grupo, femenino, masculino, femenino+masculino as total from reporte_estadistica_os_piramide_poblacional order by id;


END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
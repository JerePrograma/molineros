create type reporte_estadistica_os_poblacion_provincia_result AS (jurisdiccion varchar, femenino integer, masculino integer, total integer)

CREATE OR REPLACE FUNCTION reporte_estadistica_os_poblacion_provincia()
  RETURNS SETOF reporte_estadistica_os_poblacion_provincia_result AS
$BODY$
begin
	drop table if exists reporte_estadistica_os_poblacion_provincia;
	drop table if exists aux;

	create temp table aux as
	select cuil_titular, inte, sexo
	from afiliado a
	where (a.baja_fecha is null or baja_fecha>current_date)
			  and exists (select 1 from afi_aportes p 
					where p.cuil_titular=a.cuil_titular 
					and id_aporte in (1,2,9,8) 
					and (fecha_egre is null or fecha_egre>current_date) 
					and (p.baja_fecha is null or p.baja_fecha>current_date));
					
        alter table aux add id_provincia integer;					

        update aux x
        set id_provincia= d.provincia
        from afi_domicilio d 
        where d.cuil_titular=x.cuil_titular
        and (d.baja_fecha is null or d.baja_fecha>current_date);

        update aux set id_provincia=1 where id_provincia is null;


	CREATE temp table reporte_estadistica_os_poblacion_provincia AS
	SELECT mthreport.*	
	FROM 
		crosstab('select p.detalle as jurisdiccion, upper(a.sexo), count(*)::integer as cantidad
			  from provincia p, aux a
			  where a.id_provincia=p.id_provincia			  
			  group by p.detalle,upper(a.sexo)
			  order by p.detalle--129985' 
			 )
			As mthreport(jurisdiccion varchar,femenino integer , masculino integer)
	order by jurisdiccion;



return query
select jurisdiccion, femenino, masculino, femenino+masculino as total from reporte_estadistica_os_poblacion_provincia order by jurisdiccion;


END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
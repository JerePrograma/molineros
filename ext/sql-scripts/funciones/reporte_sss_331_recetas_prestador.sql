CREATE OR REPLACE FUNCTION reporte_sss_331_recetas_prestador(fecha_ini_p date, fecha_fin_p date, fecha_ciere_p date, porcentaje_p numeric)
  RETURNS SETOF reporte_sss_331_recetas_prestador AS
$BODY$
BEGIN
drop table if exists prestadores_temp;
drop table if exists prestadores_temp2;

CREATE TEMP TABLE prestadores_temp AS
select  cast('' as varchar) as cuit,--cuit
	lfa.farmacia as farmacia_liq,
	lfa.prestador as prestador,
	lfa.farmacia as farmacia,
	lfa.nro_recetario, --recetario
	total_ospim, --total ospim
	per_codigo as periodo --periodo
--into temp table prestadores_temp        
from liquidacion_farmacia_amtima lfa--liquidacion_farmacia_amtima lfa--
left outer join afiliado a
on lfa.id_ospim = a.id_ospim
and lfa.inte = a.inte
and a.id_ospim not in (36653,
40632,
42524,
45046,
45835,
50519
) 
left outer join afi_situ_laboral al
on al.cuil_titular=a.cuil_titular
and al.inte=0
inner join parentesco_sss s
--on upper(a.parentesco)=s.descripcion
on a.id_parentesco_sss=s.codigo
inner join estado_civil_sss ecs
--on upper(a.civil_esta)=ecs.descripcion
on a.id_estado_civil_sss=ecs.codigo
inner join nacionalidad na
on a.nacionalidad=na.id
left outer join afi_domicilio ad
on ad.cuil_titular=a.cuil_titular
and ad.inte=0
and (ad.baja_fecha is null or ad.baja_fecha>=fecha_ini_p or ad.cuil_titular='20219215208') 
and ad.alta_fecha<=fecha_fin_p+interval '1 day'
left outer join localidad l
on ad.localidad=id_localidad
left outer join provincia pr
on ad.provincia=pr.id_provincia
left outer join situacion_revista sr
on al.id_revista=sr.id_situ_revista
left outer join categoria_laboral cl
on al.id_categoria=cl.id_categoria
inner join medicamentos mm
on cast(lfa.nro_troquel as numeric)=mm.troquel
/*inner join genericos_sss g
on mm.gen_codigo=g.gen_codigo
and g.gen_cobertura=mm.pat_cobertura*/
inner join periodos_sss pe
on pe.per_fechainicio=fecha_ini_p
where (mm.pat_cobertura=porcentaje_p)
and (pmi is null or rtrim(pmi)='')
and (al.baja_fecha is null or (al.baja_fecha>=fecha_ini_p and al.baja_fecha<=fecha_fin_p))
and (al.fecha_egre is null or fecha_egre>current_date)  --49635 afiliados con trabajos activos
and al.fecha_ingre=(select max(fecha_ingre) 
		    from afi_situ_laboral al2 where al2.cuil_titular=al.cuil_titular and al2.inte=al.inte
		    and (al2.baja_fecha is null or (al2.baja_fecha>=fecha_ini_p and al2.baja_fecha<=fecha_fin_p))
		    and (al2.fecha_egre is null or al2.fecha_egre>current_date))
--and a.parentesco is not null --196 liquidaciones con parentesco desconocido
--and upper(a.civil_esta)<>'DESCONOCIDO' --158 liquidaciones de afiliados con estado civil desconocido
and periodo>=fecha_ini_p
and periodo<= fecha_fin_p
and mm.pat_codigo is not null
and mm.gen_codigo is not null
union 
select  cast('' as varchar) as cuit,--cuit
	lfa.farmacia as farmacia_liq,
	lfa.prestador as prestador,
	lfa.farmacia as farmacia,
	lfa.nro_recetario, --recetario
	total_ospim, --total ospim
	per_codigo as periodo --periodo
--into temp table prestadores_temp        
from liquidacion_farmacia_ospim lfa--liquidacion_farmacia_amtima lfa--
left outer join afiliado a
on lfa.id_ospim = a.id_ospim
and lfa.inte = a.inte
and a.id_ospim not in (36653,
40632,
42524,
45046,
45835,
50519
) 
left outer join afi_situ_laboral al
on al.cuil_titular=a.cuil_titular
and al.inte=0
inner join parentesco_sss s
--on upper(a.parentesco)=s.descripcion
on a.id_parentesco_sss=s.codigo
inner join estado_civil_sss ecs
--on upper(a.civil_esta)=ecs.descripcion
on a.id_estado_civil_sss=ecs.codigo
inner join nacionalidad na
on a.nacionalidad=na.id
left outer join afi_domicilio ad
on ad.cuil_titular=a.cuil_titular
and ad.inte=0
and (ad.baja_fecha is null or ad.baja_fecha>=fecha_ini_p or ad.cuil_titular='20219215208') 
and ad.alta_fecha<=fecha_fin_p+interval '1 day'
left outer join localidad l
on ad.localidad=id_localidad
left outer join provincia pr
on ad.provincia=pr.id_provincia
left outer join situacion_revista sr
on al.id_revista=sr.id_situ_revista
left outer join categoria_laboral cl
on al.id_categoria=cl.id_categoria
inner join medicamentos mm
on cast(lfa.nro_troquel as numeric)=mm.troquel
/*inner join genericos_sss g
on mm.gen_codigo=g.gen_codigo
and g.gen_cobertura=mm.pat_cobertura*/
inner join periodos_sss pe
on pe.per_fechainicio=fecha_ini_p
where (mm.pat_cobertura=porcentaje_p)
and (pmi is null or rtrim(pmi)='')
and (al.baja_fecha is null or (al.baja_fecha>=fecha_ini_p and al.baja_fecha<=fecha_fin_p))
and (al.fecha_egre is null or fecha_egre>current_date)  --49635 afiliados con trabajos activos
and al.fecha_ingre=(select max(fecha_ingre) 
		    from afi_situ_laboral al2 where al2.cuil_titular=al.cuil_titular and al2.inte=al.inte
		    and (al2.baja_fecha is null or (al2.baja_fecha>=fecha_ini_p and al2.baja_fecha<=fecha_fin_p))
		    and (al2.fecha_egre is null or al2.fecha_egre>current_date))
--and a.parentesco is not null --196 liquidaciones con parentesco desconocido
--and upper(a.civil_esta)<>'DESCONOCIDO' --158 liquidaciones de afiliados con estado civil desconocido
and periodo>=fecha_ini_p
and periodo<= fecha_fin_p
and mm.pat_codigo is not null
and mm.gen_codigo is not null;


update prestadores_temp p
set cuit=f.cuit
from farmacia f
where rtrim(upper(p.prestador)) like '%'||rtrim(upper(f.farmacia))||'%'
and (p.cuit is null or rtrim(p.cuit)='')
and (f.cuit is not null or rtrim(f.cuit)<>'');


update prestadores_temp p
set cuit=f.cuit
from farmacia f
where p.farmacia=f.farmacia
and f.cuit=(select max(cuit) from farmacia f2 where f2.farmacia=p.farmacia)
and f.cod_farm=(select max(cod_farm) from farmacia f3 where f3.farmacia=p.farmacia)
and (p.cuit is null or rtrim(p.cuit)='')
and (f.cuit is not null or rtrim(f.cuit)<>'');


update prestadores_temp p
set farmacia=f.farmacia
from farmacia f
where p.cuit=f.cuit
and (p.cuit is null or rtrim(p.cuit)='')
and (f.cuit is not null or rtrim(f.cuit)<>'');

update prestadores_temp p
set farmacia=f.farmacia,
    cuit=f.cuit
from farmacia f
where rtrim(p.farmacia_liq) like '%'||rtrim(f.farmacia)||'%'
and p.cuit is null;

update prestadores_temp p
set cuit=f.cuit
from farmacia f
where rtrim(p.farmacia_liq) like '%'||rtrim(f.farmacia)||'%'
and p.cuit is null
and f.cuit is not null;


update prestadores_temp p
set cuit=f.cuit
from farmacia f
where rtrim(p.prestador) like '%'||rtrim(f.farmacia)||'%'
and p.cuit is null
and f.cuit is not null;

create temp table prestadores_temp2 as 
select 	periodo, 
	cast(max(cuit) as varchar) as cuit, 	
	nro_recetario,
	sum(total_ospim) as total_ospim
from prestadores_temp
group by periodo,  nro_recetario;

return query
select 	periodo,
	cuit,
	lpad(cast(count(*) as varchar),11,'0') as cant_recetas,
	lpad(cast(sum(total_ospim) as varchar),11,'0') as monto,
	cast('A' as text) as cod_mov
from prestadores_temp2
group by periodo,cuit;	
       


END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
CREATE OR REPLACE FUNCTION reporte_sss_331_medicamentos_ranking_anexo(fecha_ini_p date, fecha_fin_p date)
  RETURNS SETOF reporte_sss_331_medicamentos_ranking_anexo AS
$BODY$
BEGIN
drop table if exists recetas;

create temp table recetas as
select  per_codigo, 
	1||lpad(lfa.nro_recetario,9,'0') as nro_receta, --nro_receta	
	nro_troquel,
	lfa.medicamento,
	porcentaje_ospim,
	nro_prestador,
	prestador as prestador,	                
        lpad(cast(sum(lfa.cantidad) as varchar),2,'0') as cantidad, --cantidad
        sum(lfa.total_ospim) as monto_ospim        
from amtima_federico lfa--liquidacion_farmacia_amtima lfa--
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
inner join genericos_sss g
on mm.gen_codigo=g.gen_codigo
and g.gen_cobertura=mm.pat_cobertura
inner join periodos_sss pe
on pe.per_fechainicio=fecha_ini_p
where (mm.pat_cobertura=70 or mm.pat_cobertura=100)
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
group by  per_codigo,
        nro_receta, --nro_receta	
        nro_troquel,
        lfa.medicamento,
        porcentaje_ospim,
	nro_prestador,
	prestador    
union 
select  per_codigo,
	1||lpad(lfa.nro_recetario,9,'0') as nro_receta, --nro_receta
	nro_troquel,	
	lfa.medicamento,
	porcentaje_ospim,
	nro_prestador,
	prestador as prestador,	                
        lpad(cast(sum(lfa.cantidad) as varchar),2,'0') as cantidad, --cantidad
        sum(lfa.total_ospim) as monto_ospim        
from amtima_federico lfa--liquidacion_farmacia_amtima lfa--
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
inner join periodos_sss pe
on pe.per_fechainicio=fecha_ini_p
where porcentaje_ospim=40
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
group by  per_codigo,
	nro_receta, --nro_receta
        nro_troquel,
        lfa.medicamento,
        porcentaje_ospim,
	nro_prestador,
	prestador;        

return query
select   per_codigo, medicamento, cast(sum(monto_ospim) as double precision)
from recetas r
where porcentaje_ospim=(select max(porcentaje_ospim) from recetas r2 where r2.nro_troquel=r.nro_troquel and r2.nro_receta=r.nro_receta)
group by  per_codigo, medicamento
order by cast(sum(monto_ospim) as double precision) desc;


END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
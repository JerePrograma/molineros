CREATE OR REPLACE FUNCTION reporte_sss_331_cantidad_afiliados(fecha_ini_p date, fecha_fin_p date, cobertura_p numeric)
  RETURNS SETOF reporte_sss_331_cantidad_afiliados AS
$BODY$
BEGIN
drop table if exists cant_afi;
create temp table cant_afi as
select per_codigo as periodo,--periodo
       lfa.id_ospim,
       lfa.inte,
       lpad(cast(cast(mm.pat_cobertura as double precision) as varchar),3,'0') as porc, --cobertura
       lpad(cast(isNull(pr.id_sssalud,0) as varchar),2,'0') as provincia, --PROVINCIA,
       lpad(cast(count(*) as varchar),11,'0') as cantidad
from liquidacion_farmacia_amtima lfa--liquidacion_farmacia_amtima lfa--
left outer join afiliado a
on lfa.id_ospim = a.id_ospim
and lfa.inte = a.inte
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
and (ad.alta_fecha<=fecha_fin_p +interval '1 day' or a.id_ospim in (56174, 41646))
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
where (mm.pat_cobertura=cobertura_p)
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
and a.id_ospim not in (56174,
41646, 55705)
group by per_codigo, lfa.id_ospim, lfa.inte, lpad(cast(cast(mm.pat_cobertura as double precision) as varchar),3,'0'), 
       lpad(cast(isNull(pr.id_sssalud,0) as varchar),2,'0')
union 
select per_codigo as periodo,--periodo
       lfa.id_ospim,
       lfa.inte,
       lpad(cast(cast(mm.pat_cobertura as double precision) as varchar),3,'0') as porc, --cobertura
       lpad(cast(isNull(pr.id_sssalud,0) as varchar),2,'0') as provincia, --PROVINCIA,
       lpad(cast(count(*) as varchar),11,'0') as cantidad
from liquidacion_farmacia_ospim lfa--liquidacion_farmacia_amtima lfa--
left outer join afiliado a
on lfa.id_ospim = a.id_ospim
and lfa.inte = a.inte
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
and (ad.alta_fecha<=fecha_fin_p +interval '1 day' or a.id_ospim in (56174, 41646))
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
where (mm.pat_cobertura=cobertura_p)
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
and a.id_ospim not in (56174,
41646, 55705)
group by per_codigo, lfa.id_ospim, lfa.inte, lpad(cast(cast(mm.pat_cobertura as double precision) as varchar),3,'0'), 
       lpad(cast(isNull(pr.id_sssalud,0) as varchar),2,'0'); 


return query
select  periodo,--periodo
	porc, --cobertura
        provincia, --PROVINCIA,
        lpad(cast(count(*) as varchar),11,'0') as cantidad
from cant_afi        
group by periodo, porc, provincia
order by provincia;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
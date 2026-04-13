CREATE OR REPLACE FUNCTION reporte_sss_331_prestadores(fecha_ini_p date, fecha_fin_p date, fecha_ciere_p date, cobertura_p numeric)
  RETURNS SETOF reporte_sss_331_prestadores AS
$BODY$
BEGIN
drop table if exists prestadores_temp;
drop table if exists prestadores_temp2;

create temp table prestadores_temp (cuit varchar, prestador varchar, farmacia_liq varchar, farmacia varchar, tipo_pre varchar, domicilio text,
				    localidad text, id_ospim int, representante varchar, insc_RNP int, nro_rnp varchar, asis_ambu int, asis_inter int,
				    asis_alta int, asis_urg int, asis_salud int, prest_odon int, prest_farmacia int, cod_mov text, id serial);

insert into prestadores_temp (cuit, prestador, farmacia_liq, farmacia, tipo_pre, domicilio, localidad, id_ospim, representante, insc_RNP, nro_rnp, 
			      asis_ambu, asis_inter, asis_alta, asis_urg, asis_salud, prest_odon, prest_farmacia, cod_mov)			       
--CREATE TEMP TABLE prestadores_temp AS
select  cast('' as varchar) as cuit,--cuit
        lfa.prestador,
	lfa.farmacia as farmacia_liq,
	lfa.farmacia as farmacia,--rpad(rtrim(isNull(f.farmacia,'')),70,' ') as nombre, --FARMACIA
	cast('09' as varchar) as tipo_pre, --TIPO PRESTADOR
	cast('' as text) as domicilio,--rpad(rtrim(isNull(f.calle,'')),70,' ') as domicilio, --domicilio
	cast('' as text) as localidad,--lpad(cast(l.id_localidadesss as varchar),5,'0') as localidad, --LOCALIDAD
	min(a.id_ospim) as id_ospim,
	rpad(cast('' as varchar),70,' ') as representante, --REPRESENTANTE
        0 as insc_RNP, --Inscripto RNP
        rpad(cast('' as varchar),50,' ') as nro_RNP, --NRO INSCR RNP
        0 as asis_ambu,
        0 as asis_inter,
        0 as asis_alta,
        0 as asis_urg,
        0 as asis_salud,
        0 as prest_odon,
        1 as prest_farmacia,
        cast('A' as text) as cod_mov
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
on a.id_parentesco_sss=s.codigo
--on upper(a.parentesco)=s.descripcion
inner join estado_civil_sss ecs
on a.id_estado_civil_sss=ecs.codigo
--on upper(a.civil_esta)=ecs.descripcion
inner join nacionalidad na
on a.nacionalidad=na.id
left outer join afi_domicilio ad
on ad.cuil_titular=a.cuil_titular
and ad.inte=0				
and (ad.baja_fecha is null or ad.baja_fecha>=fecha_ini_p or ad.cuil_titular='20219215208') 
and ad.alta_fecha<=cast(fecha_fin_p as date)+interval '1 day'
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
group by lfa.prestador, lfa.farmacia
union 
select  cast('' as varchar) as cuit,--cuit
        lfa.prestador,
	lfa.farmacia as farmacia_liq,
	lfa.farmacia as farmacia,--rpad(rtrim(isNull(f.farmacia,'')),70,' ') as nombre, --FARMACIA
	cast('09' as varchar) as tipo_pre, --TIPO PRESTADOR
	cast('' as text) as domicilio,--rpad(rtrim(isNull(f.calle,'')),70,' ') as domicilio, --domicilio
	cast('' as text) as localidad,--lpad(cast(l.id_localidadesss as varchar),5,'0') as localidad, --LOCALIDAD
	min(a.id_ospim) as id_ospim,
	rpad(cast('' as varchar),70,' ') as representante, --REPRESENTANTE
        0 as insc_RNP, --Inscripto RNP
        rpad(cast('' as varchar),50,' ') as nro_RNP, --NRO INSCR RNP
        0 as asis_ambu,
        0 as asis_inter,
        0 as asis_alta,
        0 as asis_urg,
        0 as asis_salud,
        0 as prest_odon,
        1 as prest_farmacia,
        cast('A' as text) as cod_mov
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
on a.id_parentesco_sss=s.codigo
--on upper(a.parentesco)=s.descripcion
inner join estado_civil_sss ecs
on a.id_estado_civil_sss=ecs.codigo
--on upper(a.civil_esta)=ecs.descripcion
inner join nacionalidad na
on a.nacionalidad=na.id
left outer join afi_domicilio ad
on ad.cuil_titular=a.cuil_titular
and ad.inte=0				
and (ad.baja_fecha is null or ad.baja_fecha>=fecha_ini_p or ad.cuil_titular='20219215208') 
and ad.alta_fecha<=cast(fecha_fin_p as date)+interval '1 day'
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
group by lfa.prestador, lfa.farmacia
order by  1;

update prestadores_temp p
set farmacia=f.farmacia,
    cuit=f.cuit,
    domicilio=f.calle
from farmacia f
where rtrim(upper(p.prestador)) like '%'||rtrim(upper(f.farmacia))||'%'
and (p.cuit is null or rtrim(p.cuit)='')
and (f.cuit is not null or rtrim(f.cuit)<>'');


update prestadores_temp p
set farmacia=f.farmacia,
    cuit=f.cuit
from farmacia f
where p.farmacia=f.farmacia
and f.cuit=(select max(cuit) from farmacia f2 where f2.farmacia=p.farmacia)
and f.cod_farm=(select max(cod_farm) from farmacia f3 where f3.farmacia=p.farmacia)
and (p.cuit is null or rtrim(p.cuit)='')
and (f.cuit is not null or rtrim(f.cuit)<>'');


update prestadores_temp p
set farmacia=f.farmacia,
    domicilio=f.calle
from farmacia f
where p.cuit=f.cuit
and (p.cuit is null or rtrim(p.cuit)='')
and (f.cuit is not null or rtrim(f.cuit)<>'');

update prestadores_temp p
set farmacia=f.farmacia,
    domicilio=f.calle,	
    cuit=f.cuit
from farmacia f
where rtrim(p.farmacia_liq) like '%'||rtrim(f.farmacia)||'%'
and p.cuit is null;

update prestadores_temp p
set farmacia=f.farmacia,
    cuit=f.cuit
from farmacia f
where rtrim(p.farmacia_liq) like '%'||rtrim(f.farmacia)||'%'
and p.cuit is null
and f.cuit is not null;

update prestadores_temp p
set localidad=l.id_localidadesss
from afiliado a, localidad l, afi_domicilio al
where a.id_ospim=p.id_ospim
and a.inte=0
and al.cuil_titular=a.cuil_titular
and al.inte=0
and (al.baja_fecha is null or al.baja_fecha>fecha_fin_p)
and l.id_localidad=al.localidad;

update prestadores_temp p
set farmacia=f.farmacia,
    cuit=f.cuit,
    domicilio=f.calle
from farmacia f
where rtrim(p.prestador) like '%'||rtrim(f.farmacia)||'%'
and p.cuit is null
and f.cuit is not null;

update prestadores_temp
set farmacia='FEDERACION ARGENTINA DE CAMARAS DE FARMACIAS'
where cuit='30632378056';

update prestadores_temp
set farmacia='COLEGIO DE FARMACEUTICOS DE LA PROVINCIA DE BUENOS AIRES'
where cuit='30550857061';

create temp table prestadores_temp2 as 
select 	distinct cuit,
	cast('' as text) as farmacia,
	cast(tipo_pre as varchar) as tipo_pre, --TIPO PRESTADOR
	cast('' as text) as domicilio, 
	cast('' as text) as localidad, 
	rpad(cast('' as varchar),50,' ') as representante, --REPRESENTANTE
        0 as insc_rnp, --Inscripto RNP
        0 as nro_rnp, --NRO INSCR RNP
        asis_ambu,
        asis_inter,
        asis_alta,
        asis_urg,
        asis_salud,
        prest_odon,
        prest_farmacia,
        cod_mov,
        min(id) as id
from prestadores_temp p
group by cuit, tipo_pre,asis_ambu,
        asis_inter,
        asis_alta,
        asis_urg,
        asis_salud,
        prest_odon,
        prest_farmacia,
        cod_mov;

update prestadores_temp2 pp
set farmacia=p.farmacia,
    domicilio=p.domicilio,
    localidad=p.localidad
from prestadores_temp p
where p.id=pp.id    ;
    
    

RETURN QUERY
select p.cuit,--cuit	
        rpad(farmacia,70,' '),
	cast(tipo_pre as varchar), --TIPO PRESTADOR
	rpad(domicilio,70,' '),
	lpad(localidad,5,'0'),
	rpad(cast(representante as varchar),50,' ') , 
        insc_RNP, --Inscripto RNP
        rpad(cast(nro_RNP as varchar),10,' '), --NRO INSCR RNP
        asis_ambu,
        asis_inter,
        asis_alta,
        asis_urg,
        asis_salud,
        prest_odon,
        prest_farmacia,
        cod_mov
FROM prestadores_temp2 p;
--where cuit not like '%XXX%';

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
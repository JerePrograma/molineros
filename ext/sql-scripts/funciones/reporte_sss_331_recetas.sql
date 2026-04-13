CREATE OR REPLACE FUNCTION reporte_sss_331_recetas(fecha_ini_p date, fecha_fin_p date, fecha_ciere_p date, cobertura_p numeric)
  RETURNS SETOF reporte_sss_331_recetas AS
$BODY$
BEGIN
drop table if exists aux_recetas;

create temp table aux_recetas as
select  1||lpad(lfa.nro_recetario,9,'0') as nro_receta, --SE AGREGA EL UNO PARA NO REPETIR CON OMINT	
	upper(a.documento_tipo) as docu_tipo,--tipo documento
        lpad(a.docu_numero,8,'0') as docu_nro, --nro documento
        lfa.nro_troquel,
        per_codigo as periodo, --periodo
        to_char(lfa.fecha,'DDMMYYYY') as fecha, --fecha
        lpad(cast(g.gen_codigo as varchar),3,'0') as generico, --generico
        lpad(cast(g.gen_cobertura as varchar),3,'0')  as cobertura, --código de patología
        sum(lfa.cantidad) as cantidad, --cantidad
        sum(lfa.total_ospim/case when lfa.cantidad<=0 then 1 else lfa.cantidad end) as monto_ospim,
        cast('A' as text) as cod_mov        
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
inner join genericos_sss g
on mm.gen_codigo=g.gen_codigo
and g.gen_cobertura=mm.pat_cobertura
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
group by 1||lpad(lfa.nro_recetario,9,'0'), 
	upper(a.documento_tipo),--tipo documento
        lpad(a.docu_numero,8,'0'), --nro documento
        lfa.nro_troquel,
        per_codigo, --periodo
        to_char(lfa.fecha,'DDMMYYYY'), --fecha
        lpad(cast(g.gen_codigo as varchar),3,'0'), --generico
        lpad(cast(g.gen_cobertura as varchar),3,'0') --código de patología        
union 
select  1||lpad(lfa.nro_recetario,9,'0') as nro_receta, --nro_receta	
	upper(a.documento_tipo) as docu_tipo,--tipo documento
        lpad(a.docu_numero,8,'0') as docu_nro, --nro documento
        lfa.nro_troquel,
        per_codigo as periodo, --periodo
        to_char(lfa.fecha,'DDMMYYYY') as fecha, --fecha
        lpad(cast(g.gen_codigo as varchar),3,'0') as generico, --generico
        lpad(cast(g.gen_cobertura as varchar),3,'0')  as cobertura, --código de patología
        sum(lfa.cantidad) as cantidad, --cantidad
        sum(lfa.total_ospim/case when lfa.cantidad<=0 then 1 else lfa.cantidad end) as monto_ospim,
        cast('A' as text) as cod_mov        
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
inner join genericos_sss g
on mm.gen_codigo=g.gen_codigo
and g.gen_cobertura=mm.pat_cobertura
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
group by 1||lpad(lfa.nro_recetario,9,'0'), 
	upper(a.documento_tipo),--tipo documento
        lpad(a.docu_numero,8,'0'), --nro documento
        lfa.nro_troquel,
        per_codigo, --periodo
        to_char(lfa.fecha,'DDMMYYYY'), --fecha
        lpad(cast(g.gen_codigo as varchar),3,'0'), --generico
        lpad(cast(g.gen_cobertura as varchar),3,'0'); --código de patología           
        
return query        
select  nro_receta, --nro_receta	
	docu_tipo,--tipo documento
        docu_nro, --nro documento
        periodo, --periodo
        fecha, --fecha
        generico, --generico
        cobertura, --código de patología
        lpad(cast(sum(cantidad) as varchar),2,'0') as cantidad, --cantidad
        lpad(cast(sum(monto_ospim/case when cantidad<=0 then 1 else cantidad end) as varchar),11,'0') as monto_ospim,
        cod_mov
from aux_recetas
group by nro_receta, docu_tipo, docu_nro, periodo, fecha, generico, cobertura,cod_mov;


END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
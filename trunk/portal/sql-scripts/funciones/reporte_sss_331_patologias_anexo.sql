CREATE OR REPLACE FUNCTION reporte_sss_331_patologias_anexo(fecha_ini_p date, fecha_fin_p date)
  RETURNS SETOF reporte_sss_331_patologias_anexo AS
$BODY$
BEGIN
drop table if exists pato_temp;

create temp table pato_temp as 
select  mm.pat_cobertura  as cobertura,--porcentaje_ospim as porcentaje_ospim, --porc_cobertura
	mm.pat_codigo, mm.pat_grupo, 
	cast('' as varchar) as patologia,/*ppp.descripcion as patologias,*/
	cast(upper(a.documento_tipo) as varchar) as docu_tipo,--tipo documento
        cast(lpad(a.docu_numero,8,'0') as varchar) as docu_nro, --nro documento
        cast(upper(a.apellido)||', '||upper(a.nombre) as varchar)as nomape,
        pr.detalle as provincia,
        fu_obtener_edad(a.naci_fecha,current_date) as edad
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
where (mm.pat_cobertura=100 or mm.pat_cobertura=70)
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
group by  mm.pat_cobertura, mm.pat_codigo, mm.pat_grupo, upper(a.documento_tipo), lpad(a.docu_numero,8,'0'), upper(a.apellido)||', '||upper(a.nombre),pr.detalle, fu_obtener_edad(a.naci_fecha,current_date) 
order by  cast(lpad(a.docu_numero,8,'0') as varchar) ;

RAISE INFO 'ACTUALIZO PATOS';

update pato_temp mm
set patologia=ppp.descripcion
from patologias_sss ppp
where ppp.pat_codigo=mm.pat_codigo
and ppp.pat_cobertura=mm.cobertura
and ppp.pat_grupo=mm.pat_grupo;

return query
select  cobertura,--porcentaje_ospim as porcentaje_ospim, --porc_cobertura
	patologia,/*ppp.descripcion as patologias,*/
	docu_tipo,--tipo documento
        docu_nro, --nro documento
        nomape,
        provincia,
        edad
from pato_temp
order by cobertura, edad,nomape;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
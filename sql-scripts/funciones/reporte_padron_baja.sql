CREATE OR REPLACE FUNCTION reporte_padron_baja(p_id_terc character varying, p_id_seccional character varying, p_fecha_alta_desde date, p_fecha_alta_hasta date, p_id_provincia character varying, p_id_localidad character varying, p_solotitular character varying, p_parentesco integer, p_cuit character varying, p_naci_fecha_desde date, p_naci_fecha_hasta date, p_plan_id character varying, p_aporte_id character varying, p_escala_salarial character varying, p_motivo_baja character varying)
  RETURNS SETOF reporte_padron_result AS
$BODY$
BEGIN
drop table if exists reporte_result;
drop table if exists aux_alta;

--BAJAS DE GRUPO
create temp table aux_alta (cuil_titular varchar);
/*select distinct cuil_titular 
from afiliado 
where baja_fecha>=p_fecha_alta_desde and baja_fecha<=p_fecha_alta_hasta
and baja_fecha is not null
and inte=0;*/
--AGREGAR BAJAS DE OSPIM
insert into aux_alta(cuil_titular)
select distinct cuil_titular
from afi_aportes a
where a.fecha_egre>=p_fecha_alta_desde and fecha_egre<=p_fecha_alta_hasta
and a.id_aporte in (select id_aporte from aporte where es_os is true)
and not exists (select 1 from afi_aportes aa
		where aa.fecha_ingre<=p_fecha_alta_hasta
		and aa.cuil_titular=a.cuil_titular
		and aa.inte=a.inte
		and (aa.fecha_egre is null or aa.fecha_egre>=p_fecha_alta_hasta)
		and aa.id_aporte in (select id_aporte from aporte where es_os is true)
		and aa.id<>a.id);



create table reporte_result as 

select case when (id_ospim_baja_fecha is null or id_ospim_baja_fecha>p_fecha_alta_hasta) then a.id_ospim else 0 end as id_ospim, 
       case when (id_amtima_baja_fecha is null or id_amtima_baja_fecha>p_fecha_alta_hasta) then a.id_amtima else 0 end as id_amtima,
       cast (a.alta_fecha as date) as alta_fecha,
       case when (a.aportante_titular=1 and a.inte <>0) then a.cuil_titular else 'no' end as unifica, 
	cast(s.id_seccional||' - ' ||s.descripcion as varchar) as seccional , 
	ate.id_tercerizadora, 
	a.cuil_titular, 
	a.cuil, 
	a.inte,
	a.id_parentesco_sss, 
	pa.descripcion as parentesco, 
	a.apellido, 
       a.nombre, 
       a.documento_tipo, 
       a.docu_numero, 
       a.naci_fecha, 
       a.sexo,
       a.id_estado_civil_sss, 
       ec.descripcion as civil_esta, 
       n.detalle as nacionalidad, 
       p.detalle as provincia, 
       l.detalle as localidad, 
       d.postal_codi, 
       d.calle, 
       d.numero, 
       d.piso, 
       d.depto, 
       cast(COALESCE(d.cod_area_telefono,'') || ' ' || COALESCE(d.telefono,'') || ' ' || 
       COALESCE(d.cod_area_tel_laboral,'') || ' ' || COALESCE(d.tel_laboral,'') || ' ' || 
       COALESCE(d.cod_area_celular,'') || ' ' || COALESCE(d.celular,'') as character varying) as telefono,
       a.email,
       cast('' as char(50)) as categoria, 
       cast('' as char(50)) as ramo, 
       0 as id_plan, 
       cast('' as char(50)) as plan, 
       a.ingre_fecha, 
       a.baja_fecha, 
       case when (id_uoma_baja_fecha is null or id_uoma_baja_fecha>p_fecha_alta_desde) then a.id_uoma else 0 end as id_uoma,
       cast('' as char(11)) as cuit, 
       cast('' as character varying(200)) as razon_soc,
       cast(null as date) as fecha_ospim,
       cast(null as varchar) as escala_salarial,
       cast('' as varchar) as plan_omint,
       a.discapacitado,
       cast(null as varchar) as motivo_baja,
       cast(null as date) as fpp 
	from afiliado a
	inner join parentesco_sss pa on a.id_parentesco_sss = pa.codigo
	inner join estado_civil_sss ec on a.id_estado_civil_sss = ec.codigo	
	inner join aux_alta x
	on x.cuil_titular=a.cuil_titular
	left outer join afi_tercerizadora_servicio ate
	on ate.cuil_titular=a.cuil_titular 
	and ate.inte= 0
	and ate.baja_fecha is null
	and  ate.fecha_inicio_pres=(select max(fecha_inicio_pres)
				  from afi_tercerizadora_servicio at2
				  where at2.cuil_titular=ate.cuil_titular
				  and at2.inte=ate.inte
				  and at2.baja_fecha is null
				  and at2.id_tercerizadora = ate.id_tercerizadora
				  and (at2.baja_fecha is null or at2.baja_fecha >= p_fecha_alta_desde))
	left outer join seccional s
	on a.id_seccional=s.id_seccional
	left outer join afi_domicilio d
	on a.cuil_titular=d.cuil_titular
	and d.inte  = 0 
	and d.id_domicilio=(select max(id_domicilio)
			   from afi_domicilio d2
			   where d.cuil_titular=d2.cuil_titular
			   and d.inte=d2.inte)
	left outer join nacionalidad n 
	on a.nacionalidad=n.id
	left outer join provincia p
	on d.provincia=p.id_provincia
	left outer join localidad l
	on d.localidad=l.id_localidad
	where (p_id_seccional is null or (p_id_seccional  is not null and a.id_seccional in ( select split_cadena(p_id_seccional , ','))))	
	and (p_id_provincia is null or (p_id_provincia is not null and d.provincia in ( select split_cadena(p_id_provincia, ','))))	
	and (p_id_localidad is null or (p_id_localidad is not null and D.LOCALIDAD in ( select split_cadena(p_id_localidad, ','))))		
	and (p_id_terc is null or (p_id_terc is not null and ate.id_tercerizadora in ( select split_cadena_str(p_id_terc, ','))));	
--UNION ALL BAJA DE INTEGRANTES
insert into reporte_result(id_ospim, id_amtima, alta_fecha,unifica, seccional, id_tercerizadora,cuil_titular,cuil,inte, id_parentesco_sss, parentesco,
			   apellido, nombre, documento_tipo, docu_numero,naci_fecha, sexo, id_estado_civil_sss, civil_esta, nacionalidad, provincia, 
			   localidad, postal_codi, calle,numero, piso, depto, telefono, email, categoria,ramo,id_plan, plan, ingre_fecha, 
			   baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, escala_salarial, plan_omint, discapacitado, motivo_baja, fpp)
select case when (id_ospim_baja_fecha is null or id_ospim_baja_fecha>p_fecha_alta_hasta) then a.id_ospim else 0 end as id_ospim, 
       case when (id_amtima_baja_fecha is null or id_amtima_baja_fecha>p_fecha_alta_hasta) then a.id_amtima else 0 end as id_amtima,
       cast (a.alta_fecha as date),
       case when (a.aportante_titular=1 and a.inte <>0) then a.cuil_titular else 'no' end as unifica, 
	cast(s.id_seccional||' - ' ||s.descripcion as varchar) as seccional , 
	ate.id_tercerizadora, 
	a.cuil_titular, 
	a.cuil, 
	a.inte,
	a.id_parentesco_sss, 
	pa.descripcion as parentesco, 
	a.apellido, 
       a.nombre, 
       a.documento_tipo, 
       a.docu_numero, 
       a.naci_fecha, 
       a.sexo,
       a.id_estado_civil_sss, 
       ec.descripcion as civil_esta, 
       n.detalle as nacionalidad, 
       p.detalle as provincia, 
       l.detalle as localidad, 
       d.postal_codi, 
       d.calle, 
       d.numero, 
       d.piso, 
       d.depto, 
       cast(COALESCE(d.cod_area_telefono,'') || ' ' || COALESCE(d.telefono,'') || ' ' || 
       COALESCE(d.cod_area_tel_laboral,'') || ' ' || COALESCE(d.tel_laboral,'') || ' ' || 
       COALESCE(d.cod_area_celular,'') || ' ' || COALESCE(d.celular,'') as character varying) as telefono,
       a.email,
       cast('' as char(50)) as categoria, 
       cast('' as char(50)) as ramo, 
       0 as id_plan, 
       cast('' as char(50)) as plan, 
       a.ingre_fecha, 
       a.baja_fecha, 
       case when (id_uoma_baja_fecha is null or id_uoma_baja_fecha>p_fecha_alta_desde) then a.id_uoma else 0 end as id_uoma,
       cast('' as char(11)) as cuit, 
       cast('' as character varying(200)) as razon_soc,
       cast(null as date) as fecha_ospim,
       cast(null as varchar) as escala_salarial,
       cast('' as varchar) as plan_omint,
       a.discapacitado,
       mb.descripcion as motivo_baja,
       cast(null as date) as fpp 
	from afiliado a	
	inner join motivo_baja mb
	on mb.id_motivo_baja=a.id_motivo_baja
	inner join parentesco pa on a.id_parentesco_sss = pa.codigo
	inner join estado_civil_sss ec on a.id_estado_civil_sss = ec.codigo
	left outer join afi_tercerizadora_servicio ate
	on ate.cuil_titular=a.cuil_titular 
	and ate.inte= 0
	and  ate.fecha_inicio_pres=(select max(fecha_inicio_pres)
				  from afi_tercerizadora_servicio at2
				  where at2.cuil_titular=ate.cuil_titular
				  and at2.inte=ate.inte
				  and at2.id_tercerizadora = ate.id_tercerizadora
				  and (at2.baja_fecha is null or at2.baja_fecha >= p_fecha_alta_desde))
	left outer join seccional s
	on a.id_seccional=s.id_seccional
	left outer join afi_domicilio d
	on a.cuil_titular=d.cuil_titular
	and d.inte  = 0 
	and d.id_domicilio=(select max(id_domicilio)
			   from afi_domicilio d2
			   where d.cuil_titular=d2.cuil_titular
			   and d.inte=d2.inte)
	left outer join nacionalidad n 
	on a.nacionalidad=n.id
	left outer join provincia p
	on d.provincia=p.id_provincia
	left outer join localidad l
	on d.localidad=l.id_localidad
	where a.inte<>0
	and a.baja_fecha>=p_fecha_alta_desde
	and a.baja_fecha<=p_fecha_alta_hasta
	and (p_id_seccional is null or (p_id_seccional  is not null and a.id_seccional in ( select split_cadena(p_id_seccional , ','))))	
	and (p_id_provincia is null or (p_id_provincia is not null and d.provincia in ( select split_cadena(p_id_provincia, ','))))	
	and (p_id_localidad is null or (p_id_localidad is not null and D.LOCALIDAD in ( select split_cadena(p_id_localidad, ','))))		
	and (p_id_terc is null or (p_id_terc is not null and ate.id_tercerizadora in ( select split_cadena_str(p_id_terc, ','))))
	and not exists (select 1 from reporte_result rr where rr.cuil_titular=a.cuil_titular and rr.inte=a.inte);	
	
	
-------------------------------------------------------------------------------
if p_aporte_id is not null then
	delete from reporte_result a
	where not exists (select 1 from afi_aportes apo
			where apo.cuil_titular=a.cuil_titular
			and apo.inte=0
			and fecha_egre>=p_fecha_alta_desde 
			and fecha_egre<=p_fecha_alta_hasta
			and fecha_ingre<>fecha_egre 
			and baja_fecha is null
			and apo.id_aporte in (select split_cadena(p_aporte_id, ','))	
			and (p_motivo_baja is null or (p_motivo_baja is not null and id_motivo_baja in (select split_cadena(p_motivo_baja, ',')))));
			
end if;				
	
if p_motivo_baja is not null and p_aporte_id is null then
delete from reporte_result a
	where not exists (select 1 from afi_aportes apo
			where apo.cuil_titular=a.cuil_titular
			and apo.inte=0
			and fecha_egre>=p_fecha_alta_desde 
			and fecha_egre<=p_fecha_alta_hasta
			and fecha_ingre<>fecha_egre 
			and baja_fecha is null
			and (p_motivo_baja is null or (p_motivo_baja is not null and apo.id_motivo_baja in (select split_cadena(p_motivo_baja, ',')))));
end if;		

update reporte_result r
set motivo_baja=mb.descripcion
from afi_aportes a, motivo_baja mb
where a.cuil_titular=r.cuil_titular
and a.inte=0
and mb.id_motivo_baja=a.id_motivo_baja
and a.fecha_egre>=p_fecha_alta_desde 
and a.fecha_egre<=p_fecha_alta_hasta
and a.fecha_ingre<>a.fecha_egre 
and a.baja_fecha is null
and (p_motivo_baja is null or (p_motivo_baja is not null and a.id_motivo_baja in (select split_cadena(p_motivo_baja, ','))));



update reporte_result a
set categoria=c.categoria,
    cuit=al.cuit,    
    escala_salarial=al.escala_salarial
from afi_situ_laboral al, categoria_laboral c
where al.cuil_titular=a.cuil_titular
and al.cuil_titular=a.cuil_titular 
and al.inte=0
and al.id_categoria=c.id_categoria
and al.fecha_ingre=(select max(fecha_ingre)
                    from afi_situ_laboral al2
		    where al2.cuil_titular=al.cuil_titular
		    and al2.inte=al.inte
		    and (al2.baja_fecha is null or al2.baja_fecha >= p_fecha_alta_desde));

update reporte_result a
set razon_soc=e.razon_soc,
    ramo=case when id_ramo_empresa is null then 0 else id_ramo_empresa end
from empresa e
where e.cuit=a.cuit
and e.sucursal='000';

update reporte_result 
set razon_soc=apellido||', '||nombre
where (razon_soc is null or rtrim(razon_soc)='')
and cuit=cuil_titular;


update reporte_result a
set id_plan=pl.id_plan,
    plan=pl.descripcion
from afi_plan ap, plan pl
where ap.cuil_titular=a.cuil_titular 
and ap.inte=0
and pl.id_plan=ap.id_plan
and ap.alta_fecha=(select max(alta_fecha)
 		    from afi_plan app
	            where app.cuil_titular=ap.cuil_titular
		    and app.inte=ap.inte
		    and app.baja_fecha is null
		    and (app.vigen_hasta is null or (app.vigen_hasta >= p_fecha_alta_desde)))
and ap.baja_fecha is null		    
and (ap.vigen_hasta is null or ap.vigen_hasta>=p_fecha_alta_desde);

update reporte_result a
set plan_omint=po.descripcion
from afi_plan ap, plan_omint po
where ap.cuil_titular=a.cuil_titular 
and ap.inte=0
and ap.id_plan_omint=po.id_plan_omint
and ap.alta_fecha=(select max(alta_fecha)
 		    from afi_plan app
	            where app.cuil_titular=ap.cuil_titular
		    and app.inte=ap.inte);

update reporte_result a
set fecha_ospim=aa.fecha_ingre    
from afi_aportes aa
where aa.cuil_titular=a.cuil_titular
and aa.id_aporte in (select id_aporte from aporte where es_os=true)
and aa.fecha_ingre=(select max (fecha_ingre)
		   from afi_aportes aaa		   
		   where aaa.cuil_titular=aa.cuil_titular
		   and fecha_egre is not null 
		   and fecha_egre>=p_fecha_alta_desde and fecha_egre<=p_fecha_alta_hasta 
		   and (baja_fecha is null)
		   and aaa.id_aporte in (select id_aporte from aporte where es_os=true));

update reporte_result a
set baja_fecha=aa.fecha_egre
from afi_aportes aa
where aa.cuil_titular=a.cuil_titular
and aa.id_aporte in (select id_aporte from aporte where es_os=true)
and fecha_egre=(select max(fecha_egre) from afi_aportes aab 
				   where aab.cuil_titular=aa.cuil_titular
				   and aab.fecha_egre is not null 
				   and aab.fecha_egre>=p_fecha_alta_desde and aab.fecha_egre<=p_fecha_alta_hasta 
				   and (aab.baja_fecha is null)
				   and aab.id_aporte in (select id_aporte from aporte where es_os=true));	   

if (p_soloTitular = '0') then
	return query 	select * from reporte_result a
	WHERE (p_cuit is null or (p_cuit is not null and cuit = p_cuit))
	AND (p_naci_fecha_desde is null or (p_naci_fecha_desde is not null and NACI_FECHA >= p_naci_fecha_desde))
	AND (p_naci_fecha_hasta is null or (p_naci_fecha_hasta is not null and NACI_FECHA <= p_naci_fecha_hasta))
	AND (p_parentesco is null or (p_parentesco is not null and id_parentesco_sss = p_parentesco))
	and (p_plan_id is null or (p_plan_id is not null and id_plan in ( select split_cadena(p_plan_id, ','))))	
	and a.baja_fecha=(select max(baja_fecha) from reporte_result b
			  where a.cuil_titular=b.cuil_titular
			  and a.inte=b.inte)
	order by cuil_titular;
else 
	if (p_soloTitular = '1') then
		return query select * from reporte_result a
		WHERE (p_cuit is null or (p_cuit is not null and cuit = p_cuit))
		AND (p_naci_fecha_desde is null or (p_naci_fecha_desde is not null and NACI_FECHA >= p_naci_fecha_desde))
		AND (p_naci_fecha_hasta is null or (p_naci_fecha_hasta is not null and NACI_FECHA <= p_naci_fecha_hasta))
		AND (p_parentesco is null or (p_parentesco is not null and id_parentesco_sss = p_parentesco))
		and (p_plan_id is null or (p_plan_id is not null and id_plan in ( select split_cadena(p_plan_id, ','))))		
		AND inte = 0
		and a.baja_fecha=(select max(baja_fecha) from reporte_result b
			  where a.cuil_titular=b.cuil_titular
			  and a.inte=b.inte)
		order by cuil_titular;
	else 
		--DEBEMOS AGREGAR A LOS INTEGRANTES
		
		return query select * from reporte_result a
		WHERE (p_cuit is null or (p_cuit is not null and cuit = p_cuit))
		AND (p_naci_fecha_desde is null or (p_naci_fecha_desde is not null and NACI_FECHA >= p_naci_fecha_desde))
		AND (p_naci_fecha_hasta is null or (p_naci_fecha_hasta is not null and NACI_FECHA <= p_naci_fecha_hasta))
		AND (p_parentesco is null or (p_parentesco is not null and id_parentesco_sss = p_parentesco))
		and (p_plan_id is null or (p_plan_id is not null and id_plan in (select split_cadena(p_plan_id, ','))))		
		AND inte <> 0
		and a.baja_fecha=(select max(baja_fecha) from reporte_result b
			  where a.cuil_titular=b.cuil_titular
			  and a.inte=b.inte)
		order by cuil_titular;
	end if;
end if;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
  
  
 CREATE OR REPLACE FUNCTION reporte_padron_baja_fecha_proceso(p_id_terc character varying, p_id_seccional character varying, p_fecha_alta_desde date, p_fecha_alta_hasta date, p_id_provincia character varying, p_id_localidad character varying, p_solotitular character varying, p_parentesco integer, p_cuit character varying, p_naci_fecha_desde date, p_naci_fecha_hasta date, p_plan_id character varying, p_aporte_id character varying, p_escala_salarial character varying, p_motivo_baja character varying)
  RETURNS SETOF reporte_padron_result AS
$BODY$
BEGIN
drop table if exists reporte_result;
drop table if exists aux_alta;

--BAJAS DE GRUPO
create temp table aux_alta (cuil_titular varchar);
/*select distinct cuil_titular 
from afiliado 
where baja_fecha>=p_fecha_alta_desde and baja_fecha<=p_fecha_alta_hasta
and baja_fecha is not null
and inte=0;*/
--AGREGAR BAJAS DE OSPIM
insert into aux_alta(cuil_titular)
select distinct cuil_titular
from afi_aportes a
where a.modi_fecha>=p_fecha_alta_desde and a.modi_fecha<=p_fecha_alta_hasta
and a.fecha_egre<=p_fecha_alta_hasta
and trim(a.modi_usr)<>'corrAfi062014'
and a.id_aporte in (select id_aporte from aporte where es_os is true)
and not exists (select 1 from afi_aportes aa
		where aa.fecha_ingre<=p_fecha_alta_hasta
		and aa.cuil_titular=a.cuil_titular
		and aa.inte=a.inte
		and (aa.fecha_egre is null or aa.fecha_egre>=p_fecha_alta_hasta)
		and aa.id_aporte in (select id_aporte from aporte where es_os is true)
		and aa.id<>a.id);



create table reporte_result as 

select case when (id_ospim_baja_fecha is null or id_ospim_baja_fecha>p_fecha_alta_hasta) then a.id_ospim else 0 end as id_ospim, 
       case when (id_amtima_baja_fecha is null or id_amtima_baja_fecha>p_fecha_alta_hasta) then a.id_amtima else 0 end as id_amtima,
       cast (a.alta_fecha as date) as alta_fecha,
       case when (a.aportante_titular=1 and a.inte <>0) then a.cuil_titular else 'no' end as unifica, 
	cast(s.id_seccional||' - ' ||s.descripcion as varchar) as seccional , 
	ate.id_tercerizadora, 
	a.cuil_titular, 
	a.cuil, 
	a.inte, 
	a.id_parentesco_sss, 
	pa.descripcion as parentesco, 
	a.apellido, 
       a.nombre, 
       a.documento_tipo, 
       a.docu_numero, 
       a.naci_fecha, 
       a.sexo, 
       a.id_estado_civil_sss,
       ec.descripcion as civil_esta, 
       n.detalle as nacionalidad, 
       p.detalle as provincia, 
       l.detalle as localidad, 
       d.postal_codi, 
       d.calle, 
       d.numero, 
       d.piso, 
       d.depto, 
       cast(COALESCE(d.cod_area_telefono,'') || ' ' || COALESCE(d.telefono,'') || ' ' || 
       COALESCE(d.cod_area_tel_laboral,'') || ' ' || COALESCE(d.tel_laboral,'') || ' ' || 
       COALESCE(d.cod_area_celular,'') || ' ' || COALESCE(d.celular,'') as character varying) as telefono,
       a.email,
       cast('' as char(50)) as categoria, 
       cast('' as char(50)) as ramo, 
       0 as id_plan, 
       cast('' as char(50)) as plan, 
       a.ingre_fecha, 
       a.baja_fecha, 
       case when (id_uoma_baja_fecha is null or id_uoma_baja_fecha>p_fecha_alta_desde) then a.id_uoma else 0 end as id_uoma,
       cast('' as char(11)) as cuit, 
       cast('' as character varying(200)) as razon_soc,
       cast(null as date) as fecha_ospim,
       cast(null as varchar) as escala_salarial,
       cast('' as varchar) as plan_omint,
       a.discapacitado,
       cast(null as varchar) as motivo_baja,
       cast(null as date) as fpp 
	from afiliado a	
	inner join parentesco_sss pa on a.id_parentesco_sss = c.codigo
	inner join estado_civil_sss ec on a.id_estado_civil_sss = ec.codigo
	inner join aux_alta x
	on x.cuil_titular=a.cuil_titular
	left outer join afi_tercerizadora_servicio ate
	on ate.cuil_titular=a.cuil_titular 
	and ate.inte= 0
	and ate.baja_fecha is null
	and  ate.fecha_inicio_pres=(select max(fecha_inicio_pres)
				  from afi_tercerizadora_servicio at2
				  where at2.cuil_titular=ate.cuil_titular
				  and at2.inte=ate.inte
				  and at2.baja_fecha is null
				  and at2.id_tercerizadora = ate.id_tercerizadora
				  and (at2.baja_fecha is null or at2.baja_fecha >= p_fecha_alta_desde))
	left outer join seccional s
	on a.id_seccional=s.id_seccional
	left outer join afi_domicilio d
	on a.cuil_titular=d.cuil_titular
	and d.inte  = 0 
	and d.id_domicilio=(select max(id_domicilio)
			   from afi_domicilio d2
			   where d.cuil_titular=d2.cuil_titular
			   and d.inte=d2.inte)
	left outer join nacionalidad n 
	on a.nacionalidad=n.id
	left outer join provincia p
	on d.provincia=p.id_provincia
	left outer join localidad l
	on d.localidad=l.id_localidad
	where (p_id_seccional is null or (p_id_seccional  is not null and a.id_seccional in ( select split_cadena(p_id_seccional , ','))))	
	and (p_id_provincia is null or (p_id_provincia is not null and d.provincia in ( select split_cadena(p_id_provincia, ','))))	
	and (p_id_localidad is null or (p_id_localidad is not null and D.LOCALIDAD in ( select split_cadena(p_id_localidad, ','))))		
	and (p_id_terc is null or (p_id_terc is not null and ate.id_tercerizadora in ( select split_cadena_str(p_id_terc, ','))));	
--UNION ALL BAJA DE INTEGRANTES
insert into reporte_result(id_ospim, id_amtima, alta_fecha,unifica, seccional, id_tercerizadora,cuil_titular,cuil,inte,id_parentesco_sss, parentesco,
			   apellido, nombre, documento_tipo, docu_numero,naci_fecha, sexo, id_estado_civil_sss, civil_esta, nacionalidad, provincia, 
			   localidad, postal_codi, calle,numero, piso, depto, telefono, email, categoria,ramo,id_plan, plan, ingre_fecha, 
			   baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, escala_salarial, plan_omint, discapacitado, motivo_baja, fpp)
select case when (id_ospim_baja_fecha is null or id_ospim_baja_fecha>p_fecha_alta_hasta) then a.id_ospim else 0 end as id_ospim, 
       case when (id_amtima_baja_fecha is null or id_amtima_baja_fecha>p_fecha_alta_hasta) then a.id_amtima else 0 end as id_amtima,
       cast (a.alta_fecha as date),
       case when (a.aportante_titular=1 and a.inte <>0) then a.cuil_titular else 'no' end as unifica, 
	cast(s.id_seccional||' - ' ||s.descripcion as varchar) as seccional , 
	ate.id_tercerizadora, 
	a.cuil_titular, 
	a.cuil, 
	a.inte, 
	a.id_parentesco_sss,
	pa.descripcion as parentesco, 
	a.apellido, 
       a.nombre, 
       a.documento_tipo, 
       a.docu_numero, 
       a.naci_fecha, 
       a.sexo,
       a.is_estado_civil_sss, 
       ec.descripcion as civil_esta, 
       n.detalle as nacionalidad, 
       p.detalle as provincia, 
       l.detalle as localidad, 
       d.postal_codi, 
       d.calle, 
       d.numero, 
       d.piso, 
       d.depto, 
       cast(COALESCE(d.cod_area_telefono,'') || ' ' || COALESCE(d.telefono,'') || ' ' || 
       COALESCE(d.cod_area_tel_laboral,'') || ' ' || COALESCE(d.tel_laboral,'') || ' ' || 
       COALESCE(d.cod_area_celular,'') || ' ' || COALESCE(d.celular,'') as character varying) as telefono,
       a.email,
       cast('' as char(50)) as categoria, 
       cast('' as char(50)) as ramo, 
       0 as id_plan, 
       cast('' as char(50)) as plan, 
       a.ingre_fecha, 
       a.baja_fecha, 
       case when (id_uoma_baja_fecha is null or id_uoma_baja_fecha>p_fecha_alta_desde) then a.id_uoma else 0 end as id_uoma,
       cast('' as char(11)) as cuit, 
       cast('' as character varying(200)) as razon_soc,
       cast(null as date) as fecha_ospim,
       cast(null as varchar) as escala_salarial,
       cast('' as varchar) as plan_omint,
       a.discapacitado,
       mb.descripcion as motivo_baja,
       cast(null as date) as fpp 
	from afiliado a	
	inner join parentesco_sss pa on a.id_parentesco_sss = pa.codigo
	inner join estado_civil_sss ec on a.id_estado_civil_sss = ec.codigo
	inner join motivo_baja mb
	on mb.id_motivo_baja=a.id_motivo_baja
	left outer join afi_tercerizadora_servicio ate
	on ate.cuil_titular=a.cuil_titular 
	and ate.inte= 0
	and  ate.fecha_inicio_pres=(select max(fecha_inicio_pres)
				  from afi_tercerizadora_servicio at2
				  where at2.cuil_titular=ate.cuil_titular
				  and at2.inte=ate.inte
				  and at2.id_tercerizadora = ate.id_tercerizadora
				  and (at2.baja_fecha is null or at2.baja_fecha >= p_fecha_alta_desde))
	left outer join seccional s
	on a.id_seccional=s.id_seccional
	left outer join afi_domicilio d
	on a.cuil_titular=d.cuil_titular
	and d.inte  = 0 
	and d.id_domicilio=(select max(id_domicilio)
			   from afi_domicilio d2
			   where d.cuil_titular=d2.cuil_titular
			   and d.inte=d2.inte)
	left outer join nacionalidad n 
	on a.nacionalidad=n.id
	left outer join provincia p
	on d.provincia=p.id_provincia
	left outer join localidad l
	on d.localidad=l.id_localidad
	where a.inte<>0	
	and a.baja_fecha<=p_fecha_alta_hasta
	and a.modi_fecha>=p_fecha_alta_desde
	and a.modi_fecha<=p_fecha_alta_hasta
	and (p_id_seccional is null or (p_id_seccional  is not null and a.id_seccional in ( select split_cadena(p_id_seccional , ','))))	
	and (p_id_provincia is null or (p_id_provincia is not null and d.provincia in ( select split_cadena(p_id_provincia, ','))))	
	and (p_id_localidad is null or (p_id_localidad is not null and D.LOCALIDAD in ( select split_cadena(p_id_localidad, ','))))		
	and (p_id_terc is null or (p_id_terc is not null and ate.id_tercerizadora in ( select split_cadena_str(p_id_terc, ','))))
	and not exists (select 1 from reporte_result rr where rr.cuil_titular=a.cuil_titular and rr.inte=a.inte);	
	
	
-------------------------------------------------------------------------------
if p_aporte_id is not null then
	delete from reporte_result a
	where not exists (select 1 from afi_aportes apo
			where apo.cuil_titular=a.cuil_titular
			and apo.inte=0
			and fecha_egre>=p_fecha_alta_desde 
			and fecha_egre<=p_fecha_alta_hasta
			and fecha_ingre<>fecha_egre 
			and baja_fecha is null
			and apo.id_aporte in (select split_cadena(p_aporte_id, ','))	
			and (p_motivo_baja is null or (p_motivo_baja is not null and id_motivo_baja in (select split_cadena(p_motivo_baja, ',')))));
			
end if;				
	
if p_motivo_baja is not null and p_aporte_id is null then
delete from reporte_result a
	where not exists (select 1 from afi_aportes apo
			where apo.cuil_titular=a.cuil_titular
			and apo.inte=0
			and fecha_egre>=p_fecha_alta_desde 
			and fecha_egre<=p_fecha_alta_hasta
			and fecha_ingre<>fecha_egre 
			and baja_fecha is null
			and (p_motivo_baja is null or (p_motivo_baja is not null and apo.id_motivo_baja in (select split_cadena(p_motivo_baja, ',')))));
end if;		

update reporte_result r
set motivo_baja=mb.descripcion
from afi_aportes a, motivo_baja mb
where a.cuil_titular=r.cuil_titular
and a.inte=0
and mb.id_motivo_baja=a.id_motivo_baja
and a.modi_fecha>=p_fecha_alta_desde 
and a.modi_fecha<=p_fecha_alta_hasta
and a.fecha_egre<=p_fecha_alta_hasta
and a.fecha_ingre<>a.fecha_egre 
and a.baja_fecha is null
and (p_motivo_baja is null or (p_motivo_baja is not null and a.id_motivo_baja in (select split_cadena(p_motivo_baja, ','))));



update reporte_result a
set categoria=c.categoria,
    cuit=al.cuit,    
    escala_salarial=al.escala_salarial
from afi_situ_laboral al, categoria_laboral c
where al.cuil_titular=a.cuil_titular
and al.cuil_titular=a.cuil_titular 
and al.inte=0
and al.id_categoria=c.id_categoria
and al.fecha_ingre=(select max(fecha_ingre)
                    from afi_situ_laboral al2
		    where al2.cuil_titular=al.cuil_titular
		    and al2.inte=al.inte
		    and (al2.baja_fecha is null or al2.baja_fecha >= p_fecha_alta_desde));

update reporte_result a
set razon_soc=e.razon_soc,
    ramo=case when id_ramo_empresa is null then 0 else id_ramo_empresa end
from empresa e
where e.cuit=a.cuit
and e.sucursal='000';

update reporte_result 
set razon_soc=apellido||', '||nombre
where (razon_soc is null or rtrim(razon_soc)='')
and cuit=cuil_titular;


update reporte_result a
set id_plan=pl.id_plan,
    plan=pl.descripcion
from afi_plan ap, plan pl
where ap.cuil_titular=a.cuil_titular 
and ap.inte=0
and pl.id_plan=ap.id_plan
and ap.alta_fecha=(select max(alta_fecha)
 		    from afi_plan app
	            where app.cuil_titular=ap.cuil_titular
		    and app.inte=ap.inte
		    and app.baja_fecha is null
		    and (app.vigen_hasta is null or (app.vigen_hasta >= p_fecha_alta_desde)))
and ap.baja_fecha is null		    
and (ap.vigen_hasta is null or ap.vigen_hasta>=p_fecha_alta_desde);

update reporte_result a
set plan_omint=po.descripcion
from afi_plan ap, plan_omint po
where ap.cuil_titular=a.cuil_titular 
and ap.inte=0
and ap.id_plan_omint=po.id_plan_omint
and ap.alta_fecha=(select max(alta_fecha)
 		    from afi_plan app
	            where app.cuil_titular=ap.cuil_titular
		    and app.inte=ap.inte);

update reporte_result a
set fecha_ospim=aa.fecha_ingre    
from afi_aportes aa
where aa.cuil_titular=a.cuil_titular
and aa.id_aporte in (select id_aporte from aporte where es_os=true)
and aa.fecha_ingre=(select max (fecha_ingre)
		   from afi_aportes aaa		   
		   where aaa.cuil_titular=aa.cuil_titular
		   and fecha_egre is not null 
		   and fecha_egre>=p_fecha_alta_desde and fecha_egre<=p_fecha_alta_hasta 
		   and (baja_fecha is null)
		   and aaa.id_aporte in (select id_aporte from aporte where es_os=true));

update reporte_result a
set baja_fecha=aa.fecha_egre
from afi_aportes aa
where aa.cuil_titular=a.cuil_titular
and aa.id_aporte in (select id_aporte from aporte where es_os=true)
and fecha_egre=(select max(fecha_egre) from afi_aportes aab 
				   where aab.cuil_titular=aa.cuil_titular
				   and aab.fecha_egre is not null 
				   and aab.fecha_egre>=p_fecha_alta_desde and aab.fecha_egre<=p_fecha_alta_hasta 
				   and (aab.baja_fecha is null)
				   and aab.id_aporte in (select id_aporte from aporte where es_os=true));	   

if (p_soloTitular = '0') then
	return query 	select * from reporte_result a
	WHERE (p_cuit is null or (p_cuit is not null and cuit = p_cuit))
	AND (p_naci_fecha_desde is null or (p_naci_fecha_desde is not null and NACI_FECHA >= p_naci_fecha_desde))
	AND (p_naci_fecha_hasta is null or (p_naci_fecha_hasta is not null and NACI_FECHA <= p_naci_fecha_hasta))
	AND (p_parentesco is null or (p_parentesco is not null and id_parentesco_sss = p_parentesco))
	and (p_plan_id is null or (p_plan_id is not null and id_plan in ( select split_cadena(p_plan_id, ','))))	
	and a.baja_fecha=(select max(baja_fecha) from reporte_result b
			  where a.cuil_titular=b.cuil_titular
			  and a.inte=b.inte)
	order by cuil_titular;
else 
	if (p_soloTitular = '1') then
		return query select * from reporte_result a
		WHERE (p_cuit is null or (p_cuit is not null and cuit = p_cuit))
		AND (p_naci_fecha_desde is null or (p_naci_fecha_desde is not null and NACI_FECHA >= p_naci_fecha_desde))
		AND (p_naci_fecha_hasta is null or (p_naci_fecha_hasta is not null and NACI_FECHA <= p_naci_fecha_hasta))
		AND (p_parentesco is null or (p_parentesco is not null and id_parentesco_sss = p_parentesco))
		and (p_plan_id is null or (p_plan_id is not null and id_plan in ( select split_cadena(p_plan_id, ','))))		
		AND inte = 0
		and a.baja_fecha=(select max(baja_fecha) from reporte_result b
			  where a.cuil_titular=b.cuil_titular
			  and a.inte=b.inte)
		order by cuil_titular;
	else 
		--DEBEMOS AGREGAR A LOS INTEGRANTES
		
		return query select * from reporte_result a
		WHERE (p_cuit is null or (p_cuit is not null and cuit = p_cuit))
		AND (p_naci_fecha_desde is null or (p_naci_fecha_desde is not null and NACI_FECHA >= p_naci_fecha_desde))
		AND (p_naci_fecha_hasta is null or (p_naci_fecha_hasta is not null and NACI_FECHA <= p_naci_fecha_hasta))
		AND (p_parentesco is null or (p_parentesco is not null and id_parentesco_sss = p_parentesco))
		and (p_plan_id is null or (p_plan_id is not null and id_plan in (select split_cadena(p_plan_id, ','))))		
		AND inte <> 0
		and a.baja_fecha=(select max(baja_fecha) from reporte_result b
			  where a.cuil_titular=b.cuil_titular
			  and a.inte=b.inte)
		order by cuil_titular;
	end if;
end if;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000; 
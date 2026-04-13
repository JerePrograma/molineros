CREATE TYPE reporte_padron_result AS
   (id_ospim integer,
    id_amtima integer,
    alta_fecha date,
    unifica character varying,
    seccional character varying,
    id_tercerizadora character varying(3),
    cuil_titular character varying(13),
    cuil character varying(13),
    inte integer,
    id_parentesco_sss integer,	
    parentesco character varying(100),
    apellido character varying(100),
    nombre character varying(100),
    documento_tipo character varying(4),
    docu_numero character varying(15),
    naci_fecha date,
    sexo character varying(2),
    id_estado_civil_sss integer,
    civil_esta character varying(20),
    nacionalidad character varying,
    provincia character varying,
    localidad character varying,
    postal_codi character varying(4),
    calle character varying(100),
    numero character varying,
    piso character varying(5),
    depto character varying(4),
    telefono character varying,
    email character varying(50),
    categoria character(50),
    ramo character(50),
    id_plan integer,
    plan character(50),
    ingre_fecha date,
    baja_fecha timestamp without time zone,
    id_uoma integer,
    cuit character(11),
    razon_soc character varying(200),
    fecha_ospim date,
    escala_salarial character varying,
    plan_omint character varying,
    discapacitado character varying,
    motivo_baja character varying,
    fpp date);
    
 CREATE OR REPLACE FUNCTION reporte_padron(p_id_terc character varying, p_id_seccional character varying, p_fecha_vigen_desde date, p_fecha_vigen_hasta date, p_id_provincia character varying, p_id_localidad character varying, p_solotitular character varying, p_parentesco integer, p_cuit character varying, p_naci_fecha_desde date, p_naci_fecha_hasta date, p_plan_id character varying, p_aporte_id character varying, p_escala_salarial character varying)
  RETURNS SETOF reporte_padron_result AS
$BODY$
BEGIN
drop table if exists reporte_result;
create table reporte_result as 

select a.id_ospim, 
       a.id_amtima,
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
       a.id_uoma,
       cast('' as char(11)) as cuit, 
       cast('' as character varying(200)) as razon_soc,
       cast(null as date) as fecha_ospim,
       cast(null as varchar) as escala_salarial,
       cast('' as varchar) as plan_omint,
       a.discapacitado,
       cast(null as date) as fpp,
       cast(null as varchar) as incognita       
	from afiliado a
	inner join parentesco_sss pa on a.id_parentesco_sss = pa.codigo
	inner join estado_civil_sss ec on a.id_estado_civil_sss = ec.codigo	
	left outer join afi_tercerizadora_servicio ate
	on ate.cuil_titular=a.cuil_titular 
	and ate.inte= 0
	and ate.baja_fecha is null
	and  ate.fecha_inicio_pres=(select max(fecha_inicio_pres)
				  from afi_tercerizadora_servicio at2
				  where at2.cuil_titular=ate.cuil_titular
				  and at2.inte=ate.inte
				  and at2.baja_fecha is null
				  --and at2.id_tercerizadora = ate.id_tercerizadora				  
				  and (fecha_fin_pres is null or fecha_fin_pres> cast(p_fecha_vigen_desde as Date)+Interval '1 day'))
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
	and (p_id_terc is null or (p_id_terc is not null and ate.id_tercerizadora in ( select split_cadena_str(p_id_terc, ','))))	
	and a.vigen_fecha <= p_fecha_vigen_hasta
	and (a.baja_fecha is null or a.baja_fecha >= p_fecha_vigen_desde);
	
	

-------------------------------------------------------------------------------
if p_aporte_id is not null then
	delete from reporte_result a
	where not exists (select 1 from afi_aportes apo
			where apo.cuil_titular=a.cuil_titular
			and apo.inte=0
			and (apo.fecha_egre is null or apo.fecha_egre>p_fecha_vigen_hasta)
			and (apo.baja_fecha is null or apo.baja_fecha>p_fecha_vigen_hasta)
			and apo.id_aporte in (select split_cadena(p_aporte_id, ',')));
end if;									 


update reporte_result a
set categoria=c.categoria,
    cuit=al.cuit,    
    escala_salarial=al.escala_salarial
from afi_situ_laboral al, categoria_laboral c
where al.cuil_titular=a.cuil_titular
and al.cuil_titular=a.cuil_titular 
and al.inte=0
and al.id_categoria=c.id_categoria
and al.baja_fecha is null
and al.fecha_ingre=(select max(fecha_ingre)
                    from afi_situ_laboral al2
		    where al2.cuil_titular=al.cuil_titular
		    and al2.inte=al.inte
		    and al2.baja_fecha is null
		    and (al2.fecha_egre is null or fecha_egre>=p_fecha_vigen_desde)); 


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
    plan=pl.descripcion,
    id_ospim=case when (ap.id_plan in (select id_plan from plan where ospim is true) and vigen_desde<=current_date+Interval '1 day') then id_ospim else 0 end,
    id_amtima=case when (ap.id_plan in (select id_plan from plan where amtima is true) and vigen_desde<=current_date+Interval '1 day') then id_amtima else 0 end,
    id_uoma=case when (ap.id_plan in (select id_plan from plan where uoma is true) and vigen_desde<=current_date+Interval '1 day') then id_uoma else 0 end        
from afi_plan ap, plan pl
where ap.cuil_titular=a.cuil_titular 
and ap.inte=0
and pl.id_plan=ap.id_plan
and ap.alta_fecha=(select max(alta_fecha)
 		    from afi_plan app
	            where app.cuil_titular=ap.cuil_titular
		    and app.inte=ap.inte
		    and app.baja_fecha is null
		    and (app.vigen_hasta is null or (app.vigen_hasta>= p_fecha_vigen_desde)))
and ap.baja_fecha is null		    
and (ap.vigen_hasta is null or ap.vigen_hasta>=p_fecha_vigen_desde);

update reporte_result a
set plan_omint=po.descripcion
from afi_plan ap, plan_omint po
where ap.cuil_titular=a.cuil_titular 
and ap.inte=0
and ap.id_plan_omint=po.id_plan_omint
and ap.alta_fecha=(select max(alta_fecha)
 		    from afi_plan app
	            where app.cuil_titular=ap.cuil_titular
		    and app.inte=ap.inte
		    and app.baja_fecha is null
		    and (app.vigen_hasta is null or (app.vigen_hasta >= p_fecha_vigen_desde)))
and ap.baja_fecha is null		    
and (ap.vigen_hasta is null or ap.vigen_hasta>=p_fecha_vigen_desde);

update reporte_result a
set fecha_ospim=aa.fecha_ingre
from afi_aportes aa
where aa.cuil_titular=a.cuil_titular
and aa.id_aporte in (select id_aporte from aporte where es_os=true)
and aa.fecha_ingre=(select max (fecha_ingre)
		   from afi_aportes aaa		   
		   where aaa.cuil_titular=aa.cuil_titular
		   and (fecha_egre is null or (fecha_egre>=p_fecha_vigen_desde ))
		   and (baja_fecha is null or (baja_fecha>=p_fecha_vigen_desde ))
		   and aaa.id_aporte in (select id_aporte from aporte where es_os=true));

update reporte_result a
set fpp=fecha_vto+ Interval '1 month'
from afi_documento d
where a.cuil_titular=d.cuil_titular
and a.inte=d.inte
and current_date<=fecha_vto+Interval '1 month'
and id_documento=12;		   

if (p_soloTitular = '0') then
	return query 	
	select  id_ospim, 
	        id_amtima,
		alta_fecha,
		unifica, 
		seccional , 
		id_tercerizadora, 
		cuil_titular, 
		cuil, 
		inte, 
		id_parentesco_sss,
		parentesco, 
		apellido, 
		nombre, 
		documento_tipo, 
		docu_numero, 
		naci_fecha, 
		sexo,
		id_estado_civil_sss, 
		civil_esta, 
		nacionalidad, 
		provincia, 
		localidad, 
		postal_codi, 
		calle, 
		numero, 
		piso, 
		depto, 
		telefono,
		email,
		categoria, 
		ramo, 
		id_plan, 
		plan, 
		ingre_fecha, 
		baja_fecha, 
		id_uoma,
		cuit, 
		razon_soc,
		fecha_ospim,
		escala_salarial,
		plan_omint,
		discapacitado,
		incognita,
		fpp
	from reporte_result a
	WHERE (p_cuit is null or (p_cuit is not null and cuit = p_cuit))
	AND (p_naci_fecha_desde is null or (p_naci_fecha_desde is not null and NACI_FECHA >= p_naci_fecha_desde))
	AND (p_naci_fecha_hasta is null or (p_naci_fecha_hasta is not null and NACI_FECHA <= p_naci_fecha_hasta))
--	AND (p_parentesco is null or (p_parentesco is not null and id_parentesco_sss = p_parentesco))
	AND (p_parentesco is null or (p_parentesco is not null and id_parentesco_sss = p_parentesco))
	and (p_plan_id is null or (p_plan_id is not null and id_plan in ( select split_cadena(p_plan_id, ','))))	
	order by cuil_titular;
else 
	if (p_soloTitular = '1') then
		return query 
		select id_ospim, 
	        id_amtima,
		alta_fecha,
		unifica, 
		seccional , 
		id_tercerizadora, 
		cuil_titular, 
		cuil, 
		inte,
		id_parentesco_sss, 
		parentesco, 
		apellido, 
		nombre, 
		documento_tipo, 
		docu_numero, 
		naci_fecha, 
		sexo,
		id_estado_civil_sss, 
		civil_esta, 
		nacionalidad, 
		provincia, 
		localidad, 
		postal_codi, 
		calle, 
		numero, 
		piso, 
		depto, 
		telefono,
		email,
		categoria, 
		ramo, 
		id_plan, 
		plan, 
		ingre_fecha, 
		baja_fecha, 
		id_uoma,
		cuit, 
		razon_soc,
		fecha_ospim,
		escala_salarial,
		plan_omint,
		discapacitado,
		incognita,
		fpp 
		from reporte_result a
		WHERE (p_cuit is null or (p_cuit is not null and cuit = p_cuit))
		AND (p_naci_fecha_desde is null or (p_naci_fecha_desde is not null and NACI_FECHA >= p_naci_fecha_desde))
		AND (p_naci_fecha_hasta is null or (p_naci_fecha_hasta is not null and NACI_FECHA <= p_naci_fecha_hasta))
		AND (p_parentesco is null or (p_parentesco is not null and id_parentesco_sss = p_parentesco))
		and (p_plan_id is null or (p_plan_id is not null and id_plan in ( select split_cadena(p_plan_id, ','))))		
		AND inte = 0
		order by cuil_titular;
	else 
		return query 
		select id_ospim, 
	        id_amtima,
		alta_fecha,
		unifica, 
		seccional , 
		id_tercerizadora, 
		cuil_titular, 
		cuil, 
		inte,
		id_parentesco_sss, 
		parentesco, 
		apellido, 
		nombre, 
		documento_tipo, 
		docu_numero, 
		naci_fecha, 
		sexo, 
		id_estado_civil_sss,
		civil_esta, 
		nacionalidad, 
		provincia, 
		localidad, 
		postal_codi, 
		calle, 
		numero, 
		piso, 
		depto, 
		telefono,
		email,
		categoria, 
		ramo, 
		id_plan, 
		plan, 
		ingre_fecha, 
		baja_fecha, 
		id_uoma,
		cuit, 
		razon_soc,
		fecha_ospim,
		escala_salarial,
		plan_omint,
		discapacitado,
		incognita,
		fpp 
		from reporte_result a
		WHERE (p_cuit is null or (p_cuit is not null and cuit = p_cuit))
		AND (p_naci_fecha_desde is null or (p_naci_fecha_desde is not null and NACI_FECHA >= p_naci_fecha_desde))
		AND (p_naci_fecha_hasta is null or (p_naci_fecha_hasta is not null and NACI_FECHA <= p_naci_fecha_hasta))
		AND (p_parentesco is null or (p_parentesco is not null and id_parentesco_sss = p_parentesco))
		and (p_plan_id is null or (p_plan_id is not null and id_plan in (select split_cadena(p_plan_id, ','))))		
		AND inte <> 0
		order by cuil_titular;
	end if;
end if;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;

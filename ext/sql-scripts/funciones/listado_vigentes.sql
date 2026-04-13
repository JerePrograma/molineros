--en este archivo estan: type, version plan nueva y version plan 1.0
CREATE TYPE afiliados_vigentes AS
   (id_ospim integer,
    seccional character varying,
    id_tercerizadora character varying,
    cuil_titular character varying,
    cuil character varying,
    inte integer,
    id_parentesco_sss integer,
    parentesco character varying,
    apellido character varying,
    nombre character varying,
    documento_tipo character varying,
    docu_numero character varying,
    naci_fecha date,
    sexo character varying,
    id_estado_civil_sss integer,
    civil_esta character varying,
    nacionalidad character varying,
    provincia character varying,
    localidad character varying,
    postal_codi character varying,
    calle character varying,
    numero character varying,
    piso character varying,
    depto character varying,
    telefono character varying,
    categoria character(50),
    ramo character(50),
    id_plan integer,
    plan character(50),
    ingre_fecha date,
    baja_fecha timestamp without time zone,
    id_uoma integer,
    cuit character(11),
    razon_soc character varying,
    fecha_ospim date,
    os_anterior integer,
    discapacidad character varying,
    tipo_operacion character varying,
    valor_capita numeric,
    pmi date);

CREATE OR REPLACE FUNCTION listado_vigentes(id_terc_v character varying, informar_v boolean, tipo_v integer, vigen_v date)
  RETURNS SETOF afiliados_vigentes AS
$BODY$
BEGIN
drop table if exists afiliados_vigentes_result;
create table afiliados_vigentes_result as 
select 	a.id_ospim, 
	cast(s.id_seccional||' - ' ||s.descripcion as varchar) as seccional , 
	at.id_tercerizadora, 
	a.cuil_titular, 
	a.cuil, 
	a.inte, 
	a.id_parentesco_sss, 
	cast(upper(p.descripcion) as varchar) as parentesco, 
	cast(upper(a.apellido) as varchar) as apellido, 
	cast(upper(a.nombre) as varchar) as nombre, 
	a.documento_tipo, 
	a.docu_numero, 
	a.naci_fecha, 
	a.sexo, 
	a.id_estado_civil_sss,
	ec.descripcion as civil_esta, 
	n.detalle as nacionalidad, 
	cast(null as varchar) as provincia, 
	cast(null as varchar) as localidad, 
	cast(null as varchar) as postal_codi, 
	cast(null as varchar) as calle, 
	cast(null as varchar) as numero, 
	cast(null as varchar) as piso, 
	cast(null as varchar) as depto, 
	cast(null as varchar) as telefono,
	cast('' as char(50)) as categoria, 
	cast('' as char(50)) as ramo, 
	0 as id_plan, 
	cast('' as char(50)) as plan, 
	a.ingre_fecha, 
	a.baja_fecha, 
	case when (a.id_uoma_baja_fecha is not null and a.id_uoma_baja_fecha < vigen_v) then 0 else a.id_uoma end as id_uoma, 
	cast('' as char(11)) as cuit, 
	cast(null as varchar) as razon_soc,
	cast(null as date) as fecha_ospim,
	a.anterior_os as os_anterior,
	a.discapacitado,
	cast(null as numeric) as valor_capita,
	cast(null as date) as PMI
from afiliado a
inner join parentesco_sss p on a.id_parentesco_sss = p.codigo 
inner join estado_civil_sss ec on a.id_estado_civil_sss = ec.codigo
inner join afi_tercerizadora_servicio at
on at.cuil_titular=a.cuil_titular 
and at.inte=0 
left outer join seccional s
on a.id_seccional=s.id_seccional
left outer join nacionalidad n
on a.nacionalidad=n.id
where 
(at.fecha_fin_pres is null or at.fecha_fin_pres>vigen_v) 
and at.fecha_inicio_pres=(select max(fecha_inicio_pres)
                          from afi_tercerizadora_servicio at3
			  where at3.cuil_titular=at.cuil_titular
			  and at3.inte=at.inte
			  and at3.id_tercerizadora = at.id_tercerizadora
			  and (at3.baja_fecha is null or at3.baja_fecha >vigen_v)
			  and (at3.fecha_fin_pres is null or at3.fecha_fin_pres >vigen_v) )
and (a.baja_fecha is null or a.baja_fecha>vigen_v)
and (id_terc_v is null or (id_terc_v is not null and at.id_tercerizadora=id_terc_v))
--and a.aportante_titular=1
--and a.alta_fecha<=fecha_vigen
and exists (select 1 from afi_aportes ap
		    where ap.cuil_titular=a.cuil_titular
		    and ap.inte=0
		    and (ap.fecha_egre is null or ap.fecha_egre>vigen_v)
		    and (ap.baja_fecha is null or ap.baja_fecha>vigen_v)
		    --and ap.fecha_ingre<=fecha_vigen
		    and id_aporte in (select id_aporte from aporte where es_os is true))--(1,2,7,8,9,10,12,13,14,15))
order by a.cuil_titular, a.inte;

update afiliados_vigentes_result a
set provincia=p.detalle, 
    localidad=l.detalle, 
    postal_codi=d.postal_codi, 
    calle=d.calle, 
    numero=d.numero, 
    piso=d.piso, 
    depto=d.depto, 
    telefono=d.telefono
from afi_domicilio d, provincia p, localidad l
where d.cuil_titular=a.cuil_titular 
and d.inte=0
and d.provincia=p.id_provincia
and d.vigen_desde=(select max(vigen_desde)
	           from afi_domicilio d2
		   where d.cuil_titular=d2.cuil_titular
		   and d.inte=d2.inte
		   and (baja_fecha is null or baja_fecha>vigen_v))
and (d.baja_fecha is null or d.baja_fecha>vigen_v)		   
and d.localidad=l.id_localidad
and l.id_provincia=d.provincia;



update afiliados_vigentes_result a
set categoria=c.categoria,
    cuit=substring(al.cuit,1,11)    
from afi_situ_laboral al, categoria_laboral c
where al.cuil_titular=a.cuil_titular
and al.cuil_titular=a.cuil_titular 
and al.inte=0
and al.id_categoria=c.id_categoria
and (al.fecha_egre is null or al.fecha_egre>vigen_v)
and (al.baja_fecha is null or al.baja_fecha >vigen_v)
and al.fecha_ingre=(select max(fecha_ingre)
                    from afi_situ_laboral al2
		    where al2.cuil_titular=al.cuil_titular
		    and al2.inte=al.inte
		    and (al2.fecha_egre is null or al2.fecha_egre>vigen_v)
		    and (al2.baja_fecha is null or al2.baja_fecha >vigen_v));

		    
update afiliados_vigentes_result a
set razon_soc=e.razon_soc
from  afi_situ_laboral al, empresa e
where al.cuil_titular=a.cuil_titular
and al.cuil_titular=a.cuil_titular 
and al.inte=0
and (al.fecha_egre is null or al.fecha_egre>vigen_v)
and (al.baja_fecha is null or al.baja_fecha >vigen_v)
and al.fecha_ingre=(select max(fecha_ingre)
                    from afi_situ_laboral al2
		    where al2.cuil_titular=al.cuil_titular
		    and al2.inte=al.inte
		    and (al2.fecha_egre is null or al2.fecha_egre>vigen_v)
		    and (al2.baja_fecha is null or al2.baja_fecha >vigen_v))
and e.cuit=al.cuit
and e.sucursal='000';

update afiliados_vigentes_result a
set razon_soc=cast(upper(a.apellido) as varchar) ||', '||cast(upper(a.nombre) as varchar)
where a.categoria in ('MONOTRIB. EFECTORES SOCIALES',
			'SERVICIO DOMESTICO (LEY 26.068)',
			'BENEF DE SEG DESEMPLEO(LEY24013)',
			'JUBILADOS DEL SISTEMA NACIONAL DEL SEGURO DE SALUD',
			'PEQ.CONTRIB.MONOTRIB (LEY 25.865)');



update afiliados_vigentes_result a
set categoria=c.categoria,
    cuit=al.cuit,
    razon_soc=e.razon_soc    
from afi_situ_laboral al, categoria_laboral c, empresa e
where (a.categoria is null or rtrim(a.categoria)='')
and al.cuil_titular=a.cuil_titular
and al.inte=0
and c.id_categoria=al.id_categoria
and (al.baja_fecha is null)
and al.fecha_ingre=(select max(fecha_ingre)
                    from afi_situ_laboral al2
		    where al2.cuil_titular=al.cuil_titular
		    and al2.inte=al.inte		    
		    and (al2.baja_fecha is null))
and al.id_motivo_baja in (1,3,21)
and al.fecha_egre+Interval '3 months'>=vigen_v
and e.cuit=al.cuit
and e.sucursal='000';


update afiliados_vigentes_result al
set ramo= case when categoria = 'MONOTRIB. EFECTORES SOCIALES' or categoria = 'PEQ.CONTRIB.MONOTRIB (LEY 25.865)' then 15 else id_ramo_empresa end
from empresa e
where e.cuit=al.cuit;

update afiliados_vigentes_result al
set ramo=99
where (ramo='' or ramo is null);

update afiliados_vigentes_result a
set id_plan=pl.id_plan,
    plan=pl.descripcion,
    valor_capita=pl.valor_capita
from afi_plan ap, plan_omint pl   
where ap.cuil_titular=a.cuil_titular 
and ap.inte=0
and pl.id_plan=ap.id_plan
and ap.vigen_desde=(select max(vigen_desde)
 		    from afi_plan app
	            where app.cuil_titular=ap.cuil_titular
		    and app.inte=ap.inte
		    and app.baja_fecha is null
		    and (app.vigen_hasta is null or app.vigen_hasta >vigen_v))
and ap.baja_fecha is null
and (ap.vigen_hasta is null or ap.vigen_hasta >vigen_v);

update afiliados_vigentes_result a
set id_plan=pl.id_plan,
    plan=pl.descripcion
from afi_plan ap, plan pl
where ap.cuil_titular=a.cuil_titular 
and ap.inte=0
and pl.id_plan=ap.id_plan
and ap.vigen_desde=(select max(vigen_desde)
 		    from afi_plan app
	            where app.cuil_titular=ap.cuil_titular
		    and app.inte=ap.inte
		    and app.baja_fecha is null
		    and (app.vigen_hasta is null or app.vigen_hasta >vigen_v))
and ap.baja_fecha is null		    
and (ap.vigen_hasta is null or ap.vigen_hasta >vigen_v)
and a.id_plan=0;

update afiliados_vigentes_result a
set fecha_ospim=aa.fecha_ingre
from afi_aportes aa
where aa.cuil_titular=a.cuil_titular
and aa.inte=0
and aa.id_aporte in (select id_aporte from aporte where es_os is true)--(1,2,7,8,9,10,12,13,14,15)
and aa.fecha_ingre=(select max (fecha_ingre)
		   from afi_aportes aaa		   
		   where aaa.cuil_titular=aa.cuil_titular
		   and aaa.inte=0
		   and (fecha_egre is null or fecha_egre>vigen_v)
		   and (baja_fecha is null or baja_fecha>vigen_v));

update afiliados_vigentes_result a
set PMI=d.fecha_vto
from afi_documento d
where a.cuil_titular=d.cuil_titular
and a.inte=d.inte
and d.id_documento=12
and d.baja_fecha is null
and d.fecha_vto=(select max(dd.fecha_vto)
		 from afi_documento dd
		 where dd.cuil_titular=d.cuil_titular
		 and dd.inte=d.inte
		 and dd.id_documento=12
		 and dd.baja_fecha is null);
if(tipo_v=2) then
	RAISE INFO 'VOY A DIFERENCIAS';
	perform reporte_diferencias_padron(id_terc_v,null);
end if;		   

if(informar_v) then
	INSERT INTO informes.listado_tercerizadora(
		    id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
		    id_parentesco_sss, parentesco, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
		    sexo, id_estado_civil_sss, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
		    calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
		    plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
		    os_anterior, discapacitado, fecha_listado, tipo, valor_capita)
	select id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, id_parentesco_sss,
		    parentesco, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
		    sexo, id_estado_civil_sss, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
		    calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
		    plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
		    os_anterior, discapacitado, current_timestamp, case tipo_v  when 1 then 'PADRON COMPLETO' when 2 then 'DIFERENCIAS' end,
		    valor_capita
	from afiliados_vigentes_result 
	where id_plan not in (select id_plan from plan where ospim is false);
end if;


if(tipo_v=2) then
	return query
	select  
	id_ospim, 
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
	os_anterior,
	discapacitado,
	modificacion,
	cast(0 as numeric),
	cast(null as date)
	from afiliados_vigentes_diferencia 
	where id_plan not in (select id_plan from plan where ospim is false)
	--and (baja_fecha is null or baja_fecha>vigen_v)
	order by cuil_titular;
else
	return query		   
	select id_ospim, 
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
	os_anterior,
	discapacitado,	
	cast('' as varchar) as modificacion,
	valor_capita,
	pmi
	from afiliados_vigentes_result 
	where id_plan not in (select id_plan from plan where ospim is false)
	--and (baja_fecha is null or baja_fecha>vigen_v)
	order by cuil_titular;
end if;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
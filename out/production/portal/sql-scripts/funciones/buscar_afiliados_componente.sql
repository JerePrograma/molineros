CREATE TYPE busca_afi_compo AS
   (cuil character varying,
    inte integer,
    id_parentesco_sss integer,
    parentesco character varying,
    apellido character varying,
    nombre character varying,
    tdoc character varying,
    documento character varying,
    id_seccional integer,
    seccional character varying,
    id_ospim integer,
    id_ospim_baja_fecha timestamp without time zone,
    id_uoma integer,
    id_uoma_baja_fecha timestamp without time zone,
    id_amtima integer,
    id_amtima_baja_fecha timestamp without time zone,
    ingreso date,
    baja_fecha timestamp without time zone,
    id_plan integer,
    nombre_plan character varying,
    id_motivo_baja integer,
    alta_fecha date,
    discapacitado character varying,
    vigen_fecha timestamp without time zone,
    fecha_nacimiento date,
    desc_tercerizadora character varying);
    

CREATE OR REPLACE FUNCTION buscar_afiliados_componente(cuil_v character, inte_v integer, tipodoc_v character, nrodoc_v character, seccional_v integer, apellido_v character, nombre_v character, entidad_in integer, afi_numero integer)
  RETURNS SETOF busca_afi_compo AS
$BODY$
declare cantidad_afiliados integer;
declare plan_vigente integer;
BEGIN
drop table if exists aux_busqueda;
create temp table aux_busqueda as 
	select 	a.cuil_titular,
		a.inte,
		a.id_parentesco_sss, 
		upper(pa.descripcion) as parentesco,
		upper(apellido) as apellido,
		upper(nombre) as nombre,
		upper(documento_tipo) as docu_tipo,
		docu_numero,		
		s.id_seccional,		
		s.descripcion,
		id_ospim ,
		id_ospim_baja_fecha,
		id_uoma ,
		id_uoma_baja_fecha ,
		id_amtima,
		id_amtima_baja_fecha,		
		ingre_fecha as ingre_fecha1,
		--aps.fecha_egre,
		a.baja_fecha, 
		p.id_plan,	
		ap.vigen_desde,	
		p.descripcion as nombre_plan,
		a.id_motivo_baja,
		a.ingre_fecha,
		a.discapacitado,
		a.vigen_fecha,
		a.naci_fecha,
		cast(null as varchar) as desc_tercerizadora
	from afiliado a 
	inner join parentesco_sss pa on a.id_parentesco_sss = pa.codigo
	inner join seccional s
	on a.id_seccional= s.id_seccional
	and ($5 is null or ($5 is not null and s.id_seccional=$5))	
	left outer join
	afi_plan ap on a.cuil_titular = ap.cuil_titular and ap.inte=0		
	and ap.alta_fecha = (select max(ap2.alta_fecha) from afi_plan ap2 where ap2.cuil_titular = ap.cuil_titular and ap2.inte = ap.inte
			     and (ap2.vigen_desde <= current_timestamp)
			     and ap2.baja_fecha is null
			     and (ap2.vigen_hasta is null  or ap2.vigen_hasta > current_timestamp))
	and (ap.vigen_desde <= current_timestamp)
	and (ap.baja_fecha is null  or ap.baja_fecha > current_timestamp)
	--inner join
	--afi_aportes aps on a.cuil_titular = aps.cuil_titular and aps.inte=0
	--and aps.modi_fecha = (select max(aps2.modi_fecha) from afi_aportes aps2 where aps2.cuil_titular = aps.cuil_titular and aps2.inte = aps.inte)
	left outer join plan p on ap.id_plan = p.id_plan 	
	where ($1 is null or ($1 is not null and a.cuil_titular=$1))
	and ($2 is null or ($2 is not null and a.inte=$2))
	and ($3 is null or ($3 is not null and documento_tipo=$3))
	and ($4 is null or ($4 is not null and docu_numero=$4))		
	and ($6 is null or ($6 is not null and upper(apellido) like '%'||upper($6)||'%'))
	and ($7 is null or ($7 is not null and upper(nombre) like '%'||upper($7)||'%'))
	and (entidad_in = 1 and (id_uoma != null or id_uoma != 0 ) or (entidad_in != 1 and (afi_numero is null or (afi_numero is not null and ((id_ospim = afi_numero and entidad_in = 0) or (id_uoma = afi_numero and entidad_in = 1) or (id_amtima = afi_numero and entidad_in = 2))))))
	and (afi_numero is null or (afi_numero is not null and ((id_ospim = afi_numero and entidad_in = 0) or (id_uoma = afi_numero and entidad_in = 1) or (id_amtima = afi_numero and entidad_in = 2))))
	order by 1,2	
	limit 20;
	
	cantidad_afiliados= count(*) from aux_busqueda;
	RAISE INFO 'CANT: %', cantidad_afiliados;
	--AFILIADO SIN PLAN VIGENTE
	update aux_busqueda x
	set id_plan=p.id_plan,
	    vigen_desde=ap.vigen_desde,
	    nombre_plan=p.descripcion
	from afi_plan ap, plan p
	where ap.cuil_titular=x.cuil_titular
	and ap.inte=0
	and p.id_plan=ap.id_plan
	and x.id_plan is null
	and ap.vigen_hasta = (select max(ap2.vigen_hasta) from afi_plan ap2 
			     where ap2.cuil_titular = ap.cuil_titular 
			     and ap2.inte = ap.inte
			     --and ap2.vigen_desde > current_timestamp
			     and ap2.baja_fecha is null)
		             --and (ap2.vigen_hasta is null  or ap2.vigen_hasta > current_timestamp))
	--and ap.vigen_desde > current_timestamp
	and (ap.baja_fecha is null or ap.baja_fecha> current_timestamp);

	if cantidad_afiliados=0 and afi_numero is not null and afi_numero<>0 then
		drop table aux_busqueda ;
		create temp table aux_busqueda as 
		select 	a.cuil_titular,
			a.inte,
			a.id_parentesco_sss, 
			upper(pa.descripcion) as parentesco,
			upper(apellido) as apellido,
			upper(nombre) as nombre,
			upper(documento_tipo) as docu_tipo,
			docu_numero,		
			s.id_seccional,		
			s.descripcion,
			id_ospim ,
			max(id_ospim_baja_fecha) as id_ospim_baja_fecha,
			id_uoma ,
			max(id_uoma_baja_fecha) as id_uoma_baja_fecha,
			id_amtima,
			max(id_amtima_baja_fecha) as id_amtima_baja_fecha,		
			max(ingre_fecha) as ingre_fecha1,
			--aps.fecha_egre,
			max(a.baja_fecha), 
			p.id_plan,
			p.descripcion as nombre_plan,
			a.id_motivo_baja,
			max(a.ingre_fecha) as ingre_fecha,
			a.discapacitado,
			max(a.vigen_fecha) as vigen_fecha,
			max(a.naci_fecha) as naci_fecha,
			cast(null as varchar) as desc_tercerizadora,
			cast(null as date) as vigen_desde
		from afi_estados_histo a 
		inner join parentesco_sss pa on a.id_parentesco_sss = pa.codigo
		inner join seccional s
		on a.id_seccional= s.id_seccional
		and ($5 is null or ($5 is not null and s.id_seccional=$5))	
		left outer join
		afi_plan ap on a.cuil_titular = ap.cuil_titular and ap.inte=0		
		and ap.alta_fecha = (select max(ap2.alta_fecha) from afi_plan ap2 where ap2.cuil_titular = ap.cuil_titular and ap2.inte = ap.inte)
		--inner join
		--afi_aportes aps on a.cuil_titular = aps.cuil_titular and aps.inte=0
		--and aps.modi_fecha = (select max(aps2.modi_fecha) from afi_aportes aps2 where aps2.cuil_titular = aps.cuil_titular and aps2.inte = aps.inte)
		left outer join plan p on ap.id_plan = p.id_plan 	
		where ($1 is null or ($1 is not null and a.cuil_titular=$1))
		and ($2 is null or ($2 is not null and a.inte=$2))
		and ($3 is null or ($3 is not null and documento_tipo=$3))
		and ($4 is null or ($4 is not null and docu_numero=$4))		
		and ($6 is null or ($6 is not null and upper(apellido) like '%'||upper($6)||'%'))
		and ($7 is null or ($7 is not null and upper(nombre) like '%'||upper($7)||'%'))
		and (entidad_in = 1 and (id_uoma != null or id_uoma != 0 ) or (entidad_in != 1 and (afi_numero is null or (afi_numero is not null and ((id_ospim = afi_numero and entidad_in = 0) or (id_uoma = afi_numero and entidad_in = 1) or (id_amtima = afi_numero and entidad_in = 2))))))
		and (afi_numero is null or (afi_numero is not null and ((id_ospim = afi_numero and entidad_in = 0) or (id_uoma = afi_numero and entidad_in = 1) or (id_amtima = afi_numero and entidad_in = 2))))
		and descripcion_operacion='ALT'
		group by a.cuil_titular,
			a.inte,
			a.id_parentesco_sss, 
			upper(pa.descripcion),
			upper(apellido),
			upper(nombre),
			upper(documento_tipo),
			docu_numero,		
			s.id_seccional,		
			s.descripcion,
			id_ospim ,			
			id_uoma ,			
			id_amtima,												
			p.id_plan,
			p.descripcion,
			a.id_motivo_baja,			
			a.discapacitado,						
			cast(null as varchar),
			cast(null as date)	
		limit 20;
	end if;

update aux_busqueda x
set id_ospim_baja_fecha=aa.fecha_egre
from aporte a, afi_aportes aa  
where aa.cuil_titular=x.cuil_titular
and a.id_aporte=aa.id_aporte
and aa.inte=0
and a.genera_id_socio='O'
and aa.fecha_egre=(select max(fecha_egre) from afi_aportes aa2
		   where aa2.cuil_titular=aa.cuil_titular
		   and aa2.inte=aa.inte		   
		   and aa2.baja_fecha is null
		   and aa2.id_aporte=aa.id_aporte);


update aux_busqueda x
set id_uoma_baja_fecha=aa.fecha_egre
from aporte a, afi_aportes aa  
where aa.cuil_titular=x.cuil_titular
and a.id_aporte=aa.id_aporte
and aa.inte=0
and a.genera_id_socio='U'
and aa.fecha_egre=(select max(fecha_egre) from afi_aportes aa2
		   where aa2.cuil_titular=aa.cuil_titular
		   and aa2.inte=aa.inte		   
		   and aa2.baja_fecha is null
		   and aa2.id_aporte=aa.id_aporte);

update aux_busqueda x
set id_amtima_baja_fecha=aa.fecha_egre
from aporte a, afi_aportes aa  
where aa.cuil_titular=x.cuil_titular
and a.id_aporte=aa.id_aporte
and aa.inte=0
and a.genera_id_socio='A'
and aa.fecha_egre=(select max(fecha_egre) from afi_aportes aa2
		   where aa2.cuil_titular=aa.cuil_titular
		   and aa2.inte=aa.inte		   
		   and aa2.baja_fecha is null
		   and aa2.id_aporte=aa.id_aporte);

return query 
select cuil_titular,
		inte, 
		id_parentesco_sss,
		cast(parentesco as varchar),
		cast(apellido as varchar),
		cast(nombre as varchar),
		cast(docu_tipo as varchar),
		docu_numero,		
		id_seccional,		
		descripcion,
		id_ospim ,
		case when (id_plan in (select id_plan from plan p where ospim is true
		and exists (select 1 from aporte a, plan_aporte pa
		where a.id_aporte=pa.id_aporte
		and p.id_plan=pa.id_plan
		and a.genera_id_socio='O')
		) and vigen_desde<=current_date+Interval '1 day') then null when id_ospim_baja_fecha is not null then id_ospim_baja_fecha else cast('18000101' as date) end as id_ospim_baja_fecha, 
		id_uoma ,		
		case when (id_plan in (select id_plan from plan where uoma is true) and vigen_desde<=current_date+Interval '1 day') then null when id_amtima_baja_fecha is not null then id_uoma_baja_fecha else cast('18000101' as date) end as id_uoma_baja_fecha,  		
		id_amtima,
		case when (id_plan in (select id_plan from plan where amtima is true) and vigen_desde<=current_date+Interval '1 day') then null when id_amtima_baja_fecha is not null then id_amtima_baja_fecha else cast('18000101' as date) end as id_amtima_baja_fecha,  		
		ingre_fecha,
		--aps.fecha_egre,
		baja_fecha, 
		id_plan,
		nombre_plan,
		id_motivo_baja,
		ingre_fecha,
		discapacitado,
		vigen_fecha,
		naci_fecha,
		desc_tercerizadora 
from aux_busqueda a order by 1,2 asc;

END;	
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;    
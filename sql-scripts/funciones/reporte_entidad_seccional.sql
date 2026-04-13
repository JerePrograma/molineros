create type reporte_entidad_seccional as (seccional varchar, 
uoma_titular integer, uoma_adherente integer, ospim_titular integer, ospim_adherente integer,
amtima_titular integer, amtima_adherente integer)

CREATE OR REPLACE FUNCTION reporte_entidad_seccional(fecha_vigencia date)
  RETURNS SETOF reporte_entidad_seccional AS
$BODY$
BEGIN

drop table if exists aux_entidad;
drop table if exists aux_total;
drop table if exists result;

--TODOS LOS AFILIADOS VIGENTES
create temp table aux_entidad AS
select a.cuil_titular, a.inte, aa.id_aporte, a.id_seccional
from afiliado a, afi_aportes aa  
where a.cuil_titular=aa.cuil_titular
and aa.inte=0
and aa.fecha_ingre<fecha_vigencia
and (aa.fecha_egre is null or aa.fecha_egre>=fecha_vigencia)
and (aa.baja_fecha is null or aa.baja_fecha>=fecha_vigencia)
and (a.baja_fecha is null or a.baja_fecha>=fecha_vigencia);

--SOLO OS POR SECCIONAL
CREATE TABLE AUX_TOTAL AS
select cast('OSPIM TITULARES' as varchar) as entidad, id_seccional, count(distinct cuil_titular) as total
from aux_entidad
where inte=0
and id_aporte in (select id_aporte from aporte where es_os=true)
group by id_seccional;


insert into aux_total(entidad, id_seccional, total)
select cast('OSPIM ADHERENTES' as varchar) as entidad, id_seccional, count(distinct cuil_titular||inte) as total
from aux_entidad
where inte<>0
and id_aporte in (select id_aporte from aporte where es_os=true)
group by id_seccional;

insert into aux_total(entidad, id_seccional, total)
select cast('OSPIM TITULARES' as varchar) as entidad, id_seccional, 0
from seccional s
where not exists (select 1 from aux_total x where s.id_seccional=x.id_seccional
		  and x.entidad=cast('OSPIM TITULARES' as varchar));

insert into aux_total(entidad, id_seccional, total)
select cast('OSPIM ADHERENTES' as varchar) as entidad, id_seccional, 0
from seccional s
where not exists (select 1 from aux_total x where s.id_seccional=x.id_seccional
		  and x.entidad=cast('OSPIM ADHERENTES' as varchar));
		  


insert into aux_total(entidad, id_seccional, total)
select cast('UOMA TITULARES' as varchar) as entidad, id_seccional, count(distinct cuil_titular) as total
from aux_entidad
where inte=0
and id_aporte in (select id_aporte from aporte where genera_id_socio='U')
group by id_seccional;



insert into aux_total(entidad, id_seccional, total)
select cast('UOMA ADHERENTES' as varchar) as entidad, id_seccional, 0 --count(distinct cuil_titular||inte) as total
from aux_entidad
where inte<>0
and id_aporte in (select id_aporte from aporte where genera_id_socio='U')
group by id_seccional;

insert into aux_total(entidad, id_seccional, total)
select cast('UOMA TITULARES' as varchar) as entidad, id_seccional, 0
from seccional s
where not exists (select 1 from aux_total x where s.id_seccional=x.id_seccional
		  and x.entidad=cast('UOMA TITULARES' as varchar));

insert into aux_total(entidad, id_seccional, total)
select cast('UOMA ADHERENTES' as varchar) as entidad, id_seccional, 0
from seccional s
where not exists (select 1 from aux_total x where s.id_seccional=x.id_seccional
		  and x.entidad=cast('UOMA ADHERENTES' as varchar));		  

insert into aux_total(entidad, id_seccional, total)
select cast('AMTIMA TITULARES' as varchar) as entidad, id_seccional, count(distinct cuil_titular) as total
from aux_entidad
where inte=0
and id_aporte in (select id_aporte from aporte where genera_id_socio='A')
group by id_seccional;


insert into aux_total(entidad, id_seccional, total)
select cast('AMTIMA ADHERENTES' as varchar) as entidad, id_seccional, 0 --count(distinct cuil_titular||inte) as total
from aux_entidad
where inte<>0
and id_aporte in (select id_aporte from aporte where genera_id_socio='A')
group by id_seccional;

insert into aux_total(entidad, id_seccional, total)
select cast('AMTIMA TITULARES' as varchar) as entidad, id_seccional, 0
from seccional s
where not exists (select 1 from aux_total x where s.id_seccional=x.id_seccional
		  and x.entidad=cast('AMTIMA TITULARES' as varchar));

insert into aux_total(entidad, id_seccional, total)
select cast('AMTIMA ADHERENTES' as varchar) as entidad, id_seccional, 0
from seccional s
where not exists (select 1 from aux_total x where s.id_seccional=x.id_seccional
		  and x.entidad=cast('AMTIMA ADHERENTES' as varchar));
		  

create temp table result as 
SELECT mthreport.*	
	FROM 
		crosstab('select id_seccional, entidad, sum(total)::integer as cantidad
			  from aux_total			  
			  group by id_seccional, entidad
			  order by id_seccional, entidad' 
			 )
			As mthreport(id integer, amtima_adherente integer, amtima_titular integer, 
				     ospim_adherente integer, ospim_titular integer, 
				     uoma_adherente integer, uoma_titular integer)
	order by id;

return query
select s.descripcion, r.uoma_titular, r.uoma_adherente, r.ospim_titular, r.ospim_adherente, 
       r.amtima_titular, r.amtima_adherente
from result r, seccional s
where r.id=s.id_seccional
order by s.descripcion;

	

END; 
$BODY$
  LANGUAGE plpgsql VOLATILE


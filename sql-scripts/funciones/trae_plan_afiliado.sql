create type trae_plan_afiliado_result as (id_plan integer, descripcion character varying, id_plan_omint integer, descripcion_omint varchar) 

-- DROP FUNCTION trae_plan_afiliado(character varying, integer);

CREATE OR REPLACE FUNCTION trae_plan_afiliado(cuilv character varying, intev integer)
  RETURNS SETOF trae_plan_afiliado_result AS
$BODY$
declare tiene_plan integer;
BEGIN
drop table if exists aux_plan;

create temp table aux_plan as 
select a.id_plan,p.descripcion, a.id_plan_omint, po.descripcion as descripcion_omint, a.vigen_desde, a.cuil_titular
from afi_plan a
inner join plan p
on p.id_plan=a.id_plan
left outer join plan_omint po
on a.id_plan_omint=po.id_plan_omint
where (a.vigen_desde is null or a.vigen_desde < current_timestamp)
and (a.baja_fecha is null  or a.baja_fecha >= current_timestamp)
and a.cuil_titular=$1
and a.inte=0;

tiene_plan=1 from aux_plan limit 1;

if tiene_plan=1 then
return query 
select id_plan,descripcion, id_plan_omint, descripcion_omint 
from aux_plan a 
where a.vigen_desde=(select max(a2.vigen_desde) 
	from afi_plan a2 where a2.cuil_titular=a.cuil_titular and a2.inte=0 and a2.id_plan=a.id_plan)
limit 1;
else
return query
select a.id_plan,p.descripcion, a.id_plan_omint, po.descripcion as descripcion_omint
from afi_plan a
inner join plan p
on p.id_plan=a.id_plan
left outer join plan_omint po
on po.id_plan_omint=a.id_plan_omint
where a.vigen_desde=(select max(a2.vigen_desde) from afi_plan a2 where a2.cuil_titular=a.cuil_titular and a2.inte=a.inte)
and a.cuil_titular=$1
and a.inte=0
limit 1;
end if;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE


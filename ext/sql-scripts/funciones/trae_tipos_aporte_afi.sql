-- Function: trae_tipos_aporte_afi(character varying, integer)

-- DROP FUNCTION trae_tipos_aporte_afi(character varying, integer);

create type trae_tipos_aporte_afi_result as (cuil_titular varchar, inte int, cuil varchar, id_aporte integer,
descripcion varchar, id_afiliado int, fecha_ingreso date, fecha_egreso date, motivo_baja varchar, id_motivo_baja smallint, es_os boolean)

-- DROP FUNCTION trae_tipos_aporte_afi(character varying, integer);

CREATE OR REPLACE FUNCTION trae_tipos_aporte_afi(cuil_p character varying, inte_p integer)
  RETURNS SETOF trae_tipos_aporte_afi_result AS
$BODY$
declare id_plan_v integer;
declare aporte_vigente integer;
BEGIN

drop table if exists temp_aportes;
id_plan_v= id_plan from trae_plan_afiliado(cuil_p, inte_p);


create temp table temp_aportes AS      
select a.cuil_titular,
       a.inte,
       af.cuil,
       a.id_aporte, 
       t.descripcion as aporte,
       case when t.genera_id_socio='O' then af.id_ospim 
	    when t.genera_id_socio='U' then af.id_uoma
	    when t.genera_id_socio='A' then af.id_amtima end as id_afi,
       a.fecha_ingre,
       a.fecha_egre,
       m.descripcion as motivo_baja,
       m.id_motivo_baja,
       t.es_os
from plan_aporte pa
inner JOIN afi_aportes a
on a.id_aporte=pa.id_aporte
INNER JOIN aporte t on (a.id_aporte=t.id_aporte)
INNER JOIN afiliado af on (a.cuil_titular=af.cuil_titular and a.inte=af.inte)
LEFT OUTER JOIN motivo_baja m on (a.id_motivo_baja=m.id_motivo_baja)
where a.cuil_titular=cuil_p
and (a.fecha_egre is null or a.fecha_egre>current_date)
--and a.fecha_egre=(select max(fecha_egre) from afi_aportes a2 where a2.cuil_titular=a.cuil_titular and a2.inte=0 )
and a.inte=0
and a.baja_fecha is null
and pa.id_plan=id_plan_v
order by inte,a.fecha_ingre;

aporte_vigente=count(*) from temp_aportes;

if aporte_vigente>=1 then 
	return query
	select cuil_titular,
	       inte,
	       cuil,
	       id_aporte, 
	       aporte,
	       id_afi,
	       fecha_ingre,
	       fecha_egre,
	       motivo_baja,
	       id_motivo_baja,
	       es_os
	from temp_aportes;
else
	return query
	select a.cuil_titular,
	       a.inte,
	       af.cuil,
	       a.id_aporte, 
	       t.descripcion,
	       case when t.genera_id_socio='O' then af.id_ospim 
		    when t.genera_id_socio='U' then af.id_uoma
		    when t.genera_id_socio='A' then af.id_amtima end as id_afi,
	       a.fecha_ingre,
	       a.fecha_egre,
	       m.descripcion,
	       m.id_motivo_baja,
	       t.es_os
	from plan_aporte pa
	inner JOIN afi_aportes a
	on a.id_aporte=pa.id_aporte
	INNER JOIN aporte t on (a.id_aporte=t.id_aporte)
	INNER JOIN afiliado af on (a.cuil_titular=af.cuil_titular and a.inte=af.inte)
	LEFT OUTER JOIN motivo_baja m on (a.id_motivo_baja=m.id_motivo_baja)
	where a.cuil_titular=cuil_p
	and a.fecha_egre=(select max(fecha_egre) from afi_aportes a2 where a2.cuil_titular=a.cuil_titular and a2.inte=0 )
	and a.inte=0
	and a.baja_fecha is null
	and pa.id_plan=id_plan_v
	order by inte,a.fecha_ingre;
end if;


END;
$BODY$
  LANGUAGE plpgsql VOLATILE

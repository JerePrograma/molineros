CREATE OR REPLACE FUNCTION trae_ultimas_derivaciones_desregulados_filtro(id_terc character varying, fecha_desde date, fecha_hasta date)
  RETURNS SETOF ultimas_derivaciones_desregulados AS
$BODY$
begin

if ($2<'20110101') then 
return query
select ts.descripcion, ts.id_tercerizadora, fecha_liq, cast(count(*) as int) as afiliados,sum(total_terc) as derivado, 
       sum(aporte_n+contrib_n) as importeTotal  
from liquidacion_historica_tercerizadoras lht, tercerizadora_servicio ts 
where 
(($1 is null or $1='0') or ($1 is not null and $1=ts.id_tercerizadora))
and(($2 is null and fecha_liq>=current_date-Interval '6 months')or ($2 is not null and fecha_liq>=$2))
and($3 is null or ($3 is not null and fecha_liq<=$3)) 
and ts.liquida=true
group by ts.descripcion,ts.id_tercerizadora, fecha_liq
order by ts.descripcion, fecha_liq;

else
return query
select ts.descripcion, ts.id_tercerizadora, fecha_liq, cast(count(*) as int) as afiliados,sum(total_terc) as derivado, 
       sum(aporte_n+contrib_n) as importeTotal  
from liquidacion_historica_tercerizadoras_2011 lht, tercerizadora_servicio ts 
where 
(($1 is null or $1='0') or ($1 is not null and $1=ts.id_tercerizadora))
and(($2 is null and fecha_liq>=current_date-Interval '6 months')or ($2 is not null and fecha_liq>=$2))
and($3 is null or ($3 is not null and fecha_liq<=$3)) 
and ts.liquida=true
group by ts.descripcion,ts.id_tercerizadora, fecha_liq
order by ts.descripcion, fecha_liq;

end if;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION trae_ultimas_derivaciones_desregulados_filtro(character varying, date, date)
  OWNER TO postgres;



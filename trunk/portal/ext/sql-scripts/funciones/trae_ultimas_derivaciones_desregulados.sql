create type ultimas_derivaciones_desregulados as (tercerizadora varchar, id_terc varchar, fecha_liq date, cant_reg int, 
derivado double precision, importeTotal double precision)

CREATE OR REPLACE FUNCTION trae_ultimas_derivaciones_desregulados()
  RETURNS SETOF ultimas_derivaciones_desregulados AS
$BODY$
begin
return query
select ts.descripcion, ts.id_tercerizadora, fecha_liq, cast(count(*) as int) as afiliados,sum(total_terc) as derivado, 
       sum(aporte_n+contrib_n) as importeTotal  
from liquidacion_historica_tercerizadoras_2011 lht, tercerizadora_servicio ts 
where fecha_liq>=current_date-Interval '3 months'
and lht.id_terc=ts.id_tercerizadora
and ts.liquida=true
group by ts.descripcion,ts.id_tercerizadora, fecha_liq
order by ts.descripcion, fecha_liq;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION trae_ultimas_derivaciones_desregulados()
  OWNER TO postgres;


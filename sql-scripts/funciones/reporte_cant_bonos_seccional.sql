create type reporte_cant_bonos_seccional_result as (seccional varchar, total_beneficiarios bigint, cant_bonos_1 int, cant_bonos_2 int, cant_bonos_3 int)
CREATE OR REPLACE FUNCTION reporte_cant_bonos_seccional()
  RETURNS SETOF reporte_cant_bonos_seccional_result AS
$BODY$
declare resultDom integer;
BEGIN
return query
select s.descripcion as seccional, count(*) as total_beneficiarios, cast(trunc((count(*)*3.5)/12,2) as int) as cant_bonos_1,cast(trunc((count(*)*3)/12,2) as int) as cant_bonos_2, 
       cast(trunc(count(*)/12,2) as int) as cant_bonos_3
from afiliado a, seccional s
where a.id_seccional=case when a.id_seccional is null then 9999 else s.id_seccional end 
and (a.baja_fecha is null or a.baja_fecha>current_date)
group by s.descripcion
order by descripcion;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE

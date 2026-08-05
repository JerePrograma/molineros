create type reporte_suma_sueeldos_empresa_ddjj_result as (cuit varchar, razon_soc varchar, ramo integer, cant_afiliados bigint, suma_sueldos numeric);
CREATE OR REPLACE FUNCTION reporte_suma_sueldos_empresa_ddjj(periodo_p date)
  RETURNS SETOF reporte_suma_sueeldos_empresa_ddjj_result AS
$BODY$
BEGIN
drop table if exists afi_sueldo_empresa;

CREATE TEMP TABLE afi_sueldo_empresa AS
select cuit, cast('' as varchar) as razon_soc, 0 as ramo, count(*) as cant_afiliados, sum(remuneracionafectos) as suma_sueldos
from detalle_declaracion_jurada d
where periodo=periodo_p
and secobligacion=(select max(secobligacion) from detalle_declaracion_jurada d2 where d2.cuit=d.cuit and d2.cuil=d.cuil and d2.periodo=d.periodo)
group by cuit;

update afi_sueldo_empresa a
set razon_soc=e.razon_soc,
ramo=e.id_ramo_empresa
from empresa e
where e.cuit=a.cuit;

update afi_sueldo_empresa set ramo=null where ramo=0;

return query
select cuit,razon_soc,ramo,cant_afiliados,suma_sueldos from afi_sueldo_empresa order by razon_soc, ramo;

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE
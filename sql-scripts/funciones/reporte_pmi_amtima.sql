/*create type reporte_periodo_amtima as(
"Fecha" date, 
"Nro. Socio" integer, 
"Inte" integer, 
"Apellido y Nombre" text, 
"Seccional" text,
"Titular" text,
"Empresa" varchar)*/

-- DROP FUNCTION reporte_pmi_amtima(date, date);

CREATE OR REPLACE FUNCTION reporte_pmi_amtima(periodo_desde date, periodo_hasta date)
  RETURNS SETOF reporte_periodo_amtima AS
$BODY$
BEGIN
drop table if exists temp_amtima;
--Sistema Actual
create temp table temp_amtima as
select a.cuil_titular, af.fecha_vto, a.id_amtima , a.inte , a.apellido||', '||a.nombre as integrante, a.id_seccional||' - '||s.descripcion as seccional,
       at.apellido||', '||at.nombre as titular
from afi_documento af, afiliado a, seccional s, afiliado at
where a.cuil_titular=af.cuil_titular
and a.inte=af.inte
and a.id_seccional=s.id_seccional
and at.cuil_titular=a.cuil_titular
and at.inte=0
and id_documento=12
and af.fecha_vto between periodo_desde and periodo_hasta
and exists (select 1 from afi_aportes ap2 where ap2.cuil_titular=a.cuil_titular and id_aporte=4
	    and (ap2.fecha_egre is null or ap2.fecha_egre>current_date))
and (a.baja_fecha is null or a.baja_fecha >current_date)
order by af.fecha_vto;

alter table temp_amtima add empresa varchar;

update temp_amtima t
set empresa=e.razon_soc
from empresa e, afi_situ_laboral al
where al.cuil_titular=t.cuil_titular
and (al.fecha_egre is null or al.fecha_egre>current_date)
and (al.baja_fecha is null or al.baja_fecha>current_date)
and e.cuit=al.cuit
and e.sucursal=al.sucursal;

return query
select fecha_vto, id_amtima, inte, integrante , seccional, titular , empresa
from temp_amtima;


END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE

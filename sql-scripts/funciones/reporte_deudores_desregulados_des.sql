CREATE OR REPLACE FUNCTION reporte_deudores_desregulados_des(fecha_ultimo_pago date, fecha_ingreso_afiliado date)
  RETURNS SETOF reporte_deudores_desregulados_type AS
$BODY$
declare fecha_actual date;
BEGIN

fecha_actual=current_date;

drop table if exists aux_monotrib;
drop table if exists empleo;

CREATE TEMP TABLE aux_monotrib AS
select a.cuil, cast('DESREGULADOS' as text) as categoria, id_tercerizadora
from afiliado a
inner join afi_tercerizadora_servicio ats
on ats.cuil_titular=a.cuil_titular
and ats.inte=0
where (a.baja_fecha is null or a.baja_fecha>fecha_actual)
and (ats.fecha_fin_pres is null or ats.fecha_fin_pres>fecha_actual)
and (ats.baja_fecha is null or ats.baja_fecha>fecha_actual)
and (a.vigen_fecha<=fecha_actual)
and a.inte=0
and exists (select 1 from afi_plan ap where ap.cuil_titular=a.cuil_titular and ap.inte=0 and id_plan=3 and (baja_fecha is null or baja_fecha>fecha_actual))
and exists (select 1 
	    from afi_situ_laboral al, empresa e 
	    where al.cuil_titular=a.cuil_titular
	    and al.inte=a.inte
	    and (al.fecha_egre is null or al.fecha_egre>fecha_actual)
	    and (al.baja_fecha is null or al.baja_fecha>fecha_actual) 
	    and e.cuit=al.cuit
	    and e.sucursal='000'
	    and e.id_ramo_empresa<>10);-- DESREGULADOS


insert into aux_monotrib (cuil, categoria, id_tercerizadora)
select a.cuil, cast('COBERTURA' as text) as categoria, id_tercerizadora
from afiliado a
inner join afi_tercerizadora_servicio ats
on ats.cuil_titular=a.cuil_titular
and ats.inte=0
where (a.baja_fecha is null or a.baja_fecha>fecha_actual)
and (ats.fecha_fin_pres is null or ats.fecha_fin_pres>fecha_actual)
and (ats.baja_fecha is null or ats.baja_fecha>fecha_actual)
and (a.vigen_fecha<=fecha_actual)
and a.inte=0
and exists (select 1 from afi_plan ap where ap.cuil_titular=a.cuil_titular and ap.inte=0 and id_plan in(3) and (baja_fecha is null or baja_fecha>fecha_actual))
and exists (select 1 
	    from afi_situ_laboral al, empresa e 
	    where al.cuil_titular=a.cuil_titular
	    and al.inte=a.inte
	    and (al.fecha_egre is null or al.fecha_egre>fecha_actual)
	    and (al.baja_fecha is null or al.baja_fecha>fecha_actual) 
	    and e.cuit=al.cuit
	    and e.sucursal='000' 
	    and e.id_ramo_empresa=10); --COBERTURA

insert into aux_monotrib (cuil, categoria, id_tercerizadora)
select a.cuil, cast('TOTAL e INTEGRAL' as text) as categoria,id_tercerizadora
from afiliado a
inner join afi_tercerizadora_servicio ats
on ats.cuil_titular=a.cuil_titular
and ats.inte=0
where (a.baja_fecha is null or a.baja_fecha>fecha_actual)
and (ats.fecha_fin_pres is null or ats.fecha_fin_pres>fecha_actual)
and (ats.baja_fecha is null or ats.baja_fecha>fecha_actual)
and (a.vigen_fecha<=fecha_actual)
and a.inte=0
and exists (select 1 from afi_plan ap where ap.cuil_titular=a.cuil_titular and ap.inte=0 and id_plan in(1,2) and (baja_fecha is null or baja_fecha>fecha_actual))
and exists (select 1 
	    from afi_situ_laboral al, empresa e 
	    where al.cuil_titular=a.cuil_titular
	    and al.inte=a.inte
	    and (al.fecha_egre is null or al.fecha_egre>fecha_actual)
	    and (al.baja_fecha is null or al.baja_fecha>fecha_actual) 	    
	    and e.cuit=al.cuit
	    and e.sucursal='000'
	    and e.id_ramo_empresa=10 ); --TOTAL e INTEGRAL
	    

insert into aux_monotrib (cuil, categoria, id_tercerizadora)
select a.cuil, cast('COBERTURA USUFRUCTO' as text) as categoria,id_tercerizadora
from afiliado a
inner join afi_tercerizadora_servicio ats
on ats.cuil_titular=a.cuil_titular
and ats.inte=0
where (a.baja_fecha is null or a.baja_fecha>fecha_actual)
and (ats.fecha_fin_pres is null or ats.fecha_fin_pres>fecha_actual)
and (ats.baja_fecha is null or ats.baja_fecha>fecha_actual)
--and ats.id_tercerizadora='CSA'
and (a.vigen_fecha<=fecha_actual)
and a.inte=0
and exists (select 1 from afi_plan ap where ap.cuil_titular=a.cuil_titular and ap.inte=0 and id_plan in(19) and (baja_fecha is null or baja_fecha>fecha_actual))
and exists (select 1 
	    from afi_situ_laboral al, empresa e 
	    where al.cuil_titular=a.cuil_titular
	    and al.inte=a.inte
	    and (al.fecha_egre is null or al.fecha_egre>fecha_actual)
	    and (al.baja_fecha is null or al.baja_fecha>fecha_actual) 
	    --and al.id_categoria in (11)
	    and e.cuit=al.cuit
	    and e.sucursal='000'); --COBERTURA USUFRUCTO
	    

RAISE INFO 'ANTES DE EMPLEO RESTO';
--RESTO
CREATE TEMP TABLE EMPLEO AS 
select a.cuil, apellido, nombre, ad.vigen_fecha, s.descripcion, categoria, id_tercerizadora
from aux_monotrib a, afiliado ad, seccional s--, afi_situ_laboral al --911
where not exists (select 1 from detalle_declaracion_jurada_2011  o where a.cuil=o.cuil and o.periodo>fecha_ultimo_pago)
and a.cuil=ad.cuil
and a.categoria not in ('MONOTRIBUTISTAS', 'SERVICIO DOMESTICO')
and inte=0
and ad.vigen_fecha<fecha_ingreso_afiliado
and ad.id_seccional=s.id_seccional
and not exists (select 1 from detalle_desempleo_anses dda where a.cuil=dda.cuil and fecha_proceso>fecha_ultimo_pago);


alter table empleo add empresa varchar;

RAISE INFO 'EMPLEO';

update empleo p
set empresa=e.razon_soc
from afi_situ_laboral al, empresa e
where p.cuil=al.cuil_titular
and (al.fecha_egre is null or al.fecha_egre>fecha_actual)
and (al.baja_fecha is null or al.baja_fecha>fecha_actual) 
and e.cuit=al.cuit
and e.sucursal='000';

RAISE INFO 'PLAN';
alter table empleo add plan varchar;
update empleo e
set plan=pl.descripcion
from afi_plan ap, plan pl
where ap.cuil_titular=e.cuil
and ap.inte=0
and pl.id_plan=ap.id_plan
and (ap.baja_fecha is null or ap.baja_fecha>fecha_actual);

return query
select cuil, apellido, nombre, cast(vigen_fecha as date), plan,
descripcion as seccional, categoria, empresa, id_tercerizadora 
from empleo 
order by categoria, descripcion, categoria, empresa, apellido;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION reporte_deudores_desregulados_des(date, date)
  OWNER TO postgres;


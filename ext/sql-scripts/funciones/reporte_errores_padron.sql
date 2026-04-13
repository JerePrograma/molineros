CREATE OR REPLACE FUNCTION reporte_errores_padron()
  RETURNS SETOF errores_padron AS
$BODY$
BEGIN

drop table if exists aux;

create temp table aux(cuil_titular varchar, inte int, id_terc varchar, observaciones varchar);

insert into aux(cuil_titular, inte, id_terc, observaciones)
select a.cuil_titular, a.inte, null as id_terc, cast('GRUPO SIN TERCERIZADORA SERVICIO' as varchar) as observaciones
from afiliado a
where (baja_fecha is null or baja_fecha>current_date)
and not exists (select 1 from afi_tercerizadora_servicio ts 
		where (baja_fecha is null or baja_fecha>current_date) and (fecha_fin_pres is null or fecha_fin_pres>current_date) and a.cuil_titular=ts.cuil_titular)
and inte=0;

insert into aux(cuil_titular, inte, id_terc, observaciones)
select a.cuil_titular, a.inte, ts.id_tercerizadora, 'GRUPO SIN SITUACION LABORAL'
from afiliado a
left outer join afi_tercerizadora_servicio ts
on a.cuil_titular=ts.cuil_titular
and ts.inte=0
and (ts.baja_fecha is null or ts.baja_fecha>current_date) and (ts.fecha_fin_pres is null or ts.fecha_fin_pres>current_date)
where (a.baja_fecha is null or a.baja_fecha>current_date)
and a.inte=0
and not exists (select 1 from afi_situ_laboral l 
		where (baja_fecha is null or baja_fecha>current_date) and (fecha_egre is null or fecha_egre>current_date) and a.cuil_titular=l.cuil_titular)
and a.inte=0;


insert into aux(cuil_titular, inte, id_terc, observaciones)
select a.cuil_titular, a.inte, ts.id_tercerizadora,'GRUPO SIN PLAN' 
from afiliado a
left outer join afi_tercerizadora_servicio ts
on a.cuil_titular=ts.cuil_titular
and ts.inte=0
and (ts.baja_fecha is null or ts.baja_fecha>current_date) and (ts.fecha_fin_pres is null or ts.fecha_fin_pres>current_date)
where (a.baja_fecha is null or a.baja_fecha>current_date)
and not exists (select 1 from afi_aportes ts 
		where (baja_fecha is null or baja_fecha>current_date))
and a.inte=0;


insert into aux(cuil_titular, inte, id_terc, observaciones)
select a.cuil_titular, a.inte, ts.id_tercerizadora,'GRUPO SIN TITULAR'
from afiliado a
left outer join afi_tercerizadora_servicio ts
on a.cuil_titular=ts.cuil_titular
and ts.inte=0
and (ts.baja_fecha is null or ts.baja_fecha>current_date) and (ts.fecha_fin_pres is null or ts.fecha_fin_pres>current_date)
where a.inte<>0
and (a.baja_fecha is null or a.baja_fecha>current_date)
and not exists (select 1 from afiliado a2 where a2.inte=0 and a2.cuil_titular=a.cuil_titular and (a2.baja_fecha is null or a2.baja_fecha>current_date));

insert into aux(cuil_titular, inte, id_terc, observaciones)
select a.cuil_titular, a.inte, ts.id_tercerizadora,'FECHA NACIMIENTO MAYOR A FECHA ACTUAL'
from afiliado a
left outer join afi_tercerizadora_servicio ts
on a.cuil_titular=ts.cuil_titular
and ts.inte=0
and (ts.baja_fecha is null or ts.baja_fecha>current_date) and (ts.fecha_fin_pres is null or ts.fecha_fin_pres>current_date)
where a.inte<>0
and (a.baja_fecha is null or a.baja_fecha>current_date)
and fu_obtener_edad(current_date, a.naci_fecha)>21;

insert into aux(cuil_titular, inte, id_terc, observaciones)
select a.cuil_titular, a.inte, ts.id_tercerizadora,'MAYOR A 100 AÑOS'
from afiliado a
left outer join afi_tercerizadora_servicio ts
on a.cuil_titular=ts.cuil_titular
and ts.inte=0
and (ts.baja_fecha is null or ts.baja_fecha>current_date) and (ts.fecha_fin_pres is null or ts.fecha_fin_pres>current_date)
where (a.baja_fecha is null or a.baja_fecha>current_date)
and fu_obtener_edad(a.naci_fecha, current_date )>100;


insert into aux(cuil_titular, inte, id_terc, observaciones)
select a.cuil_titular, a.inte, ts.id_tercerizadora,'MAYOR A 21 AÑOS SIN DOCUMENTACION ADJUNTA'
from afiliado a
left outer join afi_tercerizadora_servicio ts
on a.cuil_titular=ts.cuil_titular
and ts.inte=0
and (ts.baja_fecha is null or ts.baja_fecha>current_date) and (ts.fecha_fin_pres is null or ts.fecha_fin_pres>current_date)
where a.inte<>0
and (a.baja_fecha is null or a.baja_fecha>current_date)
and fu_obtener_edad(a.naci_fecha,current_date)>21
and not exists (select 1 from afi_documento ad where a.cuil_titular=ad.cuil_titular and a.inte=ad.inte and (ad.baja_fecha is null or ad.baja_fecha>current_date))
and a.id_parentesco_sss in (3,4,5,6,7);
--and upper(parentesco)in ('HIJO SOLTERO DE 21 A 25 AÑOS CURSANDO ESTUDIOS REGULARES','HIJO DEL CONYUGE SOLTERO DE 21 A 25 AÑOS CURSANDO ESTUDIOS REGULARES',
--			 'HIJO SOLTERO MENOR DE 21 AÑOS','HIJO DEL CONYUGE SOLTERO MENOR DE 21 AÑOS','MENOR BAJO GUARDA O TUTELA');

insert into aux(cuil_titular, inte, id_terc, observaciones)
select a.cuil_titular, a.inte, ts.id_tercerizadora,'CONYUGE/CONCUBINO SIN ESPOSO'
from afiliado a
left outer join afi_tercerizadora_servicio ts
on a.cuil_titular=ts.cuil_titular
and ts.inte=0
and (ts.baja_fecha is null or ts.baja_fecha>current_date) and (ts.fecha_fin_pres is null or ts.fecha_fin_pres>current_date)
where a.inte<>0
and (a.baja_fecha is null or a.baja_fecha>current_date)
and not exists (select 1 from afiliado a2 where a2.inte=0 and a2.cuil_titular=a.cuil_titular and (a2.baja_fecha is null or a2.baja_fecha>current_date)
and a.id_estado_civil_sss in (2,7))
and a.id_parentesco_sss in (1,2);
--and upper(civil_esta) in ('CASADO','CONVIVENCIA'))
--and upper(parentesco)in ('CONYUGE', 'CONCUBINO/A');


return query
select * from aux order by id_terc;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
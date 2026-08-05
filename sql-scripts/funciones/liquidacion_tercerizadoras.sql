-- Function: liquidacion_tercerizadoras(character varying)

-- DROP FUNCTION liquidacion_tercerizadoras(character varying);

CREATE OR REPLACE FUNCTION liquidacion_tercerizadoras(terc_p character varying)
  RETURNS SETOF liquidacion_tercerizadoras AS
$BODY$
BEGIN

drop table if exists temporal_aportes_tercerizadora;
drop table if exists temporal_apo_terc_agrupada;
drop table if exists temporal_contribuciones;
drop table if exists temporal_cuil_aportes;
drop table if exists temporal_cuit_aportes;
drop table if exists emp_sin_aportes;
drop table if exists liq_histo;


--APORTES CORRESPONDIENTES A LA TERCERIZADORA EN CUESTION --TITULARES DE GRUPO
CREATE temp table temporal_aportes_tercerizadora AS 
select o.cuit_contribuyente, o.cuil_aportante, o.periodo, importe as aporte
from os_aportes_detalle o, conceptos_transf_os c, afi_tercerizadora_servicio ts
where o.concepto_transf=c.cod_conc
and liquidable=TRUE
and o.cuil_aportante<>'00000000000'
and (o.cuil_aportante=ts.cuil_titular --TERCERIZADORA faltan los CUILES DE LOS QUE NO SON TITULARES
and ts.inte=0
and ts.id_tercerizadora = terc_p
and ts.fecha_inicio_pres<=lastDateOfMonth(o.periodo)
and (ts.fecha_fin_pres is null or (ts.fecha_fin_pres>=o.periodo and ts.fecha_inicio_pres <> ts.fecha_fin_pres))
and (ts.baja_fecha is null))
and (o.periodo >='20060101' or o.fecha_proceso >= '2010-11-01');



--APORTES CORRESPONDIENTES A LA TERCERIZADORA EN CUESTION --PARIENTES DE GRUPO APORTANTES
insert into temporal_aportes_tercerizadora(cuit_contribuyente, cuil_aportante, periodo, aporte)
select o.cuit_contribuyente, o.cuil_aportante, o.periodo, importe as aporte
from os_aportes_detalle o, conceptos_transf_os c, afi_tercerizadora_servicio ts, afiliado a
where o.concepto_transf=c.cod_conc
and o.cuil_aportante=a.cuil
and a.inte<>0
and a.cuil<>a.cuil_titular
and a.aportante_titular<>0
and liquidable=TRUE
and o.cuil_aportante<>'00000000000'
and a.cuil_titular=ts.cuil_titular
and (o.cuil_aportante=ts.cuil_titular --TERCERIZADORA faltan los CUILES DE LOS QUE NO SON TITULARES
and ts.id_tercerizadora = terc_p
and ts.fecha_inicio_pres<=lastDateOfMonth(o.periodo)
and ts.inte=a.inte
and (ts.fecha_fin_pres is null or (ts.fecha_fin_pres>=o.periodo and ts.fecha_inicio_pres <> ts.fecha_fin_pres))
and (ts.baja_fecha is null))
and not exists (select 1 from temporal_aportes_tercerizadora tt 
                where o.cuit_contribuyente=tt.cuit_contribuyente and o.cuil_aportante=tt.cuil_aportante and o.periodo=tt.periodo)
and (o.periodo >='20060101' or o.fecha_proceso >= '2010-11-01');



--APORTES DE UNIFICA
insert into temporal_aportes_tercerizadora(cuit_contribuyente, cuil_aportante, periodo, aporte)
select o.cuit_contribuyente, o.cuil_aportante, o.periodo, importe as aporte
from os_aportes_detalle o, conceptos_transf_os c, afiliado a, afi_tercerizadora_servicio ts
where o.concepto_transf=c.cod_conc
and o.cuil_aportante=a.cuil
and a.inte<>0
and a.cuil<>a.cuil_titular
and a.aportante_titular=1
and not exists (select 1 from afiliado a2 where a2.cuil_titular=a.cuil and (a2.baja_fecha is null or a2.baja_fecha>current_date))
and liquidable=TRUE
and o.cuil_aportante<>'00000000000'
and (o.periodo >='20060101' or o.fecha_proceso >= '2010-11-01')
and a.cuil_titular=ts.cuil_titular --TERCERIZADORA faltan los CUILES DE LOS QUE NO SON TITULARES
and ts.inte=0--a.inte
and ts.id_tercerizadora = terc_p
and ts.fecha_inicio_pres<=lastDateOfMonth(o.periodo)
and (ts.fecha_fin_pres is null or (ts.fecha_fin_pres>=o.periodo and ts.fecha_inicio_pres <> ts.fecha_fin_pres))
and not exists (select 1 from temporal_aportes_tercerizadora tt 
                where o.cuit_contribuyente=tt.cuit_contribuyente and o.cuil_aportante=tt.cuil_aportante and o.periodo=tt.periodo)
and (ts.baja_fecha is null);


--AGRUPAMOS POR CUIT, CUIL, PERIODO LOS APORTES TOTALES 
CREATE temp table temporal_apo_terc_agrupada AS 
select cuit_contribuyente, cuil_aportante, periodo, sum(aporte) as aporte, 0.00 as contribucion, 0.00 as diferencia_tope
from temporal_aportes_tercerizadora
group by cuit_contribuyente, cuil_aportante, periodo;


--BORRAMOS APORTES EN 0
delete from temporal_apo_terc_agrupada where aporte=0;

--CONTRIBUCIONES QUE TENGAN APORTES
CREATE temp table temporal_contribuciones AS 
select o.cuit_contribuyente, o.periodo, sum(o.importe) as contribucion, 0.00 as aporte_empresa
from os_aportes_detalle o, conceptos_transf_os c
where o.concepto_transf=c.cod_conc
and c.liquidable=TRUE
and o.cuil_aportante='00000000000' --TOMO LAS CONTRIBUCIONES POR EMPRESA
and exists (select 1 from  temporal_apo_terc_agrupada ta 
	    where ta.cuit_contribuyente=o.cuit_contribuyente
	    and ta.periodo=o.periodo)
and (o.periodo >='20060101' or o.fecha_proceso >= '2010-11-01')
group by o.cuit_contribuyente, o.periodo;

--BORRO los 0 por División por 0
delete from temporal_contribuciones where contribucion=0;

--TOTALES POR EMPRESA
CREATE temporary table temporal_cuil_aportes AS
select o.cuit_contribuyente, o.cuil_aportante, o.periodo, cast(0 as numeric) as diferencia_tope, sum(o.importe) as total_aportes
from temporal_contribuciones ta, os_aportes_detalle o, conceptos_transf_os c
where o.cuit_contribuyente=ta.cuit_contribuyente
and o.periodo=ta.periodo
and o.concepto_transf=c.cod_conc
and liquidable=TRUE
and o.cuil_aportante<>'00000000000' --TODOS LOS REGISTROS POR CUIT QUE NO SEAN CONTRIBUCIONES
and (o.periodo >='20060101' or o.fecha_proceso >= '2010-11-01')
group by o.cuit_contribuyente, o.cuil_aportante, o.periodo;


--APORTE CALCULADO SIN TOPE
update temporal_cuil_aportes t
set diferencia_tope=trunc((dd.importebasecontribucionos*case when (to_char(t.periodo,'MM')='06' or to_char(t.periodo,'MM')='12') then 0.03825 else 0.0255 end)-t.total_aportes,2)
from topes_aportes_os tope, detalle_declaracion_jurada dd
where t.periodo>= tope.fecha_desde
and t.periodo<=tope.fecha_hasta
and t.total_aportes>=tope.tope_aporte
and dd.cuit=t.cuit_contribuyente
and dd.cuil=t.cuil_aportante
and dd.periodo=t.periodo
and dd.secobligacion=(select max(secobligacion) from detalle_declaracion_jurada d2 where d2.cuit=dd.cuit and d2.cuil=dd.cuil and d2.periodo=dd.periodo)
and ((dd.importebasecontribucionos*0.0255>t.total_aportes and to_char(t.periodo,'MM') not in ('06','12')) or (dd.importebasecontribucionos*0.03825>t.total_aportes and to_char(t.periodo,'MM') in ('06','12')) );

--ACTUALIZAMOS TABLA CON TERERIZADOS
update temporal_apo_terc_agrupada t
set diferencia_tope= tt.diferencia_tope
from temporal_cuil_aportes tt
where t.cuit_contribuyente=tt.cuit_contribuyente
and t.cuil_aportante=tt.cuil_aportante
and t.periodo=tt.periodo;


create table temporal_cuit_aportes as
select o.cuit_contribuyente, o.periodo, sum(total_aportes)+sum(diferencia_tope) as total_aportes
from temporal_cuil_aportes o
group by o.cuit_contribuyente, o.periodo;


--ACTUALIZO TOTAL DE APORTES X EMPRESA
update temporal_contribuciones tc
set aporte_empresa=tt.total_aportes
from temporal_cuit_aportes tt
where tt.cuit_contribuyente=tc.cuit_contribuyente
and tt.periodo=tc.periodo;

--BORRO los 0 por División por 0
delete from temporal_contribuciones where aporte_empresa=0;


--ACTUALIZO CONTRIBUCIONES
update temporal_apo_terc_agrupada t
set contribucion=round(cast (c.contribucion*((t.aporte+t.diferencia_tope)/c.aporte_empresa) as numeric),2)
from temporal_contribuciones c
where c.cuit_contribuyente=t.cuit_contribuyente
and c.periodo=t.periodo;

--AGREGADO 10/04/2012 PARA EMPRESAS CON CONTRIBUCIONES SIN APORTES

create temp table emp_sin_aportes as 
select cuit_contribuyente, ddjj.cuil, o.periodo, ddjj.importebasecontribucionos,sum(importe) as total_contrib
from os_aportes_detalle o, detalle_declaracion_jurada ddjj, afi_tercerizadora_servicio ts
where cuil_aportante='00000000000'
and not exists (select 1 from os_aportes_detalle o2
            where o.cuit_contribuyente=o2.cuit_contribuyente
            and o.periodo=o2.periodo            
            and cuil_aportante<>'00000000000')
and (o.periodo>='20110101')
and o.concepto_transf<>'471'
and ddjj.cuit=o.cuit_contribuyente
and ddjj.periodo=o.periodo
and ddjj.secobligacion=(select max(secobligacion) from detalle_declaracion_jurada d2 where d2.cuit=ddjj.cuit and d2.cuil=ddjj.cuil and d2.periodo=ddjj.periodo)
and ddjj.cuil=ts.cuil_titular 
and ts.inte=0
and ts.id_tercerizadora = terc_p
and ts.fecha_inicio_pres<=lastDateOfMonth(o.periodo)
and (ts.fecha_fin_pres is null or (ts.fecha_fin_pres>=o.periodo and ts.fecha_inicio_pres <> ts.fecha_fin_pres))
and (ts.baja_fecha is null)
group by cuit_contribuyente, ddjj.cuil,ddjj.importebasecontribucionos,o.periodo;

alter table emp_sin_aportes  add total_remu numeric;
alter table emp_sin_aportes  add contrib_porc numeric;


update emp_sin_aportes  a set total_remu=(select sum(importebasecontribucionos)
			     from detalle_declaracion_jurada a2
			     where a2.cuit=a.cuit_contribuyente
			     and a2.periodo=a.periodo
			     and a2.secobligacion=(select max(secobligacion) from detalle_declaracion_jurada d2 where d2.cuit=a2.cuit and d2.cuil=a2.cuil and d2.periodo=a2.periodo)
			     group by cuit, periodo);			     

delete from emp_sin_aportes  where total_remu=0;

update emp_sin_aportes set contrib_porc=trunc(importebasecontribucionos*total_contrib/total_remu,2);

insert into temporal_apo_terc_agrupada ( cuit_contribuyente, cuil_aportante, periodo, aporte, contribucion, diferencia_tope)
select cuit_contribuyente, cuil, periodo, 0, contrib_porc, 0
from emp_sin_aportes;

CREATE TEMP TABLE liq_histo as 
select cuit, cuil, periodo_hab_d, round(cast(sum(aporte_n) as numeric),2) as aporte, round(cast(sum(contrib_n) as numeric),2) as contribucion
from liquidacion_historica_tercerizadoras
where id_terc=terc_p
group by cuit, cuil, periodo_hab_d;

delete from liq_histo where aporte=0 and contribucion=0;

update temporal_apo_terc_agrupada ta
set aporte=round(cast(ta.aporte-lh.aporte as numeric),2),
    contribucion=round(cast(ta.contribucion -lh.contribucion as numeric),2)
from liq_histo lh
where lh.cuit=ta.cuit_contribuyente
and lh.cuil=ta.cuil_aportante
and lh.periodo_hab_d=ta.periodo;

insert into temporal_apo_terc_agrupada(cuit_contribuyente, cuil_aportante, periodo, aporte,contribucion)
select lh.cuit, lh.cuil, lh.periodo_hab_d, sum(lh.aporte)*-1 , sum(lh.contribucion)*-1
from liq_histo lh
where periodo_hab_d >='20060101'
and not exists (select 1 
	          from temporal_apo_terc_agrupada ta
	          where lh.cuit=ta.cuit_contribuyente
		  and lh.cuil=ta.cuil_aportante
		  and lh.periodo_hab_d=ta.periodo)
group by lh.cuit, lh.cuil, lh.periodo_hab_d;

delete from temporal_apo_terc_agrupada where aporte=0 and contribucion=0;

alter table temporal_apo_terc_agrupada add total numeric;
alter table temporal_apo_terc_agrupada add total_terce numeric;

update temporal_apo_terc_agrupada set total=aporte+contribucion;
update temporal_apo_terc_agrupada set total_terce=trunc(total*cast(0.9 as numeric),2);

alter table temporal_apo_terc_agrupada add nombre varchar;
alter table temporal_apo_terc_agrupada add apellido varchar;
alter table temporal_apo_terc_agrupada add ingre_fecha date;
alter table temporal_apo_terc_agrupada add remuneracion numeric;

--TITULARES
update temporal_apo_terc_agrupada t set nombre=a.nombre, apellido=a.apellido, ingre_fecha=a.ingre_fecha
from afiliado a
where a.cuil=t.cuil_aportante;


update temporal_apo_terc_agrupada t
set remuneracion=case when o.remuneracionafectos>o.importebasecontribucionos then o.remuneracionafectos else o.importebasecontribucionos end
from detalle_declaracion_jurada o
where t.cuit_contribuyente=o.cuit
and t.cuil_aportante=o.cuil
and t.periodo=o.periodo
and secobligacion=(select max(secobligacion) from detalle_declaracion_jurada o2 where o2.cuit=o.cuit and o2.cuil=o.cuil and o2.periodo=o.periodo);

update temporal_apo_terc_agrupada t
set remuneracion=o.importe
from os_aportes_rem o
where o.cuit_contribuyente=t.cuit_contribuyente
and o.cuil_aportante=t.cuil_aportante
and o.periodo=t.periodo
and (t.remuneracion is null or t.remuneracion=0);

update temporal_apo_terc_agrupada set remuneracion=0 where remuneracion is null;

--ACTUALIZO LIQUIDACIONES HISTORICAS
/*INSERT INTO liquidacion_historica_tercerizadoras(
            id_terc, cuit, cuil, fecha_ingre, aporte_n, contrib_n, sueldo_n, 
            periodo_d, periodo_hab_d, TOTAL_TERC, FECHA_LIQ)
select 'CEU', cuit_contribuyente, cuil_aportante, ingre_fecha, aporte, contribucion, remuneracion, '20110301', periodo, TOTAL_TERCE, CURRENT_DATE
from temporal_apo_terc_agrupada 
WHERE APELLIDO IS NOT NULL;*/



return query select cuit_contribuyente,
       to_char(periodo,'MM/yyyy'),
       to_char(ingre_fecha,'MM/yyyy'),
       cuil_aportante,
       lpad(cast(round(cast(remuneracion as numeric),2) as varchar),11,' ') as remuneracion,
       lpad(cast(round(cast(aporte as numeric),2) as varchar),16,' ') as aporte,       
       lpad(cast(round(cast(contribucion as numeric),2) as varchar),11,' ') as contribucion,
       lpad(cast(round(cast(total as numeric),2) as varchar),11,' ') as total,
       lpad(cast(round(cast(total_terce as numeric),2) as varchar),9,' ') as total_terce,
       rpad(rtrim(apellido||', '||nombre),94)     
from temporal_apo_terc_agrupada
where apellido is not null;


end;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION liquidacion_tercerizadoras(character varying)
  OWNER TO postgres;


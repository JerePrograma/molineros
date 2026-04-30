CREATE OR REPLACE FUNCTION liquidacion_tercerizadoras_periodo(fecha_ini_v date,
 fecha_fin_v date,
 id_terce_v character varying,
 id_aporte_v integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
BEGIN

truncate temporal_contribuciones;
truncate temporal_aportes;
truncate temporal_aportes_final;
drop table total_aportes_empresa;

--CONTRIBUCIONES
insert into temporal_contribuciones (cuit_contribuyente, periodo, total_contribucion, fecha_ini, fecha_fin)
select cuit_contribuyente, periodo, sum(importe) as total_contribucion, fecha_ini_v, fecha_fin_v
from os_aportes_detalle o, conceptos_transf_os c 
where o.fecha_proceso >=fecha_ini_v and o.fecha_proceso <=fecha_fin_v
and o.concepto_transf=c.cod_conc
and liquidable=TRUE
and o.cuil_aportante='00000000000'
group by cuit_contribuyente, cuil_aportante, periodo;

--APORTES (SE TOMAN TODOS DE LAS EMPRESAS INFORMADAS EN ARCHIVO, SIN IMPORTAR EL  PERIODO
insert into temporal_aportes(cuil_aportante, periodo, fecha_ingreso, cuit_contribuyente, sueldo, contribucion_empresa, total_contrib, omint_calculado,
			     nomape, aporte, coeficiente, tercerizado, contribucion_empresa_total, fecha_ini, fecha_fin)
select cuil_aportante, t.periodo, cast (null as date) as fecha_ingreso, t.cuit_contribuyente, cast (0.00 as double precision) as sueldo,   
       cast (0.00 as double precision) as contribucion_empresa, 
       cast (0.00 as double precision) as total_contrib, cast (0.00 as double precision) as omint_calculado, cast ('' as varchar) as nomape,
       sum(importe) as aporte, cast (0.00 as double precision) as coeficiente, false as tercerizado, cast (0.00 as double precision) as contribucion_empresa_total,
       fecha_ini_v, fecha_fin_v
from os_aportes_detalle o, conceptos_transf_os c, temporal_contribuciones t 
where o.concepto_transf=c.cod_conc
and liquidable=TRUE
and t.cuit_contribuyente=o.cuit_contribuyente
and t.periodo=o.periodo
and o.cuil_aportante<>'00000000000'
group by t.cuit_contribuyente, o.cuil_aportante, t.periodo;

--TOTAL APORTES POR EMPRESA
create temporary table total_aportes_empresa as
select cuit_contribuyente, periodo, sum(aporte) as total_empresa
from temporal_aportes
group by cuit_contribuyente, periodo;

--CALCULO y APLICO COEFICIENTES
update temporal_aportes t
set coeficiente=aporte/total_empresa,
    contribucion_empresa=trunc(cast (c.total_contribucion*(aporte/total_empresa) as numeric),2)
from total_aportes_empresa e, temporal_contribuciones c
where t.cuit_contribuyente=e.cuit_contribuyente
and t.cuit_contribuyente=c.cuit_contribuyente
and t.periodo=c.periodo
and total_empresa<>0
and t.periodo=e.periodo;

--PONGO APORTES EN 0
update temporal_aportes set aporte=0;

--CARGO APORTES DE LOS PERIODOS A LIQUIDAR
insert into temporal_aportes (cuil_aportante, periodo,cuit_contribuyente, aporte)
select cuil_aportante, periodo, cuit_contribuyente, sum(importe)
from os_aportes_detalle o, conceptos_transf_os c 
where o.fecha_proceso >=fecha_ini_v and o.fecha_proceso <=fecha_fin_v
and o.concepto_transf=c.cod_conc
and liquidable=TRUE
and o.cuil_aportante<>'00000000000'
group by cuit_contribuyente, cuil_aportante, periodo;

insert into temporal_aportes_final (cuil_aportante, periodo, fecha_ingreso, cuit_contribuyente, sueldo, contribucion_empresa, total_contrib,
				    omint_calculado, nomape, aporte, coeficiente, tercerizado, fecha_ini, fecha_fin)
select cuil_aportante, t.periodo, cast (null as date) as fecha_ingreso, t.cuit_contribuyente, cast (0.00 as double precision) as sueldo,   
       sum(contribucion_empresa) as contribucion_empresa, cast (0.00 as double precision) as total_contrib, cast (0.00 as double precision) as omint_calculado, 
       cast ('' as varchar) as nomape,sum(aporte) as aporte, cast (0.00 as double precision) as coeficiente, false as tercerizado, fecha_ini_v, fecha_fin_v
from temporal_aportes t
group by cuit_contribuyente, cuil_aportante, periodo;



--FILTRO OMINT
update temporal_aportes_final t
set tercerizado=true
from afi_tercerizadora_servicio ts 
where id_tercerizadora = id_terce_v 
and ts.cuil_titular=t.cuil_aportante
and ts.fecha_inicio_pres<=lastDateOfMonth(t.periodo)
and (ts.fecha_fin_pres is null or (ts.fecha_fin_pres>=t.periodo and ts.fecha_inicio_pres <> ts.fecha_fin_pres))
and (ts.baja_fecha is null);


update temporal_aportes_final t
set tercerizado=true,
    fecha_ingreso=ts.fecha_inicio_pres
from afi_tercerizadora_servicio ts, afiliado a
where t.cuil_aportante=a.cuil
and a.aportante_titular<>0
and ts.cuil_titular=a.cuil_titular
and id_tercerizadora =id_terce_v
and ts.fecha_inicio_pres<=lastDateOfMonth(t.periodo)
and (ts.fecha_fin_pres is null or (ts.fecha_fin_pres>=t.periodo and ts.fecha_inicio_pres <> ts.fecha_fin_pres))
and (ts.baja_fecha is null);

--APORTE
update temporal_aportes_final t
set tercerizado=false
from afi_aportes a
where t.cuil_aportante=a.cuil_titular
and a.id_aporte=id_aporte_v
and (a.fecha_ingre>lastDateOfMonth(t.periodo) or
(a.fecha_egre is not null and (a.fecha_egre<t.periodo or a.fecha_egre = a.fecha_ingre)))
and (a.baja_fecha is null)
and t.tercerizado=true;
--LABORAL POR AHORA NO (COMPLETAR BATCH PARA CARGAR Y ACTUALIZAR)
/*select a.cuil_titular, t.periodo
from temporal_aportes_total_omint t, afi_situ_laboral a
where t.cuil_aportante=a.cuil_titular
and t.cuit_contribuyente=a.cuit
and a.fecha_ingre<=lastDateOfMonth(t.periodo)
and (a.fecha_egre is null or (a.fecha_egre>=t.periodo and a.fecha_egre <> a.fecha_ingre))
and (a.baja_fecha is null)
and t.tercerizado=true

select *
from temporal_aportes_total_omint t, afi_situ_laboral a
where t.cuil_aportante=a.cuil_titular
and t.cuit_contribuyente=a.cuit
and t.tercerizado=true
and (a.fecha_ingre>lastDateOfMonth(t.periodo) or 
(a.fecha_egre is not null and (a.fecha_egre<t.periodo or a.fecha_egre = a.fecha_ingre)))
and (a.baja_fecha is null)*/

--Actualizo sueldos desde DDJJ 
update temporal_aportes_final o
set sueldo=d.remuneracionafectos
from detalle_declaracion_jurada d
where o.cuil_aportante=d.cuil
and o.cuit_contribuyente=d.cuit
and d.original=(select max(original) from detalle_declaracion_jurada d2 where d2.cuil=d.cuil and d2.periodo=d.periodo and d.cuit=d2.cuit)
and o.periodo=d.periodo
and o.tercerizado=true;

--Actualizo los sueldos que faltaron desde REM de transferencias
update temporal_aportes_final o
set sueldo=r.importe
from os_aportes_rem r
where o.cuil_aportante=r.cuil_aportante
and o.cuit_contribuyente=r.cuit_contribuyente
and o.periodo=r.periodo
and o.tercerizado=true
and r.fecha_recauda=(select max(fecha_recauda) 
		     from os_aportes_rem o2 
		     where o2.cuil_aportante=r.cuil_aportante 
		     and o2.cuit_contribuyente=r.cuit_contribuyente		     
		     and o2.periodo=r.periodo)
and sueldo=0;

--Actualizo nombres y apellidos y fecha de ingreso
update temporal_aportes_final o
set nomape=a.apellido || ', ' || a.nombre
from afiliado a
where a.cuil_titular=o.cuil_aportante 
--and a.aportante_titular=1
and o.tercerizado=true;

update temporal_aportes_final o
set fecha_ingreso=a.ingre_fecha
from afiliado a
where a.cuil_titular=o.cuil_aportante
and o.tercerizado=true
and o.fecha_ingreso is null;

update temporal_aportes_final o
set nomape=a.apellido || ', ' || a.nombre
from afiliado a
where a.cuil=o.cuil_aportante
and o.nomape=''
--and a.aportante_titular=1
and o.tercerizado=true;

--Total Contribucion
update temporal_aportes_final
set total_contrib=case when aporte is null then 0 else aporte end+case when contribucion_empresa is null then 0 else contribucion_empresa end
where tercerizado=true;

--Total OMINT
update temporal_aportes_final
set omint_calculado=trunc(cast(total_contrib as numeric)*0.9, 2)
where tercerizado=true;

/*select cuit_contribuyente, 
       to_char(periodo,'MM/yyyy') as periodo,
       to_char(fecha_ingreso,'MM/yyyy') as fecha_ingreso,
       cuil_aportante,
       to_char(sueldo,'9999999990D00') as sueldo,
       to_char(aporte,'99999999999990D00') as aporte,
       case when contribucion_empresa is null then to_char(0,'999999990D00') else  to_char(contribucion_empresa,'999999990D00') end as contribucion_empresa ,
       to_char(total_contrib,'99999990D00') as total_contrib,
       to_char(omint_calculado,'99999990D00') as omint_calculado,
       rpad(nomape,83,'') as nomape
from temporal_aportes_final 
where tercerizado=true 
and omint_calculado<>0;*/

return 1;

END;
$BODY$;


ALTER FUNCTION public.liquidacion_tercerizadoras_periodo(fecha_ini_v date, fecha_fin_v date, id_terce_v character varying, id_aporte_v integer) OWNER TO postgres;

--

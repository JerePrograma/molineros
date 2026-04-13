create type reporte_aportes_contrib_empresa_periodo as(cuit_contribuyente varchar,
cuil_aportante varchar,
periodo date,
aporte numeric,
contribucion numeric,
cant_afiliados_declarados integer,
cant_afiliados_pagados integer,
rem_pagada numeric ,
rem_declarada numeric,
pagado numeric,
calculado numeric,
porc numeric,
razon varchar,
localidad varchar,
provincia_id varchar,
codigopostal varchar,
numero varchar,
ramo integer,
apellido varchar,
nombre varchar,
tercerizadora varchar,
fecha_recauda date);

-- Function: reporte_aportes_contrib_empresa_periodo(character varying, date, date)

-- DROP FUNCTION reporte_aportes_contrib_empresa_periodo(character varying, date, date);

CREATE OR REPLACE FUNCTION reporte_aportes_contrib_empresa_periodo(cuit_p character varying, periodo_desde date, periodo_hasta date)
  RETURNS SETOF reporte_aportes_contrib_empresa_periodo AS
$BODY$
BEGIN
drop table if exists temp_reporte_estudio;
drop table if exists temporal_contribuciones;
drop table if exists sueldo_temp;
drop table if exists temporal_total_aportes;
drop table if exists cant_afil_empresa;
drop table if exists cant_afil_empresa_pagado;


--TOTAL DE APORTES DEL ULTIMO PERIODO
CREATE temp table temp_reporte_estudio AS 
select o.cuit_contribuyente, o.cuil_aportante, o.periodo, sum(importe) as aporte, 0.0 as contribucion 
--into temp table temp_reporte_estudio
from os_aportes_detalle o, conceptos_transf_os c
where o.concepto_transf=c.cod_conc
and o.cuit_contribuyente=cuit_p
and o.cuil_aportante<>'00000000000'
and (o.periodo >='20060101' or o.fecha_proceso >= '20101101')
and (periodo_desde is null or (periodo_desde is not null and o.periodo>=periodo_desde))
and (periodo_hasta is null or (periodo_hasta is not null and o.periodo<=periodo_hasta))
group by o.cuit_contribuyente, o.cuil_aportante, o.periodo
UNION 
select o.cuit_contribuyente, o.cuil_aportante, o.periodo, sum(importe) as aporte, 0.0 as contribucion
from os_aportes_detalle o, conceptos_transf_os c, afiliado a
where o.concepto_transf=c.cod_conc
and o.cuit_contribuyente=cuit_p
and o.cuil_aportante=a.cuil
and a.cuil<>a.cuil_titular
and a.aportante_titular<>0
and o.cuil_aportante<>'00000000000'
and (o.periodo >='20060101' or o.fecha_proceso >= '20101101')
and (periodo_desde is null or (periodo_desde is not null and o.periodo>=periodo_desde))
and (periodo_hasta is null or (periodo_hasta is not null and o.periodo<=periodo_hasta))
group by o.cuit_contribuyente, o.cuil_aportante, o.periodo;

--CONTRIBUCIONES QUE TENGAN APORTES
CREATE temp table temporal_contribuciones AS
select o.cuit_contribuyente, o.periodo, sum(o.importe) as contribucion, 0.00 as aporte_empresa
--into temp table temporal_contribuciones
from os_aportes_detalle o, conceptos_transf_os c
where o.concepto_transf=c.cod_conc
and o.cuil_aportante='00000000000'
and o.cuit_contribuyente=cuit_p
and (o.periodo >='20060101' or o.fecha_proceso >= '20101101')
and (periodo_desde is null or (periodo_desde is not null and o.periodo>=periodo_desde))
and (periodo_hasta is null or (periodo_hasta is not null and o.periodo<=periodo_hasta))
group by o.cuit_contribuyente, o.periodo;


--TOTALES APORTES POR EMPRESA
CREATE temporary table temporal_total_aportes AS
select o.cuit_contribuyente, o.periodo, sum(o.importe) as total_aportes
--into temporary table temporal_total_aportes
from temporal_contribuciones ta, os_aportes_detalle o, conceptos_transf_os c
where o.cuit_contribuyente=ta.cuit_contribuyente
and o.periodo=ta.periodo
and o.concepto_transf=c.cod_conc
and o.cuil_aportante<>'00000000000'
and (o.periodo >='20060101' or o.fecha_proceso >= '20101101')
and (periodo_desde is null or (periodo_desde is not null and o.periodo>=periodo_desde))
and (periodo_hasta is null or (periodo_hasta is not null and o.periodo<=periodo_hasta))
group by o.cuit_contribuyente, o.periodo;

--ACTUALIZO APORTE TOTAL EN CONTRIBUCIONES
update temporal_contribuciones tc
set aporte_empresa=tt.total_aportes
from temporal_total_aportes tt
where tt.cuit_contribuyente=tc.cuit_contribuyente
and tt.periodo=tc.periodo;

--CALCULO CONTRIB X AFILIADO DISTRIBUYENDO CONTRIBUCION SEGUN APORTE PORCENTUAL DE AFILIADOS
update temp_reporte_estudio t
set contribucion=trunc(cast (c.contribucion*(t.aporte/c.aporte_empresa) as numeric),2)
from temporal_contribuciones c
where c.cuit_contribuyente=t.cuit_contribuyente
and c.periodo=t.periodo;

--INSERTO LAS CONTRIBUCIONES QUE FALTAN
insert into temp_reporte_estudio(cuit_contribuyente, cuil_aportante, periodo, aporte, contribucion)
select cuit_contribuyente, '00000000000',periodo,0,contribucion
from temporal_contribuciones tc
where not exists (select 1 from temp_reporte_estudio tr
		  where tr.cuit_contribuyente=tc.cuit_contribuyente
		  and tr.periodo=tc.periodo);


alter table temp_reporte_estudio add column cant_afiliados int default 0;

--CANT X EMPRESA DECLARADO
CREATE temp table cant_afil_empresa AS
select periodo, cuit, count(*)
--into temp cant_afil_empresa
from (select distinct periodo, cuit, cuil--, count(*) 
from detalle_declaracion_jurada dd
where cuit=cuit_p
and secobligacion=(select max(secobligacion) from detalle_declaracion_jurada d3 where d3.cuit=dd.cuit and d3.cuil=dd.cuil and d3.periodo=dd.periodo)
and (periodo_desde is null or (periodo_desde is not null and dd.periodo>=periodo_desde))
and (periodo_hasta is null or (periodo_hasta is not null and dd.periodo<=periodo_hasta))) a
group by periodo, cuit
order by periodo;


--CANT X EMPRESA PAGADO
CREATE temp TABLE cant_afil_empresa_pagado AS
select periodo, cuit_contribuyente, count(*)
--into temp cant_afil_empresa_pagado
from (select distinct periodo, cuit_contribuyente, cuil_aportante 
      from os_aportes_detalle o,conceptos_transf_os c
      where o.cuit_contribuyente=cuit_p 
      and o.concepto_transf=c.cod_conc
      and cuil_aportante<>'00000000000'
      and (periodo_desde is null or (periodo_desde is not null and o.periodo>=periodo_desde))
      and (periodo_hasta is null or (periodo_hasta is not null and o.periodo<=periodo_hasta))) a
group by periodo, cuit_contribuyente
order by periodo;



alter table temp_reporte_estudio add column cant_afiliados_pagados integer default 0;

--AFILIADOS X EMPRESA PAGADO
update temp_reporte_estudio t
set cant_afiliados_pagados=c.count
from cant_afil_empresa_pagado c
where c.cuit_contribuyente=t.cuit_contribuyente
and c.periodo=t.periodo;

alter table temp_reporte_estudio add column rem_pagada double precision default 0;
alter table temp_reporte_estudio add column rem_declarada double precision default 0;

--OBTENGO TODOS LOS SUELDOS 
CREATE temp table sueldo_temp AS
select distinct cuit,cuil,periodo,remuneracionafectos
--into temp table sueldo_temp
from detalle_declaracion_jurada dd
where cuit=cuit_p
and (periodo_desde is null or (periodo_desde is not null and dd.periodo>=periodo_desde))
and (periodo_hasta is null or (periodo_hasta is not null and dd.periodo<=periodo_hasta))
and secobligacion=(select max(secobligacion) from detalle_declaracion_jurada d3 where d3.cuit=dd.cuit and d3.cuil=dd.cuil and d3.periodo=dd.periodo)
order by periodo;


--SUELDOS AFILIADOS DECLARADAS
update temp_reporte_estudio t
set rem_declarada=d.remuneracionafectos
from sueldo_temp d
where d.cuit=t.cuit_contribuyente
and d.cuil=t.cuil_aportante
and d.periodo=t.periodo;

--PERIODOS DECLARADOS QUE NO TUVIERON APORTES
insert into temp_reporte_estudio(periodo, cuit_contribuyente, cuil_aportante, rem_declarada)
select periodo, cuit, cuil, remuneracionafectos
from sueldo_temp s
where not exists (select 1 from temp_reporte_estudio t where t.cuit_contribuyente=s.cuit and t.cuil_aportante=s.cuil
						       and t.periodo=s.periodo);

--AFILIADOS X EMPRESA DECLARADO
update temp_reporte_estudio t
set cant_afiliados=c.count
from cant_afil_empresa c
where c.cuit=t.cuit_contribuyente
and c.periodo=t.periodo;

--ESTO HAY QUE HACERLO?? PARA QUE?--BORRO LOS REGISTROS DE APORTES...
--delete from temp_reporte_estudio where cuil_aportante='00000000000';
update temp_reporte_estudio t
set rem_declarada=(select sum(d.remuneracionafectos)
		   from sueldo_temp d
		   where d.cuit=t.cuit_contribuyente
	           and d.periodo=t.periodo
	           group by periodo)
where cuil_aportante='00000000000';
	      
    

--SUELDOS AFILIADOS REM_PAGADA
update temp_reporte_estudio t
set rem_pagada=o.importe
from os_aportes_rem o
where o.cuit_contribuyente=t.cuit_contribuyente
and o.cuil_aportante=t.cuil_aportante
and o.periodo=t.periodo;

alter table temp_reporte_estudio add column pagado double precision default 0;

--TOTAL PAGADO
update temp_reporte_estudio set pagado=aporte+contribucion;

alter table temp_reporte_estudio add column calculado double precision default 0;

--SUELDOS < a 2400 se calcula el 8.1 SI EXISTE DECLARADA
update temp_reporte_estudio
set calculado=rem_declarada*0.081
where rem_declarada is not null
and rem_declarada>0
and ((periodo>='20081201' and rem_declarada <= 2400) or (periodo<'20081201' and rem_declarada<=1000));

--SUELDOS < a 2400 se calcula el 8.1 SI NO EXISTE DECLARADA y EXISTE PAGADA
update temp_reporte_estudio
set calculado=rem_pagada*0.081
where (rem_declarada is null or  rem_declarada=0)
and rem_pagada is not null
and rem_pagada >0
and ((periodo>='20081201' and rem_pagada <= 2400) or (periodo<'20081201' and rem_pagada<=1000));

--SUELDOS > a 2400 se calcula el 7.65 SI EXISTE DECLARADA
update temp_reporte_estudio
set calculado=rem_declarada*0.0765
where rem_declarada is not null
and rem_declarada>0
and ((periodo>='20081201' and rem_declarada >= 2400) or (periodo<'20081201' and rem_declarada>=1000));

--SUELDOS > a 2400 se calcula el 7.65 SI NO EXISTE DECLARADA y EXISTE PAGADA
update temp_reporte_estudio
set calculado=rem_pagada*0.0765
where (rem_declarada is null or  rem_declarada=0)
and rem_pagada is not null
and rem_pagada >0
and ((periodo>='20081201' and rem_pagada >= 2400) or (periodo<'20081201' and rem_pagada>=1000));

alter table temp_reporte_estudio add column porc double precision default 0;
--CALCULO PORC = CALCULADO/DECLARADO O REM
update temp_reporte_estudio
set porc=(pagado/calculado)*100
where calculado<>0;
/*where (rem_declarada is null or rem_declarada=0)
and rem_pagada is not null
and rem_pagada >0;*/


--REPARTO LA CONTRIBUCION PAGADA ENTRE LOS EMPLEADOS
update temp_reporte_estudio set pagado=0 where pagado is null;
update temp_reporte_estudio t
set pagado=t.pagado+((t.rem_declarada/t2.rem_declarada)*t2.pagado)
from temp_reporte_estudio t2
where t.cuil_aportante<>'00000000000'
and t2.cuil_aportante='00000000000'
and t2.periodo=t.periodo;

update temp_reporte_estudio set cant_afiliados=0 where cant_afiliados is null;
update temp_reporte_estudio set rem_declarada=0 where rem_declarada is null;

alter table temp_reporte_estudio add column razon varchar;
alter table temp_reporte_estudio add column localidad varchar;
alter table temp_reporte_estudio add column provincia_id varchar;
alter table temp_reporte_estudio add column codigopostal varchar;
alter table temp_reporte_estudio add column numero varchar;
alter table temp_reporte_estudio add column ramo integer;

update temp_reporte_estudio tre
set razon=dpc.razonsocial,
localidad=dpc.localidad,
provincia_id=p.detalle,
codigopostal=dpc.codigopostal
from detalle_padron_contribuyentes dpc, provincia p
where tre.cuit_contribuyente=cast(dpc.cuit as varchar)
and case when dpc.provincia<>'' then cast(dpc.provincia as integer) else 1 end=p.id_provincia;

update temp_reporte_estudio tre
set ramo=e.id_ramo_empresa
from empresa e
where e.cuit=tre.cuit_contribuyente;

alter table temp_reporte_estudio add column apellido varchar;
alter table temp_reporte_estudio add column nombre varchar;

update temp_reporte_estudio t
set apellido=a.apellido,
    nombre=a.nombre
from afiliado a
where t.cuil_aportante=a.cuil_titular
and a.inte=0;

update temp_reporte_estudio set porc=0 where porc is null;
update temp_reporte_estudio set aporte=0 where aporte is null;
update temp_reporte_estudio set pagado=0 where pagado is null;
update temp_reporte_estudio set contribucion=0 where contribucion is null;

--FECHA RECAUDA
alter table temp_reporte_estudio add fecha_recauda date;

update temp_reporte_estudio t
set fecha_recauda=o.fecha_recauda
from os_aportes_detalle o
where t.cuit_contribuyente=o.cuit_contribuyente
and   t.periodo=o.periodo
and o.fecha_recauda=(select max(o2.fecha_recauda)
                   from os_aportes_detalle o2
                   where o2.cuit_contribuyente=o.cuit_contribuyente
                   and o2.periodo=o.periodo);

--IMPORTA LA FECHA PAGADA DE LA CONTRIBUCION? POR AHORA LAS ELIMINO...
delete from temp_reporte_estudio where cuil_aportante='00000000000';

--adicionales
update temp_reporte_estudio te
set aporte=aporte+(ddj.aporteadicionalos)*0.9,
    contribucion=contribucion+(ddj.importeadicionalos)*0.9,
    calculado=calculado+(ddj.aporteadicionalos+ddj.importeadicionalos)*0.9
from detalle_declaracion_jurada ddj
where ddj.cuit=te.cuit_contribuyente
and ddj.cuil=te.cuil_aportante
and ddj.periodo=te.periodo
and secobligacion=(select max(secobligacion) from detalle_declaracion_jurada d3 where d3.cuit=ddj.cuit and d3.cuil=ddj.cuil and d3.periodo=ddj.periodo);


--AGREGO TERCERIZADORA
alter table temp_reporte_estudio add tercerizadora varchar;

update temp_reporte_estudio te
set tercerizadora=t.descripcion
from afi_tercerizadora_servicio ats, tercerizadora_servicio t
where ats.cuil_titular=te.cuil_aportante
and ats.inte=0
and (ats.fecha_fin_pres is null or ats.fecha_fin_pres>current_date)
and (ats.baja_fecha is null or ats.baja_fecha>current_date)
and ats.id_tercerizadora=t.id_tercerizadora;



return query 
select cuit_contribuyente,
cuil_aportante,
periodo ,
round(cast(aporte as numeric),2),
round(cast(contribucion as numeric),2),
cant_afiliados,
cant_afiliados_pagados ,
round(cast(rem_pagada as numeric),2) ,
round(cast(rem_declarada as numeric),2) ,
round(cast(pagado as numeric),2) ,
round(cast(calculado as numeric),2) ,
round(cast(porc as numeric),2) ,
razon ,
localidad ,
provincia_id ,
codigopostal ,
numero ,
ramo ,
apellido ,
nombre,
tercerizadora,
fecha_recauda 
from temp_reporte_estudio order by periodo, cuil_aportante;

END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE

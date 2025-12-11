/*create type reporte_deuda_nomina_empresa_periodo as (
cuit_contribuyente varchar,
	cuil_aportante varchar,
	periodo date,
	fecha_recauda date,
	aporte numeric,
	contribucion numeric,
	rem_pagada numeric,
	rem_declarada numeric,
	calculado numeric,
	pagado numeric,
	deuda numeric,
	apellido varchar,
	nombre varchar)*/

-- Function: reporte_deuda_nomina_empresa(character varying, date, date)

-- DROP FUNCTION reporte_deuda_nomina_empresa(character varying, date, date);

CREATE OR REPLACE FUNCTION reporte_deuda_nomina_empresa(cuit_p character varying, periodo_desde date, periodo_hasta date)
  RETURNS SETOF reporte_deuda_nomina_empresa_periodo AS
$BODY$
BEGIN
drop table if exists temp_reporte_estudio;
drop table if exists temporal_contribuciones;
drop table if exists sueldo_temp;
drop table if exists temporal_total_aportes;
drop table if exists cant_afil_empresa;
drop table if exists cant_afil_empresa_pagado;


--APORTES PAGADOS
CREATE temp table temp_reporte_estudio AS 
select o.cuit_contribuyente, o.cuil_aportante, o.fecha_recauda, o.periodo, sum(importe) as aporte, 0.0 as contribucion 
--into temp table temp_reporte_estudio
from os_aportes_detalle o, conceptos_transf_os c
where o.concepto_transf=c.cod_conc
and o.cuit_contribuyente=cuit_p
and o.cuil_aportante<>'00000000000'
and (o.periodo >='20040301' or o.fecha_transf >='20050701')
and (periodo_desde is null or (periodo_desde is not null and o.periodo>=periodo_desde))
and (periodo_hasta is null or (periodo_hasta is not null and o.periodo<=periodo_hasta))
group by o.cuit_contribuyente, o.cuil_aportante,o.fecha_recauda, o.periodo
UNION 
select o.cuit_contribuyente, o.cuil_aportante, o.fecha_recauda, o.periodo, sum(importe) as aporte, 0.0 as contribucion
from os_aportes_detalle o, conceptos_transf_os c, afiliado a
where o.concepto_transf=c.cod_conc
and o.cuit_contribuyente=cuit_p
and o.cuil_aportante=a.cuil
and a.cuil<>a.cuil_titular
and a.aportante_titular<>0
and o.cuil_aportante<>'00000000000'
and (o.periodo >='20040301' or o.fecha_transf >='20050701')
and (periodo_desde is null or (periodo_desde is not null and o.periodo>=periodo_desde))
and (periodo_hasta is null or (periodo_hasta is not null and o.periodo<=periodo_hasta))
group by o.cuit_contribuyente, o.cuil_aportante, o.fecha_recauda, o.periodo;

--CONTRIBUCIONES PAGADAS
CREATE temp table temporal_contribuciones AS
select o.cuit_contribuyente, o.fecha_recauda, o.periodo, sum(o.importe) as contribucion, 0.00 as aporte_empresa
--into temp table temporal_contribuciones
from os_aportes_detalle o, conceptos_transf_os c
where o.concepto_transf=c.cod_conc
and o.cuil_aportante='00000000000'
and o.cuit_contribuyente=cuit_p
and (periodo_desde is null or (periodo_desde is not null and o.periodo>=periodo_desde))
and (periodo_hasta is null or (periodo_hasta is not null and o.periodo<=periodo_hasta))
group by o.cuit_contribuyente, o.fecha_recauda, o.periodo;


--TOTALES APORTES PAGADOS POR EMPRESA
CREATE temporary table temporal_total_aportes AS
select o.cuit_contribuyente, o.periodo, sum(o.importe) as total_aportes
--into temporary table temporal_total_aportes
from temporal_contribuciones ta, os_aportes_detalle o, conceptos_transf_os c
where o.cuit_contribuyente=ta.cuit_contribuyente
and o.periodo=ta.periodo
and o.concepto_transf=c.cod_conc
and o.cuil_aportante<>'00000000000'
and (o.periodo >='20040301' or o.fecha_transf >='20050701')
and (periodo_desde is null or (periodo_desde is not null and o.periodo>=periodo_desde))
and (periodo_hasta is null or (periodo_hasta is not null and o.periodo<=periodo_hasta))
group by o.cuit_contribuyente, o.periodo;

--ACTUALIZO APORTE TOTAL POR EMPRESA EN CONTRIBUCIONES
update temporal_contribuciones tc
set aporte_empresa=tt.total_aportes
from temporal_total_aportes tt
where tt.cuit_contribuyente=tc.cuit_contribuyente
and tt.periodo=tc.periodo;

--CALCULO CONTRIB PORCENTUALES X EMPRESA
update temp_reporte_estudio t
set contribucion=trunc(cast (c.contribucion*(t.aporte/c.aporte_empresa) as numeric),2)
from temporal_contribuciones c
where c.cuit_contribuyente=t.cuit_contribuyente
and c.periodo=t.periodo
and c.fecha_recauda=t.fecha_recauda;

--PAGOS QUE NO SE TOMARON AL 100 PORQUE FALTARIA APORTANTES
insert into temp_reporte_estudio(cuit_contribuyente, cuil_aportante, fecha_recauda, periodo, aporte, contribucion)
select tc.cuit_contribuyente, '00000000000', tc.fecha_recauda, tc.periodo,0, tc.contribucion-tr.contribucion
from temporal_contribuciones tc, (select cuit_contribuyente, fecha_recauda, periodo, sum(contribucion) as contribucion from temp_reporte_estudio tr
				  where contribucion>0
				  group by cuit_contribuyente, fecha_recauda, periodo) tr
where tc.cuit_contribuyente=tr.cuit_contribuyente
and tc.fecha_recauda=tr.fecha_recauda
and tc.periodo=tr.periodo
and tc.contribucion-tr.contribucion>1;


--INSERTO LAS CONTRIBUCIONES QUE FALTAN
insert into temp_reporte_estudio(cuit_contribuyente, cuil_aportante, fecha_recauda, periodo, aporte, contribucion)
select cuit_contribuyente, '00000000000', fecha_recauda, periodo,0,contribucion
from temporal_contribuciones tc
where not exists (select 1 from temp_reporte_estudio tr
		  where tr.cuit_contribuyente=tc.cuit_contribuyente
		  and tr.periodo=tc.periodo
		  and tr.fecha_recauda=tc.fecha_recauda);


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


--ESTO HAY QUE HACERLO??? SACO DEL REPORTE LOS CAMPOS DE CUIL=000000
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

alter table temp_reporte_estudio add column calculado double precision default 0;

--CALCULADO
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

--SUELDOS > a 2400 se calcula el 7.65 SI EXISTE DECLARADA. PRIMERO APORTES
update temp_reporte_estudio te
set calculado=case when rem_declarada<=tope_remuneracion then rem_declarada else tope_remuneracion end*0.0255
from topes_aportes_os tope
where tope.fecha_desde<=te.periodo
and tope.fecha_hasta>=te.periodo
and rem_declarada is not null
and rem_declarada>0
and ((periodo>='20081201' and rem_declarada >= 2400) or (periodo<'20081201' and rem_declarada>=1000));




--LUEGO CONTRIBUCIONES
update temp_reporte_estudio
set calculado=calculado+(rem_declarada*0.0510)
where rem_declarada is not null
and rem_declarada>0
and ((periodo>='20081201' and rem_declarada >= 2400) or (periodo<'20081201' and rem_declarada>=1000));


--SUELDOS > a 2400 se calcula el 7.65 SI NO EXISTE DECLARADA y EXISTE PAGADA PRIMERO APORTES
update temp_reporte_estudio te
set calculado=case when rem_pagada<tope_remuneracion then rem_pagada else tope_remuneracion end*0.0255
from topes_aportes_os tope 
where tope.fecha_desde>=te.periodo
and tope.fecha_hasta<=te.periodo
and (rem_declarada is null or  rem_declarada=0)
and rem_pagada is not null
and rem_pagada >0
and ((periodo>='20081201' and rem_pagada >= 2400) or (periodo<'20081201' and rem_pagada>=1000));

--LUEGO CONTRIBUCIONES
update temp_reporte_estudio
set calculado=calculado+(rem_pagada*0.0510)
where (rem_declarada is null or  rem_declarada=0)
and rem_pagada is not null
and rem_pagada >0
and ((periodo>='20081201' and rem_pagada >= 2400) or (periodo<'20081201' and rem_pagada>=1000));

--CONTRIBUCION Y APORTE ADICIONAL
update temp_reporte_estudio te
set calculado=calculado+(ddj.importeadicionalos*0.9)+(ddj.aporteadicionalos*0.9)
from detalle_declaracion_jurada ddj
where secobligacion=(select max(secobligacion) from detalle_declaracion_jurada d3 where d3.cuit=ddj.cuit and d3.cuil=ddj.cuil and d3.periodo=ddj.periodo)
and ddj.cuit=te.cuit_contribuyente
and ddj.cuil=te.cuil_aportante
and ddj.periodo=te.periodo;


update temp_reporte_estudio set rem_declarada=0 where rem_declarada is null;


--TOTAL PAGADO
alter table temp_reporte_estudio add column pagado double precision default 0;

update temp_reporte_estudio set pagado=aporte+contribucion;

update temp_reporte_estudio set pagado=0 where pagado is null;

--REPARTO LA CONTRIBUCION PAGADA ENTRE LOS EMPLEADOS SEGUN LA FECHA DE RECAUDA
update temp_reporte_estudio t
set pagado=t.pagado+((t.rem_declarada/t2.rem_declarada)*t2.pagado),
    fecha_recauda=t2.fecha_recauda
from temp_reporte_estudio t2
where t.cuil_aportante<>'00000000000'
and t2.cuil_aportante='00000000000'
and t2.periodo=t.periodo
and t2.pagado<>0 
and t2.rem_declarada<>0 
and (t2.fecha_recauda=t.fecha_recauda or t.fecha_recauda is null);

--LOS QUE NO TIENEN LA MISMA FECHA DE RECAUDA, DEBO INSERTARLOS
insert into temp_reporte_estudio(cuit_contribuyente, cuil_aportante, fecha_recauda, periodo,calculado, contribucion, pagado,rem_declarada, rem_pagada)
select distinct t.cuit_contribuyente, t.cuil_aportante, t2.fecha_recauda, t.periodo, t.calculado, 0+((t.rem_declarada/t2.rem_declarada)*t2.pagado), 
       0+((t.rem_declarada/t2.rem_declarada)*t2.pagado),t.rem_declarada, t.rem_pagada
from temp_reporte_estudio t, temp_reporte_estudio t2 
where t.cuit_contribuyente=t2.cuit_contribuyente
and t2.cuil_aportante='00000000000'
and t.cuil_aportante<>'00000000000'
and t2.periodo=t.periodo
and t2.fecha_recauda is not null
and t2.fecha_recauda<>t.fecha_recauda
and t2.rem_declarada<>0 
and not exists (select 1 from temp_reporte_estudio t3 where t3.cuil_aportante<>'00000000000' and t3.cuit_contribuyente=t2.cuit_contribuyente and 
                t3.fecha_recauda=t2.fecha_recauda and t3.periodo=t2.periodo);


alter table temp_reporte_estudio add column apellido varchar;
alter table temp_reporte_estudio add column nombre varchar;

update temp_reporte_estudio t
set apellido=a.apellido,
    nombre=a.nombre
from afiliado a
where t.cuil_aportante=a.cuil_titular
and a.inte=0;

update temp_reporte_estudio set aporte=0 where aporte is null;
update temp_reporte_estudio set pagado=0 where pagado is null;
update temp_reporte_estudio set contribucion=0 where contribucion is null;


--IMPORTA LA FECHA PAGADA DE LA CONTRIBUCION? POR AHORA LAS ELIMINO...
delete from temp_reporte_estudio where cuil_aportante='00000000000';



return query 
select  cuit_contribuyente,
	cuil_aportante,
	periodo,
	fecha_recauda,
	cast(aporte as numeric),
	cast (contribucion as numeric),
	cast (rem_pagada as numeric),
	cast (rem_declarada as numeric),
	round(cast (calculado as numeric),2),
	round(cast (pagado as numeric),2),
	round(cast((calculado - pagado)as numeric),2) as deuda,
	apellido,
	nombre 
from temp_reporte_estudio order by periodo, cuil_aportante;

END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE

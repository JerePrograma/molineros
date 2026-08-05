CREATE OR REPLACE FUNCTION reporte_deuda_empresa_detalle(cuit_p character varying, periodo_desde date, periodo_hasta date)
  RETURNS SETOF reporte_deuda_empresa_detalle_result AS
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
and (o.periodo >='20040301' or o.fecha_transf >='20050701')
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
and (o.periodo >='20040301' or o.fecha_transf >='20050701')
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
and (o.periodo >='20040301' or o.fecha_transf >='20050701')
and (periodo_desde is null or (periodo_desde is not null and o.periodo>=periodo_desde))
and (periodo_hasta is null or (periodo_hasta is not null and o.periodo<=periodo_hasta))
group by o.cuit_contribuyente, o.periodo;

--ACTUALIZO APORTE TOTAL EN CONTRIBUCIONES
update temporal_contribuciones tc
set aporte_empresa=tt.total_aportes
from temporal_total_aportes tt
where tt.cuit_contribuyente=tc.cuit_contribuyente
and tt.periodo=tc.periodo;

--CALCULO CONTRIB
update temp_reporte_estudio t
set contribucion=trunc(cast (c.contribucion*(t.aporte/c.aporte_empresa) as numeric),2)
from temporal_contribuciones c
where c.cuit_contribuyente=t.cuit_contribuyente
and c.aporte_empresa<>0
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

update temp_reporte_estudio set cant_afiliados=0 where cant_afiliados is null;
update temp_reporte_estudio set rem_declarada=0 where rem_declarada is null;
update temp_reporte_estudio set pagado=0 where pagado is null;

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

return query 
select periodo,
cuit_contribuyente,
cant_afiliados as cant_afiliados_declarados,
cant_afiliados_pagados ,
round(cast(sum(rem_declarada) as numeric),2) as rem_declarada,
round(cast(sum(rem_pagada)as numeric),2) as rem_pagada,
fecha_recauda,
round(cast(sum(pagado)as numeric),2) as pagado,
round(cast(sum(calculado)as numeric),2) as calculado,
round(cast(sum(calculado)-sum(pagado) as numeric),2) as deuda
from temp_reporte_estudio 
where calculado>1
group by periodo, cuit_contribuyente, cant_afiliados, cant_afiliados_pagados, fecha_recauda
order by periodo;

END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION reporte_deuda_empresa_detalle(character varying, date, date) OWNER TO postgres;

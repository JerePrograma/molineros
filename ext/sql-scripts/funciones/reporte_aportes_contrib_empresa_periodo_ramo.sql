CREATE OR REPLACE FUNCTION reporte_aportes_contrib_empresa_periodo_ramo(periodo_v date, ramo_v integer)
  RETURNS SETOF reporte_aportes_contrib_empresa_periodo AS
$BODY$
BEGIN

drop table if exists sueldo_temp;
drop table if exists cant_afil_empresa;
drop table if exists cant_afil_empresa_pagado;
drop table if exists aux_contrib;
drop table if exists AUX_OS;
drop table if exists AUX_DDJJ;
drop table if exists result;
drop table if exists aux_sdos;
drop table if exists AUX_TOTAL;
drop table if exists AUX_pagados;
drop table if exists AUX_declarados;

create temp table aux_contrib AS
 select distinct cuit 
 from buscar_empresas_ramo(ramo_v,periodo_v,periodo_v);
  --from detalle_declaracion_jurada ddjj
 --where ddjj.cuil=cuil_p;
 
 
 CREATE temp table sueldo_temp (cuit varchar, cuil varchar, periodo date, remuneracionafectos numeric);	 
 CREATE temp table cant_afil_empresa_pagado(periodo date, cuit_contribuyente varchar, count numeric);
 CREATE temp table cant_afil_empresa (periodo date, cuit varchar, count numeric);

 CREATE TEMP TABLE AUX_DDJJ AS
 select dd.* 
 from detalle_declaracion_jurada_2011 dd, aux_contrib a
 where dd.cuit=a.cuit
 and periodo=periodo_v
 and secobligacion=(select max(secobligacion) 
		    from detalle_declaracion_jurada_2011 d3 
		    where d3.cuit=dd.cuit 
		    and d3.cuil=dd.cuil 
		    and d3.periodo=dd.periodo);

 CREATE TEMP TABLE AUX_TOTAL AS
 select o.cuit_contribuyente, o.cuil_aportante, o.periodo, fecha_recauda,  importe
 from os_aportes_detalle_2011 o, conceptos_transf_os c, aux_contrib a
 where a.cuit=o.cuit_contribuyente
 and periodo=periodo_v 
 and o.concepto_transf=c.cod_conc
 and c.liquidable=true;
 

 CREATE TEMP TABLE AUX_OS AS
 select o.cuit_contribuyente, o.cuil_aportante, o.periodo, sum(importe) as importe
 from AUX_TOTAL o
 group by o.cuit_contribuyente, o.cuil_aportante, o.periodo;
 

--OBTENGO TODOS LOS SUELDOS 
insert into sueldo_temp(cuit, remuneracionafectos)	
select cuit, SUM(remuneracionafectos)+SUM(importeadicionalos+aporteadicionalos)/0.09
from AUX_DDJJ dd
GROUP BY CUIT;

--SUELDOS QUE FALTAN
insert into sueldo_temp(cuit, remuneracionafectos)
select cuit_contribuyente, sum(importe)
from os_aportes_rem  o, aux_contrib a
where a.cuit=o.cuit_contribuyente
and not exists (select 1 from sueldo_temp s where s.cuit=o.cuit_contribuyente)
and periodo=periodo_v
group by cuit_contribuyente;

create temp table result (cuit varchar, cuil varchar, periodo date, aporte numeric,
			     contribucion numeric, rem_pagada numeric, rem_declarada numeric, 
			     total_rem_empresa numeric, total_contrib_empresa numeric,
			     calculado numeric, calculado_contrib numeric, porc numeric, razon varchar, localidad varchar, provincia varchar ,
			     codigopostal varchar, numero varchar, ramo int, apellido varchar , nombre varchar,
			     tercerizadora varchar, fecha_recauda date, apo_adic numeric, cont_adic numeric, cant_afiliados_declarados int, cant_afiliados_pagados int);
			     
insert into result(cuit, cuil, periodo, aporte, contribucion, rem_pagada, rem_declarada,  calculado, porc, apo_adic, cont_adic) 
select cuit, cuil, periodo, 0, 0, 0 ,  remuneracionafectos, 0, 0, aporteadicionalos, importeadicionalos
from AUX_DDJJ;

update result
set apo_adic=trunc(apo_adic*0.9,2),
    cont_adic=trunc(cont_adic*0.9,2)
where rem_declarada is not null
and rem_declarada>0
and ((periodo>='20081201' and rem_declarada < 2400) or (periodo<'20081201' and rem_declarada<1000));

update result
set apo_adic=trunc(apo_adic*0.85,2),
    cont_adic=trunc(cont_adic*0.85,2)
where rem_declarada is not null
and rem_declarada>0
and ((periodo>='20081201' and rem_declarada >= 2400) or (periodo<'20081201' and rem_declarada>=1000));


insert into result(cuit, cuil, periodo, aporte, contribucion, rem_pagada, rem_declarada, calculado, porc) 
select cuit_contribuyente, cuil_aportante, periodo, 0, 0, 0 , 0, 0, 0 
from AUX_OS o
where not exists (select 1 from result r where r.cuit=o.cuit_contribuyente and r.cuil=o.cuil_aportante);

update result r
set rem_pagada=importe
from os_aportes_rem  o
where r.cuit=o.cuit_contribuyente
and r.cuil=o.cuil_aportante
and o.periodo=periodo_v;

--APORTES
update result r
set aporte=importe
from aux_OS o
where r.cuit=o.cuit_contribuyente
and r.cuil=o.cuil_aportante
and r.periodo=o.periodo;

update result r
set total_contrib_empresa=o.importe
from aux_os o
where r.cuit=o.cuit_contribuyente
and r.periodo=o.periodo
and o.cuil_aportante='00000000000';

delete from result where cuil='00000000000';

--REMUNERACIONES TOTAL
create temp table aux_sdos as
select cuit, sum(case when rem_pagada>rem_declarada then rem_pagada else rem_declarada end) as total_sdo
from result
group by cuit;

update result r
set total_rem_empresa=total_sdo
from aux_sdos a
where r.cuit=a.cuit;

--CALCULO DE CUANTO SERIA LA CONTRIB
update result
set calculado_contrib=trunc(rem_declarada*0.054,2)
where rem_declarada is not null
and rem_declarada>0
and ((periodo>='20081201' and rem_declarada < 2400) or (periodo<'20081201' and rem_declarada<1000));

update result
set calculado_contrib=trunc(rem_declarada*0.051,2)
where rem_declarada is not null
and rem_declarada>0
and ((periodo>='20081201' and rem_declarada >= 2400) or (periodo<'20081201' and rem_declarada>=1000));


--CONTRIBUCIONES 
update result r
set contribucion=trunc(total_contrib_empresa*(case when rem_pagada>rem_declarada then rem_pagada else rem_declarada end)/total_rem_empresa,2)
where total_rem_empresa is not null and total_rem_empresa >0
and cuil<>'00000000000';



--SUELDOS < a 2400 se calcula el 8.1 SI EXISTE DECLARADA
update result
set calculado=trunc(rem_declarada*0.081,2)
where rem_declarada is not null
and rem_declarada>0
and ((periodo>='20081201' and rem_declarada < 2400) or (periodo<'20081201' and rem_declarada<1000));

--SUELDOS < a 2400 se calcula el 8.1 SI NO EXISTE DECLARADA y EXISTE PAGADA
update result
set calculado=trunc(rem_pagada*0.081,2)
where (rem_declarada is null or  rem_declarada=0)
and rem_pagada is not null
and rem_pagada >0
and ((periodo>='20081201' and rem_pagada < 2400) or (periodo<'20081201' and rem_pagada<1000));

--SUELDOS > a 2400 se calcula el 7.65 SI EXISTE DECLARADA
update result
set calculado=trunc(rem_declarada*0.0765,2)
where rem_declarada is not null
and rem_declarada>0
and ((periodo>='20081201' and rem_declarada >= 2400) or (periodo<'20081201' and rem_declarada>=1000));

--SUELDOS > a 2400 se calcula el 7.65 SI NO EXISTE DECLARADA y EXISTE PAGADA
update result
set calculado=trunc(rem_pagada*0.0765,2)
where (rem_declarada is null or  rem_declarada=0)
and rem_pagada is not null
and rem_pagada >0
and ((periodo>='20081201' and rem_pagada >= 2400) or (periodo<'20081201' and rem_pagada>=1000));



update result
set porc=round((aporte+contribucion)/calculado*100,0)
where calculado<>0;


update result tre
set razon=dpc.razonsocial,
localidad=dpc.localidad,
provincia=p.detalle,
codigopostal=dpc.codigopostal
from detalle_padron_contribuyentes dpc, provincia p
where cast (tre.cuit as numeric)=dpc.cuit
and case when dpc.provincia<>'' then cast(dpc.provincia as integer) else 1 end=p.id_provincia;

update result tre
set ramo=e.id_ramo_empresa
from empresa e
where e.cuit=tre.cuit
and e.sucursal='000';

update result t
set apellido=a.apellido,
    nombre=a.nombre
from afiliado a
where t.cuil=a.cuil_titular
and a.inte=0;

create temp table aux_pagados as
select cuit, count(*) as cantidad
from result
where aporte>0
group by cuit;

create temp table aux_declarados as
select cuit, count(*) as cantidad
from result
where rem_declarada>0
group by cuit;

update result r
set cant_afiliados_pagados=cantidad
from aux_pagados p
where r.cuit=p.cuit;

update result r
set cant_afiliados_declarados=cantidad
from aux_declarados p
where r.cuit=p.cuit;




update result te
set tercerizadora=t.descripcion
from afi_tercerizadora_servicio ats, tercerizadora_servicio t
where ats.cuil_titular=te.cuil
and ats.inte=0
and (ats.fecha_fin_pres is null or ats.fecha_fin_pres>current_date)
and (ats.baja_fecha is null or ats.baja_fecha>current_date)
and ats.id_tercerizadora=t.id_tercerizadora;                              

return query 
select cuit,
cuil,
periodo ,
case when aporte is not null then round(cast(aporte as numeric),2) else 0 end,
case when contribucion is not null then round(cast(contribucion as numeric),2) else 0 end,
case when cant_afiliados_declarados is not null then cant_afiliados_declarados else 0 end,
case when cant_afiliados_pagados is not null then cant_afiliados_pagados else 0 end,
case when rem_pagada is not null then round(cast(rem_pagada as numeric),2) else 0 end,
case when rem_pagada is not null then round(cast(rem_declarada as numeric),2) else 0 end,
case when aporte+contribucion is not null then round(cast(aporte+contribucion as numeric),2) else 0 end,
case when calculado is not null then round(cast(calculado as numeric),2) else 0 end,
case when porc is not null then round(cast(porc as numeric),2) else 0 end ,
razon ,
localidad ,
provincia ,
codigopostal ,
numero ,
ramo ,
apellido ,
nombre,
tercerizadora,
fecha_recauda 
from result order by cuit, cuil, periodo;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE


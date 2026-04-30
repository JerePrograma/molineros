-- Function: informes.reporte_deuda_empresas_periodo_batch(date, date, boolean, integer, integer, character varying, timestamp without time zone)

-- DROP FUNCTION informes.reporte_deuda_empresas_periodo_batch(date, date, boolean, integer, integer, character varying, timestamp without time zone);

CREATE OR REPLACE FUNCTION informes.reporte_deuda_empresas_periodo_batch(periodo_desde date, periodo_hasta date, sin_deuda boolean, ramo_desde_p integer, ramo_hasta_p integer, usuario_p character varying, fecha_solicitado_p timestamp without time zone)
  RETURNS void AS
$BODY$
declare _id_cab integer;
BEGIN

drop table if exists reporte_deuda;
--1ro a partir de la ddjj calculo cuánto debe para los períodos ingresados
CREATE TEMP TABLE reporte_deuda AS
select distinct periodo, 
       cuit, 
       sum(case when remuneracionafectos<2400 then remuneracionafectos*.081 else cast(0 as numeric) end) as "8.1",
       sum(case when remuneracionafectos>=2400 then remuneracionafectos*.0765 else cast(0 as numeric) end) as "7.65", 
       cast(0 as numeric) as pagado, 
       cast (0 as numeric) as calculado_acta_convenio, 
       cast (0 as numeric) as pagado_acta_convenio,       
       cast(0 as smallint) as ramo, cast('' as varchar) as razon,
       cast(0 as numeric) as deuda,
       sum(case when remuneracionafectos<2400 then remuneracionafectos else cast(0 as numeric) end) as total_rem_81,
       count(case when remuneracionafectos<2400 then 1 else null end) as total_afi_81,       
       sum(case when remuneracionafectos>=2400 then remuneracionafectos else cast(0 as numeric) end) as total_rem_765,
       count(case when remuneracionafectos>=2400 then 1 else null end) as total_afi_765,
       sum(remuneracionafectos) as total_remuneracion,
       count(*) as total_empleados
from detalle_declaracion_jurada dd
where periodo>=periodo_desde
and periodo<=periodo_hasta
and secobligacion=(select max(secobligacion) from detalle_declaracion_jurada d3 where d3.cuit=dd.cuit and d3.cuil=dd.cuil and d3.periodo=dd.periodo)
group by cuit, periodo
limit 1000;

RAISE INFO 'TERMINO CON LIMIT 10';


--2do veo lo que pagó para ese período
--PAGADO
update reporte_deuda r
set pagado=(select sum(o.importe)
		from os_aportes_detalle o
		where o.periodo=r.periodo--'20110101'
		and o.cuit_contribuyente=r.cuit		
		group by cuit_contribuyente, periodo);
		
update reporte_deuda
set pagado=0
where pagado is null;

--3ro Ve lo que debe por actas y convenios 
--CALCULADO ACTAS
update reporte_deuda r
set calculado_acta_convenio=(select sum(ap.subtotal)--+ap.interes)
			     from acta a
			     inner join acta_periodos ap
			     on ap.acta_id=a.id
			     where a.cierre_fecha is not null 
			     and a.baja_fecha is null
			     and a.acta_cerrada=true
			     and ap.baja_fecha is null
			     and ap.subtotal <>0				
			     and not exists (select 1 from acta_pagos apb where convenio_acta_id is not null and apb.acta_id=ap.acta_id and apb.baja_fecha is null)
			     and a.cuit=r.cuit
			     and ap.periodo>=periodo_desde
			     and ap.periodo<=periodo_hasta);
			     
update reporte_deuda set calculado_acta_convenio=0 where calculado_acta_convenio is null;

--ACTAS RELACIONADAS
update reporte_deuda r
set calculado_acta_convenio= calculado_acta_convenio+isnull((select sum(ape.subtotal)--+ape.interes)
			      from acta a
			      inner join acta_relacion ar
			      on a.id=ar.acta_id
			      inner join acta_periodos ape
			      on ar.acta_relacionada_id=ape.acta_id
			      where a.cierre_fecha is not null 
			      and a.baja_fecha is null
			      and ar.baja_fecha is null
			      and ape.baja_fecha is null
			      and a.cuit=r.cuit
			      and ape.periodo>=periodo_desde
			      and ape.periodo<=periodo_hasta),0);
			      
update reporte_deuda set calculado_acta_convenio=0 where calculado_acta_convenio is null;

--CONVENIOS
update reporte_deuda r
set calculado_acta_convenio= calculado_acta_convenio+ isnull((select sum(ap.subtotal)--+ap.interes)
						       from convenio c
						       inner join convenio_actas ca
					               on c.id=ca.convenio_id
						       inner join acta_periodos ap
					               on ap.acta_id=ca.acta_id
						       inner join acta a
						       on a.id=ap.acta_id
						       where ap.subtotal <>0	
						       and a.cuit=r.cuit
						       and ap.periodo>=periodo_desde
						       and ap.periodo<=periodo_hasta),0);	
						       	
update reporte_deuda set calculado_acta_convenio=0 where calculado_acta_convenio is null;						       
	
--PAGADO POR ACTAS CHEQUES
update reporte_deuda r
set pagado_acta_convenio=(select sum(c.importe)
			  from 	recibo_ingresos ri, 
				acta_pagos apa, 
				cheque c, 
				recibo_conceptos rc, 
				recibo_conceptos_pagos rcp, 
				recibo rcb
			  where ri.fecha<=periodo_hasta
				and apa.baja_fecha is null
				and apa.acta_relacion_id is null
				and apa.convenio_acta_id is null
				and rcp.recibo_ingreso_id = ri.id
				and ri.nro_cheque = c.nro_cheque
				and ri.id_banco = c.id_banco
				and rcp.recibo_concepto_id=rc.id
				and ri.id=rcp.recibo_ingreso_id
				and c.id_estado in (4,6)
				and rc.acta_id is not null
				and rc.acta_id=apa.acta_id
				and rcb.id=ri.recibo_id				
				and rcb.baja_fecha is null
				and rcb.cuit=r.cuit);

update reporte_deuda
set pagado_acta_convenio=0
where pagado_acta_convenio is null;
				
--ACTAS EFECTIVO
update reporte_deuda r
set pagado_acta_convenio=pagado_acta_convenio+isnull((select sum(ri.importe)
					       from recibo_ingresos ri, 
						    recibo_conceptos rc, 
						    recibo_conceptos_pagos rcp, 
						    recibo rcb
						where ri.fecha<=periodo_hasta
						and rcp.recibo_concepto_id=rc.id
						and ri.id=rcp.recibo_ingreso_id
						and rc.acta_id is not null
						and ri.nro_cheque is null
						and rcb.id=ri.recibo_id
						and rcb.cuit=r.cuit
						and rcb.baja_fecha is null),0);


update reporte_deuda
set pagado_acta_convenio=0
where pagado_acta_convenio is null;						

--PAGADO POR CONVENIOS CHEQUE
update reporte_deuda r
set pagado_acta_convenio=pagado_acta_convenio+isnull((select sum(c.importe)
			  from 	recibo_ingresos ri, 
				cheque c, 
				recibo_conceptos rc, 
				recibo_conceptos_pagos rcp, 
				recibo rcb
			  where ri.fecha<=periodo_hasta
			  and rcp.recibo_ingreso_id = ri.id
			  and ri.nro_cheque = c.nro_cheque
			  and ri.id_banco = c.id_banco
			  and rcp.recibo_concepto_id=rc.id
			  and ri.id=rcp.recibo_ingreso_id
	                  and c.id_estado in (4,6)
			  and rc.convenio_id is not null
			  and rcb.id=ri.recibo_id
		          and rcb.fecha>'20110101'
		          and rcb.cuit=r.cuit
			  and rcb.baja_fecha is null),0);

update reporte_deuda
set pagado_acta_convenio=0
where pagado_acta_convenio is null;
			  
--CONVENIO EFVO
update reporte_deuda r
set pagado_acta_convenio=pagado_acta_convenio+isnull((select sum(ri.importe)
			   from recibo_ingresos ri, 
				convenio_pagos apa, 
				recibo_conceptos rc, 
				recibo_conceptos_pagos rcp, 
				recibo rcb
			   where ri.fecha<=periodo_hasta
			   and apa.baja_fecha is null
			   and apa.convenio_id=rc.convenio_id
			   and rcp.recibo_concepto_id=rc.id
			   and ri.id=rcp.recibo_ingreso_id
			   and rc.convenio_id is not null
			   and ri.nro_cheque is null
			   and rcb.id=ri.recibo_id			   
			   and rcb.baja_fecha is null
			   and rcb.cuit=r.cuit),0);	

update reporte_deuda
set pagado_acta_convenio=0
where pagado_acta_convenio is null;	

update reporte_deuda
set pagado_acta_convenio=calculado_acta_convenio
where pagado_acta_convenio>calculado_acta_convenio; 		   		  

update reporte_deuda
set deuda=("7.65"+"8.1")-pagado-pagado_acta_convenio;

update reporte_deuda r
set ramo=e.id_ramo_empresa,
    razon=e.razon_soc
from empresa e
where r.cuit=e.cuit;

update reporte_deuda
set total_remuneracion=total_rem_765+total_rem_81,
    total_empleados=total_afi_765+total_afi_81;

RAISE INFO 'ANTES DE CAB';

INSERT INTO informes.reporte_deuda_empresas_periodo_cab(
            usuario, fecha_solicitado,  fecha_desde_param, 
            fecha_hasta_param, ramo_desde_param, ramo_hasta_param, agrupa_x_remun_param, 
            empresa_sin_deuda_param)
select usuario_p, fecha_solicitado_p, periodo_desde,periodo_hasta,ramo_desde_p,ramo_hasta_p,false, sin_deuda;

_id_cab= currval('informes.reporte_deuda_empresas_periodo_cab_id_seq');

RAISE INFO 'ANTES DE INSERTA ID_CAB_ %',_id_cab;

INSERT INTO informes.reporte_deuda_empresas_periodo_det(
            id_cab, periodo, cuit, razon_soc, ramo, total_afi_81, total_afi_765, 
            total_empleados, total_rem_81, total_rem_765, total_remuneracion, 
            calculado_81, calculado_765, total_calculado, pagado, pagado_acta_convenio, 
            porc_pagado, deuda, calle, numero, piso, dpto, localidad, provincia, 
            cod_postal)
select _id_cab,
       cast(periodo as date),
       a.cuit, 
       razon, 
       ramo, 
       total_afi_81, 
       total_afi_765, 
       total_empleados,       
       trunc(total_rem_81,2), 
       trunc(total_rem_765,2), 
       trunc(total_remuneracion,2), 
       trunc(total_rem_81*0.081,2), 
       trunc(total_rem_765*0.0765,2), 
       trunc((total_rem_765*0.0765)+(total_rem_81*0.081),2), 
       pagado,       
       pagado_acta_convenio,
       round(case when (pagado+pagado_acta_convenio=0 or (total_rem_765=0 and total_rem_81=0)) then 0 else ((pagado+pagado_acta_convenio)*100)/((total_rem_765*0.0765)+(total_rem_81*0.081)) end,2) as porc_pagado_total_calculado,
       trunc(deuda,2),
       b.calle,
       b.numero,
       b.piso,
       b.dpto,
       b.localidad,
       p.detalle as provincia,
       b.codigopostal       
from reporte_deuda a
left outer join detalle_padron_contribuyentes b
on a.cuit=cast(b.cuit as varchar)
left outer join provincia p
on p.id_provincia_afip=b.provincia
where case when sin_deuda=true then deuda=deuda else deuda>1 end
and case when ramo_desde_p is null then 1=1 else ramo>=ramo_desde_p end
and case when ramo_hasta_p is null then 1=1 else ramo<=ramo_hasta_p end
and b.fecha_proceso=(select max(fecha_proceso) from detalle_padron_contribuyentes bb where b.cuit=bb.cuit)
group by cast(periodo as date),
       a.cuit, 
       razon, 
       ramo,
       total_afi_81, 
       total_afi_765, 
       total_empleados,       
       trunc(total_rem_81,2), 
       trunc(total_rem_765,2), 
       trunc(total_remuneracion,2), 
       trunc(total_rem_81*0.081,2), 
       trunc(total_rem_765*0.0765,2), 
       trunc((total_rem_765*0.0765)+(total_rem_81*0.081),2), 
       pagado,       
       pagado_acta_convenio,
       round(case when (pagado+pagado_acta_convenio=0 or (total_rem_765=0 and total_rem_81=0)) then 0 else ((pagado+pagado_acta_convenio)*100)/((total_rem_765*0.0765)+(total_rem_81*0.081)) end,2),
       trunc(deuda,2),
       b.calle,
       b.numero,
       b.piso,
       b.dpto,
       b.localidad,
       p.detalle,
       b.codigopostal
order by ramo, sum(trunc(deuda,2))desc;
RAISE INFO 'DETALLE INSERTADO';

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION informes.reporte_deuda_empresas_periodo_batch(date, date, boolean, integer, integer, character varying, timestamp without time zone)
  OWNER TO postgres;

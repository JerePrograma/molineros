create type return_busqueda_aportes_afip_meses as
(periodo date,
importe numeric,
contrib_est numeric,
fecha_transf date, 
cuil_titular varchar, 
apellido varchar, 
nombre varchar, 
fecha_ingre date, 
fecha_baja date, 
cuit varchar, 
razon_soc varchar, 
sucursal varchar,
fecha_terc date,
total_terc numeric,
liq_actas numeric,
comisionOS numeric,
fechaTransf date,
fecha_recauda date,
concepto varchar,
remuneracion numeric,
id_terc varchar)


CREATE OR REPLACE FUNCTION buscar_aportes_afip_meses(p_cuit character, fecha_desde date)
  RETURNS SETOF return_busqueda_aportes_afip_meses AS
$BODY$
DECLARE _record 	RECORD;
begin

drop table if exists aporte_histo;
drop table if exists cuil_temp;


create temp table aporte_histo (periodo date, importe numeric, contrib_est numeric, fecha_transf date, cuil_titular varchar, apellido varchar, nombre varchar, ingre_fecha date,
                                baja_fecha date, cuit varchar, razon_soc varchar, sucursal varchar, fecha_liq date, total_terc numeric, liq_actas numeric, comisionOS numeric, 
                                fechaTransf date, fecha_recauda date, concepto varchar, remuneracion numeric, id_terc varchar);

if fecha_desde<cast('20110101' as date) then

INSERT INTO aporte_histo(
            periodo, importe, contrib_est, fecha_transf, cuil_titular, apellido, 
            nombre, ingre_fecha, baja_fecha, cuit, razon_soc, sucursal, fecha_liq, 
            total_terc, liq_actas, comisionOS, fecha_recauda, concepto)
select 	o.periodo, 
	o.importe as importe, 
	o.importe*2 as contrib_est,
	o.fecha_transf, 
	a.cuil, 
	a.apellido, 
	a.nombre, 
	a.ingre_fecha, 
	cast(a.baja_fecha as date), 
	e.cuit, 
	e.razon_soc, 
	e.sucursal,
	cast (null as date),--h.fecha_liq,
	cast(0 as numeric) as total_terc,
	cast(0 as numeric) as liq_actas,
	cast(0 as numeric) as comisionOS,
	o.fecha_recauda,
	c.descripcion
from afiliado a 
left outer join os_aportes_detalle o
on o.cuil_aportante = a.cuil
and o.periodo>=fecha_desde
and exists (select 1 from conceptos_transf_os c where o.concepto_transf = c.cod_conc
and c.liquidable = true)
left outer join empresa e
on o.cuit_contribuyente = e.cuit
and e.sucursal = (select min(em.sucursal) from empresa em where em.cuit = e.cuit)
left outer join conceptos_transf_os c
on c.cod_conc=o.concepto_transf
/*left outer join liquidacion_historica_tercerizadoras h
on h.cuil=o.cuil_aportante
and h.cuit=o.cuit_contribuyente
and h.periodo_hab_d=o.periodo
and h.periodo_d not in ('18100101', '18000101')*/
where
a.cuil_titular=p_cuit  and
(a.aportante_titular = 1 or a.inte = 0);



INSERT INTO aporte_histo(
            periodo, importe, contrib_est, fecha_transf, cuil_titular, apellido, 
            nombre, ingre_fecha, baja_fecha, cuit, razon_soc, sucursal, fecha_liq, 
            total_terc, liq_actas, comisionOS, fechaTransf, id_terc)
select 	periodo_hab_d, sum(h.aporte_n), sum(h.contrib_n), h.fecha_liq, h.cuil, 'DERIVACION','DE APORTES', null,null, e.cuit, e.razon_soc, null, fecha_liq, sum(h.total_terc), null,(sum(h.aporte_n)+sum(h.contrib_n))*0.1,
	h.fecha_liq,  t.descripcion
from liquidacion_historica_tercerizadoras h	
left outer join empresa e
on e.cuit=h.cuit
and e.sucursal='000'
left outer join tercerizadora_servicio t
on t.id_tercerizadora=h.id_terc
where  exists (select 1 from aporte_histo o2 where o2.cuil_titular=h.cuil)
and h.periodo_hab_d>=fecha_desde
and h.periodo_d not in ('18100101', '18000101')
and h.id_terc in ('CCH','CEU','CMS', 'CEM','GAL','OMI')
group by fecha_liq, cuil,e.cuit,e.razon_soc, periodo_hab_d, t.descripcion;


/*update aporte_histo o
set    --importe=h.aporte_n,
       --contrib_est=h.contrib_n,
       fecha_liq=h.fecha_liq,
       total_terc=h.total_terc,
       comisionOS=(h.aporte_n+h.contrib_n)*0.1
from (select cuil, cuit, periodo_hab_d, sum(aporte_n) as aporte_n, sum(h.contrib_n) as contrib_n, max(fecha_liq) as fecha_liq, sum(total_terc) as total_terc
from liquidacion_historica_tercerizadoras h
where  exists (select 1 from aporte_histo o2 where o2.cuil_titular=h.cuil)
--and h.cuit=o.cuit
and h.periodo_hab_d>=fecha_desde
and h.periodo_d not in ('18100101', '18000101')
group by cuil,cuit,periodo_hab_d) as h
where  h.cuil=o.cuil_titular
and h.cuit=o.cuit
and h.periodo_hab_d=o.periodo
and o.fecha_transf=(select max(o2.fecha_transf) from aporte_histo o2 where o2.cuit=o.cuit and o2.cuil_titular=o.cuil_titular and o2.periodo=o.periodo);*/

--UPDATE table t1 SET column1=sq.column1 FROM (SELECT t2.column1, column2 FROM table t2 INNER JOIN table t3 USING (column2) GROUP BY column2) AS sq WHERE t1.column2=sq.column2;

	
INSERT INTO aporte_histo(
            periodo, importe, contrib_est, fecha_transf, cuil_titular, apellido, 
            nombre, ingre_fecha, baja_fecha, cuit, razon_soc, sucursal, fecha_liq, 
            total_terc, liq_actas)
select 	o.periodo_d, 
	cast(0 as numeric) as importe, 
	cast(0 as numeric) as contrib_est,
	o.periodo_d, 
	a.cuil, 
	'DERIVACION', 
	'ACTAS Y CONVENIOS', 
	a.ingre_fecha, 
	cast(a.baja_fecha as date), 
	e.cuit, 
	e.razon_soc, 
	e.sucursal,
	o.fecha_liq,  --VER
	cast(omint_d as numeric) as total_terc,
	cast(omint_d as numeric) as liq_actas
from afiliado a, liquidacion_actas o
left outer join empresa e
on o.cuit= e.cuit
and e.sucursal = (select min(em.sucursal) from empresa em where em.cuit = e.cuit)
left outer join liquidacion_historica_tercerizadoras h
on h.cuil=o.cuil
and h.cuit=o.cuit
and h.periodo_hab_d=o.periodo_d
where o.cuil= a.cuil
and o.periodo_d>=fecha_desde
and a.cuil=p_cuit
and (a.aportante_titular = 1 or a.inte = 0)
and not exists (select 1 from aporte_histo ah2 where a.cuil=ah2.cuil_titular and ah2.cuit=e.cuit and ah2.periodo=o.periodo_d);
--and h.periodo_d not in ('18100101', '18000101');

--LAS DDJJ QUE NO SE PAGARON
INSERT INTO aporte_histo(
            periodo, importe, contrib_est, fecha_transf, cuil_titular, apellido, 
            nombre, ingre_fecha, baja_fecha, cuit, razon_soc, sucursal, fecha_liq, 
            total_terc, liq_actas)
select 	o.periodo, 
	case when o.remuneracionafectos>=2400 then round(o.remuneracionafectos*0.0255,2) else round(o.remuneracionafectos*0.027,2) end+o.aporteadicionalos, 
	case when o.remuneracionafectos>=2400 then round(o.remuneracionafectos*0.0510,2) else round(o.remuneracionafectos*0.054,2) end+o.importeadicionalos,
	null, 
	a.cuil, 
	a.apellido, 
	a.nombre, 
	a.ingre_fecha, 
	cast(a.baja_fecha as date), 
	e.cuit, 
	e.razon_soc||' - DDJJ sin aportes', 
	e.sucursal,
	null,  --VER
	null,
	0
from afiliado a, detalle_declaracion_jurada o
left outer join empresa e
on o.cuit= e.cuit
and e.sucursal = (select min(em.sucursal) from empresa em where em.cuit = e.cuit)
where o.cuil= a.cuil
and o.periodo>=fecha_desde
and a.cuil=p_cuit
and a.ingre_fecha=(select max(ingre_fecha) from afiliado a2 where a.cuil=a2.cuil and (a2.aportante_titular=1 or a.inte=0))
and o.secobligacion=(select max(secobligacion) from detalle_declaracion_jurada o2 where o.cuit=o2.cuit and o.cuil=o2.cuil and o.periodo=o2.periodo)
and (a.aportante_titular = 1 or a.inte = 0)
and not exists (select 1 from aporte_histo ah where ah.cuil_titular=o.cuil and ah.cuit=o.cuit and ah.periodo=o.periodo);

update aporte_histo a
set remuneracion=d.remuneracionafectos
from detalle_declaracion_jurada d
where d.cuil=a.cuil_titular
and d.cuit=a.cuit
and d.periodo=a.periodo;


else
RAISE INFO '>2011 OS APORTES DETALLE';
INSERT INTO aporte_histo(
            periodo, importe, contrib_est, fecha_transf, cuil_titular, apellido, 
            nombre, ingre_fecha, baja_fecha, cuit, razon_soc, sucursal, fecha_liq, 
            total_terc, liq_actas, comisionOS, fecha_recauda, concepto)
select 	o.periodo, 
	o.importe as importe, 
	o.importe*2 as contrib_est,
	o.fecha_transf, 
	a.cuil, 
	a.apellido, 
	a.nombre, 
	a.ingre_fecha, 
	cast(a.baja_fecha as date), 
	e.cuit, 
	e.razon_soc, 
	e.sucursal,
	cast (null as date),--h.fecha_liq,
	cast(0 as numeric) as total_terc,
	cast(0 as numeric) as liq_actas,
	cast(0 as numeric) as comisionOS,
	o.fecha_recauda,
	c.descripcion
from afiliado a 
left outer join os_aportes_detalle_2011 o
on o.cuil_aportante = a.cuil
and o.periodo>=fecha_desde
and exists (select 1 from conceptos_transf_os c where o.concepto_transf = c.cod_conc
and c.liquidable = true)
left outer join empresa e
on o.cuit_contribuyente = e.cuit
and e.sucursal = (select min(em.sucursal) from empresa em where em.cuit = e.cuit)
left outer join conceptos_transf_os c
on c.cod_conc=o.concepto_transf
/*left outer join liquidacion_historica_tercerizadoras h
on h.cuil=o.cuil_aportante
and h.cuit=o.cuit_contribuyente
and h.periodo_hab_d=o.periodo
and h.periodo_d not in ('18100101', '18000101')*/
where
a.cuil_titular=p_cuit  and
(a.aportante_titular = 1 or a.inte = 0);

RAISE INFO '>2011 LIQ. HIST TERCE';

INSERT INTO aporte_histo(
            periodo, importe, contrib_est, fecha_transf, cuil_titular, apellido, 
            nombre, ingre_fecha, baja_fecha, cuit, razon_soc, sucursal, fecha_liq, 
            total_terc, liq_actas, comisionOS, fechaTransf, id_terc)
select 	periodo_hab_d, sum(h.aporte_n), sum(h.contrib_n), h.fecha_liq, h.cuil, 'DERIVACION','DE APORTES', null,null, e.cuit, e.razon_soc, null, fecha_liq, sum(h.total_terc), null,(sum(h.aporte_n)+sum(h.contrib_n))*0.1,
	h.fecha_liq, t.descripcion
from liquidacion_historica_tercerizadoras_2011 h	
left outer join empresa e
on e.cuit=h.cuit
and e.sucursal='000'
left outer join tercerizadora_servicio t
on t.id_tercerizadora=h.id_terc
where  exists (select 1 from aporte_histo o2 where o2.cuil_titular=h.cuil)
and h.periodo_hab_d>=fecha_desde
and h.periodo_d not in ('18100101', '18000101')
and h.id_terc in ('CCH','CEU','CMS', 'CEM','GAL','OMI')
group by fecha_liq, cuil,e.cuit,e.razon_soc, periodo_hab_d, t.descripcion;

RAISE INFO '>2011 LIQUIDACION ACTAS';

	
INSERT INTO aporte_histo(
            periodo, importe, contrib_est, fecha_transf, cuil_titular, apellido, 
            nombre, ingre_fecha, baja_fecha, cuit, razon_soc, sucursal, fecha_liq, 
            total_terc, liq_actas)
select 	o.periodo_d, 
	cast(0 as numeric) as importe, 
	cast(0 as numeric) as contrib_est,
	o.periodo_d, 
	a.cuil, 
	'DERIVACION', 
	'ACTAS Y CONVENIOS', 
	a.ingre_fecha, 
	cast(a.baja_fecha as date), 
	e.cuit, 
	e.razon_soc, 
	e.sucursal,
	o.fecha_liq,  --VER
	cast(omint_d as numeric) as total_terc,
	cast(omint_d as numeric) as liq_actas
from afiliado a, liquidacion_actas o
left outer join empresa e
on o.cuit= e.cuit
and e.sucursal = (select min(em.sucursal) from empresa em where em.cuit = e.cuit)
left outer join liquidacion_historica_tercerizadoras_2011 h
on h.cuil=o.cuil
and h.cuit=o.cuit
and h.periodo_hab_d=o.periodo_d
where o.cuil= a.cuil
and o.periodo_d>=fecha_desde
and a.cuil=p_cuit
and (a.aportante_titular = 1 or a.inte = 0)
and not exists (select 1 from aporte_histo ah2 where a.cuil=ah2.cuil_titular and ah2.cuit=e.cuit and ah2.periodo=o.periodo_d);
--and h.periodo_d not in ('18100101', '18000101');

RAISE INFO '>2011 3 DDJJ';

--LAS DDJJ QUE NO SE PAGARON
INSERT INTO aporte_histo(
            periodo, importe, contrib_est, fecha_transf, cuil_titular, apellido, 
            nombre, ingre_fecha, baja_fecha, cuit, razon_soc, sucursal, fecha_liq, 
            total_terc, liq_actas)
select 	o.periodo, 
	case when o.remuneracionafectos>=2400 then round(o.remuneracionafectos*0.0255,2) else round(o.remuneracionafectos*0.027,2) end+o.aporteadicionalos, 
	case when o.remuneracionafectos>=2400 then round(o.remuneracionafectos*0.0510,2) else round(o.remuneracionafectos*0.054,2) end+o.importeadicionalos,
	null, 
	a.cuil, 
	a.apellido, 
	a.nombre, 
	a.ingre_fecha, 
	cast(a.baja_fecha as date), 
	e.cuit, 
	e.razon_soc||' - DDJJ sin aportes', 
	e.sucursal,
	null,  --VER
	null,
	0
from afiliado a, detalle_declaracion_jurada_2011 o
left outer join empresa e
on o.cuit= e.cuit
and e.sucursal = (select min(em.sucursal) from empresa em where em.cuit = e.cuit)
where o.cuil= a.cuil
and o.periodo>=fecha_desde
and a.cuil=p_cuit
and a.ingre_fecha=(select max(ingre_fecha) from afiliado a2 where a.cuil=a2.cuil and (a2.aportante_titular=1 or a.inte=0))
and o.secobligacion=(select max(secobligacion) from detalle_declaracion_jurada_2011 o2 where o.cuit=o2.cuit and o.cuil=o2.cuil and o.periodo=o2.periodo)
and (a.aportante_titular = 1 or a.inte = 0)
and not exists (select 1 from aporte_histo ah where ah.cuil_titular=o.cuil and ah.cuit=o.cuit and ah.periodo=o.periodo);

update aporte_histo a
set remuneracion=d.remuneracionafectos
from detalle_declaracion_jurada_2011 d
where d.cuil=a.cuil_titular
and d.cuit=a.cuit
and d.periodo=a.periodo;

end if;

RAISE INFO '>2011 DESEMPLEO';

--DESEMPLEO
INSERT INTO aporte_histo(
            periodo, importe, contrib_est, fecha_transf, cuil_titular, apellido, 
            nombre, ingre_fecha, baja_fecha, cuit, razon_soc, sucursal, fecha_liq, 
            total_terc, liq_actas)
select fecha_proceso,v.importe, 0, fecha_proceso, a.cuil_titular, 'DERIVACION', 'DESEMPLEO', a.ingre_fecha, cast(a.baja_fecha as date),'33637617449','DESEMPLEO ANSES','000',fecha_proceso,round(v.importe*0.9,2),0
from detalle_desempleo_anses d, afiliado a, valor_capitas_desempleo v
where d.cuil_titular=p_cuit
and a.cuil_titular=d.cuil_titular 
and a.cuil=d.cuil
and fu_obtener_edad(fecha_nac,fecha_proceso) >v.min
and fu_obtener_edad(fecha_nac,fecha_proceso) <v.max
and d.sexo=v.sexo
and d.fecha_proceso>fecha_desde
and (v.vigen_hasta is null or d.fecha_vig<=v.vigen_hasta);




return query
select      periodo, importe, contrib_est, fecha_transf, cuil_titular, apellido, 
            nombre, ingre_fecha, baja_fecha, cuit, razon_soc, sucursal, fecha_liq, 
            total_terc, liq_actas, comisionOS, fechaTransf, fecha_recauda, concepto, remuneracion, id_terc
 from aporte_histo
 where periodo is not null 
order by periodo desc, total_terc desc;

end;
$BODY$
  LANGUAGE plpgsql VOLATILE

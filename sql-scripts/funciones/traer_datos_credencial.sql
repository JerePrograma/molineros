CREATE TYPE datos_credencial_afi AS
   (cuil character varying,
    plan_p text,
    apellido text,
    nombre text,
    tdoc text,
    nro_doc integer,
    inte integer,
    categoria text,
    id_uoma integer,
    id_ospim integer,
    id_amtima integer,
    parentesco text,
    ingreso text,
    baja_fecha text,
    id_seccional integer,
    seccional_p character varying,
    cuit character varying,
    razon_soc character varying,
    disca character varying,
    plan_omint character varying);
    


CREATE OR REPLACE FUNCTION traer_datos_credencial(id_lote_p character varying)
  RETURNS SETOF datos_credencial_afi AS
$BODY$
BEGIN

drop table if exists creden_to_print;
create temp table creden_to_print as --RELACION DE DEPENDENCIA
select a.cuil_titular, upper(pl.descripcion_tarjeta) as descr, upper(a.apellido) as apellido, upper(a.nombre) as nombre, upper(a.documento_tipo) as docu_tipo, 
		   cast (a.docu_numero as integer), a.inte ,cl.id_categoria, upper(cl.categoria) as categoria,		    
		    case when ap.id_plan in (select id_plan from plan where uoma is true) then a.id_uoma else null  end as uoma_baja,  
		    case when ap.id_plan in (select id_plan from plan where ospim is true) then a.id_ospim else null end as ospim_baja, 
		    case when ap.id_plan in (select id_plan from plan where amtima is true) then a.id_amtima else null end as amtima_baja,
		    --upper(a.parentesco) as parentesco, 
		    upper(pa.descripcion) as parentesco, 
		    to_char(a.vigen_fecha, 'DD/MM/YY') as vigen_fecha, 
		    case when a.inte=0 and (ap.vigen_hasta<a.baja_fecha or a.baja_fecha is null) 
			then ap.vigen_hasta
			when a.inte>0 and ap.vigen_hasta<a.baja_fecha then ap.vigen_hasta
			else a.baja_fecha
			end as baja_fecha, 
		    s.id_seccional, s.descripcion, 
		    e.cuit, cast(rtrim(e.razon_soc) as varchar) as razon_soc, a.discapacitado, plo.descripcion as plan_omint, ap.id_plan,
		    sl.fecha_egre as fecha_fin_laboral
from afi_creden_lote ac
inner join afiliado a on
a.cuil_titular=ac.cuil_titular
and   a.inte=ac.inte
inner join parentesco_sss pa on a.id_parentesco_sss = pa.codigo 
inner join afi_plan ap on
ap.cuil_titular=a.cuil_titular
and   ap.inte=0 -- a.inte Titular
inner join  plan pl on
pl.id_plan=ap.id_plan
inner join seccional s on
s.id_seccional=a.id_seccional
inner join afi_situ_laboral sl on
sl.cuil_titular=a.cuil_titular
and   sl.inte=0 -- a.inte Titular
inner join  empresa e on
e.cuit=sl.cuit
and   e.sucursal=sl.sucursal
inner join categoria_laboral cl on
cl.id_categoria=sl.id_categoria
left outer join plan_omint plo
on plo.id_plan=pl.id_plan
where ac.id_lote=cast($1 as integer) 
and ap.baja_fecha is null
and   (ap.vigen_hasta is null or (ap.vigen_hasta is not null and ap.vigen_hasta>current_date
				  and ap.vigen_hasta =(select max(vigen_hasta) 
						       from afi_plan app
						       where app.cuil_titular=ap.cuil_titular
						       and app.inte=ap.inte
						       and app.baja_fecha is null)))
and   (a.baja_fecha is null or a.baja_fecha > current_timestamp)
and   sl.baja_fecha is null
and   (sl.fecha_egre is null or sl.fecha_egre > current_timestamp or (sl.id_motivo_baja in (1,3,17) and current_date<sl.fecha_egre+Interval '3 month'))
and   cl.categoria not like ('%JUBIL%') 
and   cl.categoria not like ('%MONOTRIB%')
UNION --JUBILADOS Y MONOTRIBUTISTAS
select a.cuil_titular, upper(pl.descripcion_tarjeta), upper(a.apellido), upper(a.nombre), upper(a.documento_tipo), cast(a.docu_numero as integer), a.inte ,
	cl.id_categoria,upper(cl.categoria),       
       case when ap.id_plan in (select id_plan from plan where uoma is true) then a.id_uoma else null  end as uoma_baja,  
       case when ap.id_plan in (select id_plan from plan where ospim is true) then a.id_ospim else null end as ospim_baja, 
       case when ap.id_plan in (select id_plan from plan where amtima is true) then a.id_amtima else null end as amtima_baja,
       --upper(a.parentesco),
       upper(pa.descripcion) as parentesco,  
       to_char(a.vigen_fecha, 'DD/MM/YY'),
       case when a.inte=0 and (ap.vigen_hasta<a.baja_fecha or a.baja_fecha is null) 
			then ap.vigen_hasta
			when a.inte>0 and ap.vigen_hasta<a.baja_fecha then ap.vigen_hasta
			else a.baja_fecha
			end as baja_fecha, 
       --to_char(a.baja_fecha,'DD/MM/YY'), 
       s.id_seccional, s.descripcion, a.cuil_titular, upper(a.apellido)||' '||upper(a.nombre), a.discapacitado, plo.descripcion as plan_omint,
       ap.id_plan, sl.fecha_egre as fecha_fin_laboral
from afi_creden_lote ac     
inner join afiliado a on
a.cuil_titular=ac.cuil_titular
and   a.inte=ac.inte
inner join parentesco_sss pa on a.id_parentesco_sss = pa.codigo 
inner join afi_plan ap
on ap.cuil_titular=a.cuil_titular
and   ap.inte=0 
inner join plan pl
on pl.id_plan=ap.id_plan
left outer join plan_omint plo
on plo.id_plan=pl.id_plan    
inner join seccional s on             
s.id_seccional=a.id_seccional
inner join afi_situ_laboral sl on 
sl.cuil_titular=a.cuil_titular
and   sl.inte=0 -- a.inte Titular
inner join categoria_laboral cl
on cl.id_categoria=sl.id_categoria
where ac.id_lote=cast($1 as integer) 
and ap.baja_fecha is null
and   (ap.vigen_hasta is null or (ap.vigen_hasta is not null and ap.vigen_hasta>current_date
				  and ap.vigen_hasta =(select max(vigen_hasta) 
						       from afi_plan app
						       where app.cuil_titular=ap.cuil_titular
						       and app.inte=ap.inte
						       and app.baja_fecha is null)))
--and   upper(a.parentesco) like '%TITULAR%'
and   (a.baja_fecha is null or a.baja_fecha > current_timestamp)
and   (sl.baja_fecha is null or sl.baja_fecha > current_timestamp)
and   (sl.fecha_egre is null or sl.fecha_egre > current_timestamp)
and   (cl.categoria like ('%JUBIL%') or cl.categoria like ('%MONOTRIB%') or cl.categoria like ('%DOMESTICO%'));

--EN EL CASO DE QUE HAYA DOS VIGENCIAS DE PLAN, SOLO QUEREMOS LA ULTIMA.
delete from creden_to_print c
where (c.baja_fecha is not null 
	and exists (select 1 from creden_to_print cc 
	where cc.cuil_titular=c.cuil_titular and cc.inte=c.inte and (cc.baja_fecha is null or cc.baja_fecha>c.baja_fecha)));
	
--EN EL CASO DE QUE HAYA DOS SITUS LABORALES, SOLO QUEREMOS LA ULTIMA.
delete from creden_to_print c
where (c.fecha_fin_laboral is not null 
	and exists (select 1 from creden_to_print cc 
	where cc.cuil_titular=c.cuil_titular and cc.inte=c.inte and (cc.fecha_fin_laboral is null or cc.fecha_fin_laboral>c.fecha_fin_laboral)));	

--EL PLAN YA NO IMPORTA MAS QUITAR DEL JRXML POR AHORA PARA QUE ANDE SIN REINICIAR
update creden_to_print set plan_omint='';
update creden_to_print set descr='';

return query 
select distinct cuil_titular, descr, apellido, nombre, docu_tipo, docu_numero, inte, categoria, uoma_baja, ospim_baja, amtima_baja,
		parentesco, vigen_fecha, to_char(baja_fecha,'DD/MM/YY') , id_seccional, descripcion, case when id_categoria in (8,10,12) then cast('' as varchar) else cuit end as cuit, 
case when id_categoria in (8,10,12) then cast('' as varchar) else cast(rtrim(substring(razon_soc,1,25)) as varchar) end as razon_soc, discapacitado, plan_omint 
from creden_to_print c 
order by 16;


END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
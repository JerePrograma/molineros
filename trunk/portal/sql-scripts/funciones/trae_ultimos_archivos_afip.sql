create type ultimos_archivos_afip as (tipo varchar, fecha_proceso date, cant_reg integer, importe_total double precision)


create or replace function trae_ultimos_archivos_afip() 
returns setof ultimos_archivos_afip AS
$BODY$
begin
drop table if exists archivos_ddjj;

create temp table archivos_ddjj as
select cast('APORTES' as varchar) as tipo,fecha_proceso, cant_reg, importe_total 
from os_aportes_footer 
order by fecha_proceso desc
limit 2;

insert into archivos_ddjj(tipo, fecha_proceso, cant_reg, importe_total)
select cast('DDJJ' as varchar), fechaproceso, cantregistros, cast(0 as numeric)
from footer_declaracion_jurada 
order by fechaproceso desc
limit 2;

insert into archivos_ddjj(tipo, fecha_proceso, cant_reg, importe_total)
select cast('EX.BCRIA' as varchar), fecha, cast(null as int), cast(null as numeric)
from extraccion_bancaria
order by fecha desc
limit 2;

insert into archivos_ddjj(tipo, fecha_proceso, cant_reg, importe_total)
select cast('TRANSF.EX' as varchar),fecha_proceso, cast(null as int), sum(importe_transferencia)
from detalle_transferencia_externa dte
group by fecha_proceso
order by fecha_proceso desc
limit 2;

--insert into archivos_ddjj(tipo, fecha_proceso, cant_reg, importe_total)
--select cast('SUBSIDIO OS' as varchar), fecha_proceso, cantidadregistros, importesubsidio 
--from footer_subsidio_os  
--order by fecha_proceso desc
--limit 2;
insert into archivos_ddjj(tipo, fecha_proceso, cant_reg, importe_total)
select identificador, fecha_proceso, cantidadregistros, importesubsidio 
from footer_subsidio_os 
where identificador in ('SUMARTE','SUMA70','SUBASI')
order by fecha_proceso desc
limit 3;

insert into archivos_ddjj(tipo, fecha_proceso, cant_reg, importe_total)
select cast('DESEMPLEO' as varchar), fecha_proceso, cast(count(*) as int), cast(null as numeric) 
from detalle_desempleo_anses  
group by fecha_proceso
order by fecha_proceso desc
limit 1;

insert into archivos_ddjj(tipo, fecha_proceso, cant_reg, importe_total)
select cast('PADRON CONTRIB' as varchar), fechaproceso, cantregistros, cast(null as numeric) 
from footer_padron_contribuyentes    
order by fechaproceso desc
limit 2;

return query
select * from archivos_ddjj
order by tipo,fecha_proceso;

end;
$BODY$
Language 'plpgsql'

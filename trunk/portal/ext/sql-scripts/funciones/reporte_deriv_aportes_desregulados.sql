create type liquidacion_tercerizadoras as (cuit_contribuyente varchar, periodo text, ingre_fecha text, cuil_aportante varchar,
                                        remuneracion text, aporte text, contribucion text, total text, total_terce text,
					apellido_nombre text)


create or replace function reporte_deriv_aportes_desregulados (id_terc_v varchar, fecha_liq_v date) 
returns setof liquidacion_tercerizadoras AS
$BODY$
begin 
drop table if exists aux;
create temp table aux as 
select lh.cuit,
       to_char(lh.periodo_hab_d,'MM/yyyy') as periodo_hab_d,
       cast (null as text) as ingre_fecha, --to_char(a.ingre_fecha,'MM/yyyy'),
       lh.cuil,
       lpad(cast(round(cast(lh.sueldo_n as numeric),2) as varchar),11,' ') as remuneracion,
       lpad(cast(round(cast(lh.aporte_n as numeric),2) as varchar),16,' ') as aporte,       
       lpad(cast(round(cast(lh.contrib_n as numeric),2) as varchar),11,' ') as contribucion,
       lpad(cast(round(cast(lh.aporte_n+lh.contrib_n as numeric),2) as varchar),11,' ') as total,
       lpad(cast(round(cast(total_terc as numeric),2) as varchar),9,' ') as total_terce,
       cast(null as text) as apellido_nombre --rpad(rtrim(apellido||', '||nombre),94)     
from liquidacion_historica_tercerizadoras_2011 lh
where lh.id_terc=id_terc_v
and fecha_liq=fecha_liq_v;


update aux x
set ingre_fecha=to_char(a.ingre_fecha,'MM/yyyy'),
    apellido_nombre=rpad(rtrim(apellido||', '||nombre),94)
from afiliado a
where a.cuil=x.cuil
and a.inte=(select min(inte) from afiliado a2 where a2.cuil=a.cuil);    

return query
select cuit,
       periodo_hab_d,
       ingre_fecha, --to_char(a.ingre_fecha,'MM/yyyy'),
       cuil,
       remuneracion,
       aporte,       
       contribucion,
       total,
       total_terce,
       apellido_nombre --rpad(rtrim(apellido||', '||nombre),94)     
from aux a
where apellido_nombre is not null
order by cuit, cuil;

			
end;
$BODY$
Language 'plpgsql'


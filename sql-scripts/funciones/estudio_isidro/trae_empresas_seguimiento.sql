create type empresas_seguimiento as (razon_soc varchar, cuit varchar, contacto text, tel text, email text, estado text,
fecha_calc_deuda timestamp without time zone, molinera boolean, carta_doc varchar, ubicacion_carpeta varchar)

CREATE OR REPLACE FUNCTION trae_empresas_seguimiento(cuit_p character varying, razon_p character varying, estado_p character varying, molinera_p boolean)
  RETURNS SETOF empresas_seguimiento AS
$BODY$
BEGIN

drop table if exists aux_seg;
if cuit_p is not null and rtrim(cuit_p)<>'' then
create temp table aux_seg as
select distinct e.razon_soc, e.cuit, cast(e.contacto as text) as contacto, cast(t.numero as text) as numero,
cast(substring(ce.contacto,0,50) as text) as email, i.estado, ac.fecha_pago, r.molinera, i.carta_doc, i.ubicacion_carpeta
from estudio_llamadas_empresas  el 
left outer join empresa e
on e.cuit=el.cuit
and e.sucursal='000'
left outer join ramo_empresa r
on e.id_ramo_empresa=r.id_ramo_empresa
left outer join estudio_empresas_info i
on i.cuit=el.cuit
and i.fecha=(select max(fecha) from estudio_empresas_info ii where ii.cuit=i.cuit)
left outer join emp_contacto_e ece
on ece.cuit=el.cuit
and ece.sucursal='000'
and ece.id_contacto_e=(select max(e2.id_contacto_e) from emp_contacto_e ece2, contacto_e e2
		       where ece2.cuit=ece.cuit 
		       and ece2.sucursal=ece.sucursal
		       and e2.id_contacto_e=ece2.id_contacto_e
		       and e2.tipo_contacto_e='E' )
left outer join contacto_e ce
on ce.id_contacto_e=ece.id_contacto_e
left outer join emp_telefono et
on et.cuit=el.cuit
and et.sucursal='000'
and et.id_telefono=(select max(e2.id_telefono) from emp_telefono ece2, telefono e2
		       where ece2.cuit=ece.cuit 
		       and ece2.sucursal=ece.sucursal
		       and e2.id_telefono=ece2.id_telefono
		       and rtrim(e2.numero)<>'' and e2.numero is not null)
left outer join telefono t
on t.id_telefono=et.id_telefono
left outer join acta ac
on ac.cuit=el.cuit
and ac.sucursal='000'
and ac.acta_cerrada=false
and ac.modi_fecha=(select max(modi_fecha) from acta ac2 
		where ac2.cuit=ac.cuit
		and ac2.sucursal='000'
		and ac2.acta_cerrada=false
		and ac2.baja_fecha is null)
where i.cuit=case when cuit_p is null then i.cuit else cuit_p end
order by e.razon_soc;
else 
create temp table aux_seg as
select distinct e.razon_soc, e.cuit, cast(e.contacto as text) as contacto, cast(t.numero as text) as numero, cast(substring(ce.contacto,0,50) as text) as email, i.estado, ac.fecha_pago, i.molinera
, i.carta_doc, i.ubicacion_carpeta
from estudio_llamadas_empresas  el 
left outer join empresa e
on e.cuit=el.cuit
and e.sucursal='000'
left outer join estudio_empresas_info i
on cast(i.cuit as varchar)=el.cuit
and i.fecha=(select max(fecha) from estudio_empresas_info ii where ii.cuit=i.cuit)
left outer join emp_contacto_e ece
on ece.cuit=el.cuit
and ece.sucursal='000'
and ece.id_contacto_e=(select max(e2.id_contacto_e) from emp_contacto_e ece2, contacto_e e2
		       where ece2.cuit=ece.cuit 
		       and ece2.sucursal=ece.sucursal
		       and e2.id_contacto_e=ece2.id_contacto_e
		       and e2.tipo_contacto_e='E' )
left outer join contacto_e ce
on ce.id_contacto_e=ece.id_contacto_e
left outer join emp_telefono et
on et.cuit=el.cuit
and et.sucursal='000'
and et.id_telefono=(select max(e2.id_telefono) from emp_telefono ece2, telefono e2
		       where ece2.cuit=ece.cuit 
		       and ece2.sucursal=ece.sucursal
		       and e2.id_telefono=ece2.id_telefono
		       and rtrim(e2.numero)<>'' and e2.numero is not null)
left outer join telefono t
on t.id_telefono=et.id_telefono
left outer join acta ac
on ac.cuit=el.cuit
and ac.sucursal='000'
and ac.acta_cerrada=false
and ac.modi_fecha=(select max(modi_fecha) from acta ac2 
		where ac2.cuit=ac.cuit
		and ac2.sucursal='000'
		and ac2.acta_cerrada=false
		and ac2.baja_fecha is null)
where (estado_p is null or (estado_p is not null  and upper(rtrim(i.estado)) like '%' || upper(estado_p) || '%')) 
--and (razon_p is null or (razon_p is not null  and upper(rtrim(e.razon_soc)) like '%' || upper(razon_p) || '%')) 
order by e.razon_soc;
end if;
return query
select razon_soc, cuit, contacto, numero, email, estado, fecha_pago, molinera, carta_doc, ubicacion_carpeta
from aux_seg
where molinera=case when molinera_p is null then molinera else molinera_p end;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE

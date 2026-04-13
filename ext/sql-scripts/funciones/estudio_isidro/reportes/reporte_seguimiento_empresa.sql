create type reporte_seguimiento_empresa as (cuit varchar, razon_soc varchar, estado text, fecha_estado timestamp without time zone,
molinera boolean, carta_doc varchar, ubicacion_carpeta varchar, fecha_llamado timestamp without time zone, observaciones varchar,
usuario varchar)

CREATE OR REPLACE FUNCTION reporte_seguimiento_empresa(cuit_p character varying, periodo_ini date, periodo_fin date)
  RETURNS SETOF reporte_seguimiento_empresa AS
$BODY$
BEGIN
return query
select e.cuit, e.razon_soc, ei.estado, ei.fecha as fecha_estado, ei.molinera, ei.carta_doc, ei.ubicacion_carpeta, 
       el.fecha as fecha_llamado, el.observaciones, el.usuario
from estudio_llamadas_empresas  el
inner join empresa e
on e.cuit=el.cuit
and e.sucursal='000'
left outer join estudio_empresas_info ei
on el.cuit=ei.cuit
and ei.fecha=(select max(fecha) from estudio_empresas_info ei2 where ei2.cuit=ei.cuit)
where (cuit_p is null or (cuit_p is not null  and el.cuit=cuit_p))
and el.fecha>=periodo_ini
and el.fecha<=periodo_fin
order by e.cuit,el.fecha;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE


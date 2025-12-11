create type reporte_ultimo_padron as(fecha timestamp with time zone,id_terc varchar, tercerizadora varchar, tipo varchar)
CREATE OR REPLACE FUNCTION from informes.traer_ultimo_padron()
  RETURNS SETOF reporte_ultimo_padron AS
$BODY$
declare resultDom integer;
BEGIN
return query 
select t.fecha_listado, ts.id_tercerizadora, ts.descripcion, tipo
from informes.listado_tercerizadora t, tercerizadora_servicio ts
where ts.id_tercerizadora=t.id_tercerizadora
group by t.fecha_listado,ts.id_tercerizadora, ts.descripcion, tipo
order by t.fecha_listado,ts.descripcion, tipo;


END;
$BODY$
  LANGUAGE plpgsql VOLATILE

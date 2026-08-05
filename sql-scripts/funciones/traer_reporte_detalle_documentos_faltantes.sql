drop function traer_reporte_detalle_documentos_faltantes(IN p_cuil_titular character varying, IN p_inte integer, IN p_fecha_desde date, IN p_fecha_hasta date);

CREATE OR REPLACE FUNCTION traer_reporte_detalle_documentos_faltantes(IN p_cuil_titular character varying, IN p_inte integer, IN p_fecha_desde date, IN p_fecha_hasta date)

RETURNS TABLE(id_trata integer, documentos_string character varying) AS
$BODY$

declare hola integer;
begin

hola = setval('reporte_doc_faltante', 0);

return query

SELECT subq.id_tratamiento, cast(cast(nextval('reporte_doc_faltante') as character varying) 
|| '. ' || array_to_string(array_agg(subq.descr), ' - ') as character varying) as docs_str

from
(
select df.id_tratamiento, dd.id_documento, dd.descripcion descr
from documento_faltante_tratamiento df, documento_discapacidad dd where id_tratamiento in (

select id_tratamiento
from tratamiento_discapacidad td, nomenclador n
where td.cuil_titular = p_cuil_titular
and td.inte = p_inte
and td.periodo_desde >= p_fecha_desde
and td.periodo_hasta <= p_fecha_hasta
and td.id_prestacion = n.id_prestacion
and td.baja_fecha is null
and df.id_documento = dd.id_documento
and td.id_tratamiento in (
select id_tratamiento from documento_faltante_tratamiento 
)

)
order by df.id_tratamiento

) subq

group by (subq.id_tratamiento)

order by subq.id_tratamiento;

end;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;

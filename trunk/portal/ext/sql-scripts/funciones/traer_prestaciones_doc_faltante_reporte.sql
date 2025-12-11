drop function traer_prestaciones_doc_faltante_reporte(IN p_cuil_titular character varying, IN p_inte integer, IN p_fecha_desde date, IN p_fecha_hasta date);

CREATE OR REPLACE FUNCTION traer_prestaciones_doc_faltante_reporte(IN p_cuil_titular character varying, IN p_inte integer, IN p_fecha_desde date, IN p_fecha_hasta date)

RETURNS TABLE(id_trata integer, prestacion_string character varying) AS
$BODY$

declare hola integer;
begin

hola = setval('reporte_doc_faltante', 0);

return query

SELECT td.id_tratamiento, cast(cast(nextval('reporte_doc_faltante') as character varying) || '. ' || n.descripcion as character varying) as prest
from tratamiento_discapacidad td, nomenclador n
where td.cuil_titular = p_cuil_titular
and td.inte = p_inte
and td.periodo_desde >= p_fecha_desde
and td.periodo_hasta <= p_fecha_hasta
and td.id_prestacion = n.id_prestacion
and td.baja_fecha is null
and td.id_tratamiento in (

select id_tratamiento from documento_faltante_tratamiento

)
order by prest;	  	

end;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;

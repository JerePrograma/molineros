-- Function: trae_tipos_aporte_plan(character varying, integer, integer)

-- DROP FUNCTION trae_tipos_aporte_plan(character varying, integer, integer);

CREATE OR REPLACE FUNCTION trae_tipos_aporte_plan(IN cuil character varying, IN inte integer, IN id_plan_v integer)
  RETURNS TABLE(cuil_titular character varying, inte integer, cuil character varying, id_aporte integer, descripcion character varying, fechaingreso date, es_os boolean) AS
$BODY$
select $1, --cuil_titular,
       $2, --inte,
       $1, --cuil,
       a.id_aporte, 
       a.descripcion,
       current_date as fecha_ingreso,
       es_os --ARREGLAR
from aporte a,plan_aporte ap--, afiliado f
where a.id_aporte=ap.id_aporte
and ap.id_plan=$3
--and f.cuil_titular=$1
--and f.inte=$2
order by descripcion
$BODY$
  LANGUAGE sql VOLATILE


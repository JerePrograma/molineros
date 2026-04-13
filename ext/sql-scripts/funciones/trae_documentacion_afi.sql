CREATE OR REPLACE FUNCTION trae_documentacion_afi(IN cuil character varying, IN inte integer)
  RETURNS TABLE(cuil_titular character varying, inte integer, id_documento integer, descripcion character varying, fecha_ingreso date, fecha_egreso date, id integer) AS
$BODY$
select a.cuil_titular,
       a.inte,
       a.id_documento, 
       t.descripcion,
       a.fecha_ini,
       a.fecha_vto,
       a.id
from afi_documento a
INNER JOIN documento t on (a.id_documento=t.id_documento)
where a.cuil_titular=$1
and a.baja_fecha is null
--and a.inte=$2
order by a.fecha_ini
$BODY$
  LANGUAGE sql VOLATILE


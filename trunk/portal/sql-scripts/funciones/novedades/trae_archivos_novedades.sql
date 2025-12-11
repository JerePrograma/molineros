CREATE OR REPLACE FUNCTION novedades_sss.trae_archivos_novedades(IN fecha_p date)
  RETURNS TABLE(fecha_archivo date, descripcion character varying(50), 
   cant_registros integer, import_usr character varying(15), import_fecha timestamp without time zone) AS
$BODY$	

select fecha_archivo, descripcion, cant_registros, import_usr, import_fecha 
from novedades_sss.archivos_novedades
where ($1 is null or $1 is not null and fecha_archivo <=$1)
order by fecha_archivo DESC , descripcion 
limit 10

$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 100;
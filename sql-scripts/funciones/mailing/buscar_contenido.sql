
CREATE OR REPLACE FUNCTION buscar_contenido(IN id_boletin_p integer)
  RETURNS TABLE(id_contenido integer, titulo character varying, contenido character varying, seccion character varying) AS
$BODY$
select id, titulo, contenido, seccion
from boletin_contenido  o
where o.baja_fecha is null
and id_boletin=$1
$BODY$
  LANGUAGE sql VOLATILE
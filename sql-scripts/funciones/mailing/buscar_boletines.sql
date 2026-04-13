CREATE OR REPLACE FUNCTION buscar_boletines(IN id_p integer, IN nombre_p character varying, IN asunto_p character varying)
  RETURNS TABLE(id_boletin integer, nombre character varying, asunto character varying, observaciones character varying) AS
$BODY$
select s.id, s.nombre, s.asunto, s.observaciones
from boletin s
where s.id= case when $1 is null then s.id else $1 end
and upper(s.nombre)= case when $2 is null then upper(s.nombre) else upper($2) end
and upper(s.asunto)= case when $3 is null then upper(s.asunto) else upper($3) end
and s.baja_fecha is null
$BODY$
  LANGUAGE sql VOLATILE

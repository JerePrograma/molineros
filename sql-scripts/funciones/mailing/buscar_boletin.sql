CREATE OR REPLACE FUNCTION buscar_boletin(IN id_boletin_p integer)
  RETURNS TABLE(id_boletin integer, nombre character varying, asunto character varying, observaciones character varying, listas character varying) AS
$BODY$
select s.id, s.nombre, s.asunto, s.observaciones, array_to_string(array(SELECT sm.id_mailing_list 
									 FROM boletin_listas sm
									 where sm.id_boletin=$1
									 and sm.baja_fecha is null), ',')
from boletin s
where s.id=$1
$BODY$
  LANGUAGE sql VOLATILE

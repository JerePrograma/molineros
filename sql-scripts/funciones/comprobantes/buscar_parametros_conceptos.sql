
CREATE OR REPLACE FUNCTION buscar_parametros_conceptos() 
RETURNS TABLE(parametro character varying,
	id_concepto integer,
	valido_desde date,
	valido_hasta date)
    LANGUAGE sql
    AS $BODY$


select parametro,
	id_concepto,
	valido_desde,
	valido_hasta 
from  parametros_conceptos;
 
$BODY$;

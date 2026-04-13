CREATE OR REPLACE FUNCTION trae_tipos_pago_contratos() 
RETURNS TABLE(id integer,
 detalle character varying)
    LANGUAGE sql
    AS $BODY$
    
    select id_tipo_pago, 
       descripcion 
from tipo_pago 
where contratos = true
order by descripcion
$BODY$;
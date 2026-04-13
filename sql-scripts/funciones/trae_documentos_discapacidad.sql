CREATE OR REPLACE FUNCTION trae_documentos_discapacidad() 
RETURNS TABLE(id_documento integer,
 descripcion character varying)
    LANGUAGE sql
    AS $BODY$
select id_documento, 
       descripcion       
from documento_discapacidad
where baja_fecha is null
order by descripcion
$BODY$;
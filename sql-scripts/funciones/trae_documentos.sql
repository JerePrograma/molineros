CREATE OR REPLACE FUNCTION trae_documentos() 
RETURNS TABLE(id_documento integer,
 descripcion character varying,
 id_motivo_baja integer)
    LANGUAGE sql
    AS $BODY$
select id_documento, 
       descripcion,
       id_motivo_baja
from documento
order by descripcion
$BODY$;


ALTER FUNCTION public.trae_documentos() OWNER TO postgres;

--
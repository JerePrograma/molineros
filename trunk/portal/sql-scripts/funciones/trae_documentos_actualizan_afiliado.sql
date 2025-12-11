CREATE OR REPLACE FUNCTION trae_documentos_actualizan_afiliado() 
RETURNS TABLE(id_documento integer,
 descripcion character varying)
    LANGUAGE sql
    AS $BODY$
select id_documento,
descripcion
from documento
where baja_fecha is null
and actualiza_afiliado = 1
order by descripcion
$BODY$;


ALTER FUNCTION public.trae_documentos_actualizan_afiliado() OWNER TO postgres;

--

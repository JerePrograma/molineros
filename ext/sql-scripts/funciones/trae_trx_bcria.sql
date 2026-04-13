CREATE OR REPLACE FUNCTION trae_trx_bcria() 
RETURNS TABLE(id_tipo_transaccion integer,
 descripcion character varying)
    LANGUAGE sql
    AS $BODY$
select id_tipo_transaccion, descripcion
from tipo_trans_bcria
$BODY$;


ALTER FUNCTION public.trae_trx_bcria() OWNER TO postgres;

--

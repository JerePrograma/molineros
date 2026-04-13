CREATE OR REPLACE FUNCTION trae_ctas_bcrias() 
RETURNS TABLE(id_cuenta_bcria integer,
 nro_cuenta integer,
 sucursal integer,
 descripcion character varying,
 id_banco integer,
 descripcion_banco character varying)
    LANGUAGE sql
    AS $BODY$
select cb.id_cuenta_bcria,cb.nro_cuenta, cb.sucursal,cb.descripcion, b.id_banco, b.descripcion
from cuenta_bcria cb, banco b
where cb.id_banco=b.id_banco
$BODY$;


ALTER FUNCTION public.trae_ctas_bcrias() OWNER TO postgres;

--

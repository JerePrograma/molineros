DROP FUNCTION trae_tipos_mov_bcrios();
CREATE OR REPLACE FUNCTION trae_tipos_mov_bcrios(fecha date) 
RETURNS TABLE(id_tipo_mov integer,
 descripcion character varying)
    LANGUAGE sql
    AS $BODY$
select id_tipo_mov_maestro, descripcion
from tipo_mov_bcrio
where baja_fecha is null
and valido_desde <= $1 and valido_hasta >= $1
order by descripcion asc;
$BODY$;


ALTER FUNCTION public.trae_tipos_mov_bcrios(fecha date) OWNER TO postgres;

--

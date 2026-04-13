CREATE OR REPLACE FUNCTION trae_tipos_aporte() 
RETURNS TABLE(id_aporte integer,
 tipo_aporte character varying,
 plan character varying,
 descripcion character varying,
 observaciones character varying,
 alta_fecha timestamp without time zone,
 alta_usr character varying,
 modi_fecha timestamp without time zone,
 modi_usr character varying,
 baja_fecha timestamp without time zone,
 baja_usr character varying,
 genera_id_socio character)
    LANGUAGE sql
    AS $BODY$
select 	id_aporte,
    tipo_aporte,
    plan,
    descripcion,
    observaciones,
    alta_fecha,
    alta_usr,
    modi_fecha,
    modi_usr,
    baja_fecha,
    baja_usr,
    genera_id_socio
from aporte
order by descripcion
$BODY$;


ALTER FUNCTION public.trae_tipos_aporte() OWNER TO postgres;

--

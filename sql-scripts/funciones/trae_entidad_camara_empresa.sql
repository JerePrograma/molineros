CREATE OR REPLACE FUNCTION trae_entidad_camara_empresa() 
RETURNS TABLE(id_entidad_cam_empresa smallint,
 descripcion character varying,
 observaciones character varying,
 alta_fecha timestamp without time zone,
 alta_usr character varying,
 modi_fecha timestamp without time zone,
 modi_usr character varying,
 baja_fecha timestamp without time zone,
 baja_usr character varying)
    LANGUAGE sql
    AS $BODY$
select id_entidad_cam_empresa,
  descripcion,
  observaciones,
  alta_fecha,
  alta_usr,
  modi_fecha,
  modi_usr,
  baja_fecha,
  baja_usr
from entidad_cam_empresa
where baja_fecha is null
order by descripcion;

$BODY$;


ALTER FUNCTION public.trae_entidad_camara_empresa() OWNER TO postgres;

--

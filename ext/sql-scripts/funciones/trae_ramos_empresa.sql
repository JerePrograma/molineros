CREATE OR REPLACE FUNCTION trae_ramos_empresa() 
RETURNS TABLE(id_ramo_empresa smallint,
 descripcion character varying,
 observaciones character varying,
 alta_fecha timestamp without time zone,
 alta_usr character varying,
 modi_fecha timestamp without time zone,
 modi_usr character varying,
 baja_fecha timestamp without time zone,
 baja_usr character)
    LANGUAGE sql
    AS $BODY$
select id_ramo_empresa,
  descripcion,
  observaciones,
  alta_fecha,
  alta_usr,
  modi_fecha,
  modi_usr,
  baja_fecha,
  baja_usr
  from ramo_empresa
  order by descripcion asc
$BODY$;


ALTER FUNCTION public.trae_ramos_empresa() OWNER TO postgres;

--

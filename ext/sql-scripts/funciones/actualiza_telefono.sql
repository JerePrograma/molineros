CREATE OR REPLACE FUNCTION actualiza_telefono(p_tipo_tele character varying,
 p_codigo_pais character varying,
 p_codigo_area character varying,
 p_numero character varying,
 p_extension character varying,
 p_observaciones character varying,
 p_modi_fecha timestamp without time zone,
 p_modi_usr character varying,
 p_id integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
  begin

update telefono set
  tipo_tele =  p_tipo_tele,
  codigo_pais = p_codigo_pais,
  codigo_area = p_codigo_area,
  numero = p_numero,
  extension = p_extension,
  observaciones = p_observaciones,
  modi_fecha = p_modi_fecha,
  modi_usr = p_modi_usr
  where id_telefono = p_id;
   
  return 1;
  end;  
$BODY$;


ALTER FUNCTION public.actualiza_telefono(p_tipo_tele character varying, p_codigo_pais character varying, p_codigo_area character varying, p_numero character varying, p_extension character varying, p_observaciones character varying, p_modi_fecha timestamp without time zone, p_modi_usr character varying, p_id integer) OWNER TO postgres;

--

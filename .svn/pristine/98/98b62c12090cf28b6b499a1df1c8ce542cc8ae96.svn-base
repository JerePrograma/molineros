CREATE OR REPLACE FUNCTION inserta_telefono(p_tipo_tele character varying,
 p_vigen_desde timestamp without time zone,
 p_codigo_pais character varying,
 p_codigo_area character varying,
 p_numero character varying,
 p_extension character varying,
 p_observaciones character varying,
 p_alta_fecha timestamp without time zone,
 p_alta_usr character varying,
 p_modi_fecha timestamp without time zone,
 p_modi_usr character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
  begin

insert into telefono (
  tipo_tele,
  vigen_desde,
  codigo_pais,
  codigo_area,
  numero,
  extension,
  observaciones,
  alta_fecha,
  alta_usr,
  modi_fecha,
  modi_usr)  
 values (
  p_tipo_tele,
  p_vigen_desde,
  p_codigo_pais,
  p_codigo_area,
  p_numero,
  p_extension,
  p_observaciones,
  p_alta_fecha,
  p_alta_usr,
  p_modi_fecha,
  p_modi_usr);
   
  return currval('telefono_id_seq');
  end;  
$BODY$;


ALTER FUNCTION public.inserta_telefono(p_tipo_tele character varying, p_vigen_desde timestamp without time zone, p_codigo_pais character varying, p_codigo_area character varying, p_numero character varying, p_extension character varying, p_observaciones character varying, p_alta_fecha timestamp without time zone, p_alta_usr character varying, p_modi_fecha timestamp without time zone, p_modi_usr character varying) OWNER TO postgres;

--

CREATE OR REPLACE FUNCTION inserta_contacto_e(p_tipo_contacto_e character varying,
 p_vigen_desde timestamp without time zone,
 p_contacto character varying,
 p_observaciones character varying,
 p_alta_fecha timestamp without time zone,
 p_alta_usr character varying,
 p_modi_fecha timestamp without time zone,
 p_modi_usr character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
  begin

  insert into contacto_e ( 
  tipo_contacto_e,
  vigen_desde,
  contacto,
  observaciones,
  alta_fecha,
  alta_usr,
  modi_fecha,
  modi_usr
  ) values (
  p_tipo_contacto_e,
  p_vigen_desde,
  p_contacto,
  p_observaciones,
  p_alta_fecha,
  p_alta_usr,
  p_modi_fecha,
  p_modi_usr); 
   
  return currval('contacto_e_id_seq');
  end;  
$BODY$;


ALTER FUNCTION public.inserta_contacto_e(p_tipo_contacto_e character varying, p_vigen_desde timestamp without time zone, p_contacto character varying, p_observaciones character varying, p_alta_fecha timestamp without time zone, p_alta_usr character varying, p_modi_fecha timestamp without time zone, p_modi_usr character varying) OWNER TO postgres;

--

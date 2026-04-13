CREATE OR REPLACE FUNCTION actualiza_contacto_e(p_tipo_contacto_e character varying,
 p_contacto character varying,
 p_observaciones character varying,
 p_modi_fecha timestamp without time zone,
 p_modi_usr character varying,
 p_id_contacto_e integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
  begin

 update  contacto_e set  
  tipo_contacto_e = p_tipo_contacto_e,
  contacto = p_contacto ,
  observaciones = p_observaciones ,
  modi_fecha = p_modi_fecha ,
  modi_usr = p_modi_usr 
  where id_contacto_e = p_id_contacto_e;
  
   
  return 1;
  end;  
$BODY$;


ALTER FUNCTION public.actualiza_contacto_e(p_tipo_contacto_e character varying, p_contacto character varying, p_observaciones character varying, p_modi_fecha timestamp without time zone, p_modi_usr character varying, p_id_contacto_e integer) OWNER TO postgres;

--

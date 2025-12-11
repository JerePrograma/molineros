CREATE OR REPLACE FUNCTION inserta_domicilio(p_domi_tipo character varying,
 p_calle character varying,
 p_piso character varying,
 p_depto character varying,
 p_oficina character varying,
 p_postal_codi character varying,
 p_barrio character varying,
 p_telefono character varying,
 p_observaciones character varying,
 p_domi_val character varying,
 p_alta_fecha timestamp without time zone,
 p_alta_usr character varying,
 p_modi_fecha timestamp without time zone,
 p_modi_usr character varying,
 p_provincia integer,
 p_localidad integer,
 p_numero character varying,
 p_localidad_nombre character,
 p_provincia_nombre character) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
  begin

  insert into domicilio (  domi_tipo,
  calle,
  piso,
  depto,
  oficina,
  postal_codi,
  telefono,
  observaciones,
  domi_val,
  alta_fecha,
  alta_usr,
  modi_fecha,
  modi_usr,
  provincia,
  localidad,
  numero,
  localidad_nombre,
  provincia_nombre
  ) values (p_domi_tipo,
  p_calle,
  p_piso,
  p_depto,
  p_oficina,
  p_postal_codi,
  p_telefono,
  p_observaciones,
  p_domi_val,
  p_alta_fecha,
  p_alta_usr,
  p_modi_fecha,
  p_modi_usr,
  p_provincia,
  p_localidad,
  p_numero, 
  p_localidad_nombre,
  p_provincia_nombre); 
   
  return currval('domicilio_id_seq');
  end;  
$BODY$;


ALTER FUNCTION public.inserta_domicilio(p_domi_tipo character varying, p_calle character varying, p_piso character varying, p_depto character varying, p_oficina character varying, p_postal_codi character varying, p_barrio character varying, p_telefono character varying, p_observaciones character varying, p_domi_val character varying, p_alta_fecha timestamp without time zone, p_alta_usr character varying, p_modi_fecha timestamp without time zone, p_modi_usr character varying, p_provincia integer, p_localidad integer, p_numero character varying, p_localidad_nombre character, p_provincia_nombre character) OWNER TO postgres;

--

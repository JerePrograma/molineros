CREATE OR REPLACE FUNCTION actualiza_domicilio(p_id integer,
 p_domi_tipo character varying,
 p_calle character varying,
 p_piso character varying,
 p_depto character varying,
 p_oficina character varying,
 p_codpostal character varying,
 p_barrio character varying,
 p_telefono character varying,
 p_obs character varying,
 p_domi_val character varying,
 p_modi_fecha timestamp without time zone,
 p_modi_usr character varying,
 p_provincia integer,
 p_localidad integer,
 p_numero character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare res integer;
  begin

  update domicilio 
  set  baja_fecha = p_modi_fecha,
  baja_usr = p_modi_usr
  where id_domicilio = p_id;

	res=inserta_domicilio( 
	  p_domi_tipo,
	  p_calle,
	  p_piso,
	  p_depto,
	  p_oficina,
	  p_codPostal,
	  p_barrio,
	  p_telefono,
	  p_obs,
	  p_domi_val,
	  p_modi_fecha,
	  p_modi_usr,
	  p_modi_fecha,
	  p_modi_usr,
	  p_provincia,
	  p_localidad,
	  p_numero,
	  null,
	  null) ;
  
  
	return res;
	
  end;  
$BODY$;


ALTER FUNCTION public.actualiza_domicilio(p_id integer, p_domi_tipo character varying, p_calle character varying, p_piso character varying, p_depto character varying, p_oficina character varying, p_codpostal character varying, p_barrio character varying, p_telefono character varying, p_obs character varying, p_domi_val character varying, p_modi_fecha timestamp without time zone, p_modi_usr character varying, p_provincia integer, p_localidad integer, p_numero character varying) OWNER TO postgres;

--

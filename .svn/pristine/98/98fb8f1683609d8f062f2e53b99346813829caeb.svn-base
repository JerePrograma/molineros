CREATE OR REPLACE FUNCTION borra_prestador(p_id_prestador integer,
 p_usuario character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
  begin

	update prestador 
	set  baja_fecha = LOCALTIMESTAMP,
	baja_usr = p_usuario
	where id_prestador = p_id_prestador;
  return 1;
	
  end;  
$BODY$;


ALTER FUNCTION public.borra_prestador(p_id_prestador integer, p_usuario character varying) OWNER TO postgres;

--

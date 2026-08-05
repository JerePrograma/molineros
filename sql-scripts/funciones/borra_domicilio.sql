CREATE OR REPLACE FUNCTION borra_domicilio(p_id integer,
 p_modi_fecha timestamp without time zone,
 p_modi_usr character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare res integer;
  begin

  update domicilio 
  set  baja_fecha = p_modi_fecha,
  baja_usr = p_modi_usr
  where id_domicilio = p_id;

		return 1;
	
  end;  
$BODY$;


ALTER FUNCTION public.borra_domicilio(p_id integer, p_modi_fecha timestamp without time zone, p_modi_usr character varying) OWNER TO postgres;

--

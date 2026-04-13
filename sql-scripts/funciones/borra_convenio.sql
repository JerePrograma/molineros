CREATE OR REPLACE FUNCTION borra_convenio(p_convenio_id integer,
 p_usuario character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare res integer;
  begin
		update convenio 
		set  baja_fecha = LOCALTIMESTAMP,
		baja_usr = p_usuario
		where id = p_convenio_id;
		
		update convenio_pagos set baja_fecha = LOCALTIMESTAMP,
		baja_usr = p_usuario
		where convenio_id = p_convenio_id;
		
		update convenio_relacion set baja_fecha = LOCALTIMESTAMP,
		baja_usr = p_usuario
		where convenio_id = p_convenio_id;
		
		update convenio_actas set baja_fecha = LOCALTIMESTAMP,
		baja_usr = p_usuario
		where convenio_id = p_convenio_id;

	return 1;
  end;  
$BODY$;


ALTER FUNCTION public.borra_convenio(p_convenio_id integer, p_usuario character varying) OWNER TO postgres;

--

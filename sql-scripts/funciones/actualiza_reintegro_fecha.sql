CREATE OR REPLACE FUNCTION actualiza_reintegro_fecha(integer,
 timestamp without time zone,
 character varying,
 integer,
 character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
  begin
  update reintegro
  set fecha = $2, modi_fecha = LOCALTIMESTAMP, modi_usr = $3, id_seccional = $4, observaciones = $5 where id_reintegro = $1;
  return 1;
  end;  
$BODY$;


ALTER FUNCTION public.actualiza_reintegro_fecha(integer, timestamp without time zone, character varying, integer, character varying) OWNER TO postgres;

--

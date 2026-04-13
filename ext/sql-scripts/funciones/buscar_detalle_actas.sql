CREATE OR REPLACE FUNCTION buscar_detalle_actas(p_id integer) 
RETURNS TABLE(id integer,
 capital numeric,
 desde timestamp without time zone,
 hasta timestamp without time zone,
 interes numeric)
    LANGUAGE sql
    AS $BODY$
	select id,
  capital,
  desde,
  hasta,
  interes
	from acta_detalle_inspectores
	where acta_id = $1
	
	$BODY$;


ALTER FUNCTION public.buscar_detalle_actas(p_id integer) OWNER TO postgres;

--

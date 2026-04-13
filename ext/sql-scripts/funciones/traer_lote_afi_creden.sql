CREATE OR REPLACE FUNCTION traer_lote_afi_creden() 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$	
declare id_lote_p integer;
BEGIN
	id_lote_p=max(id_lote)+1 from afi_creden_lote;
	if id_lote_p is null then
		id_lote_p=1;
	end if;
	return id_lote_p;
	
	
END;
$BODY$;


ALTER FUNCTION public.traer_lote_afi_creden() OWNER TO postgres;

--

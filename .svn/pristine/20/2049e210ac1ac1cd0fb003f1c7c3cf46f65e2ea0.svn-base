CREATE OR REPLACE FUNCTION inserta_afi_creden_lote(id_lote_v integer,
 cuil_titular_v character varying,
 inte_v integer,
 username_v character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$	
BEGIN
	insert into afi_creden_lote (id_lote,cuil_titular,inte,alta_usr, alta_fecha)
	values(id_lote_v,cuil_titular_v,inte_v,username_v,current_timestamp);
	return 0;
END;
$BODY$;


ALTER FUNCTION public.inserta_afi_creden_lote(id_lote_v integer, cuil_titular_v character varying, inte_v integer, username_v character varying) OWNER TO postgres;

--

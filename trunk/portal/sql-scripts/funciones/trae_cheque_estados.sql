CREATE OR REPLACE FUNCTION trae_cheque_estados() 
RETURNS TABLE(
	id integer,
    descripcion character varying
  )
LANGUAGE sql
AS $BODY$
	select id, descripcion from cheque_estado; 
	
$BODY$;
ALTER FUNCTION public.trae_cheque_estados() OWNER TO postgres;

--

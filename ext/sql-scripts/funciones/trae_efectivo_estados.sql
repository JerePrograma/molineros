CREATE OR REPLACE FUNCTION trae_efectivo_estados() 
RETURNS TABLE(
	id integer,
    descripcion character varying
  )
LANGUAGE sql
AS $BODY$
	select id, descripcion from efectivo_estado; 
	
$BODY$;
ALTER FUNCTION public.trae_efectivo_estados() OWNER TO postgres;

--

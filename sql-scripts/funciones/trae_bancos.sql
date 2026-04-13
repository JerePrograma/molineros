CREATE OR REPLACE FUNCTION trae_bancos() 
RETURNS TABLE(
	id_banco integer,
    descripcion character varying
  )
LANGUAGE sql
AS $BODY$
	select id_banco, descripcion from banco; 
	
$BODY$;
ALTER FUNCTION public.trae_bancos() OWNER TO postgres;

--

CREATE OR REPLACE FUNCTION trae_ultimo_inte_por_cuiltitular(cuil_tit character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
DECLARE 
 integr INTEGER;
BEGIN
  	
  integr = max(inte) from afiliado where cuil_titular = $1;
  IF INTEGR IS NULL THEN 
  RETURN 0;
  END IF;
  return integr;
END;
$BODY$;


ALTER FUNCTION public.trae_ultimo_inte_por_cuiltitular(cuil_tit character varying) OWNER TO postgres;

--

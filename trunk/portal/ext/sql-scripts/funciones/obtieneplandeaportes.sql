CREATE OR REPLACE FUNCTION obtieneplandeaportes() 
RETURNS character varying
    LANGUAGE plpgsql
    AS $BODY$	
DECLARE _record 	RECORD;
DECLARE	alert_mesg	VARCHAR(2000);
DECLARE	IN_VAR1 	VARCHAR(13);
DECLARE	IN_VAR2 	INTEGER;

BEGIN
alert_mesg := '';

 --Define output columns
 FOR _record IN SELECT distinct a.cuil_titular FROM afi_aportes a
-- FOR _record IN SELECT distinct cuil_titular FROM plan_prueba where id_plan is null
 LOOP
  IN_VAR1 = _record.cuil_titular;
  IN_VAR2 = id_plan from trae_plan_afiliado_x_aportes(IN_VAR1,0) limit 1;
  insert into plan_prueba(cuil_titular,id_plan)values(IN_VAR1,IN_VAR2);  
  --Build output string
  --alert_mesg := alert_mesg||rpad(IN_VAR1,20);
 END LOOP;

 RETURN alert_mesg;

   --EXCEPTION
   --   WHEN OTHERS THEN
	-- RETURN 'No troubleshooting information at this time.';


END;
	
$BODY$;


ALTER FUNCTION public.obtieneplandeaportes() OWNER TO postgres;

--

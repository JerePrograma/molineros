CREATE OR REPLACE FUNCTION actualiza_acta_relacionada(p_acta_relacion_id integer,
 p_importe numeric,
 p_saldo numeric,
 p_user character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN
UPDATE acta_relacion
   SET importe = p_importe , saldo = p_saldo,  
       modi_fecha=LOCALTIMESTAMP, modi_usr=p_user
 WHERE id=p_acta_relacion_id;

return 1;
END;
$BODY$;


ALTER FUNCTION public.actualiza_acta_relacionada(p_acta_relacion_id integer, p_importe numeric, p_saldo numeric, p_user character varying) OWNER TO postgres;

--

-- Function: actualiza_acta_relacionada(integer, numeric, numeric, character varying)

-- DROP FUNCTION actualiza_acta_relacionada(integer, numeric, numeric, character varying);

CREATE OR REPLACE FUNCTION actualiza_acta_no_os_relacionada(p_acta_relacion_id integer, p_importe numeric, p_saldo numeric, p_user character varying)
  RETURNS integer AS
$BODY$
declare resultDom integer;
BEGIN
UPDATE acta_no_os_relacion
   SET importe = p_importe , saldo = p_saldo,  
       modi_fecha=LOCALTIMESTAMP, modi_usr=p_user
 WHERE id=p_acta_relacion_id;

return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION actualiza_acta_relacionada(integer, numeric, numeric, character varying)
  OWNER TO postgres;


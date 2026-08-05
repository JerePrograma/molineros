CREATE OR REPLACE FUNCTION inserta_boletin(nombre_p character varying, asunto_p character varying, observaciones_p character varying, 
solo_texto_p boolean, username_p character varying)
  RETURNS integer AS
$BODY$
BEGIN
INSERT INTO boletin(nombre, asunto, observaciones, alta_user, alta_fecha) 
VALUES ($1,$2,$3,$4,current_date);   

return currval('boletin_id_seq');
END;
$BODY$
  LANGUAGE plpgsql VOLATILE

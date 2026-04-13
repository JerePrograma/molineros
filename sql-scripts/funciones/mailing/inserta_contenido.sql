CREATE OR REPLACE FUNCTION inserta_contenido(seccion_p character varying, titulo_p character varying, contenido_p character varying, id_boletin_p integer, username_p character varying)
  RETURNS integer AS
$BODY$
BEGIN
INSERT INTO boletin_contenido(seccion, titulo, contenido, id_boletin, alta_user, alta_fecha) 
VALUES ($1,$2,$3,$4,$5,current_date);   

return currval('boletin_contenido_id_seq');
END;
$BODY$
  LANGUAGE plpgsql VOLATILE

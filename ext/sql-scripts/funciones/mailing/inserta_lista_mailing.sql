CREATE OR REPLACE FUNCTION inserta_mailing_list(id_mail_p integer, descripcion_p character varying, observaciones_p character varying, username_p character varying)
  RETURNS integer AS
$BODY$
BEGIN
INSERT INTO mailing_list(id_mailing_list, descripcion, observaciones, alta_user, alta_fecha) 
VALUES ($1,$2,$3,$4,current_date);   

return currval('mailing_list_id_seq');
END;
$BODY$
  LANGUAGE plpgsql VOLATILE

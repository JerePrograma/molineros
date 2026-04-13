CREATE OR REPLACE FUNCTION insertar_tipo_normas_ddhh(p_sistema character varying, p_descripcion character varying)
  RETURNS integer AS
$BODY$
declare resultDom integer;
BEGIN
	
INSERT INTO tipo_normas_ddhh(sistema, descripcion) VALUES (p_sistema, p_descripcion);

return currval('tipo_normas_id_seq') ;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
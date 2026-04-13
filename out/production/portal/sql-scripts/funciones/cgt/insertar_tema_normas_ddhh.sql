CREATE OR REPLACE FUNCTION insertar_tema_normas_ddhh(p_descripcion character varying)
  RETURNS integer AS
$BODY$
declare resultDom integer;
BEGIN
	
INSERT INTO tema_normas_ddhh(descripcion) VALUES (p_descripcion);

return currval('tema_normas_id_seq') ;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
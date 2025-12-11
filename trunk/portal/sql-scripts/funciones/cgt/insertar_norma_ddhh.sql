
CREATE OR REPLACE FUNCTION insertar_norma_ddhh(p_sistema character varying, p_id_tipo_norma_ddhh integer, p_numero character varying, p_fuente_dependencia character varying, p_autor character varying, p_fecha date, p_lugar text, p_resumen text, p_contenido text, p_id_tema_norma_ddhh integer, p_link character varying, p_sigla character varying, p_inc_leg_nac character varying, p_alta_usr character varying)
  RETURNS integer AS
$BODY$
declare resultDom integer;
BEGIN
	
INSERT INTO norma_ddhh (sistema, id_tipo_norma_ddhh, numero, fuente_dependencia, autor, fecha, lugar, resumen, contenido, id_tema_norma_ddhh, link, sigla, inc_legis_nac, alta_fecha, alta_usr, modi_fecha, modi_usr)
    VALUES (p_sistema, p_id_tipo_norma_ddhh, p_numero, p_fuente_dependencia, p_autor, p_fecha, p_lugar, p_resumen, p_contenido, p_id_tema_norma_ddhh, p_link, p_sigla, p_inc_leg_nac, LOCALTIMESTAMP, p_alta_usr, LOCALTIMESTAMP, p_alta_usr);

return currval('norma_ddhh_id_seq');
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
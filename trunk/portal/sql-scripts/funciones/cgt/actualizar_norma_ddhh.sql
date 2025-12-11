CREATE OR REPLACE FUNCTION actualizar_norma_ddhh(p_id integer, p_sistema character varying, p_id_tipo_norma_ddhh integer, p_numero character varying, p_fuente_dependencia character varying, p_autor character varying, p_fecha date, p_lugar text, p_resumen text, p_contenido text, p_id_tema_norma_ddhh integer, p_link character varying, p_sigla character varying, p_inc_leg_nac character varying, p_modi_usr character varying)
  RETURNS integer AS
$BODY$
BEGIN
	
update norma_ddhh set 
	sistema = p_sistema,
	id_tipo_norma_ddhh = p_id_tipo_norma_ddhh,
	numero = p_numero,
	fuente_dependencia = p_fuente_dependencia, 
	autor = p_autor,
	fecha = p_fecha, 
	lugar = p_lugar,
	resumen = p_resumen, 
	contenido = p_contenido,
	id_tema_norma_ddhh = p_id_tema_norma_ddhh, 
	link = p_link, 
	sigla = p_sigla, 
	inc_legis_nac = p_inc_leg_nac,
	modi_fecha = current_timestamp, 
	modi_usr = p_modi_usr
where id = p_id;
return p_id;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

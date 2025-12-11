CREATE OR REPLACE FUNCTION buscar_normaddhh(IN id_normadh integer)
  RETURNS TABLE(id integer, sistema character varying, id_tipo_norma_ddhh integer, numero character varying, fuente_dependencia character varying, autor character varying, fecha date, lugar text, resumen text, contenido text, id_tema_norma_ddhh integer, link character varying, sigla character varying, inc_legis_nac character varying, descripcion_tema character varying, descripcion_tipo character varying) AS
$BODY$
select n.id, n.sistema, n.id_tipo_norma_ddhh, n.numero, n.fuente_dependencia, 
       n.autor, n.fecha, n.lugar, n.resumen, n.contenido, n.id_tema_norma_ddhh, 
       n.link, n.sigla, n.inc_legis_nac, te.descripcion, ti.descripcion 
from norma_ddhh n, tema_normas_ddhh te, tipo_normas_ddhh ti
where n.id_tema_norma_ddhh = te.id
and n.id_tipo_norma_ddhh = ti.id
and n.id=$1
and baja_fecha is null
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
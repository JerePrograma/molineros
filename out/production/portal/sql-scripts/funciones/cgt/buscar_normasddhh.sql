CREATE OR REPLACE FUNCTION buscar_normasddhh(IN p_fecha_ini date, IN p_fecha_fin date, IN p_sistema character varying, IN p_numero character varying, IN p_tema_id integer, IN p_tipo_id integer, IN p_autor character varying, IN p_lugar character varying)
  RETURNS TABLE(id integer, sistema character varying, id_tipo_norma_ddhh integer, numero character varying, fuente_dependencia character varying, autor character varying, fecha date, lugar text, resumen text, contenido text, id_tema_norma_ddhh integer, link character varying, sigla character varying, inc_legis_nac character varying, descripcion_tema character varying, descripcion_tipo character varying) AS
$BODY$

select n.id, n.sistema, n.id_tipo_norma_ddhh, n.numero, n.fuente_dependencia, 
       n.autor, n.fecha, n.lugar, n.resumen, n.contenido, n.id_tema_norma_ddhh, 
       n.link, n.sigla, n.inc_legis_nac, te.descripcion, ti.descripcion 
from norma_ddhh n, tema_normas_ddhh te, tipo_normas_ddhh ti
where n.id_tema_norma_ddhh = te.id
and n.id_tipo_norma_ddhh = ti.id
and ($1 is null or ($1 is not null and n.fecha>=$1)) 
and ($2 is null or ($2 is not null and n.fecha<=$2)) 
and ($3 is null or upper(n.sistema) = $3)
and ($4 is null or upper(n.numero) like '%'||upper($4)||'%')
and ($5 is null or (n.id_tema_norma_ddhh = $5 ))
and ($6 is null or (n.id_tipo_norma_ddhh = $6 ))
and ($7 is null or upper(n.autor) like '%'||upper($7)||'%')
and ($8 is null or upper(n.lugar) like '%'||upper($8)||'%')
and n.baja_fecha is null;
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;


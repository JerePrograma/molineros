CREATE OR REPLACE FUNCTION trae_temas_normas_ddhh()
  RETURNS TABLE(id integer, descripcion character varying) AS
$BODY$
select id, descripcion
    from tema_normas_ddhh 
    order by descripcion
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
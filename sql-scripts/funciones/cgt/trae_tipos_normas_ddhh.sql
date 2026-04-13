CREATE OR REPLACE FUNCTION trae_tipos_normas_ddhh(p_sistema character varying)
  RETURNS TABLE(id integer, sistema character varying, descripcion character varying) AS
$BODY$

  select id, sistema, descripcion 
  from tipo_normas_ddhh
  where sistema = $1
  order by descripcion
$BODY$
  LANGUAGE sql VOLATILE
  COST 100;
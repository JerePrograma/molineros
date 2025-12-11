CREATE OR REPLACE FUNCTION trae_delegaciones()
  RETURNS TABLE(id_delegacion integer, descripcion character varying, libro integer, rubrica integer, tomo integer) AS
$BODY$
select id ,
    descripcion,
    libro,
    rubrica,
    tomo
    from delegacion 
    order by descripcion
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
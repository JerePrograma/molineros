CREATE OR REPLACE FUNCTION buscar_delegacion_por_id(IN id_p integer)
  RETURNS TABLE(id_delegacion integer, descripcion character varying, libro integer, rubrica integer, tomo integer) AS
$BODY$
select id ,
    descripcion,
    libro,
    rubrica,
    tomo
    from delegacion 
    where id = $1
    order by descripcion
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
-- Function: buscar_cantidad_importe(integer, character varying, integer, timestamp without time zone)

-- DROP FUNCTION buscar_cantidad_importe(integer, character varying, integer, timestamp without time zone);

CREATE OR REPLACE FUNCTION buscar_cantidad_importe(IN p_id_prestacion integer, IN p_cuil character varying, IN p_inte integer, IN p_fechainicio timestamp without time zone)
  RETURNS TABLE(cantidad numeric, importe numeric) AS
$BODY$


select sum(cantidad) as cantidad, sum(importe) as importe from reintegro_prestacion rp
inner join reintegro r
on rp.id_reintegro = r.id_reintegro
and id_prestacion = $1 
where cuil_titular = $2
and inte = $3
and rp.fecha_prestacion >= $4
and rp.fecha_prestacion <= ($4 + interval '1 year')
group by cuil_titular, inte;

$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;

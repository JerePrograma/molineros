-- Function: trae_cantidad_prestaciones_anio(character)

-- DROP FUNCTION trae_cantidad_prestaciones_anio(character);

CREATE OR REPLACE FUNCTION trae_cantidad_prestaciones_anio(IN cuil_v character)
  RETURNS SETOF numeric AS
$BODY$
select subq.total from (

select (sum(rp.cantidad * rp.importe)) total, r.cuil_titular as b from reintegro r, reintegro_prestacion_odo_protesis rp, nomenclador p
where 
r.id_reintegro = rp.id_reintegro
and rp.id_prestacion = p.id_prestacion
and p.marca_rein_liq = 4
and r.cuil_titular = $1
and rp.fecha_prestacion >= (select date_trunc('year', localtimestamp))
group by r.cuil_titular) as subq

$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION trae_cantidad_prestaciones_anio(character) OWNER TO postgres;

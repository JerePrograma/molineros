create type bono_seccional_result as(tipo_bono text, id_seccional int, seccional varchar, fecha_envio date, nro_bono_desde integer, nro_bono_hasta integer, fecha_rendido date, id_envio int, cantidad int)

CREATE OR REPLACE FUNCTION trae_bonos_seccional(id_envio_v integer)
  RETURNS SETOF bono_seccional_result AS
$BODY$
BEGIN
return query
select cast(t.tipo_bono as varchar)||'-'||t.descripcion,s.id_seccional, s.descripcion, fecha_envio, min(nro_bono), max(nro_bono), fecha_rendido, id_envio, max(nro_bono)+1-min(nro_bono)
from bonos_seccional b, seccional s, tipos_bono t
where id_envio=id_envio_v
and s.id_seccional=b.id_seccional
and t.tipo_bono=b.tipo_bono
group by t.tipo_bono, t.descripcion,s.id_seccional, s.descripcion, fecha_envio, fecha_rendido, id_envio;


END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE

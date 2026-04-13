CREATE OR REPLACE FUNCTION busqueda_bonos_seccional(tipo_bono_v integer, seccional_v integer, fecha_desde_v date, fecha_hasta_v date, nro_bono_desde integer, nro_bono_hasta integer, rendidos integer, sin_enviar boolean)
  RETURNS SETOF bono_seccional_result AS
$BODY$
begin

drop table if exists aux_bonos;

create temp table aux_bonos as 
select cast(t.tipo_bono as varchar)||'-'||t.descripcion as tipo_bono, s.id_seccional as id_seccional, s.descripcion as descripcion, fecha_envio as fecha_envio , /*min(nro_bono) */ 1 as min_bono, /*max(nro_bono) */ 2 as max_bono, fecha_rendido as fecha_rendido, id_envio as id_envio, /*max(nro_bono)+1-min(nro_bono)*/ 3 as total
from bonos_seccional b, seccional s, tipos_bono t
where s.id_seccional=b.id_seccional
and t.tipo_bono=b.tipo_bono
and b.tipo_bono <-1; --una falsedad, usada para generar la struct de la temp table
--group by t.tipo_bono, t.descripcion, s.id_seccional, s.descripcion, fecha_envio, fecha_rendido, id_envio;

if (rendidos=1 or rendidos =3) then
  RAISE INFO 'RENDIDOS';
  PERFORM agrupa_bonos_sin_rendir(tipo_bono_v, seccional_v, fecha_desde_v, fecha_hasta_v, nro_bono_desde, nro_bono_hasta);
end if;
if sin_enviar=true then
  PERFORM agrupa_bonos_sin_enviar(tipo_bono_v,fecha_desde_v,fecha_hasta_v,nro_bono_desde,nro_bono_hasta);
end if;
if (rendidos=2 or rendidos =3) then
  RAISE INFO 'SOLO RENDIDOS';
  PERFORM agrupa_bonos_rendidos(tipo_bono_v, seccional_v, fecha_desde_v, fecha_hasta_v, nro_bono_desde, nro_bono_hasta);
end if;  

return query
select tipo_bono, id_seccional, descripcion, fecha_envio , min_bono, max_bono, fecha_rendido, id_envio, total,fecha_anulacion from aux_bonos order by min_bono;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
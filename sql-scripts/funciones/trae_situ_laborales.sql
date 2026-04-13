CREATE OR REPLACE FUNCTION trae_situ_laborales(IN cuil_p character varying, IN inte_p integer)
  RETURNS TABLE(cuil_titular character varying, inte integer, apellido character varying, nombre character varying, cuil character varying, cuit character varying, sucursal character varying, razon_social character varying, fecha_ingreso date, fecha_baja date, categoria character varying, revista character varying, id_categoria integer, id_revista integer, motivo_baja character varying, escala_salarial varchar, id_motivo_baja integer) AS
$BODY$
select a.cuil_titular, a.inte, af.apellido, af.nombre, af.cuil,a.cuit,a.sucursal,e.razon_soc, fecha_ingre,fecha_egre,c.categoria, r.detalle, c.id_categoria,
       r.id_situ_revista, mb.descripcion, a.escala_salarial, a.id_motivo_baja 
from afi_situ_laboral a
left  join empresa e on a.cuit=e.cuit and a.sucursal=e.sucursal
left join situacion_revista r on r.id_situ_revista=a.id_revista
inner join categoria_laboral c on c.id_categoria=a.id_categoria
left join motivo_baja mb on a.id_motivo_baja=mb.id_motivo_baja
inner join afiliado af on a.cuil_titular=af.cuil_titular and a.inte=af.inte
where a.cuil_titular=$1
and a.baja_fecha is null
--and inte=$2
order by fecha_ingre
$BODY$
  LANGUAGE 'sql' VOLATILE

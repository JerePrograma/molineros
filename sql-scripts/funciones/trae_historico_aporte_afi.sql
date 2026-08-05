CREATE OR REPLACE FUNCTION trae_historico_aporte_afi(IN cuil_v character, IN inte_v integer)
  RETURNS TABLE(ap__cuil_titular character varying, ap__inte integer, ap__id_aporte integer, ap__fecha_ingre date, ap__fecha_egre date, 
  ap__alta_usr character, ap__baja_usr character, ap__baja_fecha timestamp with time zone, ap__modi_fecha timestamp with time zone, 
  ap__modi_usr character, ap__alta_fecha timestamp with time zone, ap__id_motivo_baja integer, pl__id_plan integer, pl__descripcion character varying, 
  pl__alta_fecha timestamp without time zone, pl__alta_usr character varying, apo__id_aporte integer, apo__descripcion character varying, 
  cuil character varying, plan_alta_fecha timestamp without time zone, ap__id_socio integer, ap__tipo_aporte character,
  
  desc_motivo_baja character varying) AS
$BODY$


select ap.cuil_titular,
ap.inte,
ap.id_aporte,
ap.fecha_ingre,
ap.fecha_egre,
ap.alta_usr,
ap.baja_usr,
ap.baja_fecha,
ap.modi_fecha,
ap.modi_usr,
ap.alta_fecha,
ap.id_motivo_baja,
p.id_plan,
p.descripcion,
p.alta_fecha,
p.alta_usr,
a.id_aporte,
a.descripcion,
(select cuil from afiliado where cuil_titular = $1 and inte = $2) as cuil,
pa.plan_alta_fecha,
pa.id_socio,
pa.tipo_aporte,
mba.descripcion as desc_motivo_baja
from afi_plan_aporte pa
inner join afi_aportes ap
on pa.cuil_titular = ap.cuil_titular
and pa.inte = ap.inte
and pa.aporte_alta_fecha = ap.alta_fecha
and pa.id_aporte = ap.id_aporte
and pa.cuil_titular = $1
and pa.inte = $2
left outer join motivo_baja_afiliado mba
on ap.id_motivo_baja = mba.id_motivo_baja
inner join aporte a
on ap.id_aporte = a.id_aporte
inner join plan p
on pa.id_plan = p.id_plan
order by pa.plan_alta_fecha, p.id_plan asc;

$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;


CREATE OR REPLACE FUNCTION buscar_ultimo_plan_aportes(cuil_titular_p character varying)
  RETURNS SETOF afi_plan_aporte_omint AS
$BODY$
BEGIN

drop table if exists aux_aporte;

create temp table aux_aporte as
select cuil_titular_p as cuil_titular, (select 1 as existe 
from afi_plan app where app.cuil_titular=cuil_titular_p and baja_fecha is null and vigen_hasta is null) as existe;

return query
select
ap.cuil_titular as afiplan_cuil_titular, 
ap.inte as afiplan_inte,  
ap.id_plan as afiplan_id_plan,
ap.id_tarifa as afiplan_id_tarifa,
ap.vigen_desde as afiplan_vigen_desde,
ap.alta_fecha as afiplan_alta_fecha, 
ap.alta_usr as afiplan_alta_usr, 
ap.modi_fecha as afiplan_modi_fecha, 
ap.modi_usr as afiplan_modi_usr, 
ap.baja_fecha as afiplan_baja_fecha, 
ap.baja_usr as afiplan_baja_usr, 
ap.id_motivo_baja as afiplan_id_motivo_baja, 
ap.id_plan_omint as afiplan_id_plan_omint, 
ap.id as afiplan_id, 
ap.vigen_hasta as afiplan_vigen_hasta,
p.id_plan as plan_id_plan, 
p.descripcion as plan_descripcion, 
p.observaciones as plan_observaciones, 
p.alta_fecha as plan_alta_fecha, 
p.alta_usr as plan_alta_usr, 
p.modi_fecha as plan_modi_fecha, 
p.modi_usr as plan_modi_usr, 
p.baja_fecha as plan_baja_fecha, 
p.baja_usr as plan_baja_usr, 
p.ospim as plan_ospim, 
p.amtima as plan_amtima, 
p.uoma as plan_uoma, 
p.id_plan_base as plan_id_plan_base, 
p.descripcion_tarjeta as plan_descripcion_tarjeta, 
po.descripcion as plan_descripcion_omint, 
po.id_plan_omint as plan_id_plan_omint, 
mb.id_motivo_baja as motbaja_id_motivo_baja, 
mb.descripcion as motbaja_descripcion, 
mb.observaciones as motbaja_observaciones, 
mb.alta_fecha as motbaja_alta_fecha, 
mb.alta_usr as motbaja_alta_usr, 
mb.modi_fecha as motbaja_modi_fecha, 
mb.modi_usr as motbaja_modi_usr, 
mb.baja_fecha as motbaja_baja_fecha, 
mb.baja_usr as motbaja_baja_usr, 
mb.meses_a_baja as motbaja_meses_a_baja 
from afi_plan ap 
inner join plan p 
on ap.id_plan=p.id_plan 
inner join aux_aporte b
on b.cuil_titular=ap.cuil_titular
left join motivo_baja mb 
on ap.id_motivo_baja = mb.id_motivo_baja 
left join plan_omint po 
on p.id_plan = po.id_plan 
where ap.cuil_titular = cuil_titular_p
and ap.baja_fecha is null 
and ((b.existe=1 and ap.vigen_hasta is null) or (existe is null and ap.vigen_hasta=(select max(app.vigen_hasta) from afi_plan app where ap.cuil_titular=app.cuil_titular and app.baja_fecha is null)));
/*and ap.vigen_desde = (select max(ap_.vigen_desde) from afi_plan ap_ 
		     where ap_.cuil_titular = ap.cuil_titular and ap_.baja_fecha is null);*/


END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
  
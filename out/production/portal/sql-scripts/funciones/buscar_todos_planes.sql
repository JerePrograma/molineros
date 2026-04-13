CREATE TYPE plan_omintt AS
   (plan_id_plan integer,
    plan_descripcion character varying(100),
    plan_observaciones character varying(250),
    plan_alta_fecha timestamp without time zone,
    plan_alta_usr character varying(15),
    plan_modi_fecha timestamp without time zone,
    plan_modi_usr character varying(15),
    plan_baja_fecha timestamp without time zone,
    plan_baja_usr character varying(15),
    plan_ospim boolean,
    plan_amtima boolean,
    plan_uoma boolean,
    plan_id_plan_base integer,
    plan_descripcion_tarjeta character varying,
    plan_descripcion_omint character varying,
    plan_id_plan_omint integer);
    

CREATE OR REPLACE FUNCTION buscar_todos_planes()
  RETURNS SETOF plan_omintt AS
$BODY$
BEGIN

return query
select 
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
po.id_plan_omint as plan_id_plan_omint
from plan p left join plan_omint po on p.id_plan = po.id_plan 
order by p.descripcion;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE;

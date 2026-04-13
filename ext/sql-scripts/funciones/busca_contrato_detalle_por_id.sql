CREATE OR REPLACE FUNCTION busca_contrato_detalle_por_id(IN p_id_contrato_detalle integer)
  RETURNS TABLE(
  cd_id_contrato_detalle integer,
  cd_id_contrato integer,
  cd_fecha_desde timestamp without time zone,
  cd_fecha_hasta timestamp without time zone,
  cd_id_prestacion_desde integer,
  cd_codigo_desde character varying,
  cd_id_prestacion_hasta integer,
  cd_codigo_hasta character varying,
  cd_id_plan integer,
  cd_id_cartilla integer,
  cd_coseguro numeric,
  cd_tipo_valorizacion character varying,
  cd_honorarios numeric,
  cd_gastos numeric,
  cd_importe_total numeric,  
  cd_alta_fecha timestamp without time zone,
  cd_alta_usr character varying,
  cd_modi_fecha timestamp without time zone,
  cd_modi_usr character varying,
  cd_baja_fecha timestamp without time zone,
  cd_baja_usr character varying,  
  cd_plan_descripcion character varying,
  cd_servicio character varying
  
  ) AS
$BODY$

select  

cd.id_contrato_detalle,
cd.id_contrato,
cd.fecha_desde,
cd.fecha_hasta,
cd.id_prestacion_desde,
cd.codigo_desde,
cd.id_prestacion_hasta,
cd.codigo_hasta,
cd.id_plan, 
cd.id_cartilla,
cd.coseguro,
cd.tipo_valorizacion,
cd.honorarios,
cd.gastos,
cd.importe_total,  
cd.alta_fecha,
cd.alta_usr,
cd.modi_fecha,
cd.modi_usr,
cd.baja_fecha,
cd.baja_usr,

plan.descripcion,
cd.servicio
 
from 
 
contrato_detalle cd
 --inner join
 --contrato_detalle cd 
 --on c.id_contrato = cd.id_contrato
left outer join plan plan
on cd.id_plan = plan.id_plan

where

cd.id_contrato=$1;

$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
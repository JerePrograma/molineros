CREATE OR REPLACE FUNCTION busca_contrato_detalle_existente(p_id_contrato integer, p_fecha_desde timestamp without time zone, p_fecha_hasta timestamp without time zone, p_codigo_desde character varying, p_codigo_hasta character varying, p_plan integer, p_servicio character varying)

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

declare fecha_desde_ timestamp without time zone;
declare fecha_hasta_ timestamp without time zone;
declare codigo_desde_ integer;
declare codigo_hasta_ integer;

BEGIN

fecha_desde_ = case when (p_fecha_desde is null) then '19000101' else p_fecha_desde end;
fecha_hasta_ = case when (p_fecha_hasta is null) then '99990101' else p_fecha_hasta end;

codigo_desde_ = case when (p_codigo_desde is null) then 0 else cast(p_codigo_desde as integer) end;
codigo_hasta_ = case when (p_codigo_hasta = '' or p_codigo_hasta is null) then codigo_desde_ else cast(p_codigo_hasta as integer) end;

return query 

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
left outer join plan plan
on cd.id_plan = plan.id_plan

where 

p_id_contrato = cd.id_contrato and

not (
(p_fecha_desde < cd.fecha_desde and p_fecha_hasta < cd.fecha_desde)  
or 
(cd.fecha_hasta is not null and (p_fecha_desde > cd.fecha_hasta and p_fecha_hasta > cd.fecha_hasta))
) 

and

not  (
(codigo_desde_ < cast(cd.codigo_desde as integer) and codigo_hasta_ < cast(cd.codigo_desde as integer))  
or
(cd.codigo_hasta is not null and (codigo_desde_ > cast(cd.codigo_hasta as integer) and codigo_hasta_ > cast(cd.codigo_hasta as integer)))
or
(cd.codigo_hasta is null and (codigo_desde_ > cast(cd.codigo_desde as integer) and codigo_hasta_ > cast(cd.codigo_desde as integer)))
)

and 

p_plan = cd.id_plan

and

p_servicio = cd.servicio;

end; 
$BODY$
  LANGUAGE 'plpgsql' VOLATILE

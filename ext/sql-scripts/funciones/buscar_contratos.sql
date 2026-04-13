CREATE OR REPLACE FUNCTION buscar_contratos(IN p_id_contrato integer, IN p_cuit character varying, IN p_descripcion character varying, IN p_estado integer)
  RETURNS TABLE(
  
  c_id_contrato integer,
  c_id_prestador integer,
  c_estado integer,
  c_dia_recepcion integer,
  c_condicion_de_pago character varying,
  c_id_tipo_pago integer,
  c_alta_fecha timestamp without time zone,
  c_alta_usr character varying(15),
  c_modi_fecha timestamp without time zone,
  c_modi_usr character varying(15),
  c_baja_fecha timestamp without time zone,
  c_baja_usr character varying(15),
  c_cuit character varying,
  c_descripcion character varying--,
     
--  cd_id_contrato_detalle integer,
--  cd_id_contrato integer,CREATE OR REPLACE FUNCTION buscar_contratos(IN p_id_contrato integer, IN p_cuit character varying, IN p_descripcion character varying, IN p_estado integer)
  RETURNS TABLE(
  
  c_id_contrato integer,
  c_id_prestador integer,
  c_estado integer,
  c_dia_recepcion integer,
  c_condicion_de_pago character varying,
  c_id_tipo_pago integer,
  c_alta_fecha timestamp without time zone,
  c_alta_usr character varying(15),
  c_modi_fecha timestamp without time zone,
  c_modi_usr character varying(15),
  c_baja_fecha timestamp without time zone,
  c_baja_usr character varying(15),
  c_cuit character varying,
  c_descripcion character varying--,
     
--  cd_id_contrato_detalle integer,
--  cd_id_contrato integer,
--  cd_fecha_desde timestamp without time zone,
--  cd_fecha_hasta timestamp without time zone,
--  cd_id_prestacion_desde integer,
--  cd_codigo_desde character varying,
--  cd_id_prestacion_hasta integer,
--  cd_codigo_hasta character varying,
--  cd_id_plan integer,
--  cd_id_cartilla integer,
--  cd_coseguro numeric,
--  cd_tipo_valorizacion character varying,
--  cd_honorarios numeric,
--  cd_gastos numeric,
--  cd_importe_total numeric,  
--  cd_alta_fecha timestamp without time zone,
--  cd_alta_usr character varying,
--  cd_modi_fecha timestamp without time zone,
--  cd_modi_usr character varying,
--  cd_baja_fecha timestamp without time zone,
--  cd_baja_usr character varying,  
--  cd_plan_descripcion character varying
--  cd_servicio character varying
  
  ) AS
$BODY$

select  
  
c.id_contrato,  
c.id_prestador, 
c.estado,
c.dia_recepcion,
c.condicion_de_pago,
c.id_tipo_pago,
c.alta_fecha,
c.alta_usr,
c.modi_fecha,
c.modi_usr,
c.baja_fecha,
c.baja_usr,

p.cuit,
p.descripcion--,

--cd.id_contrato_detalle,
--cd.id_contrato,
--cd.fecha_desde,
--cd.fecha_hasta,
--cd.id_prestacion_desde,
--cd.codigo_desde,
--cd.id_prestacion_hasta,
--cd.codigo_hasta,
--cd.id_plan, 
--cd.id_cartilla,
--cd.coseguro,
--cd.tipo_valorizacion,
--cd.honorarios,
--cd.gastos,
--cd.importe_total,  
--cd.alta_fecha,
--cd.alta_usr,
--cd.modi_fecha,
--cd.modi_usr,
--cd.baja_fecha,
--cd.baja_usr,

--plan.descripcion,
--cd.servicio
 
 from 
 
 contrato c 
 --inner join 
 --contrato_detalle cd 
 --on c.id_contrato = cd.id_contrato
 inner join prestador p
 on c.id_prestador = p.id_prestador
 --left outer join plan plan
 --on cd.id_plan = plan.id_plan
 
 where
 
 ($1 is null or $1 = 0 or ($1 is not null and c.id_prestador=$1)) and
 ($2 is null or $2 = '' or ($2 is not null and p.cuit=$2)) and
 ($3 is null or $3 = '' or ($3 is not null and upper (p.descripcion) like '%' || upper($3) || '%')) and
 ($4 is null or $4 = 0 or ($4 is not null and c.estado = $4));

$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
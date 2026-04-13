CREATE OR REPLACE FUNCTION contrato_header_por_id_prestador(IN p_id_prestador integer)
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
 c.id_prestador=$1 and c.baja_fecha is null;

$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
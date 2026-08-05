drop function buscar_empleador_por_cuit_sucu(IN cuit character, IN sucur character varying)

CREATE OR REPLACE FUNCTION buscar_empleador_por_cuit_sucu(IN cuit character, IN sucur character varying)
  RETURNS TABLE(emp__cuit character varying, emp__sucursal character varying, emp__razon_soc character varying, emp__nombre_fantasia character varying, emp__id_ramo_empresa smallint, emp__id_seccional integer, emp__contacto character varying, emp__id_entidad_cam_empresa smallint, emp__observaciones character varying, emp__vigen_fecha timestamp without time zone, emp__motivo_baja character varying, emp__alta_fecha timestamp without time zone, emp__alta_usr character varying, emp__alta_ip character varying, emp__modi_fecha timestamp without time zone, emp__modi_usr character varying, emp__modi_ip character varying, emp__baja_fecha timestamp without time zone, emp__baja_usr character varying, emp__baja_ip character varying, emp__id_posicion_iva smallint,
  emp__domiafip character varying, emp__domiremo character varying, emp__domiestudio character varying
  ) AS
$BODY$
	select 	
	emp.cuit,
	emp.sucursal,
	emp.razon_soc,
	emp.nombre_fantasia,
	emp.id_ramo_empresa,
	emp.id_seccional,
	emp.contacto,
	emp.id_entidad_cam_empresa,
	emp.observaciones,
	emp.vigen_fecha,
	emp.motivo_baja,
	emp.alta_fecha,
	emp.alta_usr,
	emp.alta_ip,
	emp.modi_fecha,
	emp.modi_usr,
	emp.modi_ip,
	emp.baja_fecha,
	emp.baja_usr,
	emp.baja_ip,
	emp.id_posicion_iva,
	cast ('' as character varying),
	cast ('' as character varying),
	cast ('' as character varying)
	from empresa emp
	where emp.cuit = $1
	and emp.sucursal = $2	
	$BODY$
  LANGUAGE sql VOLATILE


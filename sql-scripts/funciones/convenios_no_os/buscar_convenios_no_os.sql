-- Function: buscar_convenios_no_os(character varying, character varying, character varying, character varying)

-- DROP FUNCTION buscar_convenios_no_os(character varying, character varying, character varying, character varying);

CREATE OR REPLACE FUNCTION buscar_convenios_no_os(IN p_nro character varying, IN p_cuit character varying, IN p_empresa character varying, IN p_entidad character varying)
  RETURNS TABLE(conv__id integer, conv__numero character varying, conv__cuit character varying, conv__sucursal character varying, conv__fecha_inicio timestamp without time zone, conv__fecha_pago timestamp without time zone, conv__alta_fecha timestamp without time zone, conv__alta_usr character varying, conv__alta_ip character varying, conv__modi_fecha timestamp without time zone, conv__modi_usr character varying, conv__modi_ip character varying, conv__baja_fecha timestamp without time zone, conv__baja_usr character varying, conv__baja_ip character varying, conv__interes numeric, conv__ajuste_capital numeric, conv__ajuste_interes numeric, conv__deuda_actas_asociadas numeric, conv__deuda_convenios_asociados numeric, emp__cuit character varying, emp__sucursal character varying, emp__razon_soc character varying, emp__nombre_fantasia character varying, emp__id_ramo_empresa smallint, emp__id_seccional integer, emp__contacto character varying, emp__id_entidad_cam_empresa smallint, emp__observaciones character varying, emp__vigen_fecha timestamp without time zone, emp__motivo_baja character varying, emp__alta_fecha timestamp without time zone, emp__alta_usr character varying, emp__alta_ip character varying, emp__modi_fecha timestamp without time zone, emp__modi_usr character varying, emp__modi_ip character varying, emp__baja_fecha timestamp without time zone, emp__baja_usr character varying, emp__baja_ip character varying, emp__id_posicion_iva smallint, emp__domiafip character varying, emp__domiremo character varying, emp__domiestudio character varying, conv__entidad character varying) AS
$BODY$

select c.id ,
	c.numero,
	c.cuit,
	c.sucursal,
	c.fecha_inicio,
	c.fecha_pago,
	c.alta_fecha ,
	c.alta_usr,
	c.alta_ip ,
	c.modi_fecha  ,
	c.modi_usr,
	c.modi_ip ,
	c.baja_fecha,
	c.baja_usr,
	c.baja_ip ,
	c.interes ,
	c.ajuste_capital,
	c.ajuste_interes,
	c.deuda_actas_asociadas,
	c.deuda_convenios_asociados,
	e.cuit,
	e.sucursal,
	e.razon_soc,
	e.nombre_fantasia,
	e.id_ramo_empresa,
	e.id_seccional,
	e.contacto,
	e.id_entidad_cam_empresa,
	e.observaciones,
	e.vigen_fecha,
	e.motivo_baja,
	e.alta_fecha,
	e.alta_usr,
	e.alta_ip,
	e.modi_fecha,
	e.modi_usr,
	e.modi_ip,
	e.baja_fecha,
	e.baja_usr,
	e.baja_ip,
	e.id_posicion_iva,
	cast('' as varchar) as domiafip,
	cast('' as varchar) as domiremo,
	cast('' as varchar) as domiestudio,
	c.entidad
from convenio_no_os c
left outer join empresa e
on c.cuit = e.cuit
and c.sucursal = e.sucursal
where ($1 is null or ($1 is not null  and (cast (c.id as character varying) like '%' || $1 || '%' or c.numero like '%' || $1 || '%'  )))
	and ($2 is null or ($2 is not null and c.cuit = $2))
	and ($3 is null or ($3 is not null and upper(e.razon_soc) like upper($3)||'%'))	
	and ($4 is null or ($4 is not null and upper(c.entidad) like upper($4)||'%'))	;
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION buscar_convenios_no_os(character varying, character varying, character varying, character varying)
  OWNER TO postgres;


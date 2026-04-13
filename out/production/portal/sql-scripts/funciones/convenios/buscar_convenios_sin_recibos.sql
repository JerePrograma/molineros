CREATE OR REPLACE FUNCTION buscar_convenios_sin_recibos(IN p_cuit character varying)
  RETURNS TABLE(conv__id integer, conv__numero character varying, conv__cuit character varying, conv__sucursal character varying, conv__fecha_inicio timestamp without time zone, conv__fecha_pago timestamp without time zone, conv__alta_fecha timestamp without time zone, conv__alta_usr character varying, conv__alta_ip character varying, conv__modi_fecha timestamp without time zone, conv__modi_usr character varying, conv__modi_ip character varying, conv__baja_fecha timestamp without time zone, conv__baja_usr character varying, conv__baja_ip character varying, conv__interes numeric, conv__ajuste_capital numeric, conv__ajuste_interes numeric, conv__deuda_actas_asociadas numeric, conv__deuda_convenios_asociados numeric, emp__cuit character varying, emp__sucursal character varying, emp__razon_soc character varying, emp__nombre_fantasia character varying, emp__id_ramo_empresa smallint, emp__id_seccional integer, emp__contacto character varying, emp__id_entidad_cam_empresa smallint, emp__observaciones character varying, emp__vigen_fecha timestamp without time zone, emp__motivo_baja character varying, emp__alta_fecha timestamp without time zone, emp__alta_usr character varying, emp__alta_ip character varying, emp__modi_fecha timestamp without time zone, emp__modi_usr character varying, emp__modi_ip character varying, emp__baja_fecha timestamp without time zone, emp__baja_usr character varying, emp__baja_ip character varying, 
  emp__id_posicion_iva smallint, emp__domiafip character varying, emp__domiremo varchar, emp__domiestudio varchar) AS
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
	cast('' as varchar) as domiestudio
from convenio c
left outer join (    
  select rc.convenio_id,  sum(rcp.importe) as importe
	from convenio a
	inner join recibo_conceptos rc
	on a.id = rc.convenio_id
	and a.cuit = $1
	inner join recibo_conceptos_pagos rcp
	on rc.id = rcp.recibo_concepto_id
	inner join recibo_ingresos ri
	on rcp.recibo_ingreso_id = ri.id
	where rc.baja_fecha is null
	and rcp.baja_fecha is null
	and ri.baja_fecha is null
	group by rc.convenio_id )cp
on c.id = cp.convenio_id
left outer join (
select convenio_id, null as fecha_pago, sum(importe) as importe , sum(interes) as interes,  tipo , null as conv_que_paga from convenio_pagos
where tipo = 'CUO'
and baja_fecha is null
group by convenio_id, tipo) cp2
on c.id = cp2.convenio_id
left outer join empresa e
on c.cuit = e.cuit
and c.sucursal = e.sucursal
where c.baja_fecha is null
and  ((case when c.deuda_Actas_asociadas is not null then c.interes+c.deuda_Actas_asociadas+c.ajuste_capital+c.ajuste_interes else cp2.importe+cp2.interes end) >  cp.importe 
	   OR cp.importe is null)
and c.cuit = $1;
$BODY$
  LANGUAGE sql VOLATILE


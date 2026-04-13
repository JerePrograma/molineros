-- Function: buscar_actas_no_os_sin_recibo(character varying, character varying)

-- DROP FUNCTION buscar_actas_no_os_sin_recibo(character varying, character varying);

CREATE OR REPLACE FUNCTION buscar_actas_no_os_sin_recibo(IN p_cuit character varying, IN p_entidad character varying)
  RETURNS TABLE(act__id integer, act__numero character varying, act__cuit character varying, act__fecha_inicio timestamp without time zone, act__fecha_pago timestamp without time zone, act__alta_fecha timestamp without time zone, act__alta_usr character varying, act__alta_ip character varying, act__modi_fecha timestamp without time zone, act__modi_usr character varying, act__modi_ip character varying, act__baja_fecha timestamp without time zone, act__baja_usr character varying, act__baja_ip character varying, act__sucursal character varying, act__otros numeric, act__interes numeric, act__capital numeric, act__deuda_actas_asociadas numeric, act__cierre_usr character varying, act__cierre_fecha timestamp without time zone, act__acta_cerrada boolean, act__molinera boolean, emp__cuit character varying, emp__sucursal character varying, emp__razon_soc character varying, emp__nombre_fantasia character varying, emp__id_ramo_empresa smallint, emp__id_seccional integer, emp__contacto character varying, emp__id_entidad_cam_empresa smallint, emp__observaciones character varying, emp__vigen_fecha timestamp without time zone, emp__motivo_baja character varying, emp__alta_fecha timestamp without time zone, emp__alta_usr character varying, emp__alta_ip character varying, emp__modi_fecha timestamp without time zone, emp__modi_usr character varying, emp__modi_ip character varying, emp__baja_fecha timestamp without time zone, emp__baja_usr character varying, emp__baja_ip character varying, emp__id_posicion_iva smallint, actperi__periodo_inicial date, actperi__periodo_final date, emp__domiafip character varying, emp__domiremo character varying, emp__domiestudio character varying) AS
$BODY$


select 		a.id ,
	        a.numero,
			a.cuit,
			a.fecha_inicio,
			a.fecha_pago,
			a.alta_fecha,
			a.alta_usr,
			a.alta_ip,
			a.modi_fecha,
			a.modi_usr,
			a.modi_ip ,
			a.baja_fecha ,
			a.baja_usr,
			a.baja_ip,
			a.sucursal,
			a.otros,
			a.interes,
			a.capital,
			a.deuda_actas_asociadas,
			a.cierre_usr,
			a.cierre_fecha,
			a.acta_cerrada,
			r.molinera,
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
			  (select min(periodo) from acta_periodos where acta_id = a.id and baja_fecha is null) as peri_ini,
			  (select max(periodo) from acta_periodos where acta_id = a.id and baja_fecha is null) as peri_fin,
			  cast('' as varchar) as domiafip,
			  cast('' as varchar) as domiremo,
			  cast('' as varchar) as domiestudio
from acta_no_os a
left outer join
( select acta_id, sum(importe) as importe from (select
        ap.acta_id, 
        sum(ap.importe + ap.interes) as importe
    from acta_no_os a 
    inner join  acta_no_os_pagos ap
    on a.id = ap.acta_id
    left outer join acta_no_os_relacion ar
        on ap.acta_relacion_id = ar.id
        and ar.baja_fecha is null
    left outer join acta_no_os a2
        on ar.acta_id = a2.id
        and a2.baja_fecha is null
        and a2.acta_cerrada = true
    left outer join convenio_actas_no_os ca
        on ap.convenio_acta_id = ca.id
        and ca.baja_fecha is null
    left outer join convenio_no_os c
        on ca.convenio_id = c.id
        and c.baja_fecha is null
    where ap.baja_fecha is null
    and (convenio_acta_id is not null or acta_relacion_id is not null)
    and (acta_relacion_id is null or (acta_relacion_id is not null and a2.id is not null))
    and (convenio_acta_id is null or (convenio_acta_id is not null and c.id is not null) )  
    and a.cuit = $1
    group by ap.acta_id, tipo
 union
 
	select rc.acta_id,  sum(rcp.importe) as importe
	from acta_no_os a
	inner join recibo_no_os_conceptos rc
	on a.id = rc.acta_id
	and a.cuit = $1
	inner join recibo_no_os_conceptos_pagos rcp
	on rc.id = rcp.recibo_concepto_id
	inner join recibo_no_os_ingresos ri
	on rcp.recibo_ingreso_id = ri.id
	where rc.baja_fecha is null
	and rcp.baja_fecha is null
	and ri.baja_fecha is null
	group by rc.acta_id) asd
    group by acta_id
) pagos
on a.id = pagos.acta_id
left outer join empresa e
on a.cuit = e.cuit
and a.sucursal = e.sucursal
left outer join ramo_empresa r
on r.id_ramo_empresa=e.id_ramo_empresa
where a.cuit = $1	
and a.entidad = $2
and a.acta_cerrada = true
and a.baja_fecha is null
and (otros+interes+capital+deuda_Actas_asociadas >    pagos.importe or pagos.importe is null )

$BODY$
  LANGUAGE sql VOLATILE

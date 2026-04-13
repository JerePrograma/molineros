-- Function: buscar_actas_no_os(character varying, character varying, character varying, character varying, character varying)

-- DROP FUNCTION buscar_actas_no_os(character varying, character varying, character varying, character varying, character varying);


CREATE OR REPLACE FUNCTION buscar_acta_no_os(IN p_id integer)
  RETURNS TABLE(act__id integer, act__numero character varying, act__cuit character varying, act__fecha_inicio timestamp without time zone, act__fecha_pago timestamp without time zone, act__alta_fecha timestamp without time zone, act__alta_usr character varying, act__alta_ip character varying, act__modi_fecha timestamp without time zone, act__modi_usr character varying, act__modi_ip character varying, act__baja_fecha timestamp without time zone, act__baja_usr character varying, act__baja_ip character varying, act__sucursal character varying, act__otros numeric, act__interes numeric, act__capital numeric, act__deuda_actas_asociadas numeric, act__cierre_usr character varying, act__cierre_fecha timestamp without time zone, act__acta_cerrada boolean, act__molinera boolean, act__estado character varying, act__entidad character varying, act__periodo_ini date, act__periodo_fin date, emp__cuit character varying, emp__sucursal character varying, emp__razon_soc character varying, emp__nombre_fantasia character varying, emp__id_ramo_empresa smallint, emp__id_seccional integer, emp__contacto character varying, emp__id_entidad_cam_empresa smallint, emp__observaciones character varying, emp__vigen_fecha timestamp without time zone, emp__motivo_baja character varying, emp__alta_fecha timestamp without time zone, emp__alta_usr character varying, emp__alta_ip character varying, emp__modi_fecha timestamp without time zone, emp__modi_usr character varying, emp__modi_ip character varying, emp__baja_fecha timestamp without time zone, emp__baja_usr character varying, emp__baja_ip character varying, emp__id_posicion_iva smallint, emp__domiafip character varying, emp__domiremo character varying, emp__domiestudio character varying, act__capital_sindicato numeric, act__interes_sindicato numeric, act__capital_solidario numeric, act__interes_solidario numeric, act__capital_usufructo numeric, act__interes_usufructo numeric, act__capital_art46 numeric, act__interes_art46 numeric) AS
$BODY$

select 			a.id ,
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
			a.estado,
			a.entidad,
			a.periodo_ini,
			a.periodo_fin,
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
			  a.capital_sindicato,
			  a.interes_sindicato,
			  a.capital_solidario,
			  a.interes_solidario,
			  a.capital_usufructo,
			  a.interes_usufructo,
			  a.capital_art46,
			  a.interes_art46
from acta_no_os a
left outer join empresa e
on a.cuit = e.cuit
and a.sucursal = e.sucursal
left outer join ramo_empresa r
on e.id_ramo_empresa=r.id_ramo_empresa
where ($1 is null or ($1 is not null  and a.id=$1));
$BODY$
  LANGUAGE sql VOLATILE

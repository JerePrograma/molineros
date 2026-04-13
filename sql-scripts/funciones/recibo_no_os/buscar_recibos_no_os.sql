-- Function: buscar_recibos_no_os(character varying, character varying, character varying, character varying)

-- DROP FUNCTION buscar_recibos_no_os(character varying, character varying, character varying, character varying);

CREATE OR REPLACE FUNCTION buscar_recibos_no_os(IN p_nro character varying, IN p_cuit character varying, 
IN p_empresa character varying, IN p_entidad character varying, IN p_fecha_desde date, IN p_fecha_hasta date)
  RETURNS TABLE(rec__id integer, rec__numero character varying, rec__fecha date, rec__cuit character varying, rec__sucursal character varying, rec__descripcion character varying, rec__importe numeric, rec__alta_fecha timestamp without time zone, rec__alta_usr character varying, rec__modi_fecha timestamp without time zone, rec__modi_usr character varying, rec__baja_fecha timestamp without time zone, rec__baja_usr character varying, emp__cuit character varying, emp__sucursal character varying, emp__razon_soc character varying, emp__nombre_fantasia character varying, emp__id_ramo_empresa smallint, emp__id_seccional integer, emp__contacto character varying, emp__id_entidad_cam_empresa smallint, emp__observaciones character varying, emp__vigen_fecha timestamp without time zone, emp__motivo_baja character varying, emp__alta_fecha timestamp without time zone, emp__alta_usr character varying, emp__alta_ip character varying, emp__modi_fecha timestamp without time zone, emp__modi_usr character varying, emp__modi_ip character varying, emp__baja_fecha timestamp without time zone, emp__baja_usr character varying, emp__baja_ip character varying, emp__id_posicion_iva smallint, emp__domiafip character varying, emp__domiremo character varying, emp__domiestudio character varying, rec__id_seccional integer, rec__entidad character varying) AS
$BODY$

select
 			a.id,
    		a.numero,
    		a.fecha,
    		a.cuit,
    		a.sucursal,
    		a.descripcion,
    		a.importe,
			  a.alta_fecha,
			  a.alta_usr,
			  a.modi_fecha,
			  a.modi_usr,
			  a.baja_fecha ,
			  a.baja_usr,
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
			    a.id_seccional,
			    a.entidad
from recibo_no_os a
left outer join empresa e
on a.cuit = e.cuit
and a.sucursal = e.sucursal
where ($1 is null or ($1 is not null  and a.numero like '%' || $1 || '%'))
	and ($2 is null or ($2 is not null and a.cuit = $2))
	and ($3 is null or ($3 is not null and upper(e.razon_soc) like upper($3)||'%'))
	and ($4 is null or ($4 is not null and upper(a.entidad) = upper($4)))
	and fecha>=$5 and fecha<=$6	
	order by numero;
$BODY$
  LANGUAGE sql VOLATILE

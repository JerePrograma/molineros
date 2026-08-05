-- Function: buscar_recibo_no_os_por_fechas(date, date, character varying, character varying)

-- DROP FUNCTION buscar_recibo_no_os_por_fechas(date, date, character varying, character varying);

CREATE OR REPLACE FUNCTION buscar_recibo_no_os_por_fechas(IN p_fecha_ini date, IN p_fecha_fin date, IN p_cuit character varying, IN p_sucu character varying)
  RETURNS TABLE(rec__id integer, rec__numero character varying, rec__fecha date, rec__cuit character varying, rec__sucursal character varying, rec__descripcion character varying, rec__importe numeric, rec__alta_fecha timestamp without time zone, rec__alta_usr character varying, rec__modi_fecha timestamp without time zone, rec__modi_usr character varying, rec__baja_fecha timestamp without time zone, rec__baja_usr character varying, emp__cuit character varying, emp__sucursal character varying, emp__razon_soc character varying, emp__nombre_fantasia character varying, emp__id_ramo_empresa smallint, emp__id_seccional integer, emp__contacto character varying, emp__id_entidad_cam_empresa smallint, emp__observaciones character varying, emp__vigen_fecha timestamp without time zone, emp__motivo_baja character varying, emp__alta_fecha timestamp without time zone, emp__alta_usr character varying, emp__alta_ip character varying, emp__modi_fecha timestamp without time zone, emp__modi_usr character varying, emp__modi_ip character varying, emp__baja_fecha timestamp without time zone, emp__baja_usr character varying, emp__baja_ip character varying, emp__id_posicion_iva smallint, emp__domiafip character varying, emp__domiremo character varying, emp__domiestudio character varying, rec__id_seccional integer) AS
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
			   a.id_seccional
from recibo_no_os a
left outer join empresa e
on a.cuit = e.cuit
and a.sucursal = e.sucursal
where fecha >= $1 and fecha <= $2
and ($3 is null or ($3 is not null and a.cuit = $3))
and ($4 is null or ($4 is not null and a.sucursal=$4))
and id not in (select id from recibo_no_os r
	where baja_fecha is not null
	and exists (Select 1 from recibo where numero = r.numero and id <> r.id and alta_fecha > r.alta_fecha))
order by fecha asc;
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION buscar_recibo_no_os_por_fechas(date, date, character varying, character varying)
  OWNER TO postgres;


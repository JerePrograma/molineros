CREATE OR REPLACE FUNCTION buscar_recibos_amtima(IN p_nro character varying, IN p_cuit character varying, IN p_empresa character varying, IN p_cuil_titular character varying, IN p_inte integer)
  RETURNS TABLE(rec__id integer, rec__numero character varying, rec__fecha date, rec__cuit character varying, rec__sucursal character varying, rec__descripcion character varying, rec__importe numeric, rec__alta_fecha timestamp without time zone, rec__alta_usr character varying, rec__modi_fecha timestamp without time zone, rec__modi_usr character varying, rec__baja_fecha timestamp without time zone, rec__baja_usr character varying, emp__cuit character varying, emp__sucursal character varying, emp__razon_soc character varying, emp__nombre_fantasia character varying, emp__id_ramo_empresa smallint, emp__id_seccional integer, emp__contacto character varying, emp__id_entidad_cam_empresa smallint, emp__observaciones character varying, emp__vigen_fecha timestamp without time zone, emp__motivo_baja character varying, emp__alta_fecha timestamp without time zone, emp__alta_usr character varying, emp__alta_ip character varying, emp__modi_fecha timestamp without time zone, emp__modi_usr character varying, emp__modi_ip character varying, emp__baja_fecha timestamp without time zone, emp__baja_usr character varying, emp__baja_ip character varying, emp__id_posicion_iva smallint, emp__domiafip character varying, emp__domiremo character varying, emp__domiestudio character varying, rec__id_seccional integer, a__cuil_titular character varying, a__inte integer, a__id_amtima integer, a__nombre character varying, a__apellido character varying, a__documento_tipo character varying, a__docu_numero character varying, s__id_seccional integer, s__descripcion character varying) AS
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
			  af.cuil_titular, 
			  af.inte, 
			  af.id_amtima, 
			  af.nombre, 
			  af.apellido, 
			  af.documento_tipo, 
			  af.docu_numero, 
			  s.id_seccional, 
			  s.descripcion 			
from recibo_amtima a
left outer join empresa e
on a.cuit = e.cuit
and a.sucursal = e.sucursal
left outer join afiliado af
on a.cuil_p = af.cuil_titular
and a.inte_p = af.inte
left outer join seccional s
on af.id_seccional=s.id_seccional
where ($1 is null or ($1 is not null  and a.numero like '%' || $1 || '%'))
	and ($2 is null or ($2 is not null and a.cuit = $2))
	and ($3 is null or ($3 is not null and upper(e.razon_soc) like upper($3)||'%'))
	and ($4 is null or ($4 is not null and af.cuil_titular = $4))
	and ($5 is null or ($5 is not null and af.inte = $5))
	order by numero;
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
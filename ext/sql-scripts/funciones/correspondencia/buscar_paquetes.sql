/* funcion comun, mas abajo la q pagina */
CREATE OR REPLACE FUNCTION correo.buscar_paquetes(p_edificio character varying, p_fecha timestamp without time zone, p_numero_correspondencia integer, p_tipo_registro character varying, p_paquete integer, p_tipo_envio character varying, p_tipo_remitente character varying, p_cuil character varying, p_inte integer, p_id_farmacia integer, p_otros character varying, p_id_prestador integer, p_cuit_entidad character varying, p_sucursal_entidad character varying, p_id_seccional integer, p_tipo_compro character varying, p_letra_compro character varying, p_sucu integer, p_nro_compro character varying, p_importe numeric, p_fecha_hasta timestamp without time zone, p_edificio_destino character varying, p_usuario_destino character varying, p_sector_destino character varying, p_contenido character varying)
  RETURNS SETOF correo.correspondencia_cab_item AS
$BODY$
BEGIN

return query

select

  cab.id_correspondencia,  
  cab.lugar_recep_emision,
  cab.fecha,
  cab.tipo_registro,
  cab.tipo_envio,
  cab.oblea,
  cab.alta_fecha,
  cab.alta_usr,
  cab.modi_fecha,
  cab.modi_usr,
  cab.baja_fecha,
  cab.baja_usr,
  
  ic.id, 
  ic.id_correspondencia,  
  ic.entrada_salida,
  ic.tipo_remitente_destinatario,    
  ic.edificio,
  ic.sector,
  ic.usuario,
  ic.empresa_remite,
  ic.sector_remite,
  ic.usuario_remite,
  ic.contenido,
  
  ic.estado,  
  
  ic.cuil_titular,
  ic.inte,    
  --datos farmacia
  ic.codigo_farmacia,  
  --datos otros
  ic.descripcion_otro,   
  --datos prestador y proveedor
  ic.id_prestador,
  ic.cuit_proveedor,
  ic.sucu_proveedor,
  ic.id_punto_venta,
  ic.compro_tipo,
  ic.compro_nro,
  ic.cuit,
  ic.compro_letra,
  ic.compro_sucu,
  ic.importe,
  ic.fecha_emision,
  ic.fecha_vencimiento,

  ic.id_seccional,
  
  ic.alta_fecha,
  ic.alta_usr,
  ic.modi_fecha,
  ic.modi_usr,
  ic.baja_fecha,
  ic.baja_usr,
  ic.alta_sector,

  a.cuil_titular,
  a.inte,
  a.id_ospim ,
--a.id_ospim_baja_fecha,
  a.id_uoma,
--a.id_uoma_baja_fecha,
  a.id_amtima,
--a.id_amtima_baja_fecha,
  a.apellido,
  a.nombre,
  a.documento_tipo,
  a.sexo,
  a.cuil, 
  a.docu_numero,

  f.id_farmacia,
  f.farmacia,
  f.cuit,
  f.codigo,
  f.cod_farm,
  f.sucursal,
  
  	  prs.id_prestador,
	  prs.cuit,
	  prs.id_tipo_prestador,
	  prs.id_seccional ,
	  prs.descripcion,
	  
 e.cuit,
 e.sucursal,
 e.razon_soc,
 e.nombre_fantasia,
 --e.id_ramo_empresa,
 e.id_seccional,
 
 s.id_seccional,
 s.descripcion,
  
 lp.id,
 lp.id_paquete,
 lp.id_item_correspondencia,
 lp.alta_fecha,
 lp.alta_usr,
 /*lp.modi_fecha,
 lp.modi_usr,
 lp.baja_fecha,
 lp.baja_usr,*/
 p.estado,
 cast(0 as integer)

from correo.item_correspondencia ic
inner join correo.cabecera_correspondencia cab
on ic.id_correspondencia=cab.id_correspondencia
inner join correo.lista_paquete lp
on ic.id = lp.id_item_correspondencia
inner join correo.paquete p
on lp.id_paquete = p.id
left outer join afiliado a
on ic.cuil_titular = a.cuil_titular and
ic.inte = a.inte
left outer join farmacia f
--on ic.codigo_farmacia = f.codigo
on ic.codigo_farmacia = f.id_farmacia
left outer join prestador prs
on ic.id_prestador = prs.id_prestador
left outer join empresa e
on ic.cuit_proveedor = e.cuit and
ic.sucu_proveedor = e.sucursal
left outer join seccional s 
on ic.id_seccional = s.id_seccional

where

(p_edificio is null or  p_edificio = '' or (p_edificio is not null and cab.lugar_recep_emision=p_edificio))
and (p_fecha is null or (p_fecha is not null and p_fecha <= lp.alta_fecha))
and (p_fecha_hasta is null or (p_fecha_hasta is not null and lp.alta_fecha <= p_fecha_hasta))
and (p_numero_correspondencia is null or p_numero_correspondencia = 0 or (p_numero_correspondencia is not null and p_numero_correspondencia = cab.id_correspondencia))
and (p_tipo_registro is null or p_tipo_registro = '' or (p_tipo_registro is not null and p_tipo_registro = cab.tipo_registro))
and (p_paquete is null or p_paquete = 0 or (p_paquete is not null and p_paquete = lp.id_paquete))
and (p_tipo_envio is null or p_tipo_envio= '' or (p_tipo_envio is not null and p_tipo_envio = cab.tipo_envio))
and (p_tipo_remitente is null or p_tipo_remitente= '' or (p_tipo_remitente is not null and p_tipo_remitente = ic.tipo_remitente_destinatario))
and (p_cuil is null or p_cuil = '' or (p_cuil is not null and p_cuil = ic.cuil_titular))
and (p_inte is null or p_inte = 0 or (p_inte is not null and p_inte = ic.inte))
and (p_id_farmacia is null or p_id_farmacia = 0 or (p_id_farmacia is not null and p_id_farmacia = ic.codigo_farmacia))
and (p_otros is null or p_otros = '' or (p_otros is not null and p_otros = ic.descripcion_otro))
and (p_id_prestador is null or p_id_prestador = 0 or (p_id_prestador is not null and p_id_prestador = ic.id_prestador))
and (p_cuit_entidad is null or p_cuit_entidad = '' or (p_cuit_entidad is not null and ic.cuit_proveedor = p_cuit_entidad))
and (p_sucursal_entidad is null or p_sucursal_entidad = '' or (p_sucursal_entidad is not null and ic.sucu_proveedor = p_sucursal_entidad))
and (p_id_seccional is null or p_id_seccional = 0 or (p_id_seccional is not null and p_id_seccional = ic.id_seccional))
and (p_tipo_compro is null or p_tipo_compro = '' or (p_tipo_compro is not null and ic.compro_tipo = p_tipo_compro))
and (p_letra_compro is null or p_letra_compro = '' or (p_letra_compro is not null and ic.compro_letra = p_letra_compro))
and (p_sucu is null or p_sucu = 0 or (p_sucu is not null and ic.id_punto_venta = p_sucu))
and (p_nro_compro is null or p_nro_compro = '' or (p_nro_compro is not null and ic.compro_nro = p_nro_compro))
and (p_importe is null or p_importe = '0' or (p_importe is not null and ic.importe = p_importe))

and (p_edificio_destino is null or p_edificio_destino = '' or (p_edificio_destino is not null and ic.edificio = p_edificio_destino))
and (p_usuario_destino is null or p_usuario_destino = '' or (p_usuario_destino is not null and ic.usuario = p_usuario_destino))
and (p_sector_destino is null or p_sector_destino = '' or (p_sector_destino is not null and ic.sector = p_sector_destino))
and (p_contenido is null or p_contenido = '' or (p_contenido is not null and ic.contenido = p_contenido))
order by lp.alta_fecha, ic.id;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;

  
/* funcion q pagina */
  
CREATE OR REPLACE FUNCTION correo.buscar_paquetes(p_edificio character varying, p_fecha timestamp without time zone, p_numero_correspondencia integer, p_tipo_registro character varying, p_paquete integer, p_tipo_envio character varying, p_tipo_remitente character varying, p_cuil character varying, p_inte integer, p_id_farmacia integer, p_otros character varying, p_id_prestador integer, p_cuit_entidad character varying, p_sucursal_entidad character varying, p_id_seccional integer, p_tipo_compro character varying, p_letra_compro character varying, p_sucu integer, p_nro_compro character varying, p_importe numeric, p_fecha_hasta timestamp without time zone, p_edificio_destino character varying, p_usuario_destino character varying, p_sector_destino character varying, p_contenido character varying, offset_p integer)
  RETURNS SETOF correo.correspondencia_cab_item AS
$BODY$
declare total_registros_v int;
BEGIN


if(offset_p>0) then
offset_p=offset_p*50;
end if;

total_registros_v=count(*) from correo.buscar_paquetes(p_edificio, p_fecha , p_numero_correspondencia , p_tipo_registro, 
p_paquete , p_tipo_envio , p_tipo_remitente , p_cuil , p_inte , p_id_farmacia , p_otros , p_id_prestador , p_cuit_entidad,
p_sucursal_entidad, p_id_seccional, p_tipo_compro, p_letra_compro , p_sucu , p_nro_compro , p_importe , p_fecha_hasta, p_edificio_destino, 
p_usuario_destino , p_sector_destino , p_contenido );

return query

select
  cab.id_correspondencia,  
  cab.lugar_recep_emision,
  cab.fecha,
  cab.tipo_registro,
  cab.tipo_envio,
  cab.oblea,
  cab.alta_fecha,
  cab.alta_usr,
  cab.modi_fecha,
  cab.modi_usr,
  cab.baja_fecha,
  cab.baja_usr,
  
  ic.id, 
  ic.id_correspondencia,  
  ic.entrada_salida,
  ic.tipo_remitente_destinatario,    
  ic.edificio,
  ic.sector,
  ic.usuario,
  ic.empresa_remite,
  ic.sector_remite,
  ic.usuario_remite,
  ic.contenido,
  
  ic.estado,  
  
  ic.cuil_titular,
  ic.inte,    
  --datos farmacia
  ic.codigo_farmacia,  
  --datos otros
  ic.descripcion_otro,   
  --datos prestador y proveedor
  ic.id_prestador,
  ic.cuit_proveedor,
  ic.sucu_proveedor,
  ic.id_punto_venta,
  ic.compro_tipo,
  ic.compro_nro,
  ic.cuit,
  ic.compro_letra,
  ic.compro_sucu,
  ic.importe,
  ic.fecha_emision,
  ic.fecha_vencimiento,

  ic.id_seccional,
  
  ic.alta_fecha,
  ic.alta_usr,
  ic.modi_fecha,
  ic.modi_usr,
  ic.baja_fecha,
  ic.baja_usr,
  ic.alta_sector,

  a.cuil_titular, 
  a.inte,
  a.id_ospim ,
  --a.id_ospim_baja_fecha,
  a.id_uoma,
  --a.id_uoma_baja_fecha,
  a.id_amtima,
  --a.id_amtima_baja_fecha,
  a.apellido,
  a.nombre, 
  a.documento_tipo,
  a.sexo,
  a.cuil, 
  --a.naci_fecha, 
  --a.civil_esta, 
  --a.parentesco,
  --a.ingre_fecha,
  --a.anterior_os,
  --a.vigen_fecha,
  --a.observaciones,
  --a.pres_ssalud_fecha,
  --a.alta_fecha,
  --a.alta_usr,
  --a.modi_fecha,
  --a.modi_usr,
  --a.baja_fecha,
  --a.baja_usr,
  --a.discapacitado,
  a.docu_numero,
  --a.nacionalidad,
  --a.aportante_titular,
  --a.nro_afiliado,
  
  f.id_farmacia,
  --f.camara,
  f.farmacia,
  f.cuit,
  f.codigo,
  --f.calle,
  --f.telefono,
  f.cod_farm,
  f.sucursal,
  
  	  prs.id_prestador,
	  prs.cuit,
	  prs.id_tipo_prestador,
	  --prs.tipo_matricula,
	  --prs.nro_matricula,
	  --prs.id_mat_provincia,
	  --prs.id_mat_categoria,
	  --prs.contacto ,
	  prs.id_seccional ,
	  --prs.observaciones ,
	  --prs.rein_liqui ,
	  --prs.id_condicion_de_iva,
	  --prs.cheque_a_nombre_de ,
	  --prs.alta_fecha,
	  --prs.alta_usr ,
	  --prs.modi_fecha,
	  --prs.modi_usr,
	  --prs.baja_fecha ,
	  --prs.baja_usr,
	  prs.descripcion,
	  
 e.cuit,
 e.sucursal,
 e.razon_soc,
 e.nombre_fantasia,
 --e.id_ramo_empresa,
 e.id_seccional,
 --e.contacto,
 --e.id_entidad_cam_empresa,
 --e.observaciones,
 --e.vigen_fecha,
 --e.motivo_baja,
 --e.alta_fecha,
 --e.alta_usr,
 --e.alta_ip,
 --e.modi_fecha,
 --e.modi_usr,
 --e.modi_ip,
 --e.baja_fecha,
 --e.baja_usr,
 --e.baja_ip,
 --e.id_posicion_iva,
 
 s.id_seccional,
 s.descripcion,
  
 lp.id,
 lp.id_paquete,
 lp.id_item_correspondencia,
 lp.alta_fecha,
 lp.alta_usr,
 /*lp.modi_fecha,
 lp.modi_usr,
 lp.baja_fecha,
 lp.baja_usr*/
 p.estado,
  total_registros_v

--incluir tabla paquetes
  
from correo.item_correspondencia ic
inner join correo.cabecera_correspondencia cab
on ic.id_correspondencia=cab.id_correspondencia
inner join correo.lista_paquete lp
on ic.id = lp.id_item_correspondencia
inner join correo.paquete p
on lp.id_paquete = p.id
left outer join afiliado a
on ic.cuil_titular = a.cuil_titular and
ic.inte = a.inte
left outer join farmacia f
--on ic.codigo_farmacia = f.codigo
on ic.codigo_farmacia = f.id_farmacia
left outer join prestador prs
on ic.id_prestador = prs.id_prestador
left outer join empresa e
on ic.cuit_proveedor = e.cuit and
ic.sucu_proveedor = e.sucursal
left outer join seccional s
on ic.id_seccional = s.id_seccional


where

(p_edificio is null or p_edificio = '' or (p_edificio is not null and cab.lugar_recep_emision=p_edificio))
and (p_fecha is null or (p_fecha is not null and p_fecha <= lp.alta_fecha))
and (p_fecha_hasta is null or (p_fecha_hasta is not null and lp.alta_fecha <= p_fecha_hasta))
and (p_numero_correspondencia is null or p_numero_correspondencia = 0 or (p_numero_correspondencia is not null and p_numero_correspondencia = cab.id_correspondencia))
and (p_tipo_registro is null or p_tipo_registro = '' or (p_tipo_registro is not null and p_tipo_registro = cab.tipo_registro))
and (p_paquete is null or p_paquete = 0 or (p_paquete is not null and p_paquete = lp.id_paquete))
and (p_tipo_envio is null or p_tipo_envio = '' or (p_tipo_envio is not null and p_tipo_envio = cab.tipo_envio))
and (p_tipo_remitente is null or p_tipo_remitente = '' or (p_tipo_remitente is not null and p_tipo_remitente = ic.tipo_remitente_destinatario))
and (p_cuil is null or p_cuil = '' or (p_cuil is not null and p_cuil = ic.cuil_titular))
and (p_inte is null or p_inte = 0 or (p_inte is not null and p_inte = ic.inte))
and (p_id_farmacia is null or p_id_farmacia = 0 or (p_id_farmacia is not null and p_id_farmacia = ic.codigo_farmacia))
and (p_otros is null or p_otros = '' or (p_otros is not null and p_otros = ic.descripcion_otro))
and (p_id_prestador is null or p_id_prestador = 0 or (p_id_prestador is not null and p_id_prestador = ic.id_prestador))
and (p_cuit_entidad is null or p_cuit_entidad = '' or (p_cuit_entidad is not null and ic.cuit_proveedor = p_cuit_entidad))
and (p_sucursal_entidad is null or p_sucursal_entidad = '' or (p_sucursal_entidad is not null and ic.sucu_proveedor = p_sucursal_entidad))
and (p_id_seccional is null or p_id_seccional = 0 or (p_id_seccional is not null and p_id_seccional = ic.id_seccional))
and (p_tipo_compro is null or p_tipo_compro = '' or (p_tipo_compro is not null and ic.compro_tipo = p_tipo_compro))
and (p_letra_compro is null or p_letra_compro = '' or (p_letra_compro is not null and ic.compro_letra = p_letra_compro))
and (p_sucu is null or p_sucu = 0 or (p_sucu is not null and ic.id_punto_venta = p_sucu))
and (p_nro_compro is null or p_nro_compro = '' or (p_nro_compro is not null and ic.compro_nro = p_nro_compro))
and (p_importe is null or p_importe = '0' or (p_importe is not null and ic.importe = p_importe))

and (p_edificio_destino is null or p_edificio_destino = '' or (p_edificio_destino is not null and ic.edificio = p_edificio_destino))
and (p_usuario_destino is null or p_usuario_destino = '' or (p_usuario_destino is not null and ic.usuario = p_usuario_destino))
and (p_sector_destino is null or p_sector_destino = '' or (p_sector_destino is not null and ic.sector = p_sector_destino))
and (p_contenido is null or p_contenido = '' or (p_contenido is not null and ic.contenido = p_contenido))

order by lp.alta_fecha, ic.id 
limit 50
offset offset_p;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
  

CREATE OR REPLACE FUNCTION correo.buscar_item_correspondencia_por_id(p_numero_correspondencia integer)
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
 cast(null as character varying),
 cast(0 as integer)

from correo.item_correspondencia ic
inner join correo.cabecera_correspondencia cab
on ic.id_correspondencia=cab.id_correspondencia
left outer join correo.lista_paquete lp
on ic.id = lp.id_item_correspondencia
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
ic.id = p_numero_correspondencia;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 100;
CREATE OR REPLACE FUNCTION correo.update_item (id_item integer, tipo_remitente_destinatario_p character varying,
  edificio_p character varying, sector_p character varying, usuario_p character varying, contenido_p character varying,
  cuil_titular_p character varying, inte_p integer, codigo_farmacia_p integer,
  descripcion_otro_p character varying, id_prestador_p integer, cuit_proveedor_p character varying,
  sucu_proveedor_p character varying, id_punto_venta_p smallint, compro_tipo_p character varying, 
  compro_nro_p character varying, cuit_p character varying, compro_letra_p character varying, compro_sucu_p integer,
  importe_p numeric, fecha_emision_p timestamp without time zone, fecha_vencimiento_p timestamp without time zone,
  id_seccional_p integer, username_p character varying, empresa_remite_p character varying, sector_remite_p character varying,
  usuario_remite_p character varying)
  RETURNS integer AS  
$BODY$
BEGIN
    update correo.item_correspondencia
    set tipo_remitente_destinatario = tipo_remitente_destinatario_p,
        edificio = edificio_p,
        sector = sector_p,
		usuario = usuario_p,
		contenido = contenido_p,
		cuil_titular = cuil_titular_p,
		inte = inte_p,
		codigo_farmacia = codigo_farmacia_p,
		descripcion_otro = descripcion_otro_p,
		id_prestador = id_prestador_p,
		cuit_proveedor = cuit_proveedor_p,
		sucu_proveedor = sucu_proveedor_p,
		id_punto_venta = id_punto_venta_p,
		compro_tipo = compro_tipo_p,
		compro_nro = compro_nro_p,
		cuit = cuit_p,
		compro_letra = compro_letra_p,
		compro_sucu = compro_sucu_p,
		importe = importe_p,
		fecha_emision = fecha_emision_p,
		fecha_vencimiento = fecha_vencimiento_p,
		id_seccional = id_seccional_p,	
		modi_fecha = localtimestamp,
		modi_usr = username_p,
		empresa_remite = empresa_remite_p,
        sector_remite = sector_remite_p,
		usuario_remite = usuario_remite_p
	where id=$1;
	return 1;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
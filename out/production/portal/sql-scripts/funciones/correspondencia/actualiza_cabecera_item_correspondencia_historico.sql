CREATE OR REPLACE FUNCTION correo.actualiza_cabecera_item_correspondencia_historico(p_id integer)
  RETURNS integer AS
$BODY$
 begin 

 INSERT INTO correo.cabecera_item_correspondencia_historico (id_correspondencia_c, lugar_recep_emision,
  fecha, tipo_registro, tipo_envio, oblea, alta_fecha_c, alta_usr_c, modi_fecha_c, modi_usr_c, baja_fecha_c,
  baja_usr_c, id_i, id_correspondencia_i, entrada_salida, tipo_remitente_destinatario, edificio,
  sector, usuario, contenido, estado, cuil_titular, inte, codigo_farmacia, descripcion_otro,
  id_prestador, cuit_proveedor, sucu_proveedor, id_punto_venta, compro_tipo, compro_nro,
  cuit, compro_letra, compro_sucu, importe, fecha_emision, fecha_vencimiento, id_seccional,
  alta_fecha_i, alta_usr_i, modi_fecha_i, modi_usr_i, baja_fecha_i, baja_usr_i)

 select cab.id_correspondencia, lugar_recep_emision,
  fecha, tipo_registro, tipo_envio, oblea, cab.alta_fecha, cab.alta_usr, cab.modi_fecha, cab.modi_usr, cab.baja_fecha,
  cab.baja_usr, i.id, i.id_correspondencia, entrada_salida, tipo_remitente_destinatario, edificio,
  sector, usuario, contenido, estado, cuil_titular, inte, codigo_farmacia, descripcion_otro,
  id_prestador, cuit_proveedor, sucu_proveedor, id_punto_venta, compro_tipo, compro_nro,
  cuit, compro_letra, compro_sucu, importe, fecha_emision, fecha_vencimiento, id_seccional,
  i.alta_fecha, i.alta_usr, i.modi_fecha, i.modi_usr, i.baja_fecha, i.baja_usr 
  from correo.cabecera_correspondencia cab, correo.item_correspondencia i 
 where i.id_correspondencia=cab.id_correspondencia and i.id = $1;

 return 1;
 end;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
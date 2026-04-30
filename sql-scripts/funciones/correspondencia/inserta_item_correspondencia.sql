CREATE OR REPLACE FUNCTION correo.inserta_item_correspondencia

(IN p_id_correspondencia integer, IN p_entrada_salida character varying, 
IN p_tipo_remitente_destinatario character varying,
IN p_empresa_remite character varying, IN p_sector_remite character varying, IN p_usuario_remite character varying,
IN p_contenido character varying, IN p_estado character varying, IN p_cuil_titular character varying, IN p_inte integer,
IN p_codigo_farmacia integer, IN p_descripcion_otro character varying,  
IN p_id_prestador integer, IN p_cuit_proveedor character varying,
IN p_sucu_proveedor character varying, IN p_id_punto_venta integer, 
IN p_compro_tipo character varying, IN p_compro_nro character varying,
IN p_cuit character varying, IN p_compro_letra character varying, 
IN p_compro_sucu integer, 
IN p_importe numeric, IN p_id_seccional integer, IN p_usuario_alta character varying, 
IN p_fecha_emision timestamp without time zone, IN p_fecha_vencimiento timestamp without time zone,
IN p_edificio character varying, IN p_sector character varying, IN p_usuario character varying,
IN p_alta_sector character varying)

  RETURNS integer AS
$BODY$

BEGIN

INSERT INTO correo.item_correspondencia(

			id_correspondencia, entrada_salida, tipo_remitente_destinatario, 
            edificio, sector, usuario, contenido, estado, cuil_titular, inte, 
            codigo_farmacia, descripcion_otro, id_prestador, cuit_proveedor, 
            sucu_proveedor, id_punto_venta, compro_tipo, compro_nro, cuit, 
            compro_letra, compro_sucu, importe, id_seccional, alta_fecha, 
            alta_usr, modi_fecha, modi_usr, baja_fecha, baja_usr, fecha_emision, 
            fecha_vencimiento, empresa_remite, sector_remite, usuario_remite, alta_sector            
            )
    
            VALUES (p_id_correspondencia, p_entrada_salida, p_tipo_remitente_destinatario,
			p_edificio, p_sector, p_usuario, p_contenido, p_estado, p_cuil_titular, p_inte,
			p_codigo_farmacia, p_descripcion_otro, 
			p_id_prestador, p_cuit_proveedor,
			p_sucu_proveedor, p_id_punto_venta, p_compro_tipo, p_compro_nro, p_cuit, p_compro_letra,
			p_compro_sucu, p_importe, p_id_seccional, localtimestamp, p_usuario_alta, 
			localtimestamp, p_usuario_alta, null, null, p_fecha_emision, p_fecha_vencimiento,
			p_empresa_remite, p_sector_remite, p_usuario_remite, p_alta_sector
            );

return currval('correo.item_correspondencia_id_seq');
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
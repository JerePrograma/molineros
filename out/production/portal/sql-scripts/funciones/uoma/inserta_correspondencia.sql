CREATE OR REPLACE FUNCTION uoma.inserta_correspondencia(destino_p character varying, fecha_envio_recepcion_p date, 
apellido_remitente_p character varying, nombre_remitente_p character varying, apellido_destinatario_p character varying, 
nombre_destinatario_p character varying, tipo_correo_p integer, edificio_recep_p character varying, observaciones_p character varying, 
id_seccional_remitente_p integer, id_seccional_destinatario_p integer, edificio_origen_p character varying, edificio_destino_p character varying, 
id_domicilio_remitente_v integer, id_domicilio_destinatario_v integer, razon_prestador_destinatario_p character varying, 
razon_prestador_remitente_p character varying, gastos_seccional_p boolean, reintegros_p boolean, padrones_p boolean, 
discapacidad_p boolean, otros_p boolean, documentacion_p boolean, facturacion_p boolean, tesoreria_p boolean, medicamentos_p boolean,
id_localidad_p integer, id_provincia_p integer, datos_factura_p character varying, tipo_envio_p character varying, codigo_oblea_p character varying, cod_farmacia_p character varying, farmacia_p character varying, user_v character varying)
  RETURNS integer AS
$BODY$

BEGIN

INSERT INTO uoma.correspondencia(
            alta_fecha, modi_fecha, alta_usr, modi_usr, 
            destino, id_domicilio_remitente, id_domicilio_destinatario, apellido_remitente, 
            nombre_remitente, apellido_destinatario, nombre_destinatario, 
            fecha_envio_recepcion, tipo_correo, edificio_recep, observaciones, 
            seccional_remitente, seccional_destinatario, 
            edificio_origen, edificio_destino, razon_prestador_remitente, razon_prestador_destinatario, gastos_seccional,
	    reintegro, padrones, discapacidad, otros, documentacion, facturacion, tesoreria, medicamentos,
	    id_localidad, id_provincia, datos_factura, tipo_envio, 
	    cod_farmacia, farmacia, codigo_oblea) 
values(current_date, current_date, user_v, user_v, destino_p, id_domicilio_remitente_v, id_domicilio_destinatario_v, apellido_remitente_p,
       nombre_remitente_p, apellido_destinatario_p, nombre_destinatario_p, fecha_envio_recepcion_p, tipo_correo_p,
       edificio_recep_p, observaciones_p, id_seccional_remitente_p, id_seccional_destinatario_p, edificio_origen_p, edificio_destino_p,
       razon_prestador_remitente_p, razon_prestador_destinatario_p, gastos_seccional_p,reintegros_p, padrones_p , discapacidad_p, 
       otros_p, documentacion_p, facturacion_p, tesoreria_p, medicamentos_p, id_localidad_p, id_provincia_p, datos_factura_p, tipo_envio_p, 
       cod_farmacia_p, farmacia_p, codigo_oblea_p);

return currval('uoma.correspondencia_id_seq');

END;
$BODY$
  LANGUAGE plpgsql VOLATILE


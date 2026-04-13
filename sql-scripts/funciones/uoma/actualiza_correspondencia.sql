CREATE OR REPLACE FUNCTION uoma.actualiza_correspondencia(destino_p character varying, fecha_envio_recepcion_p date, apellido_remitente_p character varying, nombre_remitente_p character varying, apellido_destinatario_p character varying, nombre_destinatario_p character varying, tipo_correo_p integer, edificio_recep_p character varying, observaciones_p character varying, id_seccional_remitente_p integer, id_seccional_destinatario_p integer, edificio_origen_p character varying, edificio_destino_p character varying, id_domicilio_remitente_v integer, id_domicilio_destinatario_v integer, id_correspondencia_v integer, razon_prestador_destinatario_p character varying, razon_prestador_remitente_p character varying, gastos_seccional_p boolean, reintegros_p boolean, padrones_p boolean, discapacidad_p boolean, otros_p boolean, documentacion_p boolean, facturacion_p boolean,tesoreria_p boolean, medicamentos_p boolean, id_localidad_p integer, id_provincia_p integer, datos_factura_p character varying, tipo_envio_p character varying, codigo_oblea_p character varying, cod_farmacia_p character varying, farmacia_p character varying, user_v character varying)
  RETURNS integer AS
$BODY$
BEGIN


INSERT INTO uoma.correspondencia_histo(
            baja_fecha, modi_fecha, alta_usr, modi_usr, baja_usr, destino, 
            id_domicilio_remitente, id_domicilio_destinatario, apellido_remitente, 
            nombre_remitente, apellido_destinatario, nombre_destinatario, 
            fecha_envio_recepcion, tipo_correo, edificio_recep, observaciones, 
            seccional_remitente, seccional_destinatario, edificio_origen, 
            edificio_destino, id_correspondencia, razon_prestador_remitente, 
            razon_prestador_destinatario, gastos_seccional, reintegro, padrones, 
            discapacidad, otros, id_provincia, id_localidad, datos_factura, 
            tipo_envio, codigo_oblea, documentacion, facturacion, tesoreria, medicamentos, cod_farmacia, 
            farmacia, alta_fecha)
select baja_fecha, modi_fecha, alta_usr, modi_usr, baja_usr, destino, 
            id_domicilio_remitente, id_domicilio_destinatario, apellido_remitente, 
            nombre_remitente, apellido_destinatario, nombre_destinatario, 
            fecha_envio_recepcion, tipo_correo, edificio_recep, observaciones, 
            seccional_remitente, seccional_destinatario, edificio_origen, 
            edificio_destino, id_correspondencia, razon_prestador_remitente, 
            razon_prestador_destinatario, gastos_seccional, reintegro, padrones, 
            discapacidad, otros, id_provincia, id_localidad, datos_factura, 
            tipo_envio, codigo_oblea, documentacion, facturacion, tesoreria, medicamentos, cod_farmacia, 
            farmacia, current_timestamp
from uoma.correspondencia
where id_correspondencia=id_correspondencia_v;            

update uoma.correspondencia
set modi_fecha=current_date,
    modi_usr=user_v,
    destino=destino_p,
    apellido_remitente=apellido_remitente_p,
    nombre_remitente=nombre_remitente_p,
    apellido_destinatario=apellido_destinatario_p,
    nombre_destinatario=nombre_destinatario_p,
    fecha_envio_recepcion=fecha_envio_recepcion_p,
    tipo_correo=tipo_correo_p,
    edificio_recep=edificio_recep_p,
    observaciones=observaciones_p,
    seccional_remitente=id_seccional_remitente_p,
    seccional_destinatario=id_seccional_destinatario_p,
    edificio_origen=edificio_origen_p,
    edificio_destino=edificio_destino_p,
    id_domicilio_remitente=id_domicilio_remitente_v, 
    id_domicilio_destinatario=id_domicilio_destinatario_v, 
    razon_prestador_remitente=razon_prestador_remitente_p, 
    razon_prestador_destinatario=razon_prestador_destinatario_p,
    gastos_seccional=gastos_seccional_p,
    reintegro=reintegros_p , 
    padrones=padrones_p, 
    discapacidad=discapacidad_p, 
    otros=otros_p,
    documentacion=documentacion_p,
    facturacion=facturacion_p,
    tesoreria=tesoreria_p,
    medicamentos=medicamentos_p,    
    id_localidad=id_localidad_p, 
    id_provincia=id_provincia_p,
    datos_factura=datos_factura_p,
    tipo_envio=tipo_envio_p,
    codigo_oblea=codigo_oblea_p,
    cod_farmacia=cod_farmacia_p,
    farmacia=farmacia_p 
where id_correspondencia=id_correspondencia_v;

return id_correspondencia_v;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE


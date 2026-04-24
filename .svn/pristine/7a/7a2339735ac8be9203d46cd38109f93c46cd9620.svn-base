CREATE OR REPLACE FUNCTION uoma.inserta_incidente_uoma(id_seccional_v integer, fecha_v timestamp without time zone, cuil_titular_v character varying, inte_v integer, id_localidad_v integer, id_provincia_v integer, calle_v character varying, numero_v character varying, piso_v character varying, depto_v character varying, postal_codi_v character varying, observaciones_v character varying, incidente_v character varying, seguimiento_v character varying, fecha_recepcion_v date, username character varying)
  RETURNS integer AS
$BODY$
declare id_domicilio_p integer;
declare id_incidente_p integer;
BEGIN
INSERT INTO uoma.domicilio(calle, piso, depto, oficina, postal_codi, barrio, 
            telefono, observaciones, domi_val, alta_fecha, alta_usr, modi_fecha, 
            modi_usr, baja_fecha, baja_usr, provincia, localidad, numero, 
            localidad_nombre, provincia_nombre) 
            values (calle_v, piso_v, depto_v, null, postal_codi_v, null, null, observaciones_v, '0', current_timestamp, username,
            current_timestamp, username, null, null, id_provincia_v, id_localidad_v, numero_v, null, null);
            
id_domicilio_p=currval('uoma.domicilio_uoma_id_seq');

INSERT INTO uoma.incidente_unidad_operativa(cuil_titular, inte, fecha, id_domicilio, alta_fecha, alta_usr, modi_fecha, modi_usr, 
            baja_fecha, baja_usr, id_seccional, detalle_incidente, fecha_recepcion)
            values(cuil_titular_v, inte_v, fecha_v, id_domicilio_p, current_timestamp, username, current_timestamp, username, null, null, 
            id_seccional_v, incidente_v, fecha_recepcion_v );
            
id_incidente_p=currval('uoma.incidente_unidad_operativa_id_incidente_seq');

INSERT INTO uoma.incidente_unidad_operativa_detalle(
            fecha, seguimiento_incidente, alta_fecha, alta_usr, modi_fecha, modi_usr, baja_fecha, baja_usr, 
            id_incidente)
            values(fecha_v, seguimiento_v, current_timestamp, username, current_timestamp, username, null, null, id_incidente_p);

return id_incidente_p;


END;
$BODY$
  LANGUAGE plpgsql VOLATILE

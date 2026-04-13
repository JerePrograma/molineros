CREATE OR REPLACE FUNCTION novedades_sss.inserta_novedad(id_proceso_p integer, codigo_ooss_p integer, cuit_empleador_p character varying, cuil_titular_p character varying, codigo_parentesco_p integer, cuil_p character varying, documento_tipo_p character varying, documento_numero_p integer, apellido_nombre_p character varying, sexo_p character varying, estado_civil_p integer, fecha_nacimiento_p integer, nacionalidad_p integer, calle_p character varying, numero_puerta_p character varying, piso_p character varying, departamento_p character varying, localidad_p character varying, codigo_postal_p character varying, provincia_p integer, tipo_domicilio_p integer, telefono_p character varying, situacion_revista_p integer, incapacidad_p integer, tipo_beneficiario_titular_p integer, fecha_alta_en_ooss_p integer, fecha_cierre_presentacion_p integer, codigo_movimiento_p character varying, detalle_novedad_p character varying, usuario_p character varying)
  RETURNS integer AS
$BODY$

BEGIN

INSERT INTO novedades_sss.novedades(
            id_proceso, codigo_ooss, cuit_empleador, cuil_titular, codigo_parentesco, 
            cuil, documento_tipo, documento_numero, apellido_nombre, sexo, 
            estado_civil, fecha_nacimiento, nacionalidad, calle, numero_puerta, 
            piso, departamento, localidad, codigo_postal, provincia, tipo_domicilio, 
            telefono, situacion_revista, incapacidad, tipo_beneficiario_titular, 
            fecha_alta_en_ooss, fecha_cierre_presentacion, codigo_movimiento, 
            detalle_novedad, alta_fecha, alta_usr, modi_fecha, modi_usr)
    VALUES (id_proceso_p, codigo_ooss_p, cuit_empleador_p, cuil_titular_p, codigo_parentesco_p, 
            cuil_p, documento_tipo_p, documento_numero_p, apellido_nombre_p, sexo_p, 
            estado_civil_p, fecha_nacimiento_p, nacionalidad_p, calle_p, numero_puerta_p, 
            piso_p, departamento_p, localidad_p, codigo_postal_p, provincia_p, tipo_domicilio_p, 
            telefono_p, situacion_revista_p, incapacidad_p, tipo_beneficiario_titular_p, 
            fecha_alta_en_ooss_p, fecha_cierre_presentacion_p, codigo_movimiento_p, 
            detalle_novedad_p, LOCALTIMESTAMP, usuario_p, LOCALTIMESTAMP, usuario_p);

return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

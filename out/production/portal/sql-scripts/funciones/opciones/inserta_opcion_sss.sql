CREATE OR REPLACE FUNCTION inserta_opcion_sss(tipo_exportacion_p character varying, id_delegacion_p integer, delegacion_p character varying, libro_p integer, tomo_p integer, nro_formulario_p integer, os_elegida_p integer, regimen_p character varying, cuil_p character varying, ape_p  character varying, nom_p character varying, sexo_p character varying, calle_p character varying, numero_p character varying, piso_p integer, departamento_p character varying, id_localidad_p character varying, id_provincia_p character varying, telefono_particular_p character varying, telefono_laboral_p character varying, telefono_celular_p character varying, email_p character varying, os_anterior_p integer, cuit_p character varying, fecha_elecc_p date, fecha_certi_p date, version_sistema_p character varying, postal_codi_p character varying, unifica_aportes_p character varying, cuil_conyuge_p character varying, ape_nom_conyuge_p character varying, user_p character varying)
  RETURNS integer AS
$BODY$

BEGIN

INSERT INTO afi_opciones_sss(tipo_exportacion, id_delegacion, delegacion, libro, tomo, nro_formulario, os_elegida, regimen, cuil, apellido, nombre, sexo,
  calle, numero, piso, departamento, localidad, provincia, telefono_particular, telefono_laboral, telefono_celular, email, 
  os_anterior, cuit, fecha_elecc, fecha_certi, version_sistema, postal_codi, unifica_apo, cuil_conyuge, ape_nom_conyuge, 
  alta_fecha, alta_usr, modi_fecha, modi_usr)
    
    VALUES (tipo_exportacion_p, id_delegacion_p, delegacion_p, libro_p, tomo_p, nro_formulario_p, os_elegida_p, regimen_p, cuil_p, 
    ape_p, nom_p, sexo_p, calle_p, numero_p, piso_p, departamento_p, id_localidad_p, id_provincia_p, telefono_particular_p, 
    telefono_laboral_p, telefono_celular_p, email_p, os_anterior_p, cuit_p, fecha_elecc_p, fecha_certi_p, version_sistema_p, 
    postal_codi_p, unifica_aportes_p, cuil_conyuge_p, ape_nom_conyuge_p, LOCALTIMESTAMP, user_p, LOCALTIMESTAMP, user_p);

    /*Actualizamos rendicion del bono por la carga de la opcion.*/
update bonos_seccional set fecha_rendido = current_date, rendicion_usr = user_p where nro_bono = nro_formulario_p and tipo_bono=100;

return currval('afi_opciones_sss_id_seq');
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
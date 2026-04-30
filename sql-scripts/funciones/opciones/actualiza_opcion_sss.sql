CREATE OR REPLACE FUNCTION actualiza_opcion_sss(tipo_exportacion_p character varying, id_delegacion_p integer, delegacion_p character varying, libro_p integer, tomo_p integer, nro_formulario_p integer, os_elegida_p integer, regimen_p character varying, cuil_p character varying, ape_p character varying, nom_p character varying, sexo_p character varying, calle_p character varying, numero_p character varying, piso_p integer, departamento_p character varying, id_localidad_p character varying, id_provincia_p character varying, telefono_particular_p character varying, telefono_laboral_p character varying, telefono_celular_p character varying, email_p character varying, os_anterior_p integer, cuit_p character varying, fecha_elecc_p date, fecha_certi_p date, version_sistema_p character varying, postal_codi_p character varying, unifica_aportes_p character varying, cuil_conyuge_p character varying, ape_nom_conyuge_p character varying, user_p character varying, id_opcion_p integer)
  RETURNS integer AS
$BODY$

BEGIN

UPDATE afi_opciones_sss
   SET tipo_exportacion=tipo_exportacion_p, id_delegacion=id_delegacion_p, delegacion=delegacion_p, 
       libro=libro_p, tomo=tomo_p, nro_formulario=nro_formulario_p, os_elegida=os_elegida_p, 
       regimen=regimen_p, cuil=cuil_p, apellido=ape_p, nombre=nom_p, sexo=sexo_p, calle=calle_p, 
       numero=numero_p, piso=piso_p, departamento=departamento_p, localidad=id_localidad_p, provincia=id_provincia_p,
       telefono_particular=telefono_particular_p, telefono_laboral=telefono_laboral_p, telefono_celular=telefono_celular_p,
       email=email_p, os_anterior=os_anterior_p, cuit=cuit_p, fecha_elecc=fecha_elecc_p, fecha_certi=fecha_certi_p, 
       unifica_apo=unifica_aportes_p, cuil_conyuge=cuil_conyuge_p, ape_nom_conyuge=ape_nom_conyuge_p, 
       version_sistema=version_sistema_p, postal_codi=postal_codi_p, modi_fecha=LOCALTIMESTAMP, modi_usr=user_p
 WHERE id=id_opcion_p;

return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;  
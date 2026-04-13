CREATE OR REPLACE FUNCTION actualiza_afiliado_y_domi(cuil_titular_p character varying, inte_p integer, apellido_p character varying, nombre_p character varying, documento_tipo_p character varying, sexo_p character varying, cuil_p character varying, naci_fecha_p timestamp without time zone, civil_esta_p integer, nacionalidad_p integer, parentesco_p integer, id_seccional_p integer, anterior_os_p integer, modi_usr_p character varying, discapacitado_p character varying, docu_numero_p character varying, domi_tipo_p character varying, calle_p character varying, piso_p character varying, depto_p character varying, oficina character varying, postal_codi_p character varying, barrio_p character varying, telefono_p character varying, observaciones_p character varying, domi_val_p character varying, alta_usr_d_p character varying, modi_usr_d_p character varying, provincia_p integer, localidad_p integer, numero_p character varying, domi character varying, vigen_fecha_p timestamp without time zone, actualiza_afi character varying, baja_fecha_p timestamp without time zone, id_motivo_baja_p integer, id_ospim_baja_fecha_p timestamp without time zone, id_uoma_baja_fecha_p timestamp without time zone, id_amtima_baja_fecha_p timestamp without time zone, cod_area_telefono_p character varying, cod_area_celular_p character varying, celular_p character varying, censo2013_p integer, cod_area_tel_laboral_p character varying, tel_laboral_p character varying, email_p character varying)
  RETURNS integer AS
$BODY$
declare id_dom_baja integer;
declare id_dom_link integer;
declare domi_baja_fecha timestamp without time zone;
declare ya_informo_modi_a_la_sss boolean;
BEGIN

--GUARDO AFILIADO EN HISTORICO DE ESTADOS AFILIADO.
if actualiza_afi is not null then 
INSERT INTO afi_estados_histo(
            cuil_titular, inte, id_ospim, id_uoma, id_amtima, apellido, nombre, 
            documento_tipo, sexo, cuil, naci_fecha, id_estado_civil_sss, id_parentesco_sss, 
            ingre_fecha, id_seccional, anterior_os, vigen_fecha, observaciones, 
            pres_ssalud_fecha, alta_fecha, alta_usr, modi_fecha, modi_usr, 
            baja_fecha, baja_usr, discapacitado, docu_numero, nacionalidad, 
            aportante_titular, nro_afiliado, id_motivo_baja, id_ospim_baja_fecha, 
            id_uoma_baja_fecha, id_amtima_baja_fecha, descripcion_operacion, censo2013, email)
select cuil_titular, inte, id_ospim, id_uoma, id_amtima, apellido, nombre, 
            documento_tipo, sexo, cuil, naci_fecha, id_estado_civil_sss, id_parentesco_sss, 
            ingre_fecha, id_seccional, anterior_os, vigen_fecha, observaciones, 
            pres_ssalud_fecha, LOCALTIMESTAMP, $14, modi_fecha, modi_usr, 
            baja_fecha, baja_usr, discapacitado, docu_numero, nacionalidad, 
            aportante_titular, nro_afiliado, id_motivo_baja, id_ospim_baja_fecha, 
            id_uoma_baja_fecha, id_amtima_baja_fecha,'MOD', censo2013, email
from afiliado
where cuil_titular=$1 and inte=$2;
end if;	

ya_informo_modi_a_la_sss = fecha_mod_super is not null from afiliado where cuil_titular=cuil_p and inte=inte_p;

if ya_informo_modi_a_la_sss then
	
/*Insertamos movimiento historico para la Super*/
	INSERT INTO informes.fechas_informe_super(cuil, fecha_mod, cuil_titular, inte, fecha_proceso)  
		SELECT cuil, fecha_mod_super, cuil_titular, inte, current_date FROM afiliado 
			WHERE cuil_titular=cuil_p and inte=inte_p;
/* Blanqueamos datos para presentar nuevamente a la SSS */
	UPDATE afiliado set fecha_mod_super=null 
		WHERE cuil_titular=cuil_p and inte=inte_p;
end if;
	
update afiliado set 
apellido=$3,
nombre=$4,
documento_tipo=$5,
sexo=$6,
cuil=$7,
naci_fecha=$8,
id_estado_civil_sss=$9,
nacionalidad=$10,
id_parentesco_sss=$11,
--id_seccional=$12,
--anterior_os=$13,
modi_fecha=LOCALTIMESTAMP,
modi_usr=$14,
discapacitado=$15,
docu_numero=$16,
observaciones=$25,
vigen_fecha=$33,
baja_fecha=baja_fecha_p,
id_motivo_baja=id_motivo_baja_p,
id_ospim_baja_fecha=id_ospim_baja_fecha_p,
id_uoma_baja_fecha=id_uoma_baja_fecha_p,
id_amtima_baja_fecha=id_amtima_baja_fecha_p,
censo2013=censo2013_p,
email=email_p 
where cuil_titular = $1 and
inte=$2;

--domi_baja_fecha=max(baja_fecha) from afi_domicilio where baja_fecha>current_date and cuil_titular=$1;

IF $32 IS NOT NULL and $2 = 0 THEN
	update afi_domicilio
	set baja_fecha = LOCALTIMESTAMP, baja_usr = $14
	where
	cuil_titular = $1 and
	inte = $2 and
	(baja_fecha is null or baja_fecha>current_date);
	id_dom_link = inserta_domicilio_afi(cuil_titular_p,inte_p,domi_tipo_p,calle_p,piso_p,depto_p,oficina,
	postal_codi_p,barrio_p,telefono_p,observaciones_p,domi_val_p,alta_usr_d_p,modi_usr_d_p,
	provincia_p,localidad_p,numero_p, null,vigen_fecha_p,cod_area_telefono_p,cod_area_celular_p,
	celular_p, cod_area_tel_laboral_p, tel_laboral_p);
END IF;

IF $2 = 0 THEN
	update afiliado SET id_seccional=$12, anterior_os=$13 where cuil_titular = $1;
END IF;

return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
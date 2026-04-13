CREATE OR REPLACE FUNCTION actualiza_afiliado(cuil_titular_p character varying, inte_p integer, apellido_p character varying, nombre_p character varying, documento_tipo_p character varying, sexo_p character varying, cuil_p character varying, naci_fecha_p timestamp without time zone, civil_esta_p integer, nacionalidad_p integer, parentesco_p integer, id_seccional_p integer, anterior_os_p integer, modi_usr_p character varying, discapacitado_p character varying, docu_numero_p character varying, observaciones_p character varying, alta_usr_d_p character varying, modi_usr_d_p character varying, vigen_fecha_p timestamp without time zone, actualiza_afi character varying, baja_fecha_p timestamp without time zone, id_motivo_baja_p integer, id_ospim_baja_fecha_p timestamp without time zone, id_uoma_baja_fecha_p timestamp without time zone, id_amtima_baja_fecha_p timestamp without time zone)
  RETURNS integer AS
$BODY$

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
            aportante_titular, nro_afiliado, id_motivo_baja, id_ospim_baja_fecha, id_uoma_baja_fecha, id_amtima_baja_fecha, descripcion_operacion)
select cuil_titular, inte, id_ospim, id_uoma, id_amtima, apellido, nombre, 
            documento_tipo, sexo, cuil, naci_fecha, id_estado_civil_sss, id_parentesco_sss, 
            ingre_fecha, id_seccional, anterior_os, vigen_fecha, observaciones, 
            pres_ssalud_fecha, LOCALTIMESTAMP, $14, modi_fecha, modi_usr, 
            baja_fecha, baja_usr, discapacitado, docu_numero, nacionalidad, 
            aportante_titular, nro_afiliado, id_motivo_baja, id_ospim_baja_fecha, id_uoma_baja_fecha, id_amtima_baja_fecha,'MOD'
from afiliado
where cuil_titular=cuil_titular_p and inte=inte_p;
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
apellido=apellido_p,
nombre=nombre_p,
documento_tipo=documento_tipo_p,
sexo=sexo_p,
cuil=cuil_p,
naci_fecha=naci_fecha_p,
id_estado_civil_sss=civil_esta_p,
nacionalidad=nacionalidad_p,
id_parentesco_sss=parentesco_p,
modi_fecha=LOCALTIMESTAMP,
modi_usr=modi_usr_d_p,
discapacitado=discapacitado_p,
docu_numero=docu_numero_p,
observaciones=observaciones_p,
vigen_fecha=vigen_fecha_p,
baja_fecha=baja_fecha_p,
id_motivo_baja=id_motivo_baja_p,
id_ospim_baja_fecha=id_ospim_baja_fecha_p,
id_uoma_baja_fecha=id_uoma_baja_fecha_p,
id_amtima_baja_fecha=id_amtima_baja_fecha_p
where cuil_titular = cuil_titular_p and inte=inte_p;


IF inte_p = 0 THEN
	update afiliado SET id_seccional=id_seccional_p, anterior_os=anterior_os_p where cuil_titular = cuil_titular_p;
END IF;

return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
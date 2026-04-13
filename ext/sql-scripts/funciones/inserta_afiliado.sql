-- DROP FUNCTION inserta_afiliado(character varying, integer, integer, integer, integer, character varying, character varying, character varying, character varying, character varying, timestamp without time zone, character varying, integer, character varying, integer, integer, timestamp without time zone, character varying, timestamp without time zone, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, integer, integer, character varying, timestamp without time zone, character varying, integer, timestamp without time zone, timestamp without time zone, timestamp without time zone, character varying, character varying, character varying, integer, character varying, character varying, character varying);

CREATE OR REPLACE FUNCTION inserta_afiliado(cuil_titular_p character varying, inte_p integer, 
id_ospim_p integer, id_uoma_p integer, id_amtima_p integer, apellido_p character varying, nombre_p character varying, 
documento_tipo_p character varying, sexo_p character varying, cuil_p character varying, naci_fecha_p timestamp without time zone, 
civil_esta_p integer, nacionalidad_p integer, parentesco_p integer, id_seccional_p integer, anterior_os_p integer, 
vigen_fecha_p timestamp without time zone, observaciones_p character varying, pres_ssalud_fecha_p timestamp without time zone, 
alta_usr_p character varying, modi_usr_p character varying, discapacitado_p character varying, docu_numero_p character varying, 
domi_tipo_p character varying, calle_p character varying, piso_p character varying, depto_p character varying, oficina character varying, 
postal_codi_p character varying, barrio_p character varying, telefono_p character varying, observaciones_dom_p character varying, 
domi_val_p character varying, alta_usr_d_p character varying, modi_usr_d_p character varying, provincia_p integer, localidad_p integer, 
numero_p character varying, baja_fecha_p timestamp without time zone, usr_fecha_p character varying, id_motivo_p integer, 
id_ospim_baja_fecha_p timestamp without time zone, id_uoma_baja_fecha_p timestamp without time zone, id_amtima_baja_fecha_p timestamp without time zone, 
cod_area_telefono_p character varying, cod_area_celular_p character varying, celular_p character varying, censo2013_p integer, cod_area_tel_laboral_p character varying, 
tel_laboral_p character varying, email_p character varying)
  RETURNS integer AS
$BODY$
declare resultDom integer;
BEGIN

insert into afiliado (
cuil_titular,
inte,
id_ospim,
id_uoma,
id_amtima,
apellido,
nombre,
documento_tipo,
sexo,
cuil,
naci_fecha,
id_estado_civil_sss,
nacionalidad,
id_parentesco_sss,
ingre_fecha,
id_seccional,
anterior_os,
vigen_fecha,
observaciones,
pres_ssalud_fecha,
alta_fecha,
alta_usr,
modi_fecha,
modi_usr,
discapacitado,
docu_numero,
baja_fecha,
baja_usr,
id_motivo_baja,
id_ospim_baja_fecha,
id_uoma_baja_fecha,
id_amtima_baja_fecha,
censo2013,
email
)
values
($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12,$13,$14,LOCALTIMESTAMP,$15,$16,$17,$18,$19,LOCALTIMESTAMP,$20,LOCALTIMESTAMP,$21,$22,$23,$39,$40,$41,$42,$43,$44,$48,$51);


IF $2 = 0 THEN
	resultDom=inserta_domicilio_afi($1,$2,$24,$25,$26,$27,$28,$29,$30,$31,$32,$33,$34,$35,$36,$37,$38,$39,$17,$45,$46,$47,$49,$50);
--inseguro!! todo chequear la forma de obtenerlo del store dirÃ©ctamente
--id_dom = select last_value from domicilio_id_seq;
END IF;
--GUARDO AFILIADO EN HISTORICO DE ESTADOS AFILIADO.
INSERT INTO afi_estados_histo(
            cuil_titular, inte, id_ospim, id_uoma, id_amtima, apellido, nombre, 
            documento_tipo, sexo, cuil, naci_fecha, id_estado_civil_sss, id_parentesco_sss, 
            ingre_fecha, id_seccional, anterior_os, vigen_fecha, observaciones, 
            pres_ssalud_fecha, alta_fecha, alta_usr, modi_fecha, modi_usr, 
            baja_fecha, baja_usr, discapacitado, docu_numero, nacionalidad, 
            aportante_titular, nro_afiliado, id_motivo_baja, descripcion_operacion, 
            id_ospim_baja_fecha, id_uoma_baja_fecha, id_amtima_baja_fecha, 
            censo2013, email)
select cuil_titular, inte, id_ospim, id_uoma, id_amtima, apellido, nombre, 
            documento_tipo, sexo, cuil, naci_fecha, id_estado_civil_sss, id_parentesco_sss, 
            ingre_fecha, id_seccional, anterior_os, vigen_fecha, observaciones, 
            pres_ssalud_fecha, LOCALTIMESTAMP, $20, modi_fecha, modi_usr, 
            baja_fecha, baja_usr, discapacitado, docu_numero, nacionalidad, 
            aportante_titular, nro_afiliado, id_motivo_baja, 'ALT',
            id_ospim_baja_fecha, id_uoma_baja_fecha, id_amtima_baja_fecha, 
            censo2013, email
from afiliado
where cuil_titular=$1 and inte=$2;

if $39 is not null then 
INSERT INTO afi_estados_histo(
            cuil_titular, inte, id_ospim, id_uoma, id_amtima, apellido, nombre, 
            documento_tipo, sexo, cuil, naci_fecha, id_estado_civil_sss, id_parentesco_sss, 
            ingre_fecha, id_seccional, anterior_os, vigen_fecha, observaciones, 
            pres_ssalud_fecha, alta_fecha, alta_usr, modi_fecha, modi_usr, 
            baja_fecha, baja_usr, discapacitado, docu_numero, nacionalidad, 
            aportante_titular, nro_afiliado, id_motivo_baja, descripcion_operacion, 
            id_ospim_baja_fecha, id_uoma_baja_fecha, id_amtima_baja_fecha, 
            censo2013, email)
select cuil_titular, inte, id_ospim, id_uoma, id_amtima, apellido, nombre, 
            documento_tipo, sexo, cuil, naci_fecha, id_estado_civil_sss, id_parentesco_sss, 
            ingre_fecha, id_seccional, anterior_os, vigen_fecha, observaciones, 
            pres_ssalud_fecha, LOCALTIMESTAMP, $20, modi_fecha, modi_usr, 
            baja_fecha, baja_usr, discapacitado, docu_numero, nacionalidad, 
            aportante_titular, nro_afiliado, id_motivo_baja, 'BPA',
            id_ospim_baja_fecha, id_uoma_baja_fecha, id_amtima_baja_fecha, 
            censo2013, email
from afiliado
where cuil_titular=$1 and inte=$2;
end if;

return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
CREATE OR REPLACE FUNCTION inserta_domicilio_afi(cuil_tit_d character varying, inte_d integer, 
domi_tipo_i character varying, calle_i character varying, piso_i character varying, depto_i character varying, 
oficina_i character varying, postal_codi_i character varying, barrio_i character varying, telefono_i character varying, 
observaciones_i character varying, domi_val_i character varying, alta_usr_i character varying, 
modi_usr_i character varying, provincia_i integer, localidad_i integer, numero_i character varying, 
baja_fecha_i timestamp without time zone, vigen_fecha timestamp without time zone, cod_area_telefono_i character varying, 
cod_area_celular_i character varying, celular_i character varying, cod_area_tel_laboral_i character varying, 
tel_laboral_i character varying)
  RETURNS integer AS
$BODY$
  begin 	 
	  
  insert into afi_domicilio (
  cuil_titular,
  inte,
  vigen_desde,
  domi_tipo,
  calle,
  piso,
  depto,
  oficina,
  postal_codi,
  barrio,
  telefono,
  observaciones,
  domi_val,
  alta_fecha,
  alta_usr,
  modi_fecha,
  modi_usr,
  provincia,
  localidad,
  numero,
  baja_fecha,
  cod_area_telefono,
  cod_area_celular,
  celular,
  cod_area_tel_laboral, 
  tel_laboral  
  ) values (
  cuil_tit_d,
  inte_d,
  vigen_fecha,
  domi_tipo_i,
  calle_i,
  piso_i,
  depto_i,
  oficina_i,
  postal_codi_i,
  barrio_i,
  telefono_i,
  observaciones_i,
  domi_val_i,
  LOCALTIMESTAMP,
  alta_usr_i,
  LOCALTIMESTAMP,
  modi_usr_i,
  provincia_i,
  localidad_i,
  numero_i,
  baja_fecha_i,
  cod_area_telefono_i,
  cod_area_celular_i,
  celular_i,
  cod_area_tel_laboral_i,
  tel_laboral_i);  
  return 1;
  end;  
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
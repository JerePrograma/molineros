CREATE OR REPLACE FUNCTION delete_afiliado(cuil_v character, inte_v integer, motivo_baja_v integer, baja_fecha_v date, baja_usr_v character)
  RETURNS integer AS
$BODY$
declare parentesco_v integer;
declare result_baja_cascada integer;
BEGIN

parentesco_v=id_parentesco_sss from afiliado where cuil_titular=$1 and inte=$2;

if parentesco_v= 0 /*TITULAR*/ THEN
	result_baja_cascada=baja_cascada(cuil_v, inte_v, baja_fecha_v , motivo_baja_v, baja_usr_v);
ELSE 
	--GUARDO HISTORICO DE ESTADOS AFILIADO
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
		    pres_ssalud_fecha, localtimestamp, baja_usr_v, modi_fecha, modi_usr, 
		    baja_fecha, baja_usr, discapacitado, docu_numero, nacionalidad, 
		    aportante_titular, nro_afiliado, id_motivo_baja, id_ospim_baja_fecha, id_uoma_baja_fecha, id_amtima_baja_fecha, 'BPA'
	from afiliado
	where cuil_titular=cuil_v
	and inte = inte_v;

	update afiliado 
	set baja_fecha = baja_fecha_v, 
	    baja_usr = baja_usr_v,
	    modi_fecha = current_timestamp,
	    modi_usr = baja_usr_v,
	    id_motivo_baja= motivo_baja_v,
	    id_ospim_baja_fecha = baja_fecha_v,
            id_uoma_baja_fecha = baja_fecha_v,
            id_amtima_baja_fecha = baja_fecha_v
	where 
	    cuil_titular = cuil_v
	    and inte = inte_v;

	update afi_domicilio 
	set baja_fecha = baja_fecha_v, 
	    baja_usr = baja_usr_v,
	    modi_fecha = current_timestamp,
	    modi_usr = baja_usr_v 
	where cuil_titular = cuil_v 
	      and inte = inte_v 
	      and baja_fecha is null;

	--BAJA SITU LABORAL
	update afi_situ_laboral
	set fecha_egre=baja_fecha_v,
	    modi_usr=baja_usr_v,
	    modi_fecha=current_timestamp,
	    id_motivo_baja=motivo_baja_v
	where cuil_titular=cuil_v
	and inte=inte_v
	and inte_v > 0
	and (fecha_egre is null or fecha_egre>current_timestamp);      
END IF;


return  1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
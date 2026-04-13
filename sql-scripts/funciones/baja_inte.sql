CREATE OR REPLACE FUNCTION baja_inte(cuil_p character varying, inte_p integer, fecha_egreso_p date, motivo_baja integer, username character varying)
  RETURNS integer AS
$BODY$
declare fecha_hoy timestamp;
BEGIN

fecha_hoy=current_timestamp;

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
            pres_ssalud_fecha, fecha_hoy, username, modi_fecha, modi_usr, 
            baja_fecha, baja_usr, discapacitado, docu_numero, nacionalidad, 
            aportante_titular, nro_afiliado, id_motivo_baja, id_ospim_baja_fecha, id_uoma_baja_fecha, id_amtima_baja_fecha, 'BPA'
from afiliado
where cuil_titular=cuil_p
and inte=inte_p;


update afiliado
set baja_fecha=fecha_egreso_p, 
    baja_usr=username,
    modi_fecha=fecha_hoy, 
    modi_usr=username, 
    id_motivo_baja=motivo_baja
where cuil_titular=cuil_p
and inte=inte_p;
--and (baja_fecha is null or baja_fecha > fecha_hoy); LO SACO EL 10/9 porque no está funcionando con esta condición


--ACTUALIZO DOMICILIOS
update afi_domicilio
set baja_fecha=fecha_egreso_p,
    baja_usr=username
where cuil_titular=cuil_p
and inte=inte_p
and (baja_fecha is null or baja_fecha > fecha_hoy);


RETURN 1;	

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
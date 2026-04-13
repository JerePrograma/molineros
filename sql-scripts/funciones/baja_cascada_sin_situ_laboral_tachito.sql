CREATE OR REPLACE FUNCTION baja_cascada_sin_situ_laboral_tachito(cuil_p character varying, inte_p integer, fecha_egreso_p date, motivo_baja integer, username character varying)
  RETURNS integer AS
$BODY$
declare fecha_hoy timestamp;
declare categoria integer;
declare id_plan_aux bigint;

BEGIN

fecha_hoy=current_timestamp;
categoria=distinct id_categoria from afi_situ_laboral where cuil_titular = cuil_p and inte  =inte_p and (baja_fecha is null or baja_fecha > fecha_hoy) and id_categoria=11;
/*DESEMPLEO, DESPIDO; RENUNCIA; FALLECIMIENTO (no lo vamos a considerar x esta baja tachito)
IF ((motivo_baja=3 or motivo_baja=21 or motivo_baja=1) and categoria not in (12,0)) or (motivo_baja=2 and categoria=11)  then
  baja_futura=1;
END IF;
IF baja_futura=1 then
	fecha_baja_futura=fecha_egreso_p + interval '3 months';
ELSE 
	fecha_baja_futura=fecha_egreso_p;
END IF;
*/
--GUARDO HISTORICO DE ESTADOS AFILIADO
INSERT INTO afi_estados_histo(
            cuil_titular, inte, id_ospim, id_uoma, id_amtima, apellido, nombre, 
            documento_tipo, sexo, cuil, naci_fecha, id_estado_civil_sss, id_parentesco_sss, 
            ingre_fecha, id_seccional, anterior_os, vigen_fecha, observaciones, 
            pres_ssalud_fecha, alta_fecha, alta_usr, modi_fecha, modi_usr, 
            baja_fecha, baja_usr, discapacitado, docu_numero, nacionalidad, 
            aportante_titular, nro_afiliado, id_motivo_baja,
            id_ospim_baja_fecha, id_uoma_baja_fecha, id_amtima_baja_fecha, descripcion_operacion )
select cuil_titular, inte, id_ospim, id_uoma, id_amtima, apellido, nombre, 
            documento_tipo, sexo, cuil, naci_fecha, id_estado_civil_sss, id_parentesco_sss, 
            ingre_fecha, id_seccional, anterior_os, vigen_fecha, observaciones, 
            pres_ssalud_fecha, fecha_hoy, username, modi_fecha, modi_usr, 
            baja_fecha, baja_usr, discapacitado, docu_numero, nacionalidad, 
            aportante_titular, nro_afiliado, id_motivo_baja, id_ospim_baja_fecha, id_uoma_baja_fecha, id_amtima_baja_fecha, 'BTO' 
from afiliado
where cuil_titular=cuil_p
and (baja_fecha is null or baja_fecha>current_date);

update afiliado
	set baja_fecha=fecha_egreso_p, 
	    baja_usr=username,
	    modi_fecha=fecha_hoy, 
	    modi_usr=username, 
	    id_motivo_baja=motivo_baja
where cuil_titular=cuil_p
and (baja_fecha is null or baja_fecha > fecha_hoy);

--ACTUALIZO DOMICILIOS
update afi_domicilio
set baja_fecha=fecha_egreso_p,
    baja_usr=username
where cuil_titular=cuil_p
and (baja_fecha is null or baja_fecha > fecha_hoy);

--AJUSTE DE PLANES y APORTES
id_plan_aux=id from afi_plan where cuil_titular=cuil_p and inte=0 
          and (vigen_hasta is null or vigen_hasta>fecha_hoy) 
          and alta_fecha = (select max(alta_fecha) from  afi_plan 
			    where cuil_titular=cuil_p and inte=0 and (baja_fecha is null or baja_fecha>fecha_hoy));

	update afi_plan set vigen_hasta = fecha_egreso_p, 
			    modi_usr = username, 
			    modi_fecha = fecha_hoy,
			    id_motivo_baja = motivo_baja
			    where id = id_plan_aux;
			    
	update afi_aportes set fecha_egre = fecha_egreso_p,
			       modi_usr = username, 
			       modi_fecha = fecha_hoy,
			       id_motivo_baja = motivo_baja
			       where id_plan_serial = id_plan_aux;		
				


--Actualizo Tercerizadora
update afi_tercerizadora_servicio 
set fecha_fin_pres=fecha_egreso_p,
modi_fecha=fecha_hoy,
modi_usr=username
where cuil_titular=cuil_p
and inte=inte_p
and baja_fecha is null
and (fecha_fin_pres is null or fecha_fin_pres > fecha_egreso_p);
/*
--SI ES UN FALLECIDO, EL FALLECIDO SE DA DE BAJA A LA FECHA ACTUAL Y SE CARGA EL EMPLEADOR "SUBSIDIO" EL RESTO QUEDA CON COBERTURA X 90 d
if motivo_baja=2 and categoria=11 then
	update afiliado
	set baja_fecha=fecha_egreso_p,
	baja_usr=username,
	id_motivo_baja=motivo_baja
	where cuil_titular=cuil_p
	and (baja_fecha is null or baja_fecha>current_date)
	and inte=inte_p;		
	--INSERTO EL SUBSIDIO
	INSERT INTO afi_situ_laboral(
            cuil_titular, inte, cuit, sucursal, fecha_ingre,  id_revista, 
            fecha_egre, alta_fecha, alta_usr,  id_motivo_baja, id_categoria)
	VALUES (cuil_p, inte_p, '99999999999', '000', fecha_egreso_p, 3, fecha_baja_futura, fecha_hoy,username,2,0);

end if;
*/
RETURN 1;	

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
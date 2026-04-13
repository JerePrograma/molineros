CREATE OR REPLACE FUNCTION reincorporarafiliado(cuil_p character varying, inte_p integer, fecha_ingreso_p date, fecha_egreso_p date, planes_laborales integer, continuidad integer, username character varying, id_motivo_baja_menor_edad integer)
  RETURNS integer AS
$BODY$

declare fecha_hoy timestamp;
declare ingre_fecha_titular date;

declare n_plan integer;
declare n_plan_omint integer;
declare fecha_n_ospim timestamp;
declare fecha_n_amtima timestamp;
declare fecha_n_uoma timestamp;
declare id_ospim_p integer;
declare id_amtima_p integer;
declare id_uoma_p integer;
declare cont integer;
declare tiene_terce integer;
declare usr_baja character varying;
BEGIN

fecha_hoy = current_timestamp;
cont = continuidad;

case when fecha_egreso_p is not null then usr_baja = username; else usr_baja=null; end case;

RAISE INFO 'CONTINUIDAD: %',cont;
RAISE INFO 'RECUPERA PLAN: %', planes_laborales;
fecha_n_ospim = a1.id_ospim_baja_fecha
		from afi_estados_histo a1 
		where a1.cuil_titular = cuil_p and a1.inte = inte_p and a1.alta_fecha = 
			(select max(a2.alta_fecha) from afi_estados_histo a2 where a2.cuil_titular = cuil_p and a2.inte = inte_p) limit 1;

	fecha_n_uoma = a1.id_uoma_baja_fecha
			from afi_estados_histo a1 
			where a1.cuil_titular = cuil_p and a1.inte = inte_p and a1.alta_fecha = 
				(select max(a2.alta_fecha) from afi_estados_histo a2 where a2.cuil_titular = cuil_p and a2.inte = inte_p) limit 1;
				
	fecha_n_amtima =  a1.id_amtima_baja_fecha
			from afi_estados_histo a1 
			where a1.cuil_titular = cuil_p and a1.inte = inte_p and a1.alta_fecha = 
				(select max(a2.alta_fecha) from afi_estados_histo a2 where a2.cuil_titular = cuil_p and a2.inte = inte_p) limit 1;

--GUARDO AFILIADO EN HISTORICO DE ESTADOS AFILIADO.
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
            aportante_titular, nro_afiliado, id_motivo_baja, id_ospim_baja_fecha, id_uoma_baja_fecha, id_amtima_baja_fecha, 'REI' 
from afiliado
where cuil_titular=cuil_p and inte=inte_p;

/*Insertamos movimiento historico para la Super*/
INSERT INTO informes.fechas_informe_super(
            cuil, fecha_alta, fecha_baja, fecha_mod, cuil_titular, inte, fecha_proceso)  
SELECT cuil, fecha_pres_super, fecha_baja_super, fecha_mod_super, cuil_titular, inte, current_date
  FROM afiliado WHERE cuil_titular=cuil_p and inte=inte_p;
/* Blanqueamos datos para presentar nuevamente a la SSS */
UPDATE afiliado set fecha_pres_super=null, fecha_baja_super=null, fecha_mod_super=null 
WHERE cuil_titular=cuil_p and inte=inte_p;

--NO CONTINUIDAD
IF cont = 0 then
	--ACTUALIZO AL AFILIADO
	update afiliado 
	set ingre_fecha = current_date, 
	modi_fecha = 
	fecha_hoy, 
	modi_usr = username, 
	vigen_fecha = fecha_ingreso_p, 
	baja_fecha = fecha_egreso_p, 
	baja_usr = usr_baja,
	id_motivo_baja = id_motivo_baja_menor_edad,
	fecha_pres_super=null,
	fecha_baja_super=null		
	where cuil_titular=cuil_p and inte=inte_p;

ELSE --CONTINUIDAD
	--ACTUALIZO AL AFILIADO
	update afiliado 
	set 	modi_fecha = fecha_hoy, 
		modi_usr = username, 
		baja_fecha = fecha_egreso_p, 	
		baja_usr = usr_baja,
		id_motivo_baja = id_motivo_baja_menor_edad, 
		id_ospim_baja_fecha = fecha_n_ospim, 
		id_uoma_baja_fecha = fecha_n_uoma, 
		id_amtima_baja_fecha = fecha_n_amtima,
		fecha_pres_super=null,
		fecha_baja_super=null	
	where cuil_titular=cuil_p and inte=inte_p;

END IF;

--ACTUALIZO DOMICILIOS
IF fecha_ingreso_p is not null then
	update afi_domicilio set baja_fecha = null, baja_usr = null, modi_fecha = fecha_hoy, modi_usr = username, vigen_desde = fecha_ingreso_p
	where cuil_titular = cuil_p
	and inte = inte_p
	and alta_fecha = (select max(a2.alta_fecha) from afi_domicilio a2 where a2.cuil_titular = cuil_p and a2.inte = inte_p);
else 
	update afi_domicilio set baja_fecha = null, baja_usr = null, modi_fecha = fecha_hoy, modi_usr = username
	where cuil_titular = cuil_p
	and inte = inte_p
	and alta_fecha = (select max(a2.alta_fecha) from afi_domicilio a2 where a2.cuil_titular = cuil_p and a2.inte = inte_p);
end if;

RETURN 1;	
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
CREATE OR REPLACE FUNCTION edita_situ_laboral(
cuil_p character varying, 
inte_p integer, 
cuit_p character varying, 
sucu_p character varying, 
revista_p integer, 
categoria_p integer, 
fecha_ingreso_p date, 
fecha_egreso_p date, 
username_p character varying, 
motivo_baja_p integer, 
escala_salarial_p character varying, 
nueva_fecha_ingreso_p date, 
fecha_baja_p date)  
RETURNS integer AS  
$BODY$
BEGIN
	UPDATE afi_situ_laboral
	    set    
	    fecha_ingre=nueva_fecha_ingreso_p,
	    id_revista=revista_p,
	    id_categoria=categoria_p,    
	    escala_salarial=escala_salarial_p,
	    fecha_egre=fecha_egreso_p,
	    id_motivo_baja=motivo_baja_p,
	    baja_fecha=fecha_baja_p,
	    modi_usr=username_p,
	    modi_fecha=current_timestamp    
	    where cuil_titular=cuil_p
	    and inte=inte_p
	    and cuit=cuit_p
	    and sucursal=sucu_p
	    and fecha_ingre=fecha_ingreso_p;

	return 1;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;

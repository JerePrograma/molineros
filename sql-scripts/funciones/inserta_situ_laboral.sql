--DROP FUNCTION inserta_situ_laboral(character varying, integer, character varying, character varying, integer, integer, date, date, character varying, character varying);	
--DROP FUNCTION inserta_situ_laboral(character varying, integer, character varying, character varying, integer, integer, date, date, character varying);
drop FUNCTION inserta_situ_laboral(
cuil_p character varying, 
inte_p integer, 
cuit_p character varying, 
sucu_p character varying, 
situ_revista integer, 
categoria integer, 
fecha_ingreso_p date, 
fecha_egreso_p date, 
username character varying, 
escala_salarial_p character varying,
id_motivo_baja_p integer);

CREATE OR REPLACE FUNCTION inserta_situ_laboral(
cuil_p character varying, 
inte_p integer, 
cuit_p character varying, 
sucu_p character varying, 
situ_revista integer, 
categoria integer, 
fecha_ingreso_p date, 
fecha_egreso_p date, 
username character varying, 
escala_salarial_p character varying,
id_motivo_baja_p integer)
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
BEGIN
	
	if exists (select 1 from afi_situ_laboral where cuil_titular = cuil_p and inte = inte_p and  cuit = cuit_p and  sucursal = sucu_p and  fecha_ingre = fecha_ingreso_p)  then
		update afi_situ_laboral set id_revista = situ_revista, id_categoria = categoria, fecha_egre = fecha_egreso_p, modi_usr = username, modi_fecha = current_timestamp, escala_salarial = escala_salarial_p, baja_fecha =null, baja_usr = null
		 where cuil_titular = cuil_p and inte = inte_p and  cuit = cuit_p and  sucursal = sucu_p and  fecha_ingre = fecha_ingreso_p and baja_fecha is not null;
	else 
	    INSERT INTO afi_situ_laboral(cuil_titular, inte, cuit, sucursal, id_revista, id_categoria, fecha_ingre, fecha_egre, alta_usr, alta_fecha, escala_salarial, id_motivo_baja)
	    VALUES (cuil_p, inte_p, cuit_p, sucu_p, situ_revista, categoria, fecha_ingreso_p,fecha_egreso_p,username,current_timestamp, escala_salarial_p, id_motivo_baja_p);
    end if;
    
    update afiliado
    set aportante_titular=1
    where cuil_titular=cuil_p
    and inte=inte_p;
    
    return 1;
END;
$BODY$;
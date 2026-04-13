drop FUNCTION inserta_tercerizadora(cuil_p character varying,
 inte_p integer,
 id_tercerizadora_p character varying,
 fecha_ingreso_p date,
 fecha_egreso_p date,
 username_p character varying) ;
 
CREATE OR REPLACE FUNCTION inserta_tercerizadora(cuil_p character varying,
 inte_p integer,
 id_tercerizadora_p character varying,
 fecha_ingreso_p date,
 fecha_egreso_p date,
 username_p character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
BEGIN

	if exists (select 1 from afi_tercerizadora_servicio where id_tercerizadora = id_tercerizadora_p and cuil_titular = cuil_p and inte = inte_p and fecha_inicio_pres = fecha_ingreso_p) then
		update  afi_tercerizadora_servicio set baja_fecha = null , baja_usr = null, modi_usr = username_p, modi_fecha  = localtimestamp , fecha_fin_pres = fecha_egreso_p where id_tercerizadora = id_tercerizadora_p and cuil_titular = cuil_p and inte = inte_p and fecha_inicio_pres = fecha_ingreso_p and baja_fecha is not null;
	else
		INSERT INTO afi_tercerizadora_servicio(cuil_titular, inte, id_tercerizadora, fecha_inicio_pres, fecha_fin_pres, alta_usr, alta_fecha)
	    VALUES (cuil_p, inte_p, id_tercerizadora_p, fecha_ingreso_p, fecha_egreso_p, username_p, current_timestamp);
	end if;
  
    return 1;
END;
$BODY$;

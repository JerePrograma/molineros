CREATE OR REPLACE FUNCTION actualiza_afi_plan_y_aportes(vigen_desde_p date, id_motivo_baja_p integer, vigen_hasta_p date, id_plan_serial_p double precision, usr_p character varying)
  RETURNS integer AS
$BODY$
  begin

	update afi_plan set vigen_desde = vigen_desde_p, vigen_hasta = vigen_hasta_p, id_motivo_baja = id_motivo_baja_p, 
	 modi_fecha = localtimestamp, modi_usr = usr_p where id = id_plan_serial_p;

	update afi_aportes 
		set fecha_ingre = vigen_desde_p, 
		fecha_egre = vigen_hasta_p, 
		id_motivo_baja = id_motivo_baja_p, 
		modi_fecha = localtimestamp, 
		modi_usr = usr_p
		where id_plan_serial = id_plan_serial_p;
	
  return 1;
  end;  
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
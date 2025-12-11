CREATE OR REPLACE FUNCTION borra_afiliado_plan(id_plan_serial_p double precision, user_p character varying)
  RETURNS integer AS
$BODY$       
begin
    --delete from afi_plan where id = id_plan_serial_p;
    --delete from afi_aportes where id_plan_serial = id_plan_serial_p;
    update afi_plan set baja_usr = user_p, baja_fecha = current_timestamp where id = id_plan_serial_p;
    update afi_aportes set baja_usr = user_p, baja_fecha = current_timestamp where id_plan_serial = id_plan_serial_p;
    return 1;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;
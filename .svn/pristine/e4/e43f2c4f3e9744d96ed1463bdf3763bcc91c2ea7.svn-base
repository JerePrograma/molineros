CREATE OR REPLACE FUNCTION correo.actualiza_estado_paquete(id_pa integer, estado_p character varying, username character varying)
  RETURNS integer AS
$BODY$       
begin
    update correo.paquete p
    set baja_fecha = localtimestamp,
    baja_usr = username,
    estado = estado_p
    where p.id=id_pa;
    return 1;

   end;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
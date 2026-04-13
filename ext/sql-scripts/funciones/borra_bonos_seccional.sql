
CREATE OR REPLACE FUNCTION borra_bonos_seccional(id_envio_v integer, username character varying)
  RETURNS integer AS
$BODY$
begin

INSERT INTO bonos_seccional_historico(tipo_bono, id_seccional, fecha_envio, nro_bono, fecha_rendido,id_envio, alta_fecha, alta_usr, baja_fecha, baja_usr)
select tipo_bono, id_seccional, fecha_envio, nro_bono, fecha_rendido, id_envio, alta_fecha, alta_usr, current_date, username
from bonos_seccional where id_envio=id_envio_v;

delete from bonos_seccional where id_envio=id_envio_v;

return 1;

END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
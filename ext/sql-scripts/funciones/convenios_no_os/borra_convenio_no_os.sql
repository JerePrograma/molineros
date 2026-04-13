-- Function: borra_convenio_no_os(integer, character varying)

-- DROP FUNCTION borra_convenio_no_os(integer, character varying);

CREATE OR REPLACE FUNCTION borra_convenio_no_os(p_convenio_id integer, p_usuario character varying)
  RETURNS integer AS
$BODY$
declare res integer;
  begin
		update convenio_no_os 
		set  baja_fecha = LOCALTIMESTAMP,
		baja_usr = p_usuario
		where id = p_convenio_id;
		
		update convenio_no_os_pagos set baja_fecha = LOCALTIMESTAMP,
		baja_usr = p_usuario
		where convenio_id = p_convenio_id;
		
		update convenio_no_os_relacion set baja_fecha = LOCALTIMESTAMP,
		baja_usr = p_usuario
		where convenio_id = p_convenio_id;
		
		update convenio_actas_no_os set baja_fecha = LOCALTIMESTAMP,
		baja_usr = p_usuario
		where convenio_id = p_convenio_id;

	return 1;
  end;  
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION borra_convenio_no_os(integer, character varying)
  OWNER TO postgres;


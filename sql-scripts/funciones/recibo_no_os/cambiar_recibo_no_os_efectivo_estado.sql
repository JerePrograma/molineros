-- Function: cambiar_recibo_no_os_efectivo_estado(integer, integer, character varying)

-- DROP FUNCTION cambiar_recibo_no_os_efectivo_estado(integer, integer, character varying);

CREATE OR REPLACE FUNCTION cambiar_recibo_no_os_efectivo_estado(p_recibo_ingreso_id integer, p_estado_id integer, p_user character varying)
  RETURNS integer AS
$BODY$
    declare res integer;
BEGIN
	update recibo_no_os_ingresos set id_estado_efectivo = p_estado_id, modi_fecha = localtimestamp, modi_usr = p_user where id = p_recibo_ingreso_id;
	
return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION cambiar_recibo_no_os_efectivo_estado(integer, integer, character varying)
  OWNER TO postgres;


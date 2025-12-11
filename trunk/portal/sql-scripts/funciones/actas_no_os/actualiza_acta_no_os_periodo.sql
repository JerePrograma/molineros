CREATE OR REPLACE FUNCTION actualiza_acta_no_os_periodo(p_acta_periodo_id integer, p_interes numeric, p_usr character varying)
  RETURNS integer AS
$BODY$
BEGIN
	
update  acta_no_os_periodos set  
  interes = p_interes,
  modi_fecha = localtimestamp,
  modi_usr = p_usr
where id = p_acta_periodo_id;

return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE


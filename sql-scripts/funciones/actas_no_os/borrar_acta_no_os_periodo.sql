CREATE OR REPLACE FUNCTION borrar_acta_no_os_periodo(p_acta_periodo_id integer)
  RETURNS integer AS
$BODY$
BEGIN
delete from acta_no_os_periodos where id = p_acta_periodo_id;
    
return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION borrar_acta_no_os_periodo(integer)
  OWNER TO postgres;


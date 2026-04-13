CREATE OR REPLACE FUNCTION cerrar_acta_no_os(p_acta_id integer, p_usr character varying)
  RETURNS integer AS
$BODY$
BEGIN
update acta_no_os set acta_cerrada = true, cierre_usr = p_usr where id = p_acta_id;
    
return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE


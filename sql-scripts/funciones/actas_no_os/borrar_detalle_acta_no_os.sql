CREATE OR REPLACE FUNCTION borrar_detalle_acta_no_os(p_detalle_insp_id integer)
  RETURNS integer AS
$BODY$
BEGIN
delete from acta_no_os_detalle_inspectores where id = p_detalle_insp_id;
    
return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE


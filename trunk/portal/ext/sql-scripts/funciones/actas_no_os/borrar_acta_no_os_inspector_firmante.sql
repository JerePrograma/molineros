-- Function: borrar_acta_no_os_inspector_firmante(integer, integer)

-- DROP FUNCTION borrar_acta_no_os_inspector_firmante(integer, integer);

CREATE OR REPLACE FUNCTION borrar_acta_no_os_inspector_firmante(p_acta_id integer, p_inspector_id integer)
  RETURNS integer AS
$BODY$
BEGIN
delete from acta_no_os_inspector where id_acta = p_acta_id and  id_inspector =  p_inspector_id;
    
return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION borrar_acta_no_os_inspector_firmante(integer, integer)
  OWNER TO postgres;


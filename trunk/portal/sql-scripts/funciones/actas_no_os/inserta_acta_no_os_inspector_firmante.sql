-- Function: inserta_acta_no_os_inspector_firmante(integer, integer)

-- DROP FUNCTION inserta_acta_no_os_inspector_firmante(integer, integer);

CREATE OR REPLACE FUNCTION inserta_acta_no_os_inspector_firmante(p_acta_id integer, p_inspector_id integer)
  RETURNS integer AS
$BODY$
BEGIN
INSERT INTO acta_no_os_inspector(
            id_acta, id_inspector)
    VALUES (p_acta_id, p_inspector_id);

return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION inserta_acta_no_os_inspector_firmante(integer, integer)
  OWNER TO postgres;


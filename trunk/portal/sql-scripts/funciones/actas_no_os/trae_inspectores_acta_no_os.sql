CREATE OR REPLACE FUNCTION trae_inspectores_acta_no_os(id integer)
  RETURNS text AS
$BODY$
 select array_to_string(array(SELECT nombre 
			      FROM acta_no_os_inspector ai, inspector i
			      where id_acta=$1
			      and ai.id_inspector=i.id), ' - ')	
 
$BODY$
  LANGUAGE sql VOLATILE
  COST 100;
ALTER FUNCTION trae_inspectores_acta_no_os(integer)
  OWNER TO postgres;


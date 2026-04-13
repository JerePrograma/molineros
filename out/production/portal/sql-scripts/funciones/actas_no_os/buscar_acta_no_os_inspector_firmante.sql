-- Function: buscar_acta_no_os_inspector_firmante(integer)

-- DROP FUNCTION buscar_acta_no_os_inspector_firmante(integer);

CREATE OR REPLACE FUNCTION buscar_acta_no_os_inspector_firmante(IN p_id integer)
  RETURNS TABLE(id integer, nombre character varying, alta_fecha timestamp without time zone, alta_usr character varying, modi_fecha timestamp without time zone, modi_usr character varying, baja_fecha timestamp without time zone, baja_usr character varying) AS
$BODY$
	select  i.id,
  i.nombre,
  i.alta_fecha,
  i.alta_usr,
  i.modi_fecha,
  i.modi_usr,
  i.baja_fecha,
  i.baja_usr
	from acta_no_os_inspector ai
	inner join inspector i
	on ai.id_acta = $1
	and ai.id_inspector = i.id
	
	$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION buscar_acta_no_os_inspector_firmante(integer)
  OWNER TO postgres;


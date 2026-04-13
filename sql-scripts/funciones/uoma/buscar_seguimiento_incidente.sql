create type  buscar_seguimiento_incidente as(fecha timestamp without time zone, detalle text)

CREATE OR REPLACE FUNCTION buscar_seguimiento_incidente(id_incidente_v integer)
  RETURNS SETOF buscar_seguimiento_incidente AS
$BODY$
BEGIN 
	return query
	select fecha, seguimiento_incidente
	from uoma.incidente_unidad_operativa_detalle i	
	where id_incidente=id_incidente_v;
	
END; 				    
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION buscar_incidente(integer) OWNER TO postgres;


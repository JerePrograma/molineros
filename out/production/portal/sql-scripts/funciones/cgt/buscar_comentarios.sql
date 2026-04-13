CREATE OR REPLACE FUNCTION buscar_comentarios(id_organismo integer, origen_p integer)
  RETURNS SETOF result_comentario AS
$BODY$
BEGIN

if origen_p=1 then 
	return query
	SELECT co.fecha, co.comentario
	FROM organismo_comentario co
	where co.id_organismo=$1
	and baja_fecha is null;
else if origen_p=2 then
	return query
	SELECT co.fecha, co.comentario
	FROM area_comentario co
	where co.id_area=$1
	and baja_fecha is null;
end if;
end if;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE


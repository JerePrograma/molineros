CREATE OR REPLACE FUNCTION buscar_lineas_trabajo(id_organismo integer, origen_p integer)
  RETURNS SETOF result_lineas_trabajo AS
$BODY$
BEGIN
if origen_p=1 then
	return query
	SELECT ol.tipo_linea, ol.linea           
	FROM organismo_linea ol
	where ol.id_organismo=$1
	and baja_fecha is null;
else if origen_p=2 then 
	return query
	SELECT ol.tipo_linea, ol.linea           
	FROM area_linea ol
	where ol.id_area=$1
	and baja_fecha is null;
end if;
end if;	

end;
$BODY$
  LANGUAGE plpgsql VOLATILE


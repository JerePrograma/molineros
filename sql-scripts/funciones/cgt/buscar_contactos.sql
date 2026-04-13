create type result_contactos as (cargo varchar, email varchar, telefono varchar, apellido varchar, nombre varchar, tratamiento varchar)

CREATE OR REPLACE FUNCTION buscar_contactos(id_organismo integer, origen_p integer)
  RETURNS SETOF result_contactos AS
$BODY$
BEGIN  
if origen_p=1 then
	return query
	SELECT c.cargo, c.email, c.telefono, c.apellido, c.nombre, c.tratamiento
	FROM organismo_contacto co, contacto c
	where co.id_organismo=$1
	and c.id_contacto=co.id_contacto
	and co.baja_fecha is null;
else if	origen_p=2 then 
	return query
	SELECT c.cargo, c.email, c.telefono, c.apellido, c.nombre, c.tratamiento
	FROM area_contacto co, contacto c
	where co.id_area=$1
	and c.id_contacto=co.id_contacto
	and co.baja_fecha is null;
end if;
end if;	

END;
$BODY$
  LANGUAGE plpgsql VOLATILE

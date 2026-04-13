CREATE OR REPLACE FUNCTION tiene_imagen_afiliado(cuil_titular_v character varying)
  RETURNS SETOF tiene_imagen_afiliado_result AS
$BODY$
BEGIN
return query
select folderid, name, (select count(*) from dlFileEntry where title like ''||$1||'%' group by substring(title,1,11)) as tiene_imagen_afiliado 
from dlFileEntry where title like ''||cuil_titular_v||'%';
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
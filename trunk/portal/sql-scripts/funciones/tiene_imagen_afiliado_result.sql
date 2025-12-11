database lportal;
-- Function: tiene_imagen_afiliado(character varying)

/*create type tiene_imagen_afiliado_result as (folderid bigint, name varchar, tiene_imagen_afiliado bigint)*/

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
  COST 100;
ALTER FUNCTION tiene_imagen_afiliado(character varying) OWNER TO postgres;
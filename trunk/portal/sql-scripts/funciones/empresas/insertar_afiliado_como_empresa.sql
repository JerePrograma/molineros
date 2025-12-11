 CREATE OR REPLACE FUNCTION insertar_afiliado_como_empresa(
 p_cuil_titular character varying,
 p_usr character varying) 
RETURNS character varying
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom character varying;
BEGIN
	

insert into empresa (cuit, sucursal, razon_soc, alta_fecha, alta_usr,modi_fecha,modi_usr, afip, nombre_fantasia, vigen_fecha)
select p_cuil_titular, '000', apellido || ' ' || nombre, localtimestamp, p_usr, localtimestamp, p_usr,false ,apellido || ' ' || nombre, localtimestamp
from afiliado where cuil_titular = p_cuil_titular and inte = 0;
 
resultDom=apellido || ' ' || nombre from afiliado where cuil_titular = p_cuil_titular and inte = 0;

return resultDom;
END;
$BODY$;

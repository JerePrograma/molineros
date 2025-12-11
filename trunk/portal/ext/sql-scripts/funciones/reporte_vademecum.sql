/*create type reporte_vademecum_result as (
droga character varying(100),
  nombre character varying(100),
  presentacion character varying(100),
  laboratorio character varying(100),
  accion character varying(100),
  troquel numeric,
  registro numeric ,
  porc_ospim numeric,
  porc_amtima numeric,
  porc_sssalud numeric,
  pmoe_n numeric,
  alta_fecha timestamp without time zone,
  alta_usr character varying,
  modi_fecha timestamp without time zone,
  baja_fecha timestamp without time zone,
  baja_usr character varying,
  modi_usr character varying,
  fecha_vig timestamp without time zone 
  )*/

 CREATE OR REPLACE FUNCTION reporte_vademecum()
  RETURNS SETOF reporte_vademecum_result AS
$BODY$ 
BEGIN
return query 
select  droga, 
        nombre, 
        presentacion, 
        laboratorio, 
        accion, 
        troquel, 
        registro, 
        porc_ospim, 
        porc_amtima ,
	porc_sssalud,
	pmoe_n,
	alta_fecha,
	alta_usr,
	modi_fecha,
	baja_fecha,
	baja_usr,
	modi_usr,
	fecha
from vademecum order by droga;
END;
$BODY$
 LANGUAGE 'plpgsql' VOLATILE

CREATE OR REPLACE FUNCTION trae_fechas_cierre_contable_asientos() 
RETURNS TABLE(
	fecha_cierre timestamp without time zone,
	observacion character varying,
	alta_fecha timestamp without time zone,
	alta_usr character varying,
	modi_fecha timestamp without time zone,
	modi_usr character varying,
	baja_fecha timestamp without time zone,
	baja_usr character varying)
    LANGUAGE sql
    AS $BODY$

select fecha_cierre ,
	observacion ,
	alta_fecha ,
	alta_usr ,
	modi_fecha,
	modi_usr ,
	baja_fecha,
	baja_usr 
 from cierre_periodo_contable_asientos
 where baja_fecha is null
 order by fecha_cierre desc;

$BODY$;

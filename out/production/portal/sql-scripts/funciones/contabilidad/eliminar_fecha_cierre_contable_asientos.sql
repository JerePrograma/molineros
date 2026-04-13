
CREATE OR REPLACE FUNCTION eliminar_fecha_cierre_contable_asientos(
 p_fecha  timestamp without time zone,
 p_username character varying
) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
BEGIN
	update cierre_periodo_contable_asientos set baja_fecha = localtimestamp, baja_usr = p_username where cast(fecha_cierre as date) = cast(p_fecha as date) and baja_fecha is null;
return 1;
END;
$BODY$;

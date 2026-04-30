
CREATE OR REPLACE FUNCTION insertar_fecha_cierre_contable_gestion(
 p_fecha  timestamp without time zone,
 p_descripcion character varying,
 p_username character varying
) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
BEGIN
	insert into cierre_periodo_contable (fecha_cierre, observacion, alta_fecha, alta_usr, modi_fecha, modi_usr)
	values (cast(p_fecha as date), p_descripcion, localtimestamp,p_username, localtimestamp, p_username);
return 1;
END;
$BODY$;

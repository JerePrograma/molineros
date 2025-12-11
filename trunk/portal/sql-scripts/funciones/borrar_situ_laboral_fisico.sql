CREATE OR REPLACE FUNCTION borrar_situ_laboral_fisico(cuil_p character varying, inte_p integer)
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
BEGIN
    DELETE FROM afi_situ_laboral
    where cuil_titular=$1
    and inte=$2
    and baja_fecha is null;
    return 0;
END;
$BODY$;
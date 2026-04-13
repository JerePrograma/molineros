CREATE OR REPLACE FUNCTION borra_tratamiento_discapacidad(
 id_tratamiento integer,
 username character varying) 
RETURNS integer
    LANGUAGE plpgsql          
    AS $BODY$       
begin
    update tratamiento_discapacidad td
    set baja_fecha = localtimestamp,
    baja_usr = $2
    where td.id_tratamiento=$1;
    return 1;
    end
$BODY$;
--

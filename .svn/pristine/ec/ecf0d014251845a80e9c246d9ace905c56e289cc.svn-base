CREATE OR REPLACE FUNCTION borra_contrato_detalle(
 id_contrato integer,
 username character varying) 
RETURNS integer
    LANGUAGE plpgsql          
    AS $BODY$       
begin
    update contrato c
    set baja_fecha = localtimestamp,
    baja_usr = $2
    where c.id_contrato=$1;
    return 1;
    ends
$BODY$;
--
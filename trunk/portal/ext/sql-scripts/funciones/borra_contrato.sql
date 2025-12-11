CREATE OR REPLACE FUNCTION borra_contrato_detalle(
 id_contrato_detalle integer,
 username character varying) 
RETURNS integer
    LANGUAGE plpgsql          
    AS $BODY$       
begin
    update contrato_detalle cd
    set baja_fecha = localtimestamp,
    baja_usr = $2
    where cd.id_contrato_detalle=$1;
    return 1;
    end
$BODY$;
--
CREATE OR REPLACE FUNCTION correo.borra_item_correspondencia(
 id_item_corr integer,
 username character varying) 
 
RETURNS integer
    LANGUAGE plpgsql          
    AS $BODY$       
begin
    update correo.item_correspondencia c
    set baja_fecha = localtimestamp,
    baja_usr = username
    where c.id=id_item_corr;
    return 1;

   end;
$BODY$;


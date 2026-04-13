CREATE OR REPLACE FUNCTION borra_prestacion_farmacia(
 id_prestacion integer,
 username character varying) 
RETURNS integer
    LANGUAGE plpgsql          
    AS $BODY$
    declare importe_total numeric(10,2);    
begin

    update medicamento_reintegro_farmacia
    set baja_fecha = localtimestamp,
    baja_usr = $2
    where id=$1;

    importe_total = sum(rp.cantidad * (rp.monto_ospim + rp.monto_amtima)) from medicamento_reintegro_farmacia rp where rp.id = $1;
    update lista_reintegro_farmacia_pago_detalle set importe = importe_total where id_reintegro = $1;

    return 1;
    end
$BODY$;
--

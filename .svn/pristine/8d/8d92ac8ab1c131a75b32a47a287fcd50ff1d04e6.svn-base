CREATE OR REPLACE FUNCTION anular_orden_pago_amtima(p_orden_pago_amtima_id integer, p_date date,
 p_usr character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
BEGIN

update orden_pago_amtima set baja_fecha = p_date, baja_usr = p_usr, 
							observaciones = (case when (position ('- ANULADA' in observaciones) <> 0) then observaciones else (coalesce(observaciones, '')  || ' - ANULADA') end) 
							where id_orden_pago = p_orden_pago_amtima_id;

update orden_pago_amtima_lista_reintegros set baja_fecha = p_date, baja_usr = p_usr where id_orden_pago_amtima = p_orden_pago_amtima_id;

update comprobante_amtima c set baja_fecha = p_date , baja_usr = p_usr
from comprobante_orden_pago_amtima copo
where c.cuit = copo.cuit
and c.compro_nro = copo.compro_nro
and c.compro_tipo = copo.compro_tipo
and c.compro_sucu = copo.compro_sucu
and c.compro_letra = copo.compro_letra
and c.id_punto_venta = copo.id_punto_venta
and copo.id_orden_pago_amtima = p_orden_pago_amtima_id
and c.compro_tipo = 'REI'
and exists (select 1 from orden_pago_amtima_lista_reintegros where id_orden_pago_amtima = copo.id_orden_pago_amtima);

return 1;
END;
$BODY$;
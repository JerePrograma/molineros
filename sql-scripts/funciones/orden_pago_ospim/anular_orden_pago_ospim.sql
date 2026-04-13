drop FUNCTION anular_orden_pago_ospim(p_orden_pago_ospim_id integer,
 p_usr character varying) ;
 
CREATE OR REPLACE FUNCTION anular_orden_pago_ospim(p_orden_pago_ospim_id integer, p_date date, p_usr character varying)
  RETURNS integer AS
$BODY$
BEGIN

update orden_pago_ospim set baja_fecha = p_date, baja_usr = p_usr, concepto = concepto || ' - ANULADA' where id_orden_pago = p_orden_pago_ospim_id;

update orden_pago_ospim_liquidaciones set baja_fecha = p_date, baja_usr = p_usr where id_orden_pago_ospim = p_orden_pago_ospim_id;
update orden_pago_ospim_lista_reintegros set baja_fecha = p_date, baja_usr = p_usr where id_orden_pago_ospim = p_orden_pago_ospim_id;
--farmacia
update orden_pago_ospim_lista_reintegros_farmacia set baja_fecha = p_date, baja_usr = p_usr where id_orden_pago_ospim = p_orden_pago_ospim_id;

update comprobante c set baja_fecha = p_date , baja_usr = p_usr
from comprobante_orden_pago_ospim copo
where c.cuit = copo.cuit
and c.compro_nro = copo.compro_nro
and c.compro_tipo = copo.compro_tipo
and c.compro_sucu = copo.compro_sucu
and c.compro_letra = copo.compro_letra
and c.id_punto_venta = copo.id_punto_venta
and copo.id_orden_pago_ospim = p_orden_pago_ospim_id
and c.compro_tipo = 'REI'
and (exists (select 1 from orden_pago_ospim_lista_reintegros where id_orden_pago_ospim = copo.id_orden_pago_ospim)
     or exists (select 1 from orden_pago_ospim_lista_reintegros_farmacia where id_orden_pago_ospim = copo.id_orden_pago_ospim)) ;


return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE

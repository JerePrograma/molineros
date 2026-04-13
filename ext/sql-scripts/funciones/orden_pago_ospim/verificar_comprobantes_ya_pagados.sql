CREATE OR REPLACE FUNCTION verificar_comprobantes_ya_pagados(p_id_op_ospim integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
    declare resultDom integer;
  begin

	  resultDom = 1 from comprobante_orden_pago_ospim copo, 
		orden_pago_ospim opo, 
		(select * from comprobante_orden_pago_ospim 
		where id_orden_pago_ospim  = p_id_op_ospim) comprobantes
		where copo.id_orden_pago_ospim = opo.id_orden_pago
		and opo.baja_fecha is null
		and copo.cuit = comprobantes.cuit
		and copo.compro_nro = comprobantes.compro_nro
		and copo.compro_tipo = comprobantes.compro_tipo
		and copo.compro_sucu = comprobantes.compro_sucu
		and copo.compro_letra = comprobantes.compro_letra
		and copo.id_punto_Venta = comprobantes.id_punto_venta
		and copo.id_orden_pago_ospim <> comprobantes.id_orden_pago_ospim
		limit 1;
	  
      if (resultDom is null) then
      	resultDom = 0;
      end if;
	  
      return resultDom;
  end;  
$BODY$;


CREATE OR REPLACE FUNCTION verificar_comprobantes_amtima_ya_pagados(p_id_op_amtima integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
    declare resultDom integer;
  begin

	  resultDom = 1 from comprobante_orden_pago_amtima copo, 
		orden_pago_amtima opo, 
		(select * from comprobante_orden_pago_amtima 
		where id_orden_pago_amtima  = p_id_op_amtima) comprobantes
		where copo.id_orden_pago_amtima = opo.id_orden_pago
		and opo.baja_fecha is null
		and copo.cuit = comprobantes.cuit
		and copo.compro_nro = comprobantes.compro_nro
		and copo.compro_tipo = comprobantes.compro_tipo
		and copo.compro_sucu = comprobantes.compro_sucu
		and copo.compro_letra = comprobantes.compro_letra
		and copo.id_punto_Venta = comprobantes.id_punto_venta
		and copo.id_orden_pago_amtima <> comprobantes.id_orden_pago_amtima
		limit 1;
	  
      if (resultDom is null) then
      	resultDom = 0;
      end if;
	  
      return resultDom;
  end;  
$BODY$;


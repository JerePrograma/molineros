CREATE OR REPLACE FUNCTION verificar_anticipos_utilizados_amtima(p_id_op_amtima integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
    declare resultDom integer;
  begin

	  resultDom = 1 from orden_pago_amtima_pagos opop, 
		orden_pago_amtima opo, 
		(select * from comprobante_orden_pago_amtima 
		where id_orden_pago_amtima  = p_id_op_amtima
		and compro_tipo = 'ANT') comprobantes
		where opop.id_orden_pago = opo.id_orden_pago
		and opo.baja_fecha is null
		and opop.cuit_antic = comprobantes.cuit
		and opop.compro_nro_antic = comprobantes.compro_nro
		and opop.compro_tipo_antic = comprobantes.compro_tipo
		and opop.compro_sucu_antic = comprobantes.compro_sucu
		and opop.compro_letra_antic = comprobantes.compro_letra
		and opop.id_punto_Venta_antic = comprobantes.id_punto_venta
		limit 1;
	  
      if (resultDom is null) then
      	resultDom = 0;
      end if;
	  
      return resultDom;
  end;  
$BODY$;



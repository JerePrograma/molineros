CREATE OR REPLACE FUNCTION verificar_anticipos_amtima_no_pagados(p_id_op_amtima integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
    declare resultDom integer;
  begin

	  resultDom = 1 from orden_pago_amtima_pagos opop 
			where opop.id_orden_pago = p_id_op_amtima
			and compro_tipo_antic = 'ANT'
			and not exists (select 1 from orden_pago_amtima opo, comprobante_orden_pago_amtima copo
					where copo.id_orden_pago_amtima = opo.id_orden_pago
					and copo.cuit = opop.cuit_antic
					and copo.compro_nro = opop.compro_nro_antic
					and copo.compro_sucu = opop.compro_sucu_antic
					and copo.compro_tipo = opop.compro_tipo_antic
					and copo.compro_letra = opop.compro_letra_antic
					and copo.id_punto_venta = opop.id_punto_venta_antic
					and opo.baja_fecha is null)
		limit 1;
	  
      if (resultDom is null) then
      	resultDom = 0;
      end if;
	  
      return resultDom;
  end;  
$BODY$;




CREATE OR REPLACE FUNCTION verificar_anticipos_reutilizados(p_id_op_ospim integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
    declare resultDom integer;
  begin
  	
	resultDom = 1 from orden_pago_ospim_pagos opop2,
			orden_pago_ospim opo
			where exists (select 1
					from orden_pago_ospim_pagos opop
					where opop.compro_tipo_antic = 'ANT'
					and opop.id_orden_pago = p_id_op_ospim
					and opop.id_orden_pago <> opop2.id_orden_pago
					and opop.compro_tipo_antic = opop2.compro_tipo_antic
					and opop.compro_nro_antic = opop2.compro_nro_antic
					and opop.compro_sucu_antic = opop2.compro_sucu_antic
					and opop.compro_letra_antic = opop2.compro_letra_antic
					and opop.cuit_antic = opop2.cuit_antic
					and opop.id_punto_venta_antic = opop2.id_punto_venta_antic)
			and opop2.id_orden_pago = opo.id_orden_pago
			and opo.baja_fecha is null
			limit 1;
  
      if (resultDom is null) then
      	resultDom = 0;
      end if;
	  
      return resultDom;
  end;  
$BODY$;




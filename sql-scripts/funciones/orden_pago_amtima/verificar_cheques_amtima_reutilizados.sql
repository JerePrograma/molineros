CREATE OR REPLACE FUNCTION verificar_cheques_amtima_reutilizados(p_id_op_amtima integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
    declare resultDom integer;
  begin

	  resultDom = 1 from orden_pago_amtima_pagos opop, 
			orden_pago_amtima opo,
			(select c.nro_cheque, c.id_banco, opop.id_orden_pago from cheque_amtima c, orden_pago_amtima_pagos opop
			where c.nro_cheque = opop.nro_cheque
			and c.id_banco = opop.id_banco_cheque
			and opop.id_orden_pago = p_id_op_amtima) cheques
			where opop.id_orden_pago = opo.id_orden_pago
			and opop.nro_cheque = cheques.nro_cheque
			and opop.id_banco_cheque = cheques.id_banco
			and opop.id_orden_pago <> cheques.id_orden_pago
			and opo.baja_fecha is null
			limit 1;
	  
      if (resultDom is null) then
      	resultDom = 0;
      end if;
	  
      return resultDom;
  end;  
$BODY$;


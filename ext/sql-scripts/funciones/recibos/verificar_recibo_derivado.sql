CREATE OR REPLACE FUNCTION verificar_recibo_derivado(p_id_recibo integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
    declare resultDom integer;
  begin

	  resultDom = 1 from recibo_conceptos  rc 
		inner join recibo_conceptos_pagos rcp
		on rc.id = rcp.recibo_concepto_id
		where (acta_id is not null or convenio_id is not null)
		and rcp.importe <> rcp.pendiente_derivar
		and rc.recibo_id = p_id_recibo
		limit 1;
	  
      if (resultDom is null) then
      	resultDom = 0;
      end if;
	  
      return resultDom;
  end;  
$BODY$;


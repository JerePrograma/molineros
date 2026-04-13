CREATE OR REPLACE FUNCTION verificar_op_creada_en_canje(p_id_op_ospim integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
    declare resultDom integer;
  begin

	  resultDom = 1 from canje_cheques_propios ccp  
			where ccp.id_orden_pago_ospim_nueva = $1
			limit 1;
	  
      if (resultDom is null) then
      	resultDom = 0;
      end if;
	  
      return resultDom;
  end;  
$BODY$;
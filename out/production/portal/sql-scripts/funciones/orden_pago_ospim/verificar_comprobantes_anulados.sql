CREATE OR REPLACE FUNCTION verificar_comprobantes_anulados(p_id_op_ospim integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
    declare resultDom integer;
  begin

	  resultDom = 1 from comprobante c, comprobante_orden_pago_ospim copo
		where c.cuit = copo.cuit
		and c.compro_nro = copo.compro_nro
		and c.compro_tipo = copo.compro_tipo
		and c.compro_sucu = copo.compro_sucu
		and c.compro_letra = copo.compro_letra
		and c.id_punto_Venta = copo.id_punto_venta
		and copo.id_orden_pago_ospim = p_id_op_ospim
		and c.baja_fecha is not null
		limit 1;
	  
      if (resultDom is null) then
      	resultDom = 0;
      end if;
	  
      return resultDom;
  end;  
$BODY$;


CREATE OR REPLACE FUNCTION existe_op_farmacia(p_periodo timestamp without time zone, p_codigoprestador character varying)
  RETURNS integer AS
$BODY$
    declare resultDom integer;
  begin

	  resultDom = 1 from liquidacion_farmacia_amtima l
	  	inner join  orden_pago_amtima op
	  	on l.orden_pago_amtima_id = op.id_orden_pago
	  	and op.baja_fecha is null
		 where nro_prestador = p_codigoPrestador and
	  			periodo = p_periodo  	limit 1;
	  			
      if(resultDom is null) then
	resultDom = 1 from liquidacion_farmacia_ospim l
	  	inner join  orden_pago_ospim op
	  	on l.orden_pago_ospim_id = op.id_orden_pago
	  	and op.baja_fecha is null
		 where nro_prestador = p_codigoPrestador and
	  			periodo = p_periodo  	limit 1;
      end if;	  
      if (resultDom is null) then
      	resultDom = 0;
      end if;
	  
      return resultDom;
  end;  
$BODY$
  LANGUAGE plpgsql VOLATILE


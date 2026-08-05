CREATE OR REPLACE FUNCTION existe_op_amtima(p_periodo timestamp without time zone, p_codigoPrestador character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
    declare resultDom integer;
  begin

	  resultDom = 1 from liquidacion_farmacia_amtima l
	  	inner join  orden_pago_amtima op
	  	on l.orden_pago_amtima_id = op.id_orden_pago
	  	and op.baja_fecha is null
		 where nro_prestador = p_codigoPrestador and
	  			periodo = p_periodo  	limit 1;
	  
      if (resultDom is null) then
      	resultDom = 0;
      end if;
	  
      return resultDom;
  end;  
$BODY$;


ALTER FUNCTION existe_op_amtima(p_periodo timestamp without time zone, p_codigoPrestador character varying) OWNER TO postgres;

--

DROP FUNCTION buscar_ordenes_pago(p_numerocheque numeric,
 p_id integer) ;
 
CREATE OR REPLACE FUNCTION buscar_ordenes_pago(p_numerocheque numeric,
 p_id integer) 
RETURNS TABLE(
  op__id_orden_pago integer,
  op__importe numeric,
  op__fecha_desde timestamp without time zone,
  op__fecha_hasta timestamp without time zone,
  op__descuento numeric,
  op__descuento_por_drogueria numeric,
  op__alta_fecha timestamp without time zone,
  op__alta_usr character varying,
  op__modi_fecha timestamp without time zone,
  op__modi_usr character varying,
  op__baja_fecha timestamp without time zone,
  op__baja_usr character varying,
  op__afiliado_razon_social character varying,
  op__id_seccional integer,
  op__cuit_acreedor character varying,
  op__sucu_acreedor character varying,
  op__observaciones character varying,
   e__razon_soc character varying
  )
    LANGUAGE sql
    AS $BODY$


select   id_orden_pago,
  importe,
  fecha_desde,
  fecha_hasta,
  descuento,
  descuento_por_drogueria,
  opo.alta_fecha,
  opo.alta_usr,
  opo.modi_fecha,
  opo.modi_usr,
  opo.baja_fecha,
  opo.baja_usr,
  afiliado_razon_social,
  opo.id_seccional,
  opo.cuit_acreedor,
  opo.sucu_acreedor,
  opo.observaciones,
   e.razon_soc
from orden_pago_amtima opo  
left outer join empresa e
on opo.cuit_acreedor = e.cuit
and opo.sucu_acreedor = e.sucursal
where ($2 is null or ($2 is not null and id_orden_pago = $2))
and ($1 is null or exists (select 1 from orden_pago_amtima_pagos where id_orden_pago = opo.id_orden_pago and nro_cheque = $1));

$BODY$;


ALTER FUNCTION public.buscar_ordenes_pago(p_numerocheque numeric, p_id integer) OWNER TO postgres;

--

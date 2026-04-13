
DROP FUNCTION buscar_ordenes_pago_ospim(p_numerocheque numeric,
 p_id integer)  ;


CREATE OR REPLACE FUNCTION buscar_ordenes_pago_ospim(IN p_numerocheque numeric, IN p_id integer)
  RETURNS TABLE(op__id_orden_pago integer, op__importe numeric, op__id_seccional integer, op__prestador boolean, op__farmacia boolean, 
  op__cuit_acreedor character varying, op__sucu_acreedor character varying, op__observaciones character varying, 
  op__alta_fecha timestamp without time zone, op__alta_usr character varying, op__alta_ip character varying, 
  op__modi_fecha timestamp without time zone, op__modi_usr character varying, op__modi_ip character varying, 
  op__baja_fecha timestamp without time zone, op__baja_usr character varying, op__baja_ip character varying, 
  e__razon_soc character varying) AS
$BODY$


select  
 opo.id_orden_pago,
 opo.importe,
 opo.id_seccional,
 opo.prestador,
 opo.farmacia,
 opo.cuit_acreedor,
 opo.sucu_acreedor,
 opo.observaciones,
 opo.alta_fecha,
 opo.alta_usr,
 opo.alta_ip,
 opo.modi_fecha,
 opo.modi_usr,
 opo.modi_ip ,
 opo.baja_fecha,
 opo.baja_usr,
 opo.baja_ip ,
 e.razon_soc
from orden_pago_ospim opo
left outer join empresa e
on opo.cuit_acreedor = e.cuit
and opo.sucu_acreedor = e.sucursal
where ($2 is null or ($2 is not null and id_orden_pago = $2))
and ($1 is null or exists (select 1 from orden_pago_ospim_pagos where id_orden_pago = opo.id_orden_pago and nro_cheque = $1))
$BODY$
  LANGUAGE sql VOLATILE


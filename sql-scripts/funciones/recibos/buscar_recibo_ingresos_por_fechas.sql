DROP FUNCTION buscar_recibo_ingresos_por_fechas(p_fecha_ini date, p_fecha_fin date);
CREATE OR REPLACE FUNCTION buscar_recibo_ingresos_por_fechas(IN p_fecha_ini date, IN p_fecha_fin date)
  RETURNS TABLE(ri__id integer, ri__recibo_id integer, ri__nro_cheque numeric, ri__id_banco integer, ri__numero_deposito character varying, ri__importe numeric, ri__fecha date, ri__id_estado_efectivo integer, ri__alta_fecha timestamp without time zone, ri__alta_usr character varying, ri__modi_fecha timestamp without time zone, ri__modi_usr character varying, ri__baja_fecha timestamp without time zone, ri__baja_usr character varying, ri__id_cuenta_bcria_destino_deposito integer, ri__id_banco_destino_deposito integer, ri__id_recibo_ingreso_tipo_deposito integer, ch__nro_cheque numeric, ch__cuit character varying, ch__a_nombre_de character varying, ch__fecha timestamp without time zone, ch__importe numeric, ch__alta_fecha timestamp without time zone, ch__alta_usr character varying, ch__alta_ip character varying, ch__modi_fecha timestamp without time zone, ch__modi_usr character varying, ch__modi_ip character varying, ch__baja_fecha timestamp without time zone, ch__baja_usr character varying, ch__baja_ip character varying, ch__concepto character varying, ch__id_cta_bcria integer, ch__debito_credito character, ch__id_banco integer, es__id integer, es__descripcion character varying, ri__id_anticipo_recibo_concepto integer, ra__id integer, ra__recibo_id integer, ra__numero character varying, ra__importe numeric, ra__recibo_fecha date, ri__nro_pagare numeric) AS
$BODY$

select 	ri.id  ,
	ri.recibo_id  ,
	ri.nro_cheque ,
	ri.id_banco ,
	ri.numero_deposito,
	ri.importe,
	ri.fecha,
	ri.id_estado_efectivo,
 	ri.alta_fecha,
    ri.alta_usr,
    ri.modi_fecha,
    ri.modi_usr,
    ri.baja_fecha,
    ri.baja_usr,
    ri.id_cuenta_bcria_destino_deposito,
    cb.id_banco,
    ri.id_recibo_ingreso_tipo_deposito,
     c.nro_cheque,
	 c.cuit,
	 c.a_nombre_de,
	 c.fecha,
	 c.importe,
	 c.alta_fecha ,
	 c.alta_usr ,
	 c.alta_ip,
	 c.modi_fecha,
	 c.modi_usr,
	 c.modi_ip ,
	 c.baja_fecha,
	 c.baja_usr ,
	 c.baja_ip,
	 c.concepto,
	 c.id_cta_bcria,
	 c.debito_credito,
	 c.id_banco,
	  ce.id,
	 ce.descripcion,
	  ri.id_anticipo_recibo_concepto,
	 
	 ri.id_anticipo_recibo_concepto, 
	 r2.id, 
	 r2.numero, 
	 ri.importe, 
	 r2.fecha,
	 cast(null as numeric) as nro_pagare
from recibo_ingresos ri
inner join recibo r
on ri.recibo_id = r.id
left outer join cheque c
on ri.nro_cheque = c.nro_cheque
and ri.id_banco = c.id_banco
left outer join cheque_estado ce
on c.id_estado = ce.id
left outer join cuenta_bcria cb
on ri.id_cuenta_bcria_destino_deposito = cb.id_cuenta_bcria
left outer join recibo_conceptos rc
on ri.id_anticipo_recibo_concepto = rc.id
left outer join recibo r2
on rc.recibo_id = r2.id
where r.fecha >= $1 and r.fecha <= $2

	
$BODY$
  LANGUAGE sql VOLATILE


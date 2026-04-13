drop function buscar_comprobantes_orden_pago_ospim (p_id_op_ospim integer) ;


CREATE OR REPLACE FUNCTION buscar_comprobantes_orden_pago_ospim(p_id_op_ospim integer) 
RETURNS TABLE(fecha_emision timestamp without time zone,
 fecha_recepcion timestamp without time zone,
 importe_comprobante numeric,
 nro character varying,
 tipo character varying,
 id_punto_venta smallint,
 cuit character,
 alta_fecha timestamp without time zone,
 alta_usr character varying,
 modi_fecha timestamp without time zone,
 modi_usr character varying,
 baja_fecha timestamp without time zone,
 baja_usr character varying,
 compro_letra character varying,
 compro_sucu integer,
  cuit_acreedor character(11),
  sucu_acreedor character(11),
  seccional integer,
 observaciones character varying(250),
 vto timestamp without time zone,
   periodo_prestacion date,
   debito_para_egreso boolean,
   anulado_fecha  timestamp without time zone,
   anulado_usr character varying
)
    LANGUAGE sql
    AS $BODY$

select  
  c.fecha_emision,
  c.fecha_recepcion,
  c.total,
  c.compro_nro,
  c.compro_tipo,
  c.id_punto_venta,
  c.cuit,
  c.alta_fecha,
  c.alta_usr,
  c.modi_fecha,
  c.modi_usr,
  c.baja_fecha,
  c.baja_usr,
  c.compro_letra,
  c.compro_sucu,
 c.cuit_acreedor ,
  c.sucu_acreedor,
  c.seccional, 
  c.observaciones,
  c.vto,
  c.periodo_prestacion,
  c.debito_para_egreso,
  c.anulado_fecha,
  c.anulado_usr 
from comprobante c
inner join comprobante_orden_pago_ospim copo
on   c.compro_nro = copo.compro_nro
and c.compro_tipo = copo.compro_tipo
and c.id_punto_venta = copo.id_punto_venta
and c.cuit = copo.cuit
and c.compro_letra = copo.compro_letra
and c.compro_sucu = copo.compro_sucu
and copo.id_orden_pago_ospim = $1;
  
$BODY$;


--

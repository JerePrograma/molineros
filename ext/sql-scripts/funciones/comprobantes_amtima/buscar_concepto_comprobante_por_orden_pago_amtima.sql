
CREATE OR REPLACE FUNCTION buscar_concepto_comprobante_por_orden_pago_amtima(IN p_op_amtima_id integer)
  RETURNS TABLE(ccc__alta_fecha timestamp without time zone, ccc__alta_usr character varying, ccc__modi_fecha timestamp without time zone, ccc__modi_usr character varying, ccc__baja_fecha timestamp without time zone, ccc__baja_usr character varying, ccc__concepto_id integer, cc__id integer, cc__descripcion character varying, cc__numero character varying, ccc__importe numeric, c__id_punto_venta smallint, c__compro_tipo character varying, c__compro_nro character varying, c__cuit character, c__compro_letra character varying, c__compro_sucu integer, cc__cuenta character varying) AS
$BODY$


select distinct c.alta_fecha, c.alta_usr,
  c.modi_fecha,
  c.modi_usr,
  c.baja_fecha,
  c.baja_usr,
  c.concepto_id,
  cc.id,
  cc.descripcion,
  cc.numero_cuenta,
  c.importe,
  c.id_punto_venta ,
 c.compro_tipo ,
    c.compro_nro ,
    c.cuit ,
    c.compro_letra ,
    c.compro_sucu ,
     pc.cuenta
from  concepto_comprobante_amtima c
inner join conceptos_amtima cc
on c.concepto_id = cc.id
inner join comprobante_orden_pago_amtima copo
on c.id_punto_venta = copo.id_punto_venta
   and c.compro_tipo = copo.compro_tipo
   and c.compro_nro = copo.compro_nro
   and c.cuit = copo.cuit
   and c.compro_letra = copo.compro_letra
   and c.compro_sucu = copo.compro_sucu
inner join orden_pago_amtima opo
on copo.id_orden_pago_amtima = opo.id_orden_pago
inner join plan_cuentas_amtima pc
on cc.numero_cuenta = pc.numero
where opo.id_orden_pago = $1
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION buscar_concepto_comprobante_por_orden_pago_amtima(integer) OWNER TO postgres;

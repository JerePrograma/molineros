
CREATE OR REPLACE FUNCTION buscar_concepto_comprobante(p_pto_vta numeric,
 p_tipo character varying,
 p_nro character varying,
 p_cuit character varying,
 p_compro_letra character varying,
 p_compro_sucu integer
 ) 
RETURNS TABLE(
 CCC__alta_fecha timestamp without time zone,
 CCC__alta_usr character varying,
 CCC__modi_fecha timestamp without time zone,
 CCC__modi_usr character varying,
 CCC__baja_fecha timestamp without time zone,
 CCC__baja_usr character varying,
 CCC__concepto_id integer,
 CC__id integer,
 CC__descripcion character varying,
 CC__numero character varying,
 CCC__importe numeric,
 CC__cuenta character varying)
    LANGUAGE sql
    AS $BODY$


select c.alta_fecha, c.alta_usr,
  c.modi_fecha,
  c.modi_usr,
  c.baja_fecha,
  c.baja_usr,
  c.concepto_id,
  cc.id_concepto_maestro,
  cc.descripcion,
  cc.numero_cuenta,
  c.importe,
  pc.cuenta
from  concepto_comprobante c
inner join comprobante comp
on c.id_punto_venta = comp.id_punto_venta
   and c.compro_tipo = comp.compro_tipo
   and c.compro_nro = comp.compro_nro
   and c.cuit = comp.cuit
   and c.compro_letra = comp.compro_letra
   and c.compro_sucu = comp.compro_sucu
inner join conceptos cc
on c.concepto_id = cc.id_concepto_maestro
and cast(cc.valido_desde as date)  <= cast(comp.fecha_recepcion as date)
and cast(cc.valido_hasta as date)  >= cast(comp.fecha_recepcion as date)
inner join plan_cuentas pc
on cc.id_plan_cuenta = pc.id_cuenta_maestro
and cast(pc.valido_desde as date)  <= cast(comp.fecha_recepcion as date)
and cast(pc.valido_hasta as date)  >= cast(comp.fecha_recepcion as date)
where  c.id_punto_venta = $1
   and c.compro_tipo = $2
   and c.compro_nro = $3
   and c.cuit = $4
   and c.compro_letra = $5
   and c.compro_sucu = $6;
 
$BODY$;

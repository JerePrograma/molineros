
CREATE OR REPLACE FUNCTION buscar_concepto_comprobante_amtima(p_pto_vta numeric,
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
  cc.id,
  cc.descripcion,
  cc.numero_cuenta,
  c.importe,
  pc.cuenta
from  concepto_comprobante_amtima c
inner join conceptos_amtima cc
on c.concepto_id = cc.id
inner join plan_cuentas_amtima pc
on cc.numero_cuenta = pc.numero
where  c.id_punto_venta = $1
   and c.compro_tipo = $2
   and c.compro_nro = $3
   and c.cuit = $4
   and c.compro_letra = $5
   and c.compro_sucu = $6;
 
$BODY$;

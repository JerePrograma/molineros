drop FUNCTION buscar_comprobante(p_pto_vta numeric,
 p_tipo character varying,
 p_nro character varying,
 p_cuit character varying,
 p_compro_letra character varying,
 p_compro_sucu integer
 ) ;

CREATE OR REPLACE FUNCTION buscar_comprobante(p_pto_vta numeric,
 p_tipo character varying,
 p_nro character varying,
 p_cuit character varying,
 p_compro_letra character varying,
 p_compro_sucu integer
 )
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
 pagado boolean,
 anulado_fecha  timestamp without time zone,
 anulado_usr character varying
 )
    LANGUAGE sql
    AS $BODY$


select c.fecha_emision,
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
  case  when exists (
	select 1 from comprobante_orden_pago_ospim copo
	inner join orden_pago_ospim opo
	on copo.id_orden_pago_ospim = opo.id_orden_pago
	where c.id_punto_venta =  copo.id_punto_venta
	and c.compro_tipo =copo.compro_tipo
	and c.compro_letra  =copo.compro_letra
	and c.compro_sucu =copo.compro_sucu
	and case when length(c.compro_nro)<8 then lpad(c.compro_nro,8,'0') else c.compro_nro end =  case when length($3)<8 then lpad(copo.compro_nro,8,'0') else copo.compro_nro end 
	and c.cuit = copo.cuit
	and opo.baja_fecha is null ) then true else false end as pagado,
   c.anulado_fecha,
   c.anulado_usr 
from  comprobante c
where  c.id_punto_venta = $1
   and c.compro_tipo = $2
   and case when length(c.compro_nro)<8 then lpad(c.compro_nro,8,'0') else c.compro_nro end =  case when length($3)<8 then lpad($3,8,'0') else $3 end 
   and c.cuit = $4
   and c.compro_letra = $5
   and c.compro_sucu = $6;
 
$BODY$;


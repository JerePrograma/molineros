------------ CUIDADO!!!!! ----------------------
/*
 * El estado de las liquidaciones esta harcodeado en java. Por lo tanto
 * si se crea un nuevo estado que represente una liquidacion cerrada,
 * y que por ese motivo deba aparecer en este listado, hay que agregarlo 
 * en los lugares correspondientes en esta query.
 */
------------ CUIDADO!!!! -------------------------

-- drop FUNCTION listado_estado_comprobantes(IN p_fecha_ini date, IN p_fecha_fin date, IN p_pago_hta date, IN p_cuit character varying, IN p_sucu character varying, IN p_seccional integer, IN p_soloconsaldo boolean, p_incluirProveedores boolean , p_incluirLiquidaciones boolean , p_incluirReintegros boolean)
CREATE OR REPLACE FUNCTION listado_estado_comprobantes(IN p_fecha_ini date, IN p_fecha_fin date, IN p_pago_hta date, IN p_cuit character varying, IN p_sucu character varying, IN p_seccional integer, IN p_soloconsaldo boolean, p_incluirProveedores boolean , p_incluirLiquidaciones boolean , p_incluirReintegros boolean)
  RETURNS TABLE(
  cuit_acreedor character varying, 
  sucu_acreedor character varying, 
  id_seccional integer, 
  razon_soc character varying, 
  seccional character varying, 
  fecha_recepcion date, 
  periodo_prestacion date, 
  descripcion character varying, 
  total numeric, 
  pagado boolean, 
  debito_para_egreso boolean
  ) AS
$BODY$

select cuit_acreedor, sucu_acreedor, seccional, razon_soc, detalle, cast(fecha_recepcion as date), periodo_prestacion, 
	descripcion,  total, pagado, debito_para_egreso
from (
select c.cuit_acreedor, c.sucu_acreedor, c.seccional, e.razon_soc, s.descripcion, c.fecha_recepcion, c.periodo_prestacion, 
	cast(c.compro_tipo || '-' || c.id_punto_venta || '-' || c.compro_nro as character varying) as detalle,  c.total, 
	case  when exists (
	select 1 from comprobante_orden_pago_ospim copo
	inner join orden_pago_ospim opo
	on copo.id_orden_pago_ospim = opo.id_orden_pago
	where c.id_punto_venta =  copo.id_punto_venta
	and c.compro_tipo =copo.compro_tipo
	and c.compro_letra  =copo.compro_letra
	and c.compro_sucu =copo.compro_sucu
	and c.compro_nro =copo.compro_nro
	and c.cuit = copo.cuit
	and cast(opo.alta_fecha as date)<= $3
	and (opo.baja_fecha is null or cast(opo.baja_fecha as date) >= $3) ) then true else false end as pagado,
	debito_para_egreso,
	case when cl.id_liquidacion is not null	then true else false end as liquidacion,
    case when exists (select 1 from orden_pago_ospim_lista_reintegros opolr, comprobante_orden_pago_ospim copo2 
    							where opolr.id_orden_pago_ospim = copo2.id_orden_pago_ospim  
								and copo2.id_punto_venta =  c.id_punto_venta
								and copo2.compro_tipo =c.compro_tipo
								and copo2.compro_letra  =c.compro_letra
								and copo2.compro_sucu =c.compro_sucu
								and copo2.compro_nro =c.compro_nro
								and copo2.cuit = c.cuit ) then true else false end as reintegro
from comprobante c
left outer join empresa e
on c.cuit_acreedor = e.cuit
and c.sucu_acreedor = e.sucursal
left outer join seccional s
on c.seccional = s.id_seccional
left outer join comprobante_liquidacion cl
on  cl.cuit = c.cuit
and cl.compro_nro = c.compro_nro
and cl.compro_tipo = c.compro_tipo
and cl.compro_sucu = c.compro_sucu
and cl.compro_letra = c.compro_letra
and cl.id_punto_venta = c.id_punto_venta
left outer join liquidacion l 
on cl.id_liquidacion = l.id_liquidacion
where  c.fecha_recepcion  > ($1 - interval '1 day') and  c.fecha_recepcion  < ($2 + interval '1 day')
and ($4 is null or cuit_acreedor = $4)
and ($4 is null or (sucu_acreedor = $5 and (($6 is null and seccional is null) or seccional = $6)))
and not exists (select 1 from recibo where numero = c.compro_nro and c.compro_tipo = 'RCB' and importe = c.total)
and (c.baja_fecha is null)
and (cl.id_liquidacion is null or l.estado in (2,10,11))
and (c.anulado_fecha is null or (c.anulado_fecha is not null and c.anulado_fecha > $3))
) aux
where ($7 = false or ($7 = true and aux.pagado = false))
and ((($9 = true and liquidacion = true) or ($10 = true and reintegro = true)) or ($8 = true and liquidacion = false and reintegro = false))
order by 1,2,3,6

$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;

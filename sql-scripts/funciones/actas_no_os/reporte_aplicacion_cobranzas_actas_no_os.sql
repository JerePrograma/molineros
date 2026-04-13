
CREATE OR REPLACE FUNCTION reporte_aplicacion_cobranzas_actas_no_os(IN p_fecha_ini date, IN p_fecha_fin date)
  RETURNS TABLE(numero character varying, fecha_acta date, fecha_actualizacion date, fecha_recepcion date, cuit character varying, sucursal character varying, razon_soc character varying, capital numeric, interes numeric, otros numeric, deuda_actas_asociadas numeric, numero_acta_asoc character varying, acta_total numeric, molinera boolean, pagado numeric) AS
$BODY$


select a.numero , cast (a.fecha_inicio as date) as fecha_acta, cast (a.fecha_pago as date) as fecha_actualizacion, cast (a.cierre_fecha as date) as fecha_recepcion, a.cuit, e.sucursal, e.razon_soc,a.capital,  a.interes,  a.otros, a.deuda_actas_asociadas,  
 a2.numero as numero_Acta_asoc, a.capital + a.interes + a.otros + a.deuda_actas_asociadas as acta_total, a.molinera,pagos.pagado
 from acta_no_os a
 inner join (select acta_id, sum(concepto_importe_por_cheques + concepto_importe_adicional) as pagado
from recibo_no_os r, recibo_no_os_conceptos rc
where r.id = rc.recibo_id
and r.baja_fecha is null
and cast(r.fecha as date)>= $1
and cast(r.fecha as date)<= $2
and rc.acta_id is not null
group by rc.acta_id) pagos
 on a.id = pagos.acta_id
 left outer join empresa e
 on a.cuit = e.cuit
 and a.sucursal = e.sucursal
 left outer join acta_no_os_relacion ar
 on a.id = ar.acta_id
 and ar.baja_fecha is null
 left outer join acta_no_os a2
 on ar.acta_relacionada_id = a2.id
 where a.acta_cerrada = true
--and a.capital + a.interes + a.otros + a.deuda_actas_asociadas <>pagos.pagado
order by numero


$BODY$
  LANGUAGE sql VOLATILE

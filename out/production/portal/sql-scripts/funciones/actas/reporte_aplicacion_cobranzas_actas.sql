
create or replace function reporte_aplicacion_cobranzas_actas(p_fecha_ini date, p_fecha_fin date) 
RETURNS TABLE (
  numero character varying(8),
  fecha_acta date,
  fecha_actualizacion date,
  fecha_recepcion date,
  cuit character varying(13),
  sucursal character varying(6),
  razon_soc character varying(200),
  capital numeric(10,2),
  interes numeric(10,2),
  otros numeric(10,2),
  deuda_actas_asociadas numeric(10,2),
  numero_acta_asoc character varying(8),
  acta_total numeric,
  molinera boolean,
  pagado numeric
)
LANGUAGE sql
AS $BODY$


select a.numero , cast (a.fecha_inicio as date) as fecha_acta, cast (a.fecha_pago as date) as fecha_actualizacion, cast (a.cierre_fecha as date) as fecha_recepcion, a.cuit, e.sucursal, e.razon_soc,a.capital,  a.interes,  a.otros, a.deuda_actas_asociadas,  
 a2.numero as numero_Acta_asoc, a.capital + a.interes + a.otros + a.deuda_actas_asociadas as acta_total, a.molinera,pagos.pagado
 from acta a
 inner join (select acta_id, sum(concepto_importe_por_cheques + concepto_importe_adicional) as pagado
from recibo r, recibo_conceptos rc
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
 left outer join acta_relacion ar
 on a.id = ar.acta_id
 and ar.baja_fecha is null
 left outer join acta a2
 on ar.acta_relacionada_id = a2.id
 where a.acta_cerrada = true
--and a.capital + a.interes + a.otros + a.deuda_actas_asociadas <>pagos.pagado
order by numero


$BODY$;
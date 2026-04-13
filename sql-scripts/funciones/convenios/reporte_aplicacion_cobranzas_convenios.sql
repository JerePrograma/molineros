
create or replace function reporte_aplicacion_cobranzas_convenios(p_fecha_ini date, p_fecha_fin date) 
RETURNS TABLE (
  numero character varying(8),
  fecha_inicio date,
  capital numeric,
  interes numeric,
  ajuste_capital numeric,
  ajuste_interes numeric,
  total numeric,
  cuit character varying,
  sucursal character varying,
  razon_soc character varying,
  acta_asoc character varying,
  pagado numeric
)
LANGUAGE sql
AS $BODY$


 
select  case when c.numero is null or trim(c.numero ) = '' then cast (c.id as character varying) else c.numero end ,
    cast(c.fecha_inicio as date),
    c.deuda_actas_asociadas as capital,
    c.interes,
    c.ajuste_capital,
    c.ajuste_interes,
    c.deuda_actas_asociadas + c.interes + c.ajuste_capital + c.ajuste_interes as total,
    e.cuit,
    e.sucursal,
    e.nombre_fantasia,
    a.numero as acta_asoc,
    pagos.pagado
from convenio c
inner join (select convenio_id, sum(concepto_importe_por_cheques + concepto_importe_adicional) as pagado
from recibo r, recibo_conceptos rc
where r.id = rc.recibo_id
and r.baja_fecha is null
and cast(r.fecha as date)>= $1
and cast(r.fecha as date)<= $2
and rc.convenio_id is not null
group by rc.convenio_id) pagos
 on c.id = pagos.convenio_id
left outer join empresa e
on c.cuit = e.cuit
and c.sucursal = e.sucursal
left outer join convenio_actas ca
on c.id = ca.convenio_id
--and ca.baja_fecha is null
left outer join acta a
on ca.acta_id = a.id
where c.baja_fecha is null
order by c.id asc


$BODY$;
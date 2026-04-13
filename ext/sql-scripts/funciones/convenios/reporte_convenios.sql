CREATE OR REPLACE function reporte_convenios(p_fecha_ini date, p_fecha_fin date) 
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
  acta_asoc character varying
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
    a.numero as acta_asoc
from convenio c
left outer join empresa e
on c.cuit = e.cuit
and c.sucursal = e.sucursal
left outer join convenio_actas ca
on c.id = ca.convenio_id
--and ca.baja_fecha is null
left outer join acta a
on ca.acta_id = a.id
where c.baja_fecha is null
and cast(c.fecha_inicio as date) >= $1
and cast(c.fecha_inicio as date) <= $2
order by c.id asc

 $BODY$;
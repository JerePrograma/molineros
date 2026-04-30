CREATE OR REPLACE FUNCTION reporte_actas_no_os(IN p_fecha_ini date, IN p_fecha_fin date)
  RETURNS TABLE(entidad character varying, numero character varying, fecha_acta date, fecha_actualizacion date, fecha_recepcion date, cuit character varying, sucursal character varying, razon_soc character varying, capital numeric, interes numeric, otros numeric, deuda_actas_asociadas numeric, numero_acta_asoc character varying, acta_total numeric, molinera boolean, periodos integer, promedio_empleados integer, promedio_pagados integer, total_remuneracion numeric, total_deuda numeric, total_calculado numeric, total_pagado numeric, total_interes numeric, inspectores text, estado character varying) AS
$BODY$

select a.entidad, a.numero , cast (a.fecha_inicio as date) as fecha_acta, cast (a.fecha_pago as date) as fecha_actualizacion, cast (a.cierre_fecha as date) as fecha_recepcion, a.cuit, e.sucursal, e.razon_soc,a.capital,  a.interes,  a.otros, a.deuda_actas_asociadas,  
 a2.numero as numero_Acta_asoc, a.capital + a.interes + a.otros + a.deuda_actas_asociadas as acta_total, r.molinera,
 cast(estadisticas.periodos as integer),
 cast(estadisticas2.empleados as integer),
 cast((estadisticas2.cant_total_pagado / estadisticas.periodos) as integer),
 estadisticas2.total_remuneracion,
 estadisticas.total_deuda,
 estadisticas2.total_calculado,
 estadisticas.total_pagado,
 estadisticas.total_interes,
 trae_inspectores_acta_no_os(a.id),
 a.estado
 from acta_no_os a
 left outer join empresa e
 on a.cuit = e.cuit
 and a.sucursal = e.sucursal
 left outer join ramo_empresa r
 on r.id_ramo_empresa=e.id_ramo_empresa
 left outer join acta_no_os_relacion ar
 on a.id = ar.acta_id
 and ar.baja_fecha is null
 left outer join acta_no_os a2
 on ar.acta_relacionada_id = a2.id
 left outer join (select a.id, 
					count(distinct ap.periodo ) as periodos, 
					sum(subtotal) as total_deuda,
					sum(pagado) as total_pagado,
					sum(ap.interes) as total_interes
					from acta_no_os a, acta_no_os_periodos ap
					where a.id = ap.acta_id
					and a.acta_cerrada = true
					and a.baja_fecha is null
					and cast (a.cierre_fecha as date)  >= $1
			 		and cast (a.cierre_fecha as date)  <= $2
					group by a.id
					) estadisticas
 on a.id = estadisticas.id
 left outer join (select count(*)/count(distinct periodo) as empleados, sum(remuneracion) as total_remuneracion, sum(calculado) as total_calculado, acta_id, count(case when pagado >0 then 1 else null end) as cant_total_pagado
 				from(
					select periodo, cuil, acta_id, max(remuneracion_declarada) as remuneracion, max(calculado) as calculado, max(pagado) as pagado
					from acta_no_os a,acta_no_os_periodos ap
					where a.id = ap.acta_id
					and a.acta_cerrada = true
					and a.baja_fecha is null
					and ap.baja_fecha is null
					and cuil is not null
					and cast (a.cierre_fecha as date)  >= $1
					and cast (a.cierre_fecha as date)  <= $2
					group by periodo, cuil, acta_id
				) resumen
				group by acta_id) estadisticas2
on a.id = estadisticas2.acta_id
 where a.acta_cerrada = true
 and a.baja_fecha is null
 --and cast (a.cierre_fecha as date)  >= $1
 --and cast (a.cierre_fecha as date)  <= $2
 and cast (a.fecha_inicio as date)  >= $1
 and cast (a.fecha_inicio as date)  <= $2
order by numero




		
		
$BODY$
  LANGUAGE sql VOLATILE


create type reporte_otros_reintegros_result as (codigo varchar, descripcion varchar, importe numeric)

CREATE OR REPLACE FUNCTION reporte_egresos_otros_reintegros_prestaciones(vigen_desde date, vigen_hasta date)
  RETURNS SETOF reporte_otros_reintegros_result AS
$BODY$
BEGIN
return query
select c.codigo, c.descripcion, trunc(sum(importe_total), 2) 
from consumo_afiliado_pago (null,null,vigen_desde,vigen_hasta) c
where codigo not in ('000001','1010000','101000','000003','101100','410000','410001','410002','400000','400003')
and tipo_consumo = 'REINTEGRO PRESTACIONAL'
and debitado_omint='0'
group by codigo, c.descripcion
order by c.descripcion;


END;
$BODY$
  LANGUAGE plpgsql VOLATILE


CREATE OR REPLACE FUNCTION buscar_consolidado_liq_actas_conv(fecha_ini date)
RETURNS TABLE (fecha date, importe numeric) AS
$BODY$
select fecha_liq,sum(omint_d) 
from liquidacion_actas 
where fecha_liq>=$1
group by fecha_liq
order by fecha_liq;;
$BODY$
LANGUAGE sql VOLATILE

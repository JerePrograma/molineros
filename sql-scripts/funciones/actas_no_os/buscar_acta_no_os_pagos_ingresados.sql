CREATE OR REPLACE FUNCTION buscar_acta_no_os_pagos_ingresados(IN p_actaid integer)
  RETURNS TABLE(recibo_id integer, importe numeric, fecha_pagado date) AS
$BODY$

select rc.recibo_id,  rcp.importe, ri.fecha
from recibo_no_os_conceptos rc
inner join recibo_no_os_conceptos_pagos rcp
on rc.id = rcp.recibo_concepto_id
inner join recibo_no_os_ingresos ri
on rcp.recibo_ingreso_id = ri.id
where rc.baja_fecha is null
and rcp.baja_fecha is null
and ri.baja_fecha is null
and rc.acta_id = $1;
$BODY$
  LANGUAGE sql VOLATILE


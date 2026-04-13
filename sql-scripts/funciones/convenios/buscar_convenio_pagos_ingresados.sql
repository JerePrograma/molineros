DROP FUNCTION buscar_convenio_pagos_ingresados(p_convenio_id integer) ;
CREATE OR REPLACE FUNCTION buscar_convenio_pagos_ingresados(p_convenio_id integer) 
RETURNS TABLE(
  recibo_id integer,
  importe numeric,
  fecha_pagado date)
LANGUAGE sql
AS $BODY$

select rc.recibo_id,  rcp.importe, ri.fecha
from recibo_conceptos rc
inner join recibo_conceptos_pagos rcp
on rc.id = rcp.recibo_concepto_id
inner join recibo_ingresos ri
on rcp.recibo_ingreso_id = ri.id
where rc.baja_fecha is null
and rcp.baja_fecha is null
and ri.baja_fecha is null
and rc.convenio_id = $1;

$BODY$;


ALTER FUNCTION public.buscar_convenio_pagos_ingresados(p_convenio_id integer)  OWNER TO postgres;

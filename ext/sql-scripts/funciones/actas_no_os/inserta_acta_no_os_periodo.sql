-- Function: inserta_acta_periodo(integer, date, character, numeric, numeric, numeric, date, numeric, numeric, character varying, character varying, character varying, boolean)

-- DROP FUNCTION inserta_acta_periodo(integer, date, character, numeric, numeric, numeric, date, numeric, numeric, character varying, character varying, character varying, boolean);

CREATE OR REPLACE FUNCTION inserta_acta_no_os_periodo(p_acta_id integer, p_periodo date, p_cuil character, p_remuneracion_declarada numeric, p_calculado numeric, p_pagado numeric, p_pagado_fecha date, p_subtotal numeric, p_interes numeric, p_ape character varying, p_nom character varying, p_usr character varying, p_agregado_manual boolean)
  RETURNS integer AS
$BODY$
BEGIN
	
insert into acta_no_os_periodos(
  acta_id,
  periodo,
  cuil,
  remuneracion_declarada,
  calculado,
  pagado,
  pagado_fecha,
  subtotal,
  interes,
  alta_fecha,
  alta_usr,
  modi_fecha,
  modi_usr,
  apellido,
  nombre,
  agregado_manual) 
values (p_acta_id ,
  p_periodo ,
  p_cuil,
  p_remuneracion_declarada ,
  p_calculado ,
  p_pagado ,
  p_pagado_fecha ,
  p_subtotal ,
  p_interes , localtimestamp, p_usr, localtimestamp, p_usr, p_ape, p_nom, p_agregado_manual);

return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION inserta_acta_periodo(integer, date, character, numeric, numeric, numeric, date, numeric, numeric, character varying, character varying, character varying, boolean)
  OWNER TO postgres;


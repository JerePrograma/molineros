-- Function: buscar_convenio_pagos(integer)

-- DROP FUNCTION buscar_convenio_pagos(integer);

CREATE OR REPLACE FUNCTION buscar_convenio_pagos(IN p_convid integer)
  RETURNS TABLE(cp__convenio_id integer, cp__tipo character, cp__fecha_pago timestamp without time zone, cp__cuota_id integer, cp__importe numeric, cp__interes numeric, cp__recibo_id integer, cp__alta_fecha timestamp without time zone, cp__alta_usr character varying, cp__alta_ip character varying, cp__modi_fecha timestamp without time zone, cp__modi_usr character varying, cp__modi_ip character varying, cp__baja_fecha timestamp without time zone, cp__baja_usr character varying, cp__baja_ip character varying, cp__id integer, cp__convenio_relacion_id integer, cp__nro_cheque numeric, cp__banco_cheque integer, ch__nro_cheque numeric, ch__cuit character varying, ch__a_nombre_de character varying, ch__fecha timestamp without time zone, ch__importe numeric, ch__alta_fecha timestamp without time zone, ch__alta_usr character varying, ch__alta_ip character varying, ch__modi_fecha timestamp without time zone, ch__modi_usr character varying, ch__modi_ip character varying, ch__baja_fecha timestamp without time zone, ch__baja_usr character varying, ch__baja_ip character varying, ch__concepto character varying, ch__id_cta_bcria integer, ch__debito_credito character, ch__id_banco integer, cp__convenio_cancalatorio_id integer) AS
$BODY$

select   cp.convenio_id ,
  cp.tipo ,
  cp.fecha_pago,
  cp.cuota_id,
  cp.importe,
  cp.interes,
  cp.recibo_id ,
  cp.alta_fecha ,
  cp.alta_usr ,
  cp.alta_ip ,
  cp.modi_fecha,
  cp.modi_usr,
  cp.modi_ip,
  cp.baja_fecha,
  cp.baja_usr,
  cp.baja_ip,
  cp.id ,
  cp.convenio_relacion_id ,
  cp.nro_cheque,
  cp.banco_cheque,
  c.nro_cheque,
	 c.cuit,
	 c.a_nombre_de,
	 c.fecha,
	 c.importe,
	 c.alta_fecha ,
	 c.alta_usr ,
	 c.alta_ip,
	 c.modi_fecha,
	 c.modi_usr,
	 c.modi_ip ,
	 c.baja_fecha,
	 c.baja_usr ,
	 c.baja_ip,
	 c.concepto,
	 c.id_cta_bcria,
	 c.debito_credito,
	 c.id_banco,
	 co.id
  from convenio_pagos cp
  left outer join cheque c
  on cp.nro_cheque = c.nro_cheque
  left outer join convenio_relacion  cr
  on convenio_relacion_id = cr.id
  left outer join convenio co
  on cr.convenio_id = co.id
  and cp.banco_cheque = c.id_banco
  where cp.convenio_id = $1
  and cp.baja_fecha is null
  and (co.id is null or (co.id is not null and co.baja_fecha is null))
  order by cp.cuota_id asc;

$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION buscar_convenio_pagos(integer) OWNER TO postgres;

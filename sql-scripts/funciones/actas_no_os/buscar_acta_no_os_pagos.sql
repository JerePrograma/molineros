-- Function: buscar_acta_no_os_pagos(integer)

-- DROP FUNCTION buscar_acta_no_os_pagos(integer);

CREATE OR REPLACE FUNCTION buscar_acta_no_os_pagos(IN p_actaid integer)
  RETURNS TABLE(ap__acta_id integer, ap__tipo character, ap__fecha_pago timestamp without time zone, ap__importe numeric, ap__interes numeric, ap__recibo_id integer, ap__alta_fecha timestamp without time zone, ap__alta_usr character varying, ap__alta_ip character varying, ap__modi_fecha timestamp without time zone, ap__modi_usr character varying, ap__modi_ip character varying, ap__baja_fecha timestamp without time zone, ap__baja_usr character varying, ap__baja_ip character varying, ap__id integer, ap__acta_relacion_id integer, ap__convenio_acta_id integer, ap__nro_cheque numeric, ap__banco_cheque integer, ap__forma character, ch__nro_cheque numeric, ch__cuit character varying, ch__a_nombre_de character varying, ch__fecha timestamp without time zone, ch__importe numeric, ch__alta_fecha timestamp without time zone, ch__alta_usr character varying, ch__alta_ip character varying, ch__modi_fecha timestamp without time zone, ch__modi_usr character varying, ch__modi_ip character varying, ch__baja_fecha timestamp without time zone, ch__baja_usr character varying, ch__baja_ip character varying, ch__concepto character varying, ch__id_cta_bcria integer, ch__debito_credito character, ch__id_banco integer, es__id integer, es__descripcion character varying, ap__acta_cancelatoria_id integer, ap__convenio_cancalatorio_id integer) AS
$BODY$

select   ap.acta_id ,
  ap.tipo ,
  ap.fecha_pago,
  ap.importe,
  ap.interes,
  ap.recibo_id ,
  ap.alta_fecha ,
  ap.alta_usr ,
  ap.alta_ip ,
  ap.modi_fecha,
  ap.modi_usr,
  ap.modi_ip,
  ap.baja_fecha,
  ap.baja_usr,
  ap.baja_ip,
  ap.id ,
  ap.acta_relacion_id,
  ap.convenio_acta_id ,
  ap.nro_cheque,
  ap.banco_cheque,
  ap.forma,
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
	  ce.id,
	 ce.descripcion,
	 a.id,
  co.id
  from acta_no_os_pagos ap
  left outer join cheque c
  on ap.nro_cheque = c.nro_cheque
  and ap.banco_cheque = c.id_banco
  left outer join cheque_estado ce
  on c.id_estado = ce.id
  left outer join convenio_actas_no_os ca
  on convenio_acta_id = ca.id
  left outer join convenio co
  on ca.convenio_id = co.id
  left outer join acta_no_os_relacion  ar
  on acta_relacion_id = ar.id
  left outer join acta_no_os a
  on ar.acta_id = a.id
  where ap.acta_id = $1
  and ap.baja_fecha is null
  and (a.id is null or (a.id is not null and a.baja_fecha is null))
  and (co.id is null or (co.id is not null and co.baja_fecha is null));

$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION buscar_acta_no_os_pagos(integer)
  OWNER TO postgres;


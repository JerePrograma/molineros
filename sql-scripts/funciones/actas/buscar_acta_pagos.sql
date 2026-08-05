DROP FUNCTION buscar_acta_pagos(p_actaid integer);

CREATE OR REPLACE FUNCTION buscar_acta_pagos(p_actaid integer) 
RETURNS TABLE(
  AP__acta_id integer ,
  AP__tipo character(3) ,
  AP__fecha_pago timestamp without time zone,
  AP__importe numeric(10,2),
  AP__interes numeric(10,2),
  AP__recibo_id integer,
  AP__alta_fecha timestamp without time zone ,
  AP__alta_usr character varying(15) ,
  AP__alta_ip character varying(15),
  AP__modi_fecha timestamp without time zone ,
  AP__modi_usr character varying(15) ,
  AP__modi_ip character varying(15),
  AP__baja_fecha timestamp without time zone,
  AP__baja_usr character varying(15),
  AP__baja_ip character varying(15),
  AP__id integer ,
  AP__acta_relacion_id integer,
  AP__convenio_acta_id integer,
  AP__nro_cheque numeric(15,0),
  AP__banco_cheque integer,
  AP__forma char(1),
  CH__nro_cheque numeric,
 CH__cuit character varying,
 CH__a_nombre_de character varying,
 CH__fecha timestamp without time zone,
 CH__importe numeric,
 CH__alta_fecha timestamp without time zone,
 CH__alta_usr character varying,
 CH__alta_ip character varying,
 CH__modi_fecha timestamp without time zone,
 CH__modi_usr character varying,
 CH__modi_ip character varying,
 CH__baja_fecha timestamp without time zone,
 CH__baja_usr character varying,
 CH__baja_ip character varying,
 CH__concepto character varying,
 CH__id_cta_bcria integer,
 CH__debito_credito char(1),
 CH__id_banco integer,
  es__id integer,
 es__descripcion character varying (50),
 AP__acta_cancelatoria_id integer,
 AP__convenio_cancalatorio_id integer)
LANGUAGE sql
AS $BODY$

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
  from acta_pagos ap
  left outer join cheque c
  on ap.nro_cheque = c.nro_cheque
  and ap.banco_cheque = c.id_banco
  left outer join cheque_estado ce
  on c.id_estado = ce.id
  left outer join convenio_actas ca
  on convenio_acta_id = ca.id
  left outer join convenio co
  on ca.convenio_id = co.id
  left outer join acta_relacion  ar
  on acta_relacion_id = ar.id
  left outer join acta a
  on ar.acta_id = a.id
  where ap.acta_id = $1
  and ap.baja_fecha is null
  and (a.id is null or (a.id is not null and a.baja_fecha is null))
  and (co.id is null or (co.id is not null and co.baja_fecha is null));

$BODY$;


ALTER FUNCTION public.buscar_acta_pagos(p_actaid integer)  OWNER TO postgres;

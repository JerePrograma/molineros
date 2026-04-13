CREATE OR REPLACE FUNCTION buscar_cheques_amtima_reutilizables() 
RETURNS TABLE(ch__nro_cheque numeric,
 ch__cuit character varying,
 ch__a_nombre_de character varying,
 ch__fecha timestamp without time zone,
 ch__importe numeric,
 ch__alta_fecha timestamp without time zone,
 ch__alta_usr character varying,
 ch__alta_ip character varying,
 ch__modi_fecha timestamp without time zone,
 ch__modi_usr character varying,
 ch__modi_ip character varying,
 ch__baja_fecha timestamp without time zone,
 ch__baja_usr character varying,
 ch__baja_ip character varying,
 ch__concepto character varying,
 ch__id_cta_bcria integer,
 ch__debito_credito char(1),
 ch__id_banco integer,
 es__id integer,
 es__descripcion character varying (50),
 ba__id_banco integer,
 ba__descripcion character varying)
    LANGUAGE sql
    AS $BODY$
	select 	 distinct
	 c.nro_cheque,
	 c.cuit,
	 c.a_nombre_de,
	 c.fecha,
	 c.importe,
	 c.alta_fecha ,
	 c.alta_usr ,
	 cast(null as character varying),
	 c.modi_fecha,
	 c.modi_usr,
	 cast(null as character varying),
	 c.baja_fecha,
	 c.baja_usr ,
	 cast(null as character varying),
	 c.concepto,
	 c.id_cta_bcria,
	 c.debito_credito,
	  c.id_banco,
	 ce.id,
	 ce.descripcion,
	 b.id_banco,
        b.descripcion 
	from orden_pago_amtima opo, orden_pago_amtima_pagos opop, cheque_amtima c, cheque_estado ce, banco b
	where opo.baja_fecha is not null
	and opo.id_orden_pago = opop.id_orden_pago
	and opop.nro_cheque = c.nro_cheque 
	and opop.id_banco_cheque = c.id_banco 
	and c.baja_fecha is null
	and c.id_estado = ce.id
	and c.id_banco = b.id_banco 
	and not exists (select 1 from orden_pago_amtima_pagos  opop2, 
					orden_pago_amtima opo2 
				where opop2.nro_cheque = c.nro_cheque
				and opop2.id_banco_cheque = c.id_banco 
				and opop2.id_orden_pago = opo2.id_orden_pago 
				and opo2.baja_fecha is null)
	order by 1 asc;
$BODY$;

--


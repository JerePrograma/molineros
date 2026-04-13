-- Function: buscar_canje_cheques_nuevos(integer)

-- DROP FUNCTION buscar_canje_cheques_nuevos(integer);

CREATE OR REPLACE FUNCTION buscar_canje_cheques_nuevos(IN id_canje integer)
  RETURNS TABLE(ch__nro_cheque numeric, ch__cuit character varying, ch__a_nombre_de character varying, ch__fecha timestamp without time zone, ch__importe numeric, ch__alta_fecha timestamp without time zone, ch__alta_usr character varying, ch__alta_ip character varying, ch__modi_fecha timestamp without time zone, ch__modi_usr character varying, ch__modi_ip character varying, ch__baja_fecha timestamp without time zone, ch__baja_usr character varying, ch__baja_ip character varying, ch__concepto character varying, ch__id_cta_bcria integer, ch__debito_credito character, ch__id_banco integer, es__id integer, es__descripcion character varying, ba__id_banco integer, ba__descripcion character varying) AS
$BODY$
	select 	
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
	 b.id_banco,
     b.descripcion 
	 from cheque c inner join cheque_estado ce
	 on c.id_estado = ce.id
	 inner join banco b
	 on c.id_banco = b.id_banco 
	 inner join canje_cheques_propios_nuevos ccpn
	 on ccpn.nro_cheque = c.nro_cheque
	 and ccpn.id_banco = c.id_banco
	where ccpn.canje_id = $1
	order by fecha desc
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION buscar_canje_cheques_nuevos(integer) OWNER TO postgres;

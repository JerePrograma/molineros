
DROP FUNCTION actualizar_orden_pago_ospim(p_importe numeric,
 p_afavorde character varying,
 p_fecha timestamp without time zone,
 p_numerocheque numeric,
 p_id_banco integer,
 p_usuario character varying,
 p_concepto character varying,
 p_afiliadorazonsocial character varying,
 p_cuitcuil character varying,
 p_idseccional integer,
 p_forma_pago_cheque character varying,
 p_cta_bcria_cheque integer,
 p_forma_pago_deb character varying,
 p_cta_bcria_deb integer,
 p_forma_pago_ret character varying,
 p_cta_bcria_ret integer,
 p_forma_pago_tran character varying,
 p_cta_bcria_tran integer,
 p_id integer) ; 
 

CREATE OR REPLACE FUNCTION actualizar_orden_pago_ospim(p_importe numeric,
 p_afavorde character varying,
 p_fecha timestamp without time zone,
 p_numerocheque numeric,
 p_id_banco integer,
 p_usuario character varying,
 p_concepto character varying,
 p_afiliadorazonsocial character varying,
 p_cuitcuil character varying,
 p_idseccional integer,
 p_forma_pago_cheque character varying,
 p_cta_bcria_cheque integer,
 p_forma_pago_deb character varying,
 p_cta_bcria_deb integer,
 p_forma_pago_ret character varying,
 p_cta_bcria_ret integer,
 p_forma_pago_tran character varying,
 p_cta_bcria_tran integer,
  p_forma_pago_total_debitos character varying,
 p_id integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
  begin

	 update orden_pago_ospim
	 set  nro_cheque = p_numerocheque, id_banco = p_id_banco, importe = p_importe, a_favor_de = p_afavorde, fecha = p_fecha,
	 	modi_fecha = LOCALTIMESTAMP, modi_usr = p_usuario, concepto = p_concepto, cuitcuil = p_cuitcuil, afiliado_razon_social = p_afiliadorazonsocial, 
            id_seccional = p_idseccional, forma_pago_cheque = p_forma_pago_cheque, id_cuenta_bcria_cheque = p_cta_bcria_cheque, forma_pago_debito_banc =p_forma_pago_deb, 
            id_cuenta_bcria_debito_banc = p_cta_bcria_deb, forma_pago_ret_gan = p_forma_pago_ret, id_cuenta_bcria_ret_gan =p_cta_bcria_ret, 
            forma_pago_transferencia =p_forma_pago_tran, id_cuenta_bcria_transf = p_cta_bcria_tran, forma_pago_total_debitos = p_forma_pago_total_debitos
      where id_orden_pago = p_id;
 
  return 1 ;
  end;  
$BODY$;


ALTER FUNCTION public.actualizar_orden_pago_ospim(p_importe numeric,
 p_afavorde character varying,
 p_fecha timestamp without time zone,
 p_numerocheque numeric,
 p_id_banco integer,
 p_usuario character varying,
 p_concepto character varying,
 p_afiliadorazonsocial character varying,
 p_cuitcuil character varying,
 p_idseccional integer,
 p_forma_pago_cheque character varying,
 p_cta_bcria_cheque integer,
 p_forma_pago_deb character varying,
 p_cta_bcria_deb integer,
 p_forma_pago_ret character varying,
 p_cta_bcria_ret integer,
 p_forma_pago_tran character varying,
 p_cta_bcria_tran integer,
  p_forma_pago_total_debitos character varying,
 p_id integer)  OWNER TO postgres;

--

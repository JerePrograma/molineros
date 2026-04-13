CREATE OR REPLACE FUNCTION inserta_movimiento_banco(fecha_movimiento_v date,
 id_tipo_mov_v integer,
 id_cuenta_bcria_v integer,
 deb_cred_v boolean,
 id_tipo_transaccion_v integer,
 id_chequera_v integer,
 nro_cheque_v character varying,
 fecha_comprobante_v date,
 importe_movimiento_v double precision,
 descripcion_v character varying,
 imprime_cheque_v boolean,
 no_a_la_orden_v boolean,
 alta_usr_v character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
BEGIN
    INSERT INTO movimiento_banco(fecha_movimiento, id_tipo_mov, id_cuenta_bcria, deb_cred, id_tipo_transaccion, id_chequera, nro_compro, 
	    fecha_comprobante, importe_movimiento, descripcion, imprime_cheque, no_a_la_orden, alta_usr,alta_fecha)
    VALUES (fecha_movimiento_v, id_tipo_mov_v, id_cuenta_bcria_v, deb_cred_v, id_tipo_transaccion_v, id_chequera_v, nro_cheque_v, fecha_comprobante_v, 
            importe_movimiento_v, descripcion_v, imprime_cheque_v, no_a_la_orden_v, alta_usr_v, current_timestamp);

     return currval('mov_bcrio_id_seq');

END;
$BODY$;


ALTER FUNCTION public.inserta_movimiento_banco(fecha_movimiento_v date, id_tipo_mov_v integer, id_cuenta_bcria_v integer, deb_cred_v boolean, id_tipo_transaccion_v integer, id_chequera_v integer, nro_cheque_v character varying, fecha_comprobante_v date, importe_movimiento_v double precision, descripcion_v character varying, imprime_cheque_v boolean, no_a_la_orden_v boolean, alta_usr_v character varying) OWNER TO postgres;

--

CREATE OR REPLACE FUNCTION edita_movimiento_banco(fecha_movimiento_v date,
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
 alta_usr_v character varying,
 id_mov integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
BEGIN
            
    update movimiento_banco
    set fecha_movimiento=fecha_movimiento_v, 
        id_tipo_mov=id_tipo_mov_v, 
        id_cuenta_bcria=id_cuenta_bcria_v, 
        deb_cred=deb_cred_v, 
        id_tipo_transaccion=id_tipo_transaccion_v, 
        id_chequera=id_chequera_v, 
        nro_compro=nro_cheque_v, 
	fecha_comprobante=fecha_comprobante_v, 
	importe_movimiento=importe_movimiento_v, 
	descripcion= descripcion_v, 
	imprime_cheque=imprime_cheque_v, 
	no_a_la_orden=no_a_la_orden_v, 
	modi_usr=alta_usr_v,
	modi_fecha=current_timestamp
    where id_movimiento=id_mov;
    RETURN 1;

END;
$BODY$;


ALTER FUNCTION public.edita_movimiento_banco(fecha_movimiento_v date, id_tipo_mov_v integer, id_cuenta_bcria_v integer, deb_cred_v boolean, id_tipo_transaccion_v integer, id_chequera_v integer, nro_cheque_v character varying, fecha_comprobante_v date, importe_movimiento_v double precision, descripcion_v character varying, imprime_cheque_v boolean, no_a_la_orden_v boolean, alta_usr_v character varying, id_mov integer) OWNER TO postgres;

--

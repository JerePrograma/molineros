
 CREATE OR REPLACE FUNCTION inserta_movimiento_banco_item(
p_id_movimiento integer,
p_nro_cheque  numeric,
p_id_banco numeric,
p_id_estado_cheque_viejo integer,
p_id_estado_cheque_nuevo integer,
p_recibo_ingreso_id integer,
p_username character varying ) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN
insert into movimiento_banco_items (
    id_movimiento,	nro_cheque,	id_banco,	id_estado_cheque_viejo,	id_estado_cheque_nuevo,	recibo_ingreso_id,
    alta_fecha,    alta_usr ,    modi_fecha,    modi_usr  )
values ( p_id_movimiento, p_nro_cheque, p_id_banco, p_id_estado_cheque_viejo, p_id_estado_cheque_nuevo, p_recibo_ingreso_id, localtimestamp, p_username, localtimestamp, p_username  );


return 1;
END;
$BODY$;


ALTER FUNCTION public.inserta_movimiento_banco_item(
p_id_movimiento integer,
p_nro_cheque  numeric,
p_id_banco numeric,
p_id_estado_cheque_viejo integer,
p_id_estado_cheque_nuevo integer,
p_recibo_ingreso_id integer,
p_username character varying ) OWNER TO postgres;

--
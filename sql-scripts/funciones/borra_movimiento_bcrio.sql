CREATE OR REPLACE FUNCTION borra_movimiento_bcrio(id_mov integer,
 user_baja character varying) 
RETURNS integer
    LANGUAGE sql
    AS $BODY$
	update movimiento_banco
	set baja_fecha=fecha_comprobante,
	    baja_usr=$2,
	    modi_fecha=localtimestamp,
	    modi_usr=$2
	where id_movimiento=$1;
	
	update cheque  c set id_estado = mbi.id_estado_cheque_viejo
	from movimiento_banco_items mbi
	where c.nro_cheque = mbi.nro_cheque
	and c.id_banco = mbi.id_banco
	and id_movimiento = $1;


	update recibo_ingresos ri set id_estado_efectivo = 2 
	from movimiento_banco_items mbi
	where ri.id = mbi.recibo_ingreso_id
	and id_movimiento = $1;

	
	select 1;
$BODY$;


ALTER FUNCTION public.borra_movimiento_bcrio(id_mov integer, user_baja character varying) OWNER TO postgres;

--

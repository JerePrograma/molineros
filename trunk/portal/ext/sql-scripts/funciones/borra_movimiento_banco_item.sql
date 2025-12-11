CREATE OR REPLACE FUNCTION borra_movimiento_banco_item(p_id_mov_bcrio integer, p_id_mov_bcrio_item integer)
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
 begin
    delete from movimiento_banco_items where id_movimiento = $1 and id = $2;

	return 1;
  end;  
$BODY$;


ALTER FUNCTION public.borra_movimiento_banco_item(p_id_mov_bcrio integer, p_id_mov_bcrio_item integer) OWNER TO postgres;

--

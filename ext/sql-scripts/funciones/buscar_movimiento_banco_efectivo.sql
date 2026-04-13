DROP FUNCTION buscar_movimiento_banco_efectivo(p_id_mov_bcrio integer) ;
CREATE OR REPLACE FUNCTION buscar_movimiento_banco_efectivo(p_id_mov_bcrio integer) 
RETURNS TABLE(id_recibo_ingreso integer, importe numeric, fecha date, id_estado_efectivo integer, descripcion character varying, mbi__id integer)
    LANGUAGE sql
    AS $BODY$
select  ri.id, ri.importe, ri.fecha, ri.id_estado_efectivo, es.descripcion, mbi.id
 from movimiento_banco_items mbi
 inner join recibo_ingresos ri
 on mbi.recibo_ingreso_id = ri.id
 inner join efectivo_estado es
 on  ri.id_estado_efectivo = es.id
 where mbi.id_movimiento = $1;

$BODY$;


ALTER FUNCTION buscar_movimiento_banco_efectivo(p_id_mov_bcrio integer)  OWNER TO postgres;

--

-- Function: buscar_recibo_no_os_efectivo_estado(integer)

-- DROP FUNCTION buscar_recibo_no_os_efectivo_estado(integer);

CREATE OR REPLACE FUNCTION buscar_recibo_no_os_efectivo_estado(IN p_estado_id integer)
  RETURNS TABLE(ri__id integer, ri__recibo_id integer, ri__nro_cheque numeric, ri__id_banco integer, ri__numero_deposito character varying, ri__importe numeric, ri__fecha date, ri__id_estado_efectivo integer, ri__alta_fecha timestamp without time zone, ri__alta_usr character varying, ri__modi_fecha timestamp without time zone, ri__modi_usr character varying, ri__baja_fecha timestamp without time zone, ri__baja_usr character varying, ri__id_cuenta_bcria_destino_deposito integer, ri__id_recibo_ingreso_tipo_deposito integer) AS
$BODY$

select 	ri.id  ,
	ri.recibo_id  ,
	ri.nro_cheque ,
	ri.id_banco ,
	ri.numero_deposito,
	ri.importe,
	ri.fecha,
	ri.id_estado_efectivo,
 	ri.alta_fecha,
    ri.alta_usr,
    ri.modi_fecha,
    ri.modi_usr,
    ri.baja_fecha,
    ri.baja_usr,
    ri.id_cuenta_bcria_destino_deposito,
    ri.id_recibo_ingreso_tipo_deposito
from recibo_no_os_ingresos ri
where id_banco is null
and numero_deposito is null
and nro_cheque is null
and id_estado_efectivo = $1;
	
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION buscar_recibo_no_os_efectivo_estado(integer)
  OWNER TO postgres;


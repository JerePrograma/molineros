DROP FUNCTION buscar_recibo_efectivo_estado(p_estado_id integer) ;
CREATE OR REPLACE FUNCTION buscar_recibo_efectivo_estado(p_estado_id integer) 
RETURNS TABLE(
    ri__id integer ,
	ri__recibo_id integer ,
	ri__nro_cheque numeric,
	ri__id_banco integer,
	ri__numero_deposito character varying (30),
	ri__importe numeric(12,2),
	ri__fecha date,
	ri__id_estado_efectivo integer,
 	ri__alta_fecha timestamp without time zone ,
    ri__alta_usr character varying(50) ,
    ri__modi_fecha timestamp without time zone ,
    ri__modi_usr character varying(50) ,
    ri__baja_fecha timestamp without time zone,
    ri__baja_usr character varying(50),
    ri__id_cuenta_bcria_destino_deposito integer,
    ri__id_recibo_ingreso_tipo_deposito integer
    )
    LANGUAGE sql
    AS $BODY$

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
from recibo_ingresos ri
where id_banco is null
and numero_deposito is null
and nro_cheque is null
and id_estado_efectivo = $1;
	
$BODY$;


ALTER FUNCTION public.buscar_recibo_efectivo_estado(p_estado_id integer)    OWNER TO postgres;

--

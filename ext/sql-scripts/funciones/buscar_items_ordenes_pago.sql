CREATE OR REPLACE FUNCTION buscar_items_ordenes_pago(p_id_orden_pago numeric) 
RETURNS TABLE(lfa__fecha timestamp without time zone,
 lfa__periodo timestamp without time zone,
 lfa__nro_liquidacion integer,
 lfa__nro_prestador character varying,
 lfa__farmacia character varying,
 lfa__nro_recetario character varying,
 lfa__nro_troquel character varying,
 lfa__medicamento character varying,
 lfa__cantidad integer,
 lfa__pvp numeric,
 lfa__total_ospim numeric,
 lfa__total_amtima numeric,
 lfa__debito character varying,
 lfa__dif_ospim numeric,
 lfa__dif_amtima numeric,
 lfa__id_ospim integer,
 lfa__id_amtima integer,
 lfa__id_uoma integer,
 lfa__inte integer,
 lfa__nombre_apellido character varying,
 lfa__alta_fecha timestamp without time zone,
 lfa__alta_usr character varying,
 lfa__alta_ip character varying,
 lfa__modi_fecha timestamp without time zone,
 lfa__modi_usr character varying,
 lfa__modi_ip character varying,
 lfa__baja_fecha timestamp without time zone,
 lfa__baja_usr character varying,
 lfa__baja_ip character varying,
 lfa__prestador character varying,
 lfa__porcentaje_ospim double precision,
 lfa__porcentaje_amtima double precision,
 lfa__pmi character varying,
 lfa__nro_farmacia integer)
    LANGUAGE sql
    AS $BODY$


select  lfa.fecha,
	lfa.periodo,
	lfa.nro_liquidacion,
	lfa.nro_prestador,
	lfa.farmacia,
	lfa.nro_recetario,
	lfa.nro_troquel,
	lfa.medicamento,
	lfa.cantidad,
	lfa.pvp,
	lfa.total_ospim,
	lfa.total_amtima,
	lfa.debito,
	lfa.dif_ospim,
	lfa.dif_amtima,
	lfa.id_ospim,
	lfa.id_amtima,
	lfa.id_uoma,
	lfa.inte,
	lfa.nombre_apellido,
	lfa.alta_fecha,
	lfa.alta_usr,
	lfa.alta_ip,
	lfa.modi_fecha,
	lfa.modi_usr,
	lfa.modi_ip,
	lfa.baja_fecha,
	lfa.baja_usr,
	lfa.baja_ip,
	lfa.prestador,
	lfa.porcentaje_ospim,
	lfa.porcentaje_amtima,
	lfa.pmi,
	lfa.nro_farmacia
from liquidacion_farmacia_amtima lfa
where orden_pago_amtima_id = $1

$BODY$;


ALTER FUNCTION public.buscar_items_ordenes_pago(p_id_orden_pago numeric) OWNER TO postgres;

--

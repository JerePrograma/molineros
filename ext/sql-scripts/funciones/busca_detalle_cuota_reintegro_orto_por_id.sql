 drop function busca_detalle_cuota_reintegro_orto_por_id(id integer) 

CREATE OR REPLACE FUNCTION busca_detalle_cuota_reintegro_orto_por_id(id integer) 
RETURNS TABLE(
    dc_id_cuota integer,  
    dc_id_reintegro integer,
    dc_nro_cuota smallint,
    dc_fecha timestamp without time zone,
    dc_periodo timestamp without time zone,
    dc_porcentaje smallint,
    dc_importe numeric,
    dc_diagnostico character varying,
    dc_plan_tratamiento character varying,
    dc_tiempo_estimado character varying,
    dc_pronostico character varying,
    dc_informe character varying,
    dc_compro_a_debitar_tipo character varying,
    dc_compro_a_debitar_numero character varying,
    dc_estado integer,
    op_id integer
 )
    LANGUAGE sql
    AS $BODY$

select
    dc.id_cuota,
    dc.id_reintegro,
    dc.nro_cuota,
    dc.fecha,
    dc.periodo,
    dc.porcentaje,
    dc.importe,
    dc.diagnostico,
    dc.plan_tratamiento,
    dc.tiempo_estimado,
    dc.pronostico,
    dc.informe,
    dc.compro_a_debitar_tipo,
    dc.compro_a_debitar_numero,
    dc.estado,
    opo.id_orden_pago

    from detalle_cuota dc
   	left outer join lista_reintegro_pago_detalle opor	
	on dc.id_cuota = opor.id_reintegro --en este caso el join se hace por detalle_cuenta 	
	left outer join orden_pago_ospim_lista_reintegros opol
	on opor.id_lista_reintegro_pago = opol.id_lista_reintegro_pago
	and opol.baja_fecha is null
	left outer join orden_pago_ospim opo
	on opol.id_orden_pago_ospim = opo.id_orden_pago
	and opo.baja_fecha is null
    
    where dc.id_reintegro = $1
    
$BODY$;
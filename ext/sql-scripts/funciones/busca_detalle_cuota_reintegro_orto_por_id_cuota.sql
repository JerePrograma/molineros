drop function busca_detalle_cuota_reintegro_orto_por_id_cuota(id integer) 

CREATE OR REPLACE FUNCTION busca_detalle_cuota_reintegro_orto_por_id_cuota(id integer) 
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
    dc_estado integer
 )
    LANGUAGE sql
    AS $BODY$

select
    id_cuota,
    id_reintegro,
    nro_cuota,
    fecha,
    periodo,
    porcentaje,
    importe,
    diagnostico,
    plan_tratamiento,
    tiempo_estimado,
    pronostico,
    informe,
    compro_a_debitar_tipo,
    compro_a_debitar_numero,
    estado

    from detalle_cuota 
    where id_cuota = $1
    
$BODY$;
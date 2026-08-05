alter table tramtamiento_discapacidad add column cantidad_viajes_mes numeric;
alter table tramtamiento_discapacidad add column cantidad_kilometros_dia numeric;
alter table tramtamiento_discapacidad add column cantidad_kilometros_mes numeric; --total
alter table tramtamiento_discapacidad add column importe_kilometro_unit numeric(9,2); 
--total mes y total dia
alter table tramtamiento_discapacidad add column hs_espera_dia numeric; --total
alter table tramtamiento_discapacidad add column hs_espera_mes numeric; --total
alter table tramtamiento_discapacidad add column importe_hs_espera_unit numeric(9,2);

-- Function: actualiza_tratamiento_discapacidad(integer, character varying, integer, numeric, character varying, timestamp without time zone, timestamp without time zone, character varying, numeric, integer, integer, character varying, boolean, integer)

-- DROP FUNCTION actualiza_tratamiento_discapacidad(integer, character varying, integer, numeric, character varying, timestamp without time zone, timestamp without time zone, character varying, numeric, integer);
-- DROP FUNCTION actualiza_tratamiento_discapacidad(integer, character varying, integer, numeric, character varying, timestamp without time zone, timestamp without time zone, character varying, numeric, integer, integer, character varying, boolean, integer);

CREATE OR REPLACE FUNCTION actualiza_tratamiento_discapacidad(p_id_prestacion integer, p_cuil_titular character varying, p_inte integer, p_cantidad numeric, p_periodicidad character varying, p_periodo_desde timestamp without time zone, p_periodo_hasta timestamp without time zone, p_usuario character varying, p_importe_total numeric, p_id_tratamiento integer, p_id_prestador integer, p_observaciones character varying, p_recupera_ape boolean, p_estado integer,
p_cuit character varying, p_prestador character varying, p_id_seccional character varying,
p_cantidad_viajes_mes numeric, p_cantidad_kilometros_dia numeric, p_cantidad_kilometros_mes numeric, p_importe_kilometro_unit numeric,
p_hs_espera_dia numeric, p_hs_espera_mes numeric, p_importe_hs_espera_unit numeric,
p_importe_tercerizado numeric, p_id_tercerizadora character varying
)
  RETURNS integer AS
$BODY$
declare resultDom integer;
BEGIN

update tratamiento_discapacidad
	set 
    id_prestacion = p_id_prestacion,
    cantidad = p_cantidad,
    periodicidad = p_periodicidad,
    periodo_desde = p_periodo_desde,
    periodo_hasta = p_periodo_hasta,
    importe_total = p_importe_total,        
    modi_fecha = LOCALTIMESTAMP,
    modi_usr = p_usuario,
    id_prestador = p_id_prestador,
    observaciones = p_observaciones,
    recupera_ape = p_recupera_ape,
    cuit = p_cuit,
    prestador = p_prestador,
    id_seccional = p_id_seccional,
    estado = p_estado,
    cantidad_viajes_mes = p_cantidad_viajes_mes,
    cantidad_kilometros_dia = p_cantidad_kilometros_dia,
    cantidad_kilometros_mes = p_cantidad_kilometros_mes,
    importe_kilometro_unit = p_importe_kilometro_unit,
    hs_espera_dia = p_hs_espera_dia,
    hs_espera_mes = p_hs_espera_mes,
    importe_hs_espera_unit = p_importe_hs_espera_unit,
    importe_tercerizado = p_importe_tercerizado,
    id_tercerizadora = p_id_tercerizadora
    
where id_tratamiento = p_id_tratamiento;
    return 1;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;

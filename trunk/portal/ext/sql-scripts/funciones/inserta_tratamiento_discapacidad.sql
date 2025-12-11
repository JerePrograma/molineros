-- Function: inserta_tratamiento_discapacidad(integer, character varying, integer, numeric, character varying, timestamp without time zone, timestamp without time zone, numeric, character varying, integer, character varying, boolean, integer);

CREATE OR REPLACE FUNCTION inserta_tratamiento_discapacidad

(p_id_prestacion integer, 
p_cuil_titular character varying, 
p_inte integer, 
p_cantidad numeric,
p_periodicidad character varying, 
p_periodo_desde timestamp without time zone,
p_periodo_hasta timestamp without time zone,
p_importe_total numeric, 
p_usuario character varying,
p_id_prestador integer, 
p_observaciones character varying, 
p_recupera_ape boolean, 
p_estado integer,
p_cuit character varying, 
p_prestador character varying, 
p_id_seccional character varying,
p_cantidad_viajes_mes numeric, p_cantidad_kilometros_dia numeric, p_cantidad_kilometros_mes numeric, p_importe_kilometro_unit numeric,
p_hs_espera_dia numeric, p_hs_espera_mes numeric, p_importe_hs_espera_unit numeric,
p_importe_tercerizado numeric, p_id_tercerizadora character varying 
)

  RETURNS integer AS
$BODY$
declare resultDom integer;
BEGIN

INSERT INTO tratamiento_discapacidad(    
    id_prestacion,
    cuil_titular,
    inte,
    cantidad,
    periodicidad,
    periodo_desde,
    periodo_hasta,
    importe_total,        
    alta_fecha,
    alta_usr,
    modi_fecha,
    modi_usr,
    id_prestador,
    observaciones,
    recupera_ape,
    estado,
    cuit,
    prestador,
    id_seccional,
    cantidad_viajes_mes,
    cantidad_kilometros_dia,
    cantidad_kilometros_mes,
    importe_kilometro_unit,
    hs_espera_dia,
    hs_espera_mes,
    importe_hs_espera_unit,
    importe_tercerizado,
    id_tercerizadora
			)
    VALUES (p_id_prestacion, p_cuil_titular, p_inte, 
    		p_cantidad, p_periodicidad, p_periodo_desde, p_periodo_hasta, 
    		p_importe_total, LOCALTIMESTAMP, p_usuario, LOCALTIMESTAMP, p_usuario, p_id_prestador,
	p_observaciones, p_recupera_ape, p_estado,p_cuit,p_prestador,p_id_seccional,
	p_cantidad_viajes_mes, p_cantidad_kilometros_dia, p_cantidad_kilometros_mes, p_importe_kilometro_unit,
	p_hs_espera_dia, p_hs_espera_mes, p_importe_hs_espera_unit, p_importe_tercerizado, p_id_tercerizadora
	);

return currval('tratamiento_discapacidad_id_seq');
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;

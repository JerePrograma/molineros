CREATE OR REPLACE FUNCTION inserta_contrato_detalle

(IN p_id_contrato integer, IN p_fecha_desde timestamp without time zone, IN p_fecha_hasta timestamp without time zone,  
IN id_prestacion_desde integer, IN p_codigo_desde character varying, IN id_prestacion_hasta integer, IN p_codigo_hasta character varying,
IN p_id_plan integer, IN p_id_cartilla integer, 
IN p_coseguro numeric, IN p_tipo_valorizacion character varying, IN p_honorarios numeric, IN p_gastos numeric, IN p_importe_total numeric, 
IN p_servicio character varying, IN p_usuario character varying)

  RETURNS integer AS
$BODY$

BEGIN

INSERT INTO contrato_detalle(
            id_contrato, fecha_desde, fecha_hasta, 
            id_prestacion_desde, codigo_desde, id_prestacion_hasta, codigo_hasta, 
            id_plan, id_cartilla, 
            coseguro, tipo_valorizacion, honorarios, gastos, importe_total, 
            alta_fecha, alta_usr, modi_fecha, modi_usr, baja_fecha, baja_usr, 
            servicio)
    VALUES (p_id_contrato, p_fecha_desde, p_fecha_hasta, 
		    id_prestacion_desde, p_codigo_desde, id_prestacion_hasta, p_codigo_hasta, 
		    p_id_plan, p_id_cartilla, 
            p_coseguro, p_tipo_valorizacion, p_honorarios, p_gastos, p_importe_total, 
            LOCALTIMESTAMP, p_usuario, LOCALTIMESTAMP, p_usuario, null, null, 
            p_servicio);

return currval('contrato_detalle_id_seq');
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
-- DROP FUNCTION inserta_prestacion(id_liquidacion_v integer, cuil_titular_v character varying, inte_v integer, id_prestacion_v integer, fecha_pres_v timestamp without time zone, cantidad_v numeric, importe_v double precision, servicio_v character varying, solicitado_v double precision, debitado_v double precision, resultado_v double precision, tercerizado_v character varying, usuario character varying, periodo_v timestamp without time zone);

CREATE OR REPLACE FUNCTION inserta_prestacion(id_liquidacion_v integer, cuil_titular_v character varying, inte_v integer, id_prestacion_v integer, fecha_pres_v timestamp without time zone, cantidad_v numeric, importe_v double precision, servicio_v character varying, solicitado_v double precision, debitado_v double precision, resultado_v double precision, tercerizado_v character varying, usuario character varying, periodo_v timestamp without time zone, motivo_alta_discapacidad_v integer)
  RETURNS integer AS
$BODY$
  declare orden_p integer;
  begin

    orden_p=max(orden)+1 from liquidacion_prestacion where id_liquidacion=id_liquidacion_v;

    if orden_p is null or orden_p <1 then
	orden_p=1;
    end if;

    INSERT INTO liquidacion_prestacion(
            id_liquidacion, orden, cuil_titular, inte, id_prestacion, fecha_prestacion, 
            cantidad, importe, servicio, solicitado, debitado, resultado, 
            tercerizado,  alta_usr, alta_fecha, periodo, motivo_alta_discapacidad)
    VALUES (id_liquidacion_v, orden_p, cuil_titular_v, inte_v, id_prestacion_v, fecha_pres_v, cantidad_v, importe_v, servicio_v, solicitado_v,
            debitado_v, resultado_v, tercerizado_v, usuario, current_timestamp, periodo_v, motivo_alta_discapacidad_v);            
 
  return orden_p;
  end;  
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
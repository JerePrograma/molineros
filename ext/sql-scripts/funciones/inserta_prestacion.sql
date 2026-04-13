 DROP FUNCTION inserta_prestacion(integer, character varying, character varying, integer, integer, timestamp without time zone, numeric, numeric, character varying, character varying, character varying, timestamp without time zone, character varying, character varying, timestamp without time zone, character varying, character varying, 
timestamp without time zone, numeric);

CREATE OR REPLACE FUNCTION inserta_prestacion(integer, character varying, character varying, integer, integer, timestamp without time zone, numeric, numeric, character varying, character varying, character varying, timestamp without time zone, character varying, character varying, timestamp without time zone, character varying, character varying, 
timestamp without time zone, numeric, integer
)
  RETURNS integer AS
$BODY$
    
declare importe_total numeric(10,2);
    
  begin
  insert into reintegro_prestacion(
  id_reintegro,
  cuit,
  descripcion,
  id_prestacion,
  id_plan,
  fecha_prestacion,
  cantidad,
  importe,
  compro_a_debitar_tipo,
  compro_a_debitar_numero,
  tercerizado,
  alta_fecha,
  alta_usr,
  modi_fecha,
  modi_usr,
  codigo,
  periodo,
  cuit_entidad,
  sucursal_entidad,
  fecha_comprobante,
  importe_comprobante,
  motivo_alta_discapacidad
  )
  
  values ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12,$13,$12,$13,$14,$15,$16,$17,$18,$19,$20);
  
  importe_total = sum(importe * cantidad) from reintegro_prestacion where id_reintegro = $1;
  update lista_reintegro_pago_detalle set importe = importe_total where id_reintegro = $1 and tipo_reintegro != 'ort';
     
  return 1;
  end;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;

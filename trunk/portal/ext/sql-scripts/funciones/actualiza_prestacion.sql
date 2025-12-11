DROP FUNCTION actualiza_prestacion(
integer, 
character varying, 
character varying, 
integer, 
integer, 
timestamp without time zone, 
numeric, 
numeric, 
character varying,
character varying, 
character varying, 
timestamp without time zone, 
character varying, 
character varying, 
integer, 
character varying, 
timestamp without time zone,
character varying,
character varying,
timestamp without time zone,
numeric);

------------------------
------------------------

CREATE OR REPLACE FUNCTION actualiza_prestacion(

integer, 
character varying, 
character varying, 
integer, 
integer, 
timestamp without time zone, 
numeric, 
numeric, 
character varying,
character varying, 
character varying, 
timestamp without time zone, 
character varying, 
character varying, 
integer, 
character varying, 
timestamp without time zone,
character varying,
character varying,
timestamp without time zone,
numeric,
integer
)
  RETURNS integer AS
$BODY$
declare importe_total numeric(10,2);

  begin
  update reintegro_prestacion 
  set cuit = $2,  
  descripcion = $3,  
  fecha_prestacion = $6,
  cantidad = $7,
  importe = $8,
  compro_a_debitar_tipo = $9,
  compro_a_debitar_numero = $10,
  tercerizado = $11,
  modi_usr = $13,
  modi_fecha = localtimestamp,
  id_prestacion = $4,
  codigo = $14,
  periodo = $17,
  cuit_entidad = $18,
  sucursal_entidad = $19,
  fecha_comprobante = $20,
  importe_comprobante = $21,
  motivo_alta_discapacidad = $22
    
  where id_reintegro = $1
  and id_prestacion=$15
  and id_plan = $5
  and alta_fecha = $12;

  importe_total = sum(importe * cantidad) from reintegro_prestacion where id_reintegro = $1;
  update lista_reintegro_pago_detalle set importe = importe_total where id_reintegro = $1 and tipo_reintegro != 'ort';

  return 1;
  end;  
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
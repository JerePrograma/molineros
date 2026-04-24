-- Function: actualiza_prestacion_liquidacion(integer, integer, timestamp without time zone, character varying, character varying, integer, integer, integer, numeric, character varying, character varying, timestamp without time zone)

DROP FUNCTION actualiza_prestacion_liquidacion( 
integer, 
integer, 
timestamp without time zone, 
character varying, 
character varying, 
integer, 
integer, 
numeric, 
numeric, 
character varying, 
character varying, 
timestamp without time zone,
timestamp without time zone);

CREATE OR REPLACE FUNCTION actualiza_prestacion_liquidacion(

integer, 
integer, 
timestamp without time zone, 
character varying, 
character varying, 
integer, 
integer, 
numeric, 
numeric, 
character varying, 
character varying, 
timestamp without time zone,
integer
)

  RETURNS integer AS
$BODY$ 
  begin
  update liquidacion_prestacion 
  set 
  
  fecha_prestacion = $3,
  servicio = $4,
  cuil_titular = $5,
  inte = $6,  
  id_prestacion = $7,  
  cantidad = $8,
  importe = $9,
  tercerizado = $10,
  modi_usr = $11,
  modi_fecha = localtimestamp,
  periodo = $12,
  motivo_alta_discapacidad = $13
  where id_liquidacion = $1
  and orden =$2;
  return 1;
  end;  
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
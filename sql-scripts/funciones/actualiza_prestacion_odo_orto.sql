-- Function: actualiza_prestacion_odo_orto(integer, character varying, character varying, integer, integer, timestamp without time zone, integer, numeric, character varying, character varying, character varying, timestamp without time zone, character varying, character varying, integer, character varying, integer, character varying, integer, numeric, integer)

-- DROP FUNCTION actualiza_prestacion_odo_orto(integer, character varying, character varying, integer, integer, timestamp without time zone, integer, numeric, character varying, character varying, character varying, timestamp without time zone, character varying, character varying, integer, character varying, integer, character varying, integer, numeric, integer);

CREATE OR REPLACE FUNCTION actualiza_prestacion_odo_orto(integer, character varying, character varying, integer, integer, timestamp without time zone, numeric, numeric, character varying, character varying, character varying, timestamp without time zone, character varying, character varying, integer, character varying, integer, character varying, integer, numeric, integer)
  RETURNS integer AS
$BODY$
  
  begin
  update reintegro_prestacion_odo_orto
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
  pieza = $17,
  cara = $18,
  id_prestador_externo = $19,
  honorarios=$20,
  nro_cuotas=$21
  where id_reintegro = $1
  and id_prestacion=$15
  and id_plan = $5
  and alta_fecha = $12;
  return 1;
  end;  
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;

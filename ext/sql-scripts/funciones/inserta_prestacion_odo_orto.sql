-- Function: inserta_prestacion_odo_orto(integer, character varying, character varying, integer, integer, timestamp without time zone, integer, numeric, character varying, character varying, character varying, timestamp without time zone, character varying, character varying, integer, character varying, integer, numeric, integer)

-- DROP FUNCTION inserta_prestacion_odo_orto(integer, character varying, character varying, integer, integer, timestamp without time zone, integer, numeric, character varying, character varying, character varying, timestamp without time zone, character varying, character varying, integer, character varying, integer, numeric, integer);

CREATE OR REPLACE FUNCTION inserta_prestacion_odo_orto(integer, character varying, character varying, integer, integer, timestamp without time zone, numeric, numeric, character varying, character varying, character varying, timestamp without time zone, character varying, character varying, integer, character varying, integer, numeric, integer)
  RETURNS integer AS
$BODY$
    declare estado_actual integer;
    declare tiene_prestacion integer;
  begin

  estado_actual = (select estado from reintegro where id_reintegro = $1);
  tiene_prestacion = (select 1 from reintegro_prestacion_odo_ort where id_reintegro = $1);

  
  if (tiene_prestacion is not null) then
  	delete from reintegro_prestacion_odo_ort where id_reintegro = $1;
  end if;
  
	  insert into reintegro_prestacion_odo_ort(
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
	  pieza,
	  cara,
	  id_prestador_externo,
	  honorarios,
	  nro_cuotas
	  )  
	  values ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12,$13,$12,$13,$14,$15,$16,$17,$18,$19);    
   
  return 1;
  end;  
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;


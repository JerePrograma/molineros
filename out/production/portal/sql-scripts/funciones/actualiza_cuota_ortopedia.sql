CREATE OR REPLACE FUNCTION actualiza_cuota_ortopedia(
 timestamp without time zone,
 timestamp without time zone, 
 character varying,
 character varying,
 character varying,
 character varying,
 character varying,
 character varying,
 character varying,
 character varying,
 integer,
 integer

) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
  begin
	
update detalle_cuota
  set fecha = $1, periodo = $2, diagnostico = $3, plan_tratamiento = $4, tiempo_estimado = $5, pronostico= $6,
  informe = $7, compro_a_debitar_tipo = $8, compro_a_debitar_numero = $9 
  where id_reintegro = $11 and nro_cuota = $12;	  
  
  return 1;
  end;  
$BODY$;

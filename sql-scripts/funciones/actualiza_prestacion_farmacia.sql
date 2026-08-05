-- Function: actualiza_prestacion_farmacia(integer, integer, integer, integer, numeric, numeric, numeric, numeric, character varying)

-- DROP FUNCTION actualiza_prestacion_farmacia(integer, integer, integer, integer, numeric, numeric, numeric, numeric, character varying);

CREATE OR REPLACE FUNCTION actualiza_prestacion_farmacia(integer, integer, integer, integer, numeric, numeric, numeric, numeric, character varying)
  RETURNS integer AS
$BODY$
declare importe_total numeric(10,2);

  begin

  update medicamento_reintegro_farmacia      
  set id_medicamento = $2,  
  cantidad = $3,
  troquel = $4,
  cober_ospim = $5,
  cober_amtima = $6,
  precio_al_publico = $7,
  total = $8,  
  modi_usr = $9,
  mod_fecha = localtimestamp
  where id = $1;
  
  importe_total = sum(rp.cantidad * (rp.monto_ospim + rp.monto_amtima)) from medicamento_reintegro_farmacia rp where rp.id = $1;
  update lista_reintegro_farmacia_pago_detalle set importe = importe_total where id_reintegro = $1;

  return 1;
  end;  
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION actualiza_prestacion_farmacia(integer, integer, integer, integer, numeric, numeric, numeric, numeric, character varying) OWNER TO postgres;

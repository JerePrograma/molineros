CREATE OR REPLACE FUNCTION actualiza_liquidacion_fecha(id_liquidacion_v integer, fecha_v timestamp without time zone, periodo_v timestamp without time zone, 
id_prestador_v integer, id_domicilio_v integer, compro_deb_tipo_v character varying, compro_deb_letra_v character varying, sucu_v integer, 
compro_a_debitar_nro_v character varying, fecha_emitido_v timestamp without time zone, fecha_recibido_v timestamp without time zone, 
fecha_vto_v timestamp without time zone, alta_usr_v character varying, importe_v numeric, debitado_v numeric, nroOC_v integer
observaciones_v character varying, tercerizado_v character varying, cuit_p character varying)
  RETURNS integer AS
$BODY$
  begin
  update liquidacion l 
  set
  fecha = fecha_v,
  periodo = periodo_v,
  id_prestador = id_prestador_v,
  id_domicilio = id_domicilio_v,

  compro_a_debitar_tipo = compro_deb_tipo_v,
  compro_a_debitar_letra = compro_deb_letra_v,
  sucu = sucu_v,
  compro_a_debitar_numero = compro_a_debitar_nro_v,
  
  fecha_emitido = fecha_emitido_v,
  fecha_recibido = fecha_recibido_v,
  fecha_vencimiento = fecha_vto_v,
  modi_fecha = localtimestamp,
  alta_usr = alta_usr_v,
  
  importe = importe_v,
  debitado = debitado_v,
  id_orden_compra = nroOC_v,
  observaciones = observaciones_v,
  tercerizado = tercerizado_v  
  where l.id_liquidacion = id_liquidacion_v;
  
  update prestador p set cuit = cuit_p where p.id_prestador = id_prestador_v;
  
  return 1;  
  end;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
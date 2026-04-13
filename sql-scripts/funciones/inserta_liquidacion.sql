CREATE OR REPLACE FUNCTION inserta_liquidacion(
 id_prestador_v integer,
 id_domicilio_v integer,
 fecha_v timestamp without time zone,
 periodo_v timestamp without time zone,
 estado_v integer,
 entidad_v character varying,
 compro_deb_tipo_v character varying,
 compro_deb_letra_v character varying,
 sucu_v integer,
 compro_a_debitar_nro_v character varying,
 fecha_emitido_v timestamp without time zone,
 fecha_recibido_v timestamp without time zone,
 fecha_vto_v timestamp without time zone,
 alta_usr_v character varying,
 tipo_reintegro_v character varying,
 importe_v numeric,
 debitado_v numeric,
 nro_oc_v integer,
 observaciones_v character varying,
 tercerizado_v character varying,
 cuit_p character varying
 ) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
  begin

  insert into liquidacion (
  id_prestador,
  id_domicilio,
  fecha, 
  periodo,
  estado,
  entidad,
  compro_a_debitar_tipo,
  compro_a_debitar_letra,
  sucu,
  compro_a_debitar_numero,  
  fecha_emitido,
  fecha_recibido,
  fecha_vencimiento,
  alta_fecha,
  alta_usr,    
  tipo_liquidacion,
  importe,
  debitado,
  id_orden_compra,
  observaciones,
  tercerizado
  )
  
  values (id_prestador_v,id_domicilio_v,fecha_v,periodo_v,estado_v,entidad_v,compro_deb_tipo_v,compro_deb_letra_v,sucu_v,compro_a_debitar_nro_v,
  fecha_emitido_v, fecha_recibido_v,fecha_vto_v, current_timestamp,alta_usr_v,tipo_reintegro_v,importe_v,debitado_v, nro_oc_v, 
  observaciones_v,tercerizado_v); 
  
  update prestador p set cuit = cuit_p where p.id_prestador =  id_prestador_v;
  
  return currval('liquidacion_id_seq');
  end;  
$BODY$;
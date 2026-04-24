CREATE OR REPLACE FUNCTION insertar_item_orden_pago(p_fecha timestamp without time zone,
 p_periodo timestamp without time zone,
 p_orden_pago_amtima_id integer,
 p_nro_liquidacion integer,
 p_nro_prestador character varying,
 p_prestador character varying,
 p_nro_farmacia integer,
 p_farmacia character varying,
 p_idospim integer,
 p_idamtima integer,
 p_iduoma integer,
 p_inte integer,
 p_nombreapellido character varying,
 p_nrorecetario character varying,
 p_troquel character varying,
 p_medicamento character varying,
 p_cantidad integer,
 p_pvp numeric,
 p_totalospim numeric,
 p_totalamtima numeric,
 p_debito character varying,
 p_difospim numeric,
 p_difamtima numeric,
 p_porcentaje_ospim double precision,
 p_porcentaje_amtima double precision,
 p_pmi character varying,
 p_usr character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
  begin


INSERT INTO liquidacion_farmacia_amtima(
            fecha, periodo, orden_pago_amtima_id, nro_liquidacion, nro_prestador, farmacia, 
            nro_recetario, nro_troquel, medicamento, cantidad, pvp, total_ospim, 
            total_amtima, debito, dif_ospim, dif_amtima, id_ospim, id_uoma, id_amtima, inte, 
            alta_fecha, alta_usr, modi_fecha, modi_usr, nombre_apellido, prestador, nro_farmacia, porcentaje_ospim, porcentaje_amtima, pmi)
   VALUES (p_fecha, p_periodo, p_orden_pago_amtima_id, p_nro_liquidacion, p_nro_prestador, p_farmacia, p_nroRecetario, 
            p_troquel, p_medicamento, p_cantidad, p_pvp, p_totalOspim, p_totalAmtima, 
            p_debito, p_difOspim, p_difAmtima, p_idOspim, p_idUoma, p_idAmtima, p_inte, LOCALTIMESTAMP, 
            p_usr, LOCALTIMESTAMP, p_usr, p_nombreApellido, p_prestador, p_nro_farmacia, p_porcentaje_ospim, p_porcentaje_amtima, p_pmi );

  
 
  return  1;
  end;  
$BODY$;


ALTER FUNCTION public.insertar_item_orden_pago(p_fecha timestamp without time zone, p_periodo timestamp without time zone, p_orden_pago_amtima_id integer, p_nro_liquidacion integer, p_nro_prestador character varying, p_prestador character varying, p_nro_farmacia integer, p_farmacia character varying, p_idospim integer, p_idamtima integer, p_iduoma integer, p_inte integer, p_nombreapellido character varying, p_nrorecetario character varying, p_troquel character varying, p_medicamento character varying, p_cantidad integer, p_pvp numeric, p_totalospim numeric, p_totalamtima numeric, p_debito character varying, p_difospim numeric, p_difamtima numeric, p_porcentaje_ospim double precision, p_porcentaje_amtima double precision, p_pmi character varying, p_usr character varying) OWNER TO postgres;

--

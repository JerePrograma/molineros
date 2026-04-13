CREATE OR REPLACE FUNCTION trae_debitos_id_liquidacion(id_liquidacion integer) 
RETURNS TABLE(
 ci_id_punto_venta smallint,
 ci_tipo character varying,
 ci_nro character varying, 
 ci_item integer,
 ci_saldo numeric,
 ci_observaciones character varying,
 ci_alta_fecha timestamp without time zone,
 ci_alta_usr character varying,
 ci_modi_fecha timestamp without time zone,
 ci_modi_usr character varying,
 ci_baja_fecha timestamp without time zone,
 ci_baja_usr character varying,
 ci_cuit character varying,
 ci_compro_letra character varying,
 ci_compro_sucu integer,
 ci_motivo integer,
 ci_descripcion_motivo character varying
 )
    LANGUAGE sql
    AS $BODY$
    
    select ci.id_punto_venta, ci.compro_tipo, ci.compro_nro, ci.item, ci.saldo, ci.observaciones, 
			ci.alta_fecha, ci.alta_usr, ci.modi_fecha, ci.modi_usr, ci.baja_fecha, ci.baja_usr, ci.cuit, ci.compro_letra, ci.compro_sucu, ci.motivo,  m.descripcion
	  from comprobante_liquidacion cl, compro_items ci, motivo m
	  where cl.id_liquidacion = $1
	  and ci.id_punto_venta = cl.id_punto_venta
	  and ci.compro_tipo = cl.compro_tipo
	  and ci.compro_letra = cl.compro_letra
	  and ci.compro_sucu = cl.compro_sucu
	  and ci.compro_nro = cl.compro_nro
	  and ci.cuit = cl.cuit
	  and (ci.compro_tipo = 'NDB' or ci.compro_tipo = 'NDI')
	  and ci.compro_tipo = m.compro_tipo
	  and ci.motivo = m.id_motivo;
$BODY$;
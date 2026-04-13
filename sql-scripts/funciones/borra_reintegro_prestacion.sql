-- Function: borra_reintegro_prestacion(integer, integer, timestamp without time zone, integer, character varying, character varying, character varying)

-- DROP FUNCTION borra_reintegro_prestacion(integer, integer, timestamp without time zone, integer, character varying, character varying, character varying);

CREATE OR REPLACE FUNCTION borra_reintegro_prestacion(id_reintegro integer, id_prestacion integer, alta_fecha timestamp without time zone, id_plan integer, tipo_compro character varying, nro_compro character varying, username character varying)
  RETURNS integer AS
$BODY$
    declare importe_total numeric(10,2);
    
begin    
    delete from reintegro_prestacion rp
    where rp.id_reintegro=$1
    and rp.id_prestacion=$2
    and rp.alta_fecha=$3
    and rp.id_plan=$4
    and rp.compro_a_debitar_tipo=$5
    and rp.compro_a_debitar_numero=$6;
    
    importe_total = sum(rp.importe * rp.cantidad) from reintegro_prestacion rp where rp.id_reintegro = $1;
	update lista_reintegro_pago_detalle l set importe = importe_total where l.id_reintegro = $1 and l.tipo_reintegro != 'ort';

    return 1;
    end
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION borra_reintegro_prestacion(integer, integer, timestamp without time zone, integer, character varying, character varying, character varying)
  OWNER TO postgres;

drop function buscar_liquidaciones_debitos (p_fecha_ini date, p_fecha_fin date)

CREATE OR REPLACE FUNCTION buscar_liquidaciones_debitos(p_fecha_ini date, p_fecha_fin date)
RETURNS TABLE(
 	ldt_id_liquidacion integer,
	ldt_periodo_hasta timestamp without time zone,
	ldt_observaciones character varying,	
	ldt_alta_fecha timestamp without time zone,
    ldt_alta_usr character varying,
    ldt_modi_fecha timestamp without time zone,
    ldt_modi_usr character varying,
    ldt_baja_fecha timestamp without time zone,
    ldt_baja_usr character varying,
    ldt_numero_ndb integer
)
    LANGUAGE sql
    AS $BODY$
select
	ld.id_liquidacion,
	ld.periodo_hasta,
	ld.observaciones,	
	ld.alta_fecha,
    ld.alta_usr,
    ld.modi_fecha,
    ld.modi_usr,
    ld.baja_fecha,
    ld.baja_usr,
    ld.numero_ndb
from liquidacion_debitos_terceros ld
where ld.periodo_hasta >= $1 and ld.periodo_hasta <= $2;
$BODY$;
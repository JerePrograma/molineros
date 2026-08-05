CREATE OR REPLACE FUNCTION borra_reintegro_prestacion_odo_protesis(id_reintegro integer,
 id_prestacion integer,
 alta_fecha timestamp without time zone,
 id_plan integer,
 tipo_compro character varying,
 nro_compro character varying,
 username character varying)
RETURNS integer
    LANGUAGE sql
    AS $BODY$
    delete from reintegro_prestacion_odo_protesis
    where id_reintegro=$1
    and id_prestacion=$2
    and alta_fecha=$3    
    and id_plan=$4
    and compro_a_debitar_tipo=$5
    and compro_a_debitar_numero=$6;        
    select 1;
$BODY$;

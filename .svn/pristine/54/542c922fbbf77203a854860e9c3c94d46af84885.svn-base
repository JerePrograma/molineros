DROP FUNCTION edita_tercerizadora(character varying, integer, character varying, date, date, character varying);

CREATE OR REPLACE FUNCTION edita_tercerizadora(cuil_p character varying, inte_p integer, id_tercerizadora character varying, fecha_ingreso_p date, fecha_egreso_p date, username character varying, fecha_ingreso_original date)
  RETURNS integer AS
$BODY$
    UPDATE afi_tercerizadora_servicio
    set fecha_fin_pres=$5,
        fecha_inicio_pres=$4,
    modi_usr=$6,
    modi_fecha=current_timestamp
    where cuil_titular=$1
    and inte=$2
    and id_tercerizadora=$3    
    and fecha_inicio_pres=$7
    and baja_fecha is null;    
    select 1;
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100;
ALTER FUNCTION edita_tercerizadora(character varying, integer, character varying, date, date, character varying) OWNER TO postgres;

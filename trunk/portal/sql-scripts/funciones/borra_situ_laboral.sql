-- Function: borra_situ_laboral(character varying, integer, character varying, character varying, date, character varying)

-- DROP FUNCTION borra_situ_laboral(character varying, integer, character varying, character varying, date, character varying);

CREATE OR REPLACE FUNCTION borra_situ_laboral(cuil_p character varying, inte_p integer, cuit_p character varying, sucu_p character varying, fecha_ingreso_p date, username character varying)
  RETURNS integer AS
$BODY$
    update afi_situ_laboral
    set baja_usr=$6,
    baja_fecha=current_timestamp
    where cuil_titular=$1
    and inte=$2
    and cuit=$3
    and sucursal=$4
    and fecha_ingre=$5;
    select 1;
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100;
ALTER FUNCTION borra_situ_laboral(character varying, integer, character varying, character varying, date, character varying) OWNER TO postgres;

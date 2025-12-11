CREATE OR REPLACE FUNCTION edita_boletin(id_boletin_p integer, nombre_p character varying, asunto_p character varying, observaciones_p character varying, username_p character varying)
  RETURNS integer AS
$BODY$
BEGIN
update boletin
set nombre=$2,
    asunto=$3,
    observaciones=$4,
    modi_user=$5,
    modi_fecha=current_date
where id=$1;

return 0;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE

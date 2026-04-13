-- Function: actualizar_lista_destinatarios(integer, character varying, character varying, character varying)

-- DROP FUNCTION actualizar_lista_destinatarios(integer, character varying, character varying, character varying);

CREATE OR REPLACE FUNCTION actualizar_lista_destinatarios(id_lista_p integer, descripcion_p character varying, observaciones_p character varying, username_p character varying)
  RETURNS void AS
$BODY$
update mailing_list
set descripcion=$2,
    observaciones=$3,
    modi_user=$4
where id_mailing_list=$1
    
$BODY$
  LANGUAGE sql VOLATILE
  COST 100;
ALTER FUNCTION actualizar_lista_destinatarios(integer, character varying, character varying, character varying)
  OWNER TO postgres;


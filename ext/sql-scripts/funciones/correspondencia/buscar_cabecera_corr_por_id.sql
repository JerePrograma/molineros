CREATE OR REPLACE FUNCTION correo.buscar_cabecera_corr_por_id(IN p_numero_correspondencia integer)
  RETURNS TABLE(cab_id_correspondencia integer, cab_lugar character varying, cab_fecha_emision timestamp without time zone, cab_tipo_registro character varying, cab_tipo_envio character varying, cab_oblea character varying, cab_alta_fecha timestamp without time zone, cab_alta_usr character varying, cab_modi_fecha timestamp without time zone, cab_modi_usr character varying, cab_baja_fecha timestamp without time zone, cab_baja_usr character varying) AS
$BODY$
BEGIN

return query

select
  cab.id_correspondencia,  
  cab.lugar_recep_emision,
  cab.fecha,
  cab.tipo_registro,
  cab.tipo_envio,
  cab.oblea,
  cab.alta_fecha,
  cab.alta_usr,
  cab.modi_fecha,
  cab.modi_usr,
  cab.baja_fecha,
  cab.baja_usr
from correo.cabecera_correspondencia cab
where cab.id_correspondencia=p_numero_correspondencia;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 100;
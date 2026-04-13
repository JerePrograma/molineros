CREATE OR REPLACE FUNCTION buscar_opcion_sss_por_cuil(IN p_cuil character varying)
  RETURNS TABLE(opsss_id integer, opsss_tipo_exportacion character varying, opsss_delegacion character varying, opsss_libro integer, opsss_tomo integer, opsss_nro_formulario integer, opsss_os_elegida integer, opsss_regimen character varying, opsss_cuil character varying, opsss_apellido character varying, opsss_nombre character varying, opsss_sexo character varying, opsss_calle character varying, opsss_numero character varying, opsss_piso integer, opsss_departamento character varying, opsss_localidad character varying, opsss_telefono_particular character varying, opsss_telefono_laboral character varying, opsss_telefono_celular character varying, opsss_email character varying, opsss_os_anterior integer, opsss_cuit character varying, opsss_unifica_apo character varying, opsss_fecha_elecc date, opsss_fecha_certi date, opsss_cuil_conyuge character varying, opsss_ape_nom_conyuge character varying, opsss_fecha_entrega date, opsss_numero_lote integer, opsss_version_sistema character varying, opsss_postal_codi character varying, opsss_provincia character varying, opsss_alta_fecha timestamp without time zone, opsss_alta_usr character varying, opsss_modi_fecha timestamp without time zone, opsss_modi_usr character varying, opsss_fecha_exportacion date, opsss_id_delegacion integer, opsss_okdesdesss boolean) AS
$BODY$
BEGIN

return query

select
  aos.id,
  aos.tipo_exportacion,
  aos.delegacion,
  aos.libro,
  aos.tomo,
  aos.nro_formulario,
  aos.os_elegida,
  aos.regimen,
  aos.cuil,
--  aos.ape_nom,
  aos.apellido,
  aos.nombre,
  aos.sexo,
  aos.calle,
  aos.numero,
  aos.piso,
  aos.departamento,
  aos.localidad,
  aos.telefono_particular,
  aos.telefono_laboral,
  aos.telefono_celular,
  aos.email,
  aos.os_anterior,
  aos.cuit,
  aos.unifica_apo,
  aos.fecha_elecc,
  aos.fecha_certi,
  aos.cuil_conyuge,
  aos.ape_nom_conyuge,
  aos.fecha_entrega,
  aos.numero_lote,
  aos.version_sistema,
  aos.postal_codi,
  aos.provincia,
  aos.alta_fecha,
  aos.alta_usr,
  aos.modi_fecha,
  aos.modi_usr,
  aos.fecha_exportacion,
  aos.id_delegacion,
  aos.okdesdesss
  
from afi_opciones_sss aos 
where aos.cuil=p_cuil
and aos.baja_fecha is null;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
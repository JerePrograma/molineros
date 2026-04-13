CREATE OR REPLACE FUNCTION buscar_sector_empleados_acceso()
  RETURNS TABLE(
  ta_id integer, 
  ta_id_tarjeta_acceso integer, 
  ta_apellido character varying, 
  ta_nombre character varying, 
  ta_entidad character varying, 
  ta_sector character varying,
  ta_piso character,
  ta_legajo integer, 
  ta_horas_jornada numeric, 
  ta_alta_fecha timestamp without time zone, 
  ta_alta_usr character varying, 
  ta_modi_fecha timestamp without time zone, 
  ta_modi_usr character varying, 
  ta_baja_fecha timestamp without time zone, 
  ta_baja_usr character varying) AS
$BODY$

select

ta.id,
ta.id_tarjeta_acceso,
ta.apellido,
ta.nombre,
ta.entidad,
ta.sector, 
ta.piso, 
ta.legajo, 
ta.horas_jornada,
ta.alta_fecha,
ta.alta_usr,
ta.modi_fecha,
ta.modi_usr,
ta.baja_fecha,
ta.baja_usr

from tarjeta_acceso ta
where ta.sector is not null 
and baja_fecha is null 
order by ta.sector, ta.apellido, ta.nombre;
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
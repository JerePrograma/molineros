-- DROP FUNCTION buscar_informacion_usuario(date, date, character varying);

CREATE OR REPLACE FUNCTION buscar_informacion_usuarios(IN p_fecha_ini timestamp without time zone, IN p_fecha_fin timestamp without time zone, IN p_id_tarjeta_acceso character varying)
  RETURNS TABLE(ra_id integer, ra_id_tarjeta_acceso integer, ra_fecha_registro timestamp without time zone, ra_tipo_registro character varying, ra_punto_acceso integer, ra_alta_fecha timestamp without time zone, ra_alta_usr character varying, ra_modi_fecha timestamp without time zone, ra_modi_usr character varying, ra_baja_fecha timestamp without time zone, ra_baja_usr character varying, ta_id integer, ta_id_tarjeta_acceso integer, ta_apellido character varying, ta_nombre character varying, ta_entidad character varying, ta_legajo integer, ta_horas_jornada numeric, ta_alta_fecha timestamp without time zone, ta_alta_usr character varying, ta_modi_fecha timestamp without time zone, ta_modi_usr character varying, ta_baja_fecha timestamp without time zone, ta_baja_usr character varying) AS
$BODY$

select

ra.id,
ra.id_tarjeta_acceso,
ra.fecha_registro,
ra.tipo_registro,
ra.punto_acceso,
ra.alta_fecha,
ra.alta_usr,
ra.modi_fecha,
ra.modi_usr,
ra.baja_fecha,
ra.baja_usr,

ta.id,
ta.id_tarjeta_acceso,

ta.apellido,
ta.nombre,

ta.entidad,
ta.legajo, 
ta.horas_jornada,

ta.alta_fecha,
ta.alta_usr,
ta.modi_fecha,
ta.modi_usr,
ta.baja_fecha,
ta.baja_usr

from registro_acceso ra
left outer join tarjeta_acceso ta
on ra.id_tarjeta_acceso = ta.id_tarjeta_acceso

where 

ra.id_tarjeta_acceso != 99999999 and
($1 is null or ($1 is not null and ra.fecha_registro >= $1)) and
($2 is null or ($2 is not null and ra.fecha_registro <= $2)) and
($3 is null or ($3 is not null and cast(ra.id_tarjeta_acceso as character varying) = $3))

order by ta.apellido, ta.nombre, ra.fecha_registro asc;
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;


--ALTER FUNCTION buscar_informacion_usuario(date, date, character varying)
--  OWNER TO postgres;

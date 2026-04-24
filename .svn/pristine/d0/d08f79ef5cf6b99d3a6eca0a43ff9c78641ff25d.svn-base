CREATE OR REPLACE FUNCTION trae_tarjetas_acceso_vigentes() 
RETURNS TABLE
(
ta_id integer,
ta_id_tarjeta_acceso integer,
ta_apellido character varying,
ta_nombre character varying,
ta_entidad character varying,
ta_legajo integer, 
ta_horas_jornada decimal,
ta_alta_fecha timestamp without time zone,
ta_alta_usr character varying,
ta_modi_fecha timestamp without time zone,
ta_modi_usr character varying,
ta_baja_fecha timestamp without time zone,
ta_baja_usr character varying
)

LANGUAGE sql
AS $BODY$

select

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

from tarjeta_acceso ta
WHERE ta.baja_fecha is null

order by apellido, nombre
$BODY$;

--

CREATE OR REPLACE FUNCTION correo.trae_usuarios_correspondencias_vigentes() 
RETURNS TABLE
(
ta_id integer,
ta_screenname character varying,
ta_name character varying,
ta_lastname character varying,
ta_sector character varying,
ta_alta_fecha timestamp without time zone,
ta_alta_usr character varying,
ta_modi_fecha timestamp without time zone,
ta_modi_usr character varying,
ta_baja_fecha timestamp without time zone,
ta_baja_usr character varying,
ta_edificio character varying
)

LANGUAGE sql
AS $BODY$

select

ta.id,
ta.screenname,
ta.name,
ta.lastname,
ta.sector,
ta.alta_fecha,
ta.alta_usr,
ta.modi_fecha,
ta.modi_usr,
ta.baja_fecha,
ta.baja_usr,
ta.edificio

from correo.usuario_correspondencia ta
WHERE ta.baja_fecha is null

order by lastname, name
$BODY$;
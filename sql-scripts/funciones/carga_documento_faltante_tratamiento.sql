-- DROP FUNCTION inserta_tratamiento_discapacidad(integer, character varying, integer, numeric, character varying, timestamp without time zone, timestamp without time zone, numeric, character varying);

CREATE OR REPLACE FUNCTION carga_documento_faltante_tratamiento(
    d_id_tratamiento integer,
    d_id_documento integer,
	d_usuario character varying
)

RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
    
BEGIN    
   INSERT INTO documento_faltante_tratamiento(
            id_tratamiento, id_documento, alta_fecha, alta_usr)
    VALUES (d_id_tratamiento, d_id_documento, LOCALTIMESTAMP, d_usuario);

return d_id_tratamiento;
END;
$BODY$;

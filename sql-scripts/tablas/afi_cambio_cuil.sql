CREATE TABLE afi_cambio_cuil
(
  cuil_titular character varying(13) NOT NULL,
  inte integer NOT NULL,
  cuil character varying(13) NOT NULL,
  documento_tipo character varying(4) NOT NULL,
  documento_numero character varying(15) NOT NULL,
  vigen_fecha timestamp without time zone NOT NULL,
  cuil_titular_anterior character varying(13) NOT NULL,
  inte_anterior integer NOT NULL,
  cuil_anterior character varying(13) NOT NULL,
  documento_tipo_anterior character varying(4) NOT NULL,
  documento_numero_anterior character varying(15) NOT NULL,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(50) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(50) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(50)
)
WITH (
  OIDS=FALSE
);
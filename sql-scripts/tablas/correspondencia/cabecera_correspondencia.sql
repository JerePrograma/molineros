CREATE TABLE correo.cabecera_correspondencia
(
  id_correspondencia integer NOT NULL DEFAULT nextval('correo.correspondencia_id_seq'::regclass),
  lugar_recep_emision character varying,
  fecha timestamp without time zone,
  tipo_registro character varying,
  tipo_envio character varying,
  oblea character varying(50),
  alta_fecha timestamp without time zone,
  alta_usr character varying,
  modi_fecha timestamp without time zone,
  modi_usr character varying,
  baja_fecha timestamp without time zone,
  baja_usr character varying,
  CONSTRAINT pk_cabecera_correspondencia PRIMARY KEY (id_correspondencia )
)
WITH (
  OIDS=FALSE
);
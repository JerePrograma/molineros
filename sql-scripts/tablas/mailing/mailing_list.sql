
CREATE TABLE mailing_list
(
  id_mailing_list integer,
  descripcion character varying,
  alta_fecha timestamp without time zone,
  baja_fecha timestamp without time zone,
  modi_fecha timestamp without time zone,
  alta_user character varying,
  modi_user character varying,
  baja_user character varying,
  observaciones character varying,
  id serial NOT NULL,
  CONSTRAINT pk_mailing_list PRIMARY KEY (id )
)
WITH (
  OIDS=FALSE
);

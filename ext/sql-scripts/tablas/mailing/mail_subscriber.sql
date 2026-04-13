CREATE TABLE mail_subscriber
(
  id_subscriber integer,
  nombre character varying,
  apellido character varying,
  tratamiento character varying,
  email character varying,
  alta_user character varying,
  modi_user character varying,
  baja_user character varying,
  alta_fecha timestamp without time zone,
  baja_fecha timestamp without time zone,
  modi_fecha timestamp without time zone,
  id serial NOT NULL,
  casilla_prueba boolean,
  CONSTRAINT pk_subscriber PRIMARY KEY (id ),
  CONSTRAINT uq_email UNIQUE (email )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE mail_subscriber
  OWNER TO postgres;

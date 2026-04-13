CREATE TABLE contacto
(
  id_contacto integer NOT NULL DEFAULT nextval('contacto_id_seq'::regclass),
  email character varying,
  telefono character varying,
  apellido character varying,
  nombre character varying,
  alta_fecha timestamp without time zone,
  modi_fecha timestamp without time zone,
  baja_fecha timestamp without time zone,
  alta_user character varying,
  modi_usr character varying,
  baja_usr character varying,
  cargo character varying,
  tratamiento character varying,
  CONSTRAINT pk_contacto PRIMARY KEY (id_contacto )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE contacto
  OWNER TO postgres;

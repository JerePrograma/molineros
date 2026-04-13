-- Table: usuario_correspondencia

-- DROP TABLE usuario_correspondencia;

CREATE TABLE usuario_correspondencia
(
  id integer DEFAULT nextval('usuario_correspondencia_id_seq'::regclass),
  screenname character varying,
  name character varying,
  lastname character varying,
  sector character varying,
  alta_fecha timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
  alta_usr character varying(15) NOT NULL DEFAULT 'admin'::character varying,
  modi_fecha timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp without time zone,
  modi_usr character varying(15) NOT NULL DEFAULT 'admin'::character varying,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15)
  edificio character varying
)
WITH (
  OIDS=FALSE
);
ALTER TABLE usuario_correspondencia
  OWNER TO postgres;

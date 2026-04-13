-- Table: estudio_llamadas_empresas

-- DROP TABLE estudio_llamadas_empresas;

CREATE TABLE estudio_llamadas_empresas
(
  cuit character varying,
  fecha timestamp without time zone,
  tipo_contacto varchar,
  observaciones character varying,
  usuario character varying DEFAULT 'estudio'::character varying
)
WITH (
  OIDS=FALSE
);
ALTER TABLE estudio_llamadas_empresas
  OWNER TO postgres;


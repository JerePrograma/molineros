-- Table: estudio_empresas_info

-- DROP TABLE estudio_empresas_info;

CREATE TABLE estudio_empresas_info
(
  cuit varchar,
  estado text,
  fecha timestamp without time zone,
  molinera boolean DEFAULT false,
  carta_doc character varying,
  ubicacion_carpeta character varying
)
WITH (
  OIDS=FALSE
);
ALTER TABLE estudio_empresas_info
  OWNER TO postgres;

-- Index: index_estudio_empresa_info

-- DROP INDEX index_estudio_empresa_info;

CREATE INDEX index_estudio_empresa_info
  ON estudio_empresas_info
  USING btree
  (cuit );



-- Table: grupo

-- DROP TABLE grupo;

CREATE TABLE grupo
(
  id_grupo integer,
  descripcion character varying
)
WITH (
  OIDS=FALSE
);
ALTER TABLE grupo OWNER TO postgres;

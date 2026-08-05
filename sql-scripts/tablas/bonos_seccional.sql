-- Table: bonos_seccional

-- DROP TABLE bonos_seccional;

CREATE TABLE bonos_seccional
(
  tipo_bono integer NOT NULL,
  id_seccional integer,
  fecha_envio date,
  nro_bono integer NOT NULL,
  fecha_rendido date,
  CONSTRAINT pk_bono_seccional PRIMARY KEY (tipo_bono, nro_bono),
  CONSTRAINT fk_seccional FOREIGN KEY (id_seccional)
      REFERENCES seccional (id_seccional) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE bonos_seccional OWNER TO postgres;
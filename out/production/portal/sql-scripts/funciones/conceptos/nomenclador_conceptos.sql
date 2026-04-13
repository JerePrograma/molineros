-- Table: nomenclador_conceptos

-- DROP TABLE nomenclador_conceptos;

CREATE TABLE nomenclador_conceptos
(
  id_prestacion integer,
  codigo character varying(100),
  descripcion character varying(100),
  valido_desde date,
  valido_hasta date,
  id serial NOT NULL,
  tipo_id integer,
  concepto_id integer,
  modi_usr character varying,
  modi_fecha date,
  alta_usr character varying,
  alta_fecha date,
  CONSTRAINT pk_nomenclador_conceptos PRIMARY KEY (id),
  CONSTRAINT fk_nc_concepto FOREIGN KEY (concepto_id)
      REFERENCES concepto_maestro (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_nc_tipo FOREIGN KEY (tipo_id)
      REFERENCES nomenclador_concepto_tipo (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_nomenc_conceptos_prst FOREIGN KEY (id_prestacion)
      REFERENCES nomenclador (id_prestacion) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE nomenclador_conceptos OWNER TO postgres;
GRANT ALL ON TABLE nomenclador_conceptos TO postgres;

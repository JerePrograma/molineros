-- Table: prestacion_prestador

-- DROP TABLE prestacion_prestador;

CREATE TABLE prestacion_prestador
(
  id_prestacion integer NOT NULL,
  id_prestador integer NOT NULL,
  id_domicilio integer NOT NULL,
  CONSTRAINT pk_prestacion_prestador PRIMARY KEY (id_prestacion, id_prestador, id_domicilio),
  CONSTRAINT fk_prestaci_prest_nomenc FOREIGN KEY (id_prestacion)
      REFERENCES nomenclador (id_prestacion) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_prestaci_prest_prestad_lugar_a FOREIGN KEY (id_prestador, id_domicilio)
      REFERENCES prestad_lugar_atencion (id_prestador, id_domicilio) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_prestacion_prestador_prest FOREIGN KEY (id_prestacion)
      REFERENCES nomenclador (id_prestacion) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE prestacion_prestador OWNER TO postgres;
GRANT ALL ON TABLE prestacion_prestador TO postgres;
GRANT SELECT ON TABLE prestacion_prestador TO dschejtman;

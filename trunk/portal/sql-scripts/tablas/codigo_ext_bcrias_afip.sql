-- Table: codigo_ext_bcrias_afip

-- DROP TABLE codigo_ext_bcrias_afip;

CREATE TABLE codigo_ext_bcrias_afip
(
  codigo integer,
  descripcion character varying,
  id_tipo_mov integer,
  CONSTRAINT fk_codigo_ext_bcrias_afip_tipo_mov FOREIGN KEY (id_tipo_mov)
      REFERENCES tipo_mov_bcrio (id_tipo_mov) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE codigo_ext_bcrias_afip OWNER TO postgres;

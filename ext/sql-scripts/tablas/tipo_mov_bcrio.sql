-- Table: tipo_mov_bcrio

-- DROP TABLE tipo_mov_bcrio;

CREATE TABLE tipo_mov_bcrio
(
  id_tipo_mov integer NOT NULL DEFAULT nextval('tipo_mov_bcrio_id_seq'::regclass),
  descripcion character varying NOT NULL,
  baja_fecha timestamp without time zone,
  concepto_id integer,
  valido_desde date,
  valido_hasta date,
  modi_usr character varying,
  modi_fecha date,
  baja_usr character varying(255),
  alta_usr character varying,
  alta_fecha date,
  id_tipo_mov_maestro integer,
  CONSTRAINT pk_tipo_mov_bcrio PRIMARY KEY (id_tipo_mov),
  CONSTRAINT fk_tipo_mov_concepto FOREIGN KEY (concepto_id)
      REFERENCES concepto_maestro (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_tipo_mov_maestro FOREIGN KEY (id_tipo_mov_maestro)
      REFERENCES tipo_mov_bcrio_maestro (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tipo_mov_bcrio OWNER TO postgres;
GRANT ALL ON TABLE tipo_mov_bcrio TO postgres;

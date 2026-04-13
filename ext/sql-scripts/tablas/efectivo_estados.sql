-- Table: efectivo_estado

-- DROP TABLE efectivo_estado;

CREATE TABLE efectivo_estado
(
  id integer NOT NULL,
  descripcion character varying(50),
  cuenta_asociada character varying(20),
  id_plan_cuenta integer,
  CONSTRAINT pk_efectivo_estado PRIMARY KEY (id),
  CONSTRAINT kf_ef_estado_cuenta FOREIGN KEY (id_plan_cuenta)
      REFERENCES plan_cuentas_maestro (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE efectivo_estado OWNER TO postgres;

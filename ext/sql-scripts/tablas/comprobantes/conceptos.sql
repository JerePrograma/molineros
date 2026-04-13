-- Table: conceptos

-- DROP TABLE conceptos;

CREATE TABLE conceptos
(
  id serial NOT NULL,
  descripcion character varying(100),
  numero_cuenta character varying(20),
  liquidaciones boolean DEFAULT false,
  egreso boolean DEFAULT false,
  ingreso boolean DEFAULT false,
  cuenta_pasivo character varying(20),
  sub_egreso boolean,
  sub_ingreso boolean DEFAULT false,
  id_plan_cuenta integer,
  id_plan_cuenta_pasivo integer,
  valido_desde date,
  valido_hasta date,
  modi_usr character varying,
  modi_fecha date,
  alta_usr character varying,
  alta_fecha date,
  id_concepto_maestro integer,
  CONSTRAINT pk_conceptos PRIMARY KEY (id),
  CONSTRAINT fk_concepto_maestro FOREIGN KEY (id_concepto_maestro)
      REFERENCES concepto_maestro (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_cta FOREIGN KEY (id_plan_cuenta)
      REFERENCES plan_cuentas_maestro (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_cta_pasivo FOREIGN KEY (id_plan_cuenta_pasivo)
      REFERENCES plan_cuentas_maestro (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE conceptos OWNER TO postgres;

-- Index: idx_conceptos

-- DROP INDEX idx_conceptos;

CREATE INDEX idx_conceptos
  ON conceptos
  USING btree
  (id_concepto_maestro, valido_desde, valido_hasta);


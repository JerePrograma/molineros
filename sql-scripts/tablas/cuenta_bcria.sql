-- Table: cuenta_bcria

-- DROP TABLE cuenta_bcria;

CREATE TABLE cuenta_bcria
(
  id_cuenta_bcria integer NOT NULL,
  nro_cuenta integer NOT NULL,
  sucursal integer NOT NULL,
  id_banco integer NOT NULL,
  descripcion character varying NOT NULL,
  cuenta_asociada character varying(20),
  id_plan_cuenta integer,
  CONSTRAINT pk_cta_bcria PRIMARY KEY (id_cuenta_bcria),
  CONSTRAINT kf_cta_bcria_cuenta FOREIGN KEY (id_plan_cuenta)
      REFERENCES plan_cuentas_maestro (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cuenta_bcria OWNER TO postgres;

-- Table: plan_cuentas

-- DROP TABLE plan_cuentas;

CREATE TABLE plan_cuentas_maestro
(
  numero character varying(20) NOT NULL,
  cuenta character varying(100),
  imputable boolean,
  tipo character varying(10),
  id serial NOT NULL,
  CONSTRAINT pk_plan_cuentas_maestro PRIMARY KEY (id),
  CONSTRAINT u_plan_cuentas UNIQUE (numero)
)
WITH (
  OIDS=FALSE
);

alter table plan_cuentas add numero character varying(20);
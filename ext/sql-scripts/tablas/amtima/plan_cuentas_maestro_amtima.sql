CREATE TABLE plan_cuentas_maestro_amtima
(
  numero character varying(20) NOT NULL,
  cuenta character varying(100),
  imputable boolean,
  tipo character varying(10),
  id integer NOT NULL DEFAULT nextval('plan_cuentas_amtima_id_seq'::regclass),
  CONSTRAINT pk_plan_cuentas_amtima PRIMARY KEY (id),
  CONSTRAINT u_plan_cuentas_amtima UNIQUE (numero)
)
WITH (
  OIDS=FALSE
);

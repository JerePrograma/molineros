CREATE TABLE plan_cuentas_amtima
(
  cuenta character varying(100),
  imputable boolean,
  tipo character varying(10),
  valido_desde date,
  valido_hasta date,
  id integer NOT NULL DEFAULT nextval('plan_cuentas_amtima_id_seq1'::regclass),
  id_cuenta_maestro integer,
  numero character varying(20),
  modi_usr character varying,
  modi_fecha date,
  alta_usr character varying,
  alta_fecha date,
  CONSTRAINT pk_plan_cuenta_amtima PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

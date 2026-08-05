CREATE TABLE conceptos_amtima
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
  CONSTRAINT pk_conceptos_amtima PRIMARY KEY (id),
  CONSTRAINT fk_concepto_maestro_amtima FOREIGN KEY (id_concepto_maestro)
      REFERENCES concepto_maestro_amtima (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_cta_amtima FOREIGN KEY (id_plan_cuenta)
      REFERENCES plan_cuentas_maestro_amtima (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_cta_pasivo_amtima FOREIGN KEY (id_plan_cuenta_pasivo)
      REFERENCES plan_cuentas_maestro_amtima (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

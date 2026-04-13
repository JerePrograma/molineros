CREATE TABLE detalle_asiento_amtima
(
  id serial NOT NULL,
  asiento_id integer,
  pase integer,
  id_plan_cuentas integer,
  comprobante character varying,
  debe numeric(12,2),
  haber numeric(12,2),
  observaciones character varying,
  CONSTRAINT pk_detalle_asiento_amtima PRIMARY KEY (id),
  CONSTRAINT fk_detalle_asiento_a_amtima FOREIGN KEY (asiento_id)
      REFERENCES asiento_amtima (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_detalle_asiento_plan_cuentas_amtima FOREIGN KEY (id_plan_cuentas)
      REFERENCES plan_cuentas_maestro_amtima (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

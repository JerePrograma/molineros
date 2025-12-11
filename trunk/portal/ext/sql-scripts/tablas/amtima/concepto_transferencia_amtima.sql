CREATE TABLE concepto_transferencia_amtima
(
  liquidable boolean,
  concepto_id integer,
  valido_desde date,
  valido_hasta date,
  id serial NOT NULL,
  modi_usr character varying,
  modi_fecha date,
  alta_usr character varying,
  alta_fecha date,
  tipo_boleta integer,
  CONSTRAINT pk_concepto_transferencia_amtima PRIMARY KEY (id),
  CONSTRAINT fk_concepto_transferencia_amtima FOREIGN KEY (concepto_id)
      REFERENCES concepto_maestro_amtima (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

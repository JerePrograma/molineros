alter table concepto_transferencia  add modi_usr character varying;
alter table concepto_transferencia add modi_fecha date;
alter table concepto_transferencia  add alta_usr character varying;
alter table concepto_transferencia add alta_fecha date;

-- Table: concepto_transferencia

-- DROP TABLE concepto_transferencia;

CREATE TABLE concepto_transferencia
(
  concepto_transf character varying(10) NOT NULL,
  liquidable boolean,
  concepto_id integer,
  valido_desde date,
  valido_hasta date,
  id serial NOT NULL,
  modi_usr character varying,
  modi_fecha date,
  alta_usr character varying,
  alta_fecha date,
  CONSTRAINT pk_conceptos_transferencia PRIMARY KEY (id),
  CONSTRAINT fk_concepto_transferencia FOREIGN KEY (concepto_id)
      REFERENCES concepto_maestro (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_conceptos_transf_os FOREIGN KEY (concepto_transf)
      REFERENCES conceptos_transf_os (cod_conc) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE concepto_transferencia OWNER TO postgres;

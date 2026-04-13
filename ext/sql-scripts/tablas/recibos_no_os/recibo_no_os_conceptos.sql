-- Table: recibo_no_os_conceptos

-- DROP TABLE recibo_no_os_conceptos;

CREATE TABLE recibo_no_os_conceptos
(
  id serial NOT NULL,
  recibo_id integer NOT NULL,
  acta_id integer,
  convenio_id integer,
  nro_cheque_no_depositado numeric,
  id_banco_no_depositado integer,
  nro_cheque_rechazado numeric,
  id_banco_rechazado integer,
  caja_concepto_id integer,
  concepto_importe_por_cheques numeric(12,2),
  concepto_importe_adicional numeric(12,2),
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(50) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(50) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(50),
  CONSTRAINT pk_recibo_no_os_conceptos PRIMARY KEY (id ),
  CONSTRAINT fk_recibo_no_os_conceptos_act FOREIGN KEY (acta_id)
      REFERENCES acta_no_os (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_recibo_no_os_conceptos_caja_concepto FOREIGN KEY (caja_concepto_id)
      REFERENCES concepto_maestro (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_recibo_no_os_conceptos_cheque_no_depo FOREIGN KEY (nro_cheque_no_depositado, id_banco_no_depositado)
      REFERENCES cheque (nro_cheque, id_banco) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_recibo_no_os_conceptos_cheque_rech FOREIGN KEY (nro_cheque_rechazado, id_banco_rechazado)
      REFERENCES cheque (nro_cheque, id_banco) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_recibo_no_os_conceptos_conv FOREIGN KEY (convenio_id)
      REFERENCES convenio_no_os (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_recibo_no_os_conceptos_rec FOREIGN KEY (recibo_id)
      REFERENCES recibo_no_os (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE recibo_no_os_conceptos
  OWNER TO postgres;


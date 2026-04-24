-- Table: cheque

-- DROP TABLE cheque;

CREATE TABLE cheque
(
  nro_cheque numeric(15,0) NOT NULL,
  id_banco integer NOT NULL,
  cuit character varying(13),
  a_nombre_de character varying(250),
  fecha timestamp without time zone NOT NULL,
  importe numeric(10,2) NOT NULL,
  prestador boolean,
  concepto character varying(700),
  id_cta_bcria integer,
  debito_credito character(1) NOT NULL,
  id_estado integer NOT NULL,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  alta_ip character varying(15),
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  modi_ip character varying(15),
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  baja_ip character varying(15),
  CONSTRAINT pk_cheque PRIMARY KEY (nro_cheque, id_banco),
  CONSTRAINT fk_cheque_banco FOREIGN KEY (id_banco)
      REFERENCES banco (id_banco) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_cheque_estado FOREIGN KEY (id_estado)
      REFERENCES cheque_estado (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_cta_bcria FOREIGN KEY (id_cta_bcria)
      REFERENCES cuenta_bcria (id_cuenta_bcria) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cheque OWNER TO postgres;

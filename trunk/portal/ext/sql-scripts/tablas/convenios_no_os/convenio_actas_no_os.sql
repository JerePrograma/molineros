-- Table: convenio_actas_no_os

-- DROP TABLE convenio_actas_no_os;

CREATE TABLE convenio_actas_no_os
(
  convenio_id integer NOT NULL,
  acta_id integer NOT NULL,
  importe numeric(10,2),
  saldo numeric(10,2),
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  alta_ip character varying(15),
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  modi_ip character varying(15),
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  baja_ip character varying(15),
  id serial NOT NULL,
  CONSTRAINT pk_convenio_actas_no_os PRIMARY KEY (id ),
  CONSTRAINT "fk_convenio_Actas_acta_no_os" FOREIGN KEY (acta_id)
      REFERENCES acta_no_os (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "fk_convenio_Actas_conv_no_os" FOREIGN KEY (convenio_id)
      REFERENCES convenio_no_os (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE convenio_actas_no_os
  OWNER TO postgres;


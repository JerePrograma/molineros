-- Table: acta_no_os_relacion

-- DROP TABLE acta_no_os_relacion;

CREATE TABLE acta_no_os_relacion
(
  acta_id integer,
  acta_relacionada_id integer,
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
  CONSTRAINT pk_acta_no_os_relacion PRIMARY KEY (id ),
  CONSTRAINT "fk_Acta_no_os_relac_relac" FOREIGN KEY (acta_relacionada_id)
      REFERENCES acta_no_os (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_acta_no_os_relac_acta FOREIGN KEY (acta_id)
      REFERENCES acta_no_os (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE acta_no_os_relacion
  OWNER TO postgres;


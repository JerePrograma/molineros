-- Table: convenio_no_os_relacion

-- DROP TABLE convenio_no_os_relacion;

CREATE TABLE convenio_no_os_relacion
(
  convenio_id integer NOT NULL,
  convenio_relacionado_id integer NOT NULL,
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
  CONSTRAINT pk_convenio_no_os_relacion PRIMARY KEY (id ),
  CONSTRAINT fk_convenio_no_os_rel_c FOREIGN KEY (convenio_id)
      REFERENCES convenio_no_os (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_convenio_no_os_rel_rel FOREIGN KEY (convenio_relacionado_id)
      REFERENCES convenio_no_os (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE convenio_no_os_relacion
  OWNER TO postgres;


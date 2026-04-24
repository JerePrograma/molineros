CREATE TABLE reintegro_farmacia
(
  fecha timestamp without time zone NOT NULL NOT NULL,
  periodo date NOT NULL,
  id_seccional integer NOT NULL,
  cuil_titular character varying NOT NULL,
  inte integer NOT NULL,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying,
  id_reintegro serial NOT NULL,
  observacion character varying(1000),
  CONSTRAINT pk_reintegro_farmacia PRIMARY KEY (id_reintegro),
  CONSTRAINT fk_id_seccional FOREIGN KEY (id_seccional)
      REFERENCES seccional (id_seccional) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE reintegro_farmacia OWNER TO postgres;
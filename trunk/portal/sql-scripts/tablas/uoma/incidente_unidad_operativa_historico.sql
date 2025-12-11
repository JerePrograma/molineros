-- Table: uoma.incidente_unidad_operativa_historico

-- DROP TABLE uoma.incidente_unidad_operativa_historico;

CREATE TABLE uoma.incidente_unidad_operativa_historico
(
  id_incidente integer NOT NULL,
  cuil_titular character varying NOT NULL,
  fecha timestamp without time zone NOT NULL,
  id_domicilio integer,
  inte integer NOT NULL,
  detalle_incidente text,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  id_seccional integer,
  fecha_recepcion date,
  CONSTRAINT fk_incidente_afiliado_hist FOREIGN KEY (cuil_titular, inte)
      REFERENCES afiliado (cuil_titular, inte) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_incidente_domicilio_histo FOREIGN KEY (id_domicilio)
      REFERENCES uoma.domicilio (id_domicilio) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE uoma.incidente_unidad_operativa_historico OWNER TO postgres;

